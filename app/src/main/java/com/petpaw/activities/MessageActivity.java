package com.petpaw.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Application;
import android.content.Context;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
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
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class MessageActivity extends AppCompatActivity {

    private ActivityMessageBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String conversationID;
    private Conversation conversation;
    private List<Message> messageList = new ArrayList<>();
    private MessageListAdapter messageListAdapter;
    private Map<String, User> userMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.btnVoiceCall.setIsVideoCall(false);
        binding.btnVideoCall.setIsVideoCall(true);

        setupMessageRV();
        getConversation();

        requestCallPermissions();

        binding.sendMessageBtn.setOnClickListener(v -> onBtnSendClick());
        binding.backBtn.setOnClickListener(v -> finish());
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

    private void getConversation() {
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
                        userMap = new HashMap<>();

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
                            StringBuilder names = new StringBuilder();
                            int cnt = 0;
                            for (String userId: conversation.getMemberIdList()) {
                                if (!Objects.equals(userId, auth.getCurrentUser().getUid())) {
                                    names.append(userMap.get(userId).getName());
                                    cnt++;

                                    if (cnt == 3) {
                                        break;
                                    }

                                    if(conversation.getMemberIdList().indexOf(userMap.get(userId).getUid()) != (conversation.getMemberIdList().size() - 1)){
                                        names.append(", ");
                                    }
                                }
                            }

                            binding.tvName.setText(names.toString());
                        }

                        messageListAdapter.setUserMap(userMap);

                        Timber.tag("MessageActivity.java").d(conversation == null ? "Null" : "No null");

                        setupVoiceCallBtn();
                        setupVideoCallBtn();
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

    private void onBtnCallClick() {
    }

    private void requestCallPermissions() {
        PermissionX.init(MessageActivity.this)
                .permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                        String message = "We need your consent for the following permissions in order to use the offline call function properly";
                        scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny");
                    }
                })
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        initCallInvitationService();
//                        onlineCall();
                    }
                });
    }

    private void initCallInvitationService() {
        long appID = 834069049;   // yourAppID
        String appSign = "73a133c6d11d9eb82624798a9d3db20b2e9d24aeb253c14b9fecc56f57181250";  // yourAppSign
        String userID = auth.getCurrentUser().getUid(); // yourUserID, userID should only contain numbers, English characters, and '_'.
        String userName = "Reciever";

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

        ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
        notificationConfig.sound = "zego_uikit_sound_call";
        notificationConfig.channelID = "CallInvitation";
        notificationConfig.channelName = "CallInvitation";

        ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);
    }

//    private void onlineCall() {
//        String currentUserId = auth.getCurrentUser().getUid();
//        String targetUserId = null;
//
//        if (conversation.getMemberIdList().size() > 2) {
//            return;
//        }
//
//        for (String userId: conversation.getMemberIdList()) {
//            if (!userId.equals(currentUserId)) {
//                targetUserId = userId;
//                break;
//            }
//        }
//
//        Log.d("messageActivity.java", "Chuan bi call");
//
//        String targetUserName = "Receiver";
//        Context context = MessageActivity.this;
//
//        ZegoSendCallInvitationButton callButton = new ZegoSendCallInvitationButton(context);
//        callButton.setIsVideoCall(false);
//        callButton.setResourceID("Pet Paw"); // Please fill in the resource ID name that has been configured in the ZEGOCLOUD's console here.
//        callButton.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserId,targetUserName)));
//
//        Log.d("messageActivity.java", "Xong cai cal");
//    }

    private void setupVoiceCallBtn() {
        String currentUserId = auth.getCurrentUser().getUid();
        String targetUserId = null;

        if (conversation.getMemberIdList().size() > 2) {
            return;
        }

        for (String userId: conversation.getMemberIdList()) {
            if (!userId.equals(currentUserId)) {
                targetUserId = userId;
                break;
            }
        }

        binding.btnVoiceCall.setIsVideoCall(false);
        binding.btnVoiceCall.setResourceID("zego_uikit_call");
        binding.btnVoiceCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserId)));
    }

    private void setupVideoCallBtn() {
        String currentUserId = auth.getCurrentUser().getUid();
        String targetUserId = null;

        if (conversation.getMemberIdList().size() > 2) {
            return;
        }

        for (String userId: conversation.getMemberIdList()) {
            if (!userId.equals(currentUserId)) {
                targetUserId = userId;
                break;
            }
        }

        binding.btnVoiceCall.setIsVideoCall(true);
        binding.btnVoiceCall.setResourceID("zego_uikit_call");
        binding.btnVoiceCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserId)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }

}