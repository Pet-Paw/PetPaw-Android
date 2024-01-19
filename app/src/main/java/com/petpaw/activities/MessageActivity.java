package com.petpaw.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.petpaw.R;
import com.petpaw.adapters.ConversationListAdapter;
import com.petpaw.adapters.MessageListAdapter;
import com.petpaw.databinding.ActivityMessageBinding;
import com.petpaw.interfaces.OnConversationClickListener;
import com.petpaw.models.Conversation;
import com.petpaw.models.Message;
import com.petpaw.models.User;
import com.petpaw.utils.ImageHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity {

    private ActivityMessageBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String conversationID;
    private Conversation conversation;
    private List<Message> messageList = new ArrayList<>();
    private MessageListAdapter messageListAdapter;
    private FusedLocationProviderClient client;

    private GeoPoint curLocation =  new GeoPoint(0,0);
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "FCM can't post notifications  without POST_NOTIFICATION permission", Toast.LENGTH_LONG).show();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        setupMessageRV();
        getConversation();


        binding.sendMessageBtn.setOnClickListener(v -> onBtnSendClick());
        binding.backBtn.setOnClickListener(v -> finish());
        binding.shareLocationBtn.setOnClickListener(v -> getLocation());

    }

    private void onBtnSendClick() {
        if(binding.etSendMessage.getText().length() != 0){
            Message message = new Message();
            message.setContent(binding.etSendMessage.getText().toString());
            message.setSenderId(auth.getCurrentUser().getUid());
            message.setSentAt(new Date());

            db.collection(Conversation.CONVERSATIONS)
                    .document(conversationID)
                    .collection(Message.MESSAGES)
                    .add(message.toDoc())
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            binding.etSendMessage.setText("");
                            updateLatestMessageId(task.getResult().getId());
                            getConversation();
                        }
                    });
        }
    }

    private void updateLatestMessageId(String latestMessageId) {
        Log.d("MessageAct", "conversationID = " + conversationID);

        db.collection(Conversation.CONVERSATIONS)
                .document(conversationID)
                .update("latestMessageId", latestMessageId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("MessageAct", "update message " + latestMessageId);
                    }
                });
    }

    private void getConversation(){
        Intent intent = getIntent();
        conversationID = intent.getStringExtra("conversationID");

        if(!intent.hasExtra("conversationID")) {
            return;
        }

//        db.collection(Conversation.CONVERSATIONS)
//                .document(conversationID)
//                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                        @Nullable FirebaseFirestoreException error) {
//                        if (error != null) {
//                            Log.w("messageAct.java", "Listen Failed", error);
//                            return;
//                        }
//
//                        if (snapshot != null && snapshot.exists()) {
//                            Log.d("messageAct.java", "data: " + snapshot.getData());
//                            conversation = snapshot.getData().to
//                        } else {
//                            Log.d("messageAct.java", "data: null");
//                        }
//                    }
//                });

//        db.collection(Conversation.CONVERSATIONS)
//                .document(conversationID)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful()){
//                            conversation = task.getResult().toObject(Conversation.class);
//                        }
//                        getMessages();
//                    }
//                });

        db.collection(Conversation.CONVERSATIONS)
                .document(conversationID)
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        conversation = value.toObject(Conversation.class);
                        getMessages();
                    }
                });
    }

    private void getMessages(){
        db.collection(Conversation.CONVERSATIONS)
                .document(conversationID)
                .collection(Message.MESSAGES)
                .orderBy("sentAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        messageList.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            messageList.add(doc.toObject(Message.class));
                        }
                        messageListAdapter.setMessageList(messageList);
                        getUsers();
                    }
                });
    }
    private void getUsers(){
        db.collection(User.USERS)
                .whereIn(FieldPath.documentId(), conversation.getMemberIdList())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, User> userMap = new HashMap<>();

                        for (DocumentSnapshot doc: queryDocumentSnapshots) {
                            User user = doc.toObject(User.class);
                            userMap.put(user.getUid(), user);
                        }
                        Log.d("TAG", "User Map: " + userMap);
                        if(conversation.getMemberIdList().size() <= 2){
                            for (String userId: conversation.getMemberIdList()) {
                                if (!Objects.equals(userId, auth.getCurrentUser().getUid())) {
                                    User user = userMap.get(userId);
                                    binding.tvName.setText(user.getName());
                                    if(user.getImageURL() == null){
                                        binding.ivUserPic.setImageResource(R.drawable.default_avatar);
                                    } else {
                                        Picasso.get()
                                                .load(user.getImageURL())
                                                .tag(System.currentTimeMillis())
                                                .into(binding.ivUserPic, new com.squareup.picasso.Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Log.d("TAG", "Load image successfully");
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        Log.e("TAG", "Load image failed");
                                                    }
                                                });
                                    }
                                }
                            }
                        } else {
                            binding.ivUserPic.setImageResource(R.drawable.group_chat_image);
                            String names = "";
                            for (String userId: conversation.getMemberIdList()) {
                                if (!Objects.equals(userId, auth.getCurrentUser().getUid())) {
                                    names += userMap.get(userId).getName();
                                    if(conversation.getMemberIdList().indexOf(userMap.get(userId).getUid()) != (conversation.getMemberIdList().size() - 1)){
                                        names += ", ";
                                    }
                                }
                            }
                            binding.tvName.setText(names);
                        }

                        messageListAdapter.setUserMap(userMap);
                    }
                });
    }


    private void setupMessageRV(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        binding.rvMessageList.setLayoutManager(linearLayoutManager);
        messageListAdapter = new MessageListAdapter(getBaseContext());
        binding.rvMessageList.setAdapter(messageListAdapter);

    }

    private boolean getLocation(){
        if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askLocationPermission();
            return false;
        } else {
            client = LocationServices.getFusedLocationProviderClient(MessageActivity.this);
            client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location == null){
                        Log.d("TAG", "No location");
                    } else {
                        curLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                    }
                }
            });
            return true;
        }

    }

    private void askLocationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if ((ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) &&  (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED)){
                    Message message = new Message();
                    message.setContent(curLocation.toString());
                    message.setSenderId(auth.getCurrentUser().getUid());
                    message.setSentAt(new Date());
                    Map<String, Object> map = message.toDoc();
                    map.put("location", curLocation);

                    db.collection(Conversation.CONVERSATIONS)
                            .document(conversationID)
                            .collection(Message.MESSAGES)
                            .add(map)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    updateLatestMessageId(task.getResult().getId());
                                    getConversation();
                                }
                            });
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
    }
}