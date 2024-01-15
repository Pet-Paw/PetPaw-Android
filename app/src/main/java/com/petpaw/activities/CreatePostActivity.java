package com.petpaw.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

//import static io.grpc.Context.LazyStorage.storage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;
import com.petpaw.R;
import com.petpaw.databinding.ActivityCreatePostBinding;
import com.petpaw.databinding.ActivityMainBinding;
import com.petpaw.models.Pet;
import com.petpaw.models.Post;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petpaw.databinding.ActivityCreatePostBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {
    ActivityCreatePostBinding binding;
    ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private String currentUserId;
    private Uri imageUri;
    private StorageReference storageReference;
    private DocumentReference postDocRef;
    private boolean isSelectNewImage = false;


//    ------------------ testing----------------------
    private List<String> selectedPetListId = new ArrayList<>(); // For storing the selected pet
//    -------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        db = FirebaseFirestore.getInstance();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        Log.d("CreatePostActivity", "Current User Id: " + currentUserId);

        Intent intent = getIntent();
        String postId = intent.getStringExtra("postId");

//        Bind the select button to the selectImage function
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

// ---------------------------------------- Edit post ------------------------------------------------
        if(postId != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Fetching Post Detail....");
            progressDialog.show();

            binding.createPostUploadButton.setVisibility(View.GONE);
            Log.d("CreatePostActivity", "PostId: " + postId);

            binding.createPostSelectImageButton.setOnClickListener(v -> {
                selectImage();
                isSelectNewImage = true;
            });

            DocumentReference postRef;
            postRef = db.collection("Posts").document(postId);
            postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            postDocRef = postRef;

//                           ----------- Load image with the URL then the description -----------
                            String imageUrl = document.getString("imageURL");
                            Picasso.get()
                                    .load(imageUrl)
                                    .tag(System.currentTimeMillis())
                                    .into(binding.createPostImageView, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            binding.createPostImageView.setVisibility(View.VISIBLE);
                                            if (progressDialog.isShowing())
                                                progressDialog.dismiss();
                                            Log.d("TAG", "Load image successfully");
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Log.e("TAG", "Load image failed");
                                        }
                                    });

                            String displayDescription = combineContentAndTag(document.getString("content"), (List<String>) document.get("tags"));
                            binding.createPostDescriptionEditText.setText(displayDescription);

//                          ------------ Render Pet List ----------------
                            selectedPetListId = (List<String>) document.get("petIdList");
                            renderPetListView(true);

//                          ------------ Check valid input  -------------
                            binding.createPostSaveButton.setOnClickListener(v -> {
                                boolean isValid = true;
                                List<String> tags = new ArrayList<>();

                                String description = binding.createPostDescriptionEditText.getText().toString();

                                if(!description.isEmpty()){
                                    String[] parts = description.split("#");
                                    for(int i=1; i<parts.length; i++) {
                                        String part = parts[i];
                                        if(!part.isEmpty() && part.charAt(0) != '#') {
                                            tags.add(part);
                                        }
                                    }
                                    Log.d("CreatePostActivity", "Tags: " + tags.toString());
                                }

//                              -------- update if valid input --------

                                if(isValid) {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("content", description);
                                    data.put("tags", tags);
                                    data.put("dateModified", new Date());
                                    data.put("modified", true);
                                    data.put("petId", selectedPetListId.get(0));
                                    data.put("petIdList", selectedPetListId);

                                    postRef.update(data)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("CreatePostActivity", "DocumentSnapshot successfully updated!");
                                                if(isSelectNewImage) {
                                                    uploadImage(postId,true);
                                                } else {
                                                    finish();
                                                }
                                            })      .addOnFailureListener(e->{
                                                Log.e("CreatePostActivity", "Error updating document", e);
                                            });
                                }
                            });

                            storageReference = FirebaseStorage.getInstance().getReference("postImages/"+postId);

                            binding.createPostDeleteButton.setOnClickListener(v -> {
                                postRef.delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                storageReference.delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("TAG", "Image deleted from storage!");
                                                            }
                                                        });

                                                Log.d("TAG", "Document deleted from Firestore!");
                                                finish();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Log.w("TAG", "Failed to delete image and/or document", exception);
                                            }
                                        });
                            });

                        } else {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Toast.makeText(CreatePostActivity.this, "Post Detail not found", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "No document found");
                            finish();
                        }
                    } else {
                        Log.d("TAG", "Get failed with ", task.getException());
                    }
                }
            });

        }
        else { // ------------------------------ CREATE a new post -----------------------------------

//            ---------- ListView to select pet -----------
            renderPetListView(false);

//            ---------- Spinner to select pet ------------
            //renderPetSpinner();

            binding.createPostSelectImageButton.setOnClickListener(v -> {
                selectImage();
            });

            binding.createPostButtonLinearLayout.setVisibility(View.GONE);
            binding.createPostUploadButton.setOnClickListener(v -> {
                boolean isValid = true;

                String tempDescription = binding.createPostDescriptionEditText.getText().toString();
                List<String> tags = getTags(tempDescription);
                String description = removeTag(tempDescription, tags);
                Log.d("CreatePostActivity", "Description: " + description);


//                -------- Check valid input --------

                if(isValid){
                    Post post = new Post();
                    post.setDateModified(new Date());
                    post.setContent(description);
                    post.setAuthorId(currentUserId);
                    post.setPetId(selectedPetListId.get(0));
                    post.setPetIdList(selectedPetListId);
                    post.setTags(tags);


                    db.collection("Posts")
                            .add(post)
                            .addOnSuccessListener(documentReference -> {
                                String id = documentReference.getId();
                                DocumentReference postRef = db.collection("Posts").document(id);
                                postRef.update("postId", id)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("CreatePostActivity", "DocumentSnapshot successfully updated!");
                                        })      .addOnFailureListener(e->{
                                    Log.e("CreatePostActivity", "Error updating document", e);
                                });

                                postDocRef = documentReference;
                                uploadImage(id, false);

                                Toast.makeText(this, "Post Successfully Created", Toast.LENGTH_SHORT).show();

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error creating post", Toast.LENGTH_SHORT).show();
                            });


                }


            });
        }

        binding.createPostBackButton.setOnClickListener(v -> {
            finish();
        });
    }

    /*
    private void renderPetSpinner(){
        CollectionReference petsRef = db.collection("Pets");
        List<String> petListId = new ArrayList<>();
        List<String> petListName = new ArrayList<>();

        Query query = petsRef.whereEqualTo("ownerId", currentUserId);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    petListId.add(document.getId());
                    petListName.add(document.getString("name"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        petListName
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.createPostTagsSpinner.setPrompt("Select a pet");
                binding.createPostTagsSpinner.setAdapter(adapter);

                binding.createPostTagsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(selectedPetListId.size() > 0){
                            selectedPetListId.remove(0);
                        }
                        selectedPetListId.add(petListId.get(position));
                        Log.d("CreatePostActivity", "Selected pet ID: " + selectedPetListId.get(0).toString());
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

            } else {
                Log.d("Get pet ID query", "Error getting documents: ", task.getException());
            }

        });
    }
    */


    private void renderPetListView(boolean isEditPost){
        Log.d("CreatePostActivity", "Render pet list view");
        CollectionReference petsRef = db.collection("Pets");
        //------- Fetch data ----------
        List<String> petListId = new ArrayList<>();
        List<String> petListName = new ArrayList<>();

        Query query = petsRef.whereEqualTo("ownerId", currentUserId);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("CreatePostActivity", "Fetch pet ID successfully");
                for (QueryDocumentSnapshot document : task.getResult()) {
                    petListId.add(document.getId());
                    petListName.add(document.getString("name"));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        petListName
                );

                binding.createPostTagsListView.setAdapter(adapter);
                Log.d("CreatePostActivity", "Pet list view adapter set");


                //------- Set color to the selected pet -----------
                Log.d("CreatePostActivity", "Selected pet ID: " + selectedPetListId);
                if(isEditPost){
                    binding.createPostTagsListView.post(() -> {
                    Log.d("CreatePostActivity", "Selected pet ID: " + selectedPetListId);
                        for(int i=0; i<selectedPetListId.size(); i++){
                            int index = petListId.indexOf(selectedPetListId.get(i));

                            View view = binding.createPostTagsListView.getChildAt(index);
                            if(view != null) {
                                view.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
                                view.setTag(true);
                            }
                        }
                    });
                }

                //--------- Set on click listener to the list view -----------
                binding.createPostTagsListView.setOnItemClickListener((parent, view, position, id) -> {

                    if(view.getTag() == null) {
                        view.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
                        view.setTag(true);
                        if(!selectedPetListId.contains(petListId.get(position))) {
                            selectedPetListId.add(petListId.get(position));
                        }

                    } else {
                        view.setBackgroundColor(Color.WHITE);
                        view.setTag(null);
                        selectedPetListId.remove(petListId.get(position));
                    }
                    Log.d("CreatePostActivity", "Selected pet ID: " + selectedPetListId.toString());
                });


            } else {
                Log.d("Get pet ID query", "Error getting documents: ", task.getException());
            }

        });
    }


    private List<String> getTags(String description) {
        List<String> tags = new ArrayList<>();
        if(!description.isEmpty()){
            String[] parts = description.split("#");
            for(int i=1; i<parts.length; i++) {
                String part = parts[i];
                if(!part.isEmpty() && part.charAt(0) != '#') {
                    tags.add(part);
                }
            }
            Log.d("CreatePostActivity", "Tags: " + tags.toString());
        }
        return tags;
    }

    private String removeTag(String description, List<String> tags) {
        for(int i=0; i<tags.size(); i++) {
            description = description.replace("#" + tags.get(i), "");
        }
        return description;
    }

    private String combineContentAndTag(String description, List<String> tags) {
        for(int i=0; i<tags.size(); i++) {
            description += "#" + tags.get(i) + " ";
        }
        return description;
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    private void uploadImage(String postId, boolean isEditImage) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading to Database....");
        progressDialog.show();

        //Change the time to VN time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.TAIWAN);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("postImages/"+postId);


        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        binding.createPostImageView.setImageURI(null);
                        binding.createPostImageView.setVisibility(View.GONE);
                        binding.createPostDescriptionEditText.setText("");
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.d("CreatePostActivity", "Image uploaded successfully");
                        // Get download URL after upload completes
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Log.d("CreatePostActivity", "Image URL: " + uri);

                                    if(isEditImage){
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("imageURL", uri.toString());
                                        postDocRef.update(data)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("CreatePostActivity", "Update image path successfully");

                                                    finish();
//                                                    startActivity(getIntent());

//                                                    recreate();

                                                })      .addOnFailureListener(e->{
                                                    Log.e("CreatePostActivity", "Error updating document", e);
                                                });
                                        finish();
                                    }else{
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("imageURL", uri.toString());
                                        postDocRef.update(data)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("CreatePostActivity", "DocumentSnapshot successfully updated!");
                                                finish();
                                                })      .addOnFailureListener(e->{
                                                    Log.e("CreatePostActivity", "Error updating document", e);
                                                });
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("CreatePostActivity", "Error uploading image", e);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();
            binding.createPostImageView.setImageURI(imageUri);
            binding.createPostImageView.setVisibility(View.VISIBLE);
        }
    }

}