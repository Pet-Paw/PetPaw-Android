package com.petpaw.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.firebase.firestore.ListenerRegistration;
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
import com.petpaw.clients.NotiSender;
import com.petpaw.database.UserCollection;
import com.petpaw.databinding.ActivityMessageBinding;
import com.petpaw.fragments.screens.MapsFragment;
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

import java.io.IOException;
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

    private FusedLocationProviderClient client;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Boolean isSharing = false;
    FragmentManager fragmentManager;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Can't share location without permission", Toast.LENGTH_LONG).show();
                }
            });

    private Map<String, User> userMap;


//    @Override
//    protected void onStart() {
//        super.onStart();
//        auth = FirebaseAuth.getInstance();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        fragmentManager = getSupportFragmentManager();
        client = LocationServices.getFusedLocationProviderClient(MessageActivity.this);
        getConversation();
        setupMessageRV();
        binding.rlMap.setVisibility(View.GONE);



        binding.btnVoiceCall.setIsVideoCall(true);

        setupMessageRV();
        getConversation();

        requestCallPermissions();


        binding.sendMessageBtn.setOnClickListener(v -> onBtnSendClick());
        binding.backBtn.setOnClickListener(v -> {
            stopLocationUpdates();
            fragmentManager = null;
            finish();
        });
        binding.mapBtn.setOnClickListener(v -> {
            if(binding.rlMap.getVisibility() == View.GONE){
                binding.mapBtn.setImageTintList(getColorStateList(R.color.white));
                binding.mapBtn.setBackgroundColor(getColor(R.color.primary));
                binding.rlMap.setVisibility(View.VISIBLE);
            } else {
                binding.mapBtn.setImageTintList(getColorStateList(R.color.primary));
                binding.mapBtn.setBackgroundColor(0x00000000);
                binding.rlMap.setVisibility(View.GONE);
            }
        });
        binding.shareLocationBtn.setOnClickListener(v -> {
            if (askLocationPermission()){
                if (isSharing) {
                    isSharing = false;
                    binding.shareLocationBtn.setText("Share Location");
                    stopLocationUpdates();
                } else {
                    isSharing = true;
                    binding.shareLocationBtn.setText("Stop Sharing");
                    startLocationUpdates();
                }
            }
        });

    }

    public void setupLocationFeature(){
        if(conversation.getMemberIdList().size() > 2){
            binding.shareLocationBtn.setVisibility(View.GONE);
        } else {
            setupMapFragment();
            setupLocationSharing();
            setupLocationSharingListener();
        }
    }

    protected void setupLocationSharing() {
        locationRequest = new LocationRequest.Builder(1000)
                .build();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d("Current Location", location.toString());
                    if(isSharing){
                        GeoPoint curLoc = new GeoPoint(location.getLatitude(), location.getLongitude());
                        Map<String, Object> doc = new HashMap<>();
                        doc.put("location", curLoc);
                        doc.put("isSharing", true);
                        db.collection("Conversations")
                                .document(conversationID)
                                .collection("locations")
                                .document(auth.getCurrentUser().getUid())
                                .update(doc);
                    }
                }
            }
        };
    }

    private void onBtnSendClick() {
        if (binding.etSendMessage.getText().length() != 0) {
            Message message = new Message();
            message.setContent(binding.etSendMessage.getText().toString());
            message.setSenderId(auth.getCurrentUser().getUid());
            message.setSentAt(new Date());

            String currentUserId = auth.getCurrentUser().getUid();
            UserCollection userCollection = UserCollection.newInstance();
            userCollection.getUser(currentUserId, new UserCollection.Callback() {
                @Override
                public void onCallback(List<User> users) {

                }

                @Override
                public void onCallBack(User currentUser) {
                    String title = currentUser.getName();
                    NotiSender notiSender = new NotiSender(currentUserId);
                    List<String> memberIdLists =  conversation.getMemberIdList();
                    for (String memberId: memberIdLists) {
                        if (!memberId.equals(currentUserId)) {
                            notiSender.sendNotificationToDifferentAccount(memberId, title, message.getContent());
                        }
                    }

                    try {
                        notiSender.sendNotificationOnCurrentAccount("You message " + message.getContent());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            });





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

        if (!intent.hasExtra("conversationID")) {
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
                        setupLocationFeature();
                    }
                });
    }

    private void getMessages() {
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

    private void getUsers() {
        db.collection(User.USERS)
                .whereIn(FieldPath.documentId(), conversation.getMemberIdList())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        userMap = new HashMap<>();

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            User user = doc.toObject(User.class);
                            userMap.put(user.getUid(), user);
                        }

                        Log.d("TAG", "User Map: " + userMap);
                        if (conversation.getMemberIdList().size() <= 2) {
                            for (String userId : conversation.getMemberIdList()) {
                                if(auth.getCurrentUser() != null){
                                    if (!Objects.equals(userId, auth.getCurrentUser().getUid())) {
                                        User user = userMap.get(userId);
                                        binding.tvName.setText(user.getName());
//                                    binding.tvShareLocation.setText(user.getName() + "is sharing their location");
                                        if (user.getImageURL() == null) {
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
                            }
                        } else {
                            binding.btnVoiceCall.setVisibility(View.GONE);
                            binding.mapBtn.setVisibility(View.GONE);
                            binding.ivUserPic.setImageResource(R.drawable.group_chat_image);
                            StringBuilder names = new StringBuilder();
                            int cnt = 0;
                            for (String userId: conversation.getMemberIdList()) {
                                if(auth.getCurrentUser() != null){
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


    private void setupMessageRV() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        binding.rvMessageList.setLayoutManager(linearLayoutManager);
        messageListAdapter = new MessageListAdapter(getBaseContext());
        binding.rvMessageList.setAdapter(messageListAdapter);
    }


    private boolean askLocationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if ((ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED)) {
                return true;
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
                return false;
            }
        }
        return true;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askLocationPermission();
            return;
        }
        client.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates(){
        GeoPoint loc = new GeoPoint(0, 0);
        Map<String, Object> doc = new HashMap<>();
        doc.put("location", loc);
        doc.put("isSharing", false);
        db.collection("Conversations")
                .document(conversationID)
                .collection("locations")
                .document(auth.getCurrentUser().getUid())
                .update(doc);
        if(isSharing){
            client.removeLocationUpdates(locationCallback);
        }
    }

    private void setupMapFragment(){
        if(fragmentManager != null) {
            Bundle bundle = new Bundle();
            bundle.putString("conversationID", conversationID);
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(binding.mapFragmentCtn.getId(), MapsFragment.class, bundle)
                    .commit();
        }
    }

    private void setupLocationSharingListener(){
        db.collection("Conversations")
                .document(conversationID)
                .collection("locations")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot query, @Nullable FirebaseFirestoreException error) {
                        for (DocumentSnapshot doc: query){
                            if(auth.getCurrentUser() != null){
                                if(doc.getId() != auth.getCurrentUser().getUid()){
                                    if((boolean) doc.get("isSharing")) {
                                        if(binding.rlMap.getVisibility() == View.GONE){
                                            Toast.makeText(getBaseContext(), "Location sharing is active. Press Map Icon to view", Toast.LENGTH_SHORT).show();
                                        }
//                                  binding.tvShareLocation.setVisibility(View.VISIBLE);
                                    }

                                }
                            }
                        }
                    }
                });
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
        if(auth.getCurrentUser() == null){
            return;
        }
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

    private void setupVideoCallBtn() {
        if(auth.getCurrentUser() == null){
            return;
        }
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