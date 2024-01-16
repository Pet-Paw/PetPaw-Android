package com.petpaw.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
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

    ActivityMessageBinding binding;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String conversationID;
    Conversation conversation;
    List<Message> messageList = new ArrayList<>();
    MessageListAdapter messageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        setupMessageRV();
        getConversation();

        binding.sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                    getConversation();
                                }
                            });
                }
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getConversation(){
        Intent intent = getIntent();
        conversationID = intent.getStringExtra("conversationID");
        if(intent.hasExtra("conversationID")){
            db.collection(Conversation.CONVERSATIONS)
                    .document(conversationID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                conversation = task.getResult().toObject(Conversation.class);
                            }
                            getMessages();
                        }
                    });
        }
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
}