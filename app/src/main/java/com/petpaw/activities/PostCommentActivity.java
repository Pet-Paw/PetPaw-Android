package com.petpaw.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.petpaw.R;
import com.petpaw.adapters.CommentListAdapter;
import com.petpaw.clients.NotiSender;
import com.petpaw.database.NotificationCollection;
import com.petpaw.database.PostCollection;
import com.petpaw.database.UserCollection;
import com.petpaw.databinding.ActivityPostCommentBinding;
import com.petpaw.databinding.FragmentProfileBinding;
import com.petpaw.databinding.FragmentSideNavBinding;
import com.petpaw.fragments.screens.SideNavFragment;
import com.petpaw.models.Comment;
import com.petpaw.models.NotificationPetPaw;
import com.petpaw.models.Post;
import com.petpaw.models.User;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PostCommentActivity extends AppCompatActivity {

    ActivityPostCommentBinding binding;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String postId;

    UserCollection userCollection = UserCollection.newInstance();
    NotificationCollection notificationCollection = NotificationCollection.newInstance();

    List<String> commentIDList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        binding = ActivityPostCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Inflate the layout for this fragment
        Intent intent = getIntent();
        if (intent.hasExtra("postID")){
            postId = intent.getExtras().getString("postID").toString();
            //Toast.makeText(this, postId, Toast.LENGTH_SHORT).show();
            getComments();
            binding.createCommentBtn.setOnClickListener(v -> {
                if (binding.createCommentEditText.getText().toString().length() != 0){
                    Comment cmt = new Comment(null, binding.createCommentEditText.getText().toString(), auth.getCurrentUser().getUid(), new ArrayList<>());
                    binding.createCommentEditText.setText("");
                    db.collection("Comments")
                            .add(cmt.toDoc())
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()){
                                        DocumentReference doc = task.getResult();
                                        db.collection("Comments")
                                                .document(doc.getId())
                                                .update("commentId", doc.getId())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        db.collection("Posts").document(postId)
                                                                .update("comments", FieldValue.arrayUnion(doc.getId()))
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        getComments();
                                                                        Log.d("TAG", "Comment posted successfully ");
                                                                    }
                                                                });
                                                    }
                                                });

                                        CollectionReference postsRef = db.collection("Posts"); // Get a reference to the Posts collection
                                        Query query = postsRef.whereEqualTo(FieldPath.documentId(), postId); // Create a query against the collection.
                                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document: task.getResult()) {
                                                        Post post = document.toObject(Post.class);
                                                        Post postTemp = new Post(post.getAuthorId(), post.getDateModified(), post.getContent(), post.isModified(), post.getImageURL(), post.getLikes(), post.getComments(), post.getPostId(), post.getTags(), post.getPetIdList(), post.getCommunityId());
                                                        String postAuthorId = postTemp.getAuthorId();
                                                        String currentUserUid = auth.getCurrentUser().getUid();

                                                        userCollection.getUser(postAuthorId, new UserCollection.Callback() {
                                                            @Override
                                                            public void onCallback(List<User> users) {

                                                            }

                                                            @Override
                                                            public void onCallBack(User postAuthor) {
                                                                NotiSender notiSender = new NotiSender(currentUserUid);

                                                                if (!Objects.equals(postAuthor.getUid(), currentUserUid)) {
                                                                    userCollection.getUser(currentUserUid, new UserCollection.Callback() {
                                                                        @Override
                                                                        public void onCallback(List<User> users) {

                                                                        }

                                                                        @Override
                                                                        public void onCallBack(User user) {
                                                                            String currentUserName = user.getName();
                                                                            notiSender.sendNotificationToDifferentAccount(postAuthor.getUid(), currentUserName, " has commented: " + cmt.getContent());
                                                                        }
                                                                    });
                                                                }
                                                                try {
                                                                    notiSender.sendNotificationOnCurrentAccount("You have commented: " + cmt.getContent());
                                                                } catch (IOException e) {
                                                                    throw new RuntimeException(e);
                                                                }
                                                            }
                                                        });

                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                }
            });
        } else {
            //handle no post id found
        }

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        super.onStart();
    }

    public void getComments(){
        List<Comment> commentList = new ArrayList<>();
        // Get a reference to the Posts collection
        db.collection("Posts").document(postId).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();
                                Post post = doc.toObject(Post.class);
                                commentIDList = post.getComments();
                                if(commentIDList.size() != 0){
                                    db.collection("Comments")
                                            .whereIn(FieldPath.documentId(), commentIDList)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        commentList.clear();
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            commentList.add(document.toObject(Comment.class));
                                                        }
                                                        binding.postDetailCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                                                        binding.postDetailCommentRecyclerView.setAdapter(new CommentListAdapter(getBaseContext(), commentList));
                                                    }
                                                }
                                            });
                                }

                            }
                        });
    }

}