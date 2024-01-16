package com.petpaw.fragments.screens;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.petpaw.activities.MessageActivity;
import com.petpaw.adapters.ConversationListAdapter;
import com.petpaw.databinding.FragmentMessagesBinding;
import com.petpaw.interfaces.OnConversationClickListener;
import com.petpaw.models.Conversation;
import com.petpaw.models.Message;
import com.petpaw.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessagesFragment extends Fragment {

    private FragmentMessagesBinding mBinding;

    private FirebaseAuth mAuth;

    private FirebaseFirestore mDb;


    private int mNumConversations;
    private List<Conversation> mConversationList;

    private List<String> mUserIdList;

    private ConversationListAdapter mConversationListAdapter;

    public MessagesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
//    public static MessagesFragment newInstance(String param1, String param2) {
//        MessagesFragment fragment = new MessagesFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMessagesBinding.inflate(inflater, container, false);
        mBinding.newChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createConversation(new ArrayList<>());
            }
        });
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupRvConversationList();
        getConversationsFromDb();


//        setupFirebaseUser();
    }


    private void setupFirebaseUser() {
//        mAuth.signInWithEmailAndPassword("u01@qq.com", "Binh1234")
//                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (!task.isSuccessful()) {
//                            return;
//                        }
//
//                        mFirebaseUser = mAuth.getCurrentUser();
//                        assert mFirebaseUser != null;
//
//                        setupRvConversationList();
//                        getConversationsFromDb();
//                    }
//                });
    }

    private void getConversationsFromDb() {
        mDb.collection(Conversation.CONVERSATIONS)
                .whereArrayContains("memberIdList", mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mNumConversations = queryDocumentSnapshots.size();
                        Log.d("messages.java", "mNumConversations = " + mNumConversations);
                        mConversationList = new ArrayList<>();

                        mUserIdList = new ArrayList<>();

                        for (DocumentSnapshot doc: queryDocumentSnapshots) {
                            Conversation conversation = doc.toObject(Conversation.class);
                            assert conversation != null;
                            conversation.setUid(doc.getId());

                            for (String userId: conversation.getMemberIdList()) {
                                if (!Objects.equals(userId, mAuth.getCurrentUser().getUid())) {
                                    mUserIdList.add(userId);
                                }
                            }

                            getLastMessageFromDb(conversation);
                        }

                        getUsersFromDb();
                    }
                });
    }

    private void getLastMessageFromDb(Conversation conversation) {
        mDb.collection(Conversation.CONVERSATIONS)
                .document(conversation.getUid())
                .collection(Message.MESSAGES)
                .orderBy("sentAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("messages.java", "get last msg ");
                        for (DocumentSnapshot doc: queryDocumentSnapshots) {
                            Message message = doc.toObject(Message.class);
                            assert message != null;

                            message.setUid(doc.getId());
                            conversation.setLastMessage(message);

                            mConversationList.add(conversation);
                        }

                        Log.d("messages.java", "Conversation list sz: " + mConversationList.size());

                        if (mConversationList.size() == mNumConversations) {
                            Log.d("messages.java", "setup conversation list");
                            mConversationListAdapter.setConversationList(mConversationList);
                        }
                    }
                });
    }

    private void getUsersFromDb() {
        if(mUserIdList.isEmpty()){
            return;
        }
        mDb.collection(User.USERS)
                .whereIn(FieldPath.documentId(), mUserIdList)
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
                        mConversationListAdapter.setUserMap(userMap);

                    }
                });
    }

    private void setupRvConversationList() {
        mBinding.rvConversationList.setLayoutManager(
                new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        );
        mConversationListAdapter = new ConversationListAdapter();
        mBinding.rvConversationList.setAdapter(mConversationListAdapter);

        mConversationListAdapter.setOnClickListener(new OnConversationClickListener() {
            @Override
            public void onClick(Conversation conversation) {
                Intent intent = new Intent(requireContext(), MessageActivity.class);
                intent.putExtra("conversationID", conversation.getUid());
                startActivity(intent);
            }
        });
    }

    //create new conversation from userIdList
    private void createConversation(List<String> userIdList){
        userIdList.add(mAuth.getCurrentUser().getUid());
        // TODO: Check if 1v1 conversation is already created
        Conversation conversation = new Conversation();
        conversation.setMemberIdList(userIdList);
        mDb.collection("Conversations")
                .add(conversation.toDoc())
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(getActivity(), MessageActivity.class);
                            intent.putExtra("conversationID", task.getResult().getId());
                            startActivity(intent);
                        }

                    }
                });
    }

}