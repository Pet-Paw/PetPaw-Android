package com.petpaw.fragments.screens;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.petpaw.R;
import com.petpaw.adapters.MessageListAdapter;
import com.petpaw.databinding.FragmentMessagesBinding;
import com.petpaw.models.Message;
import com.petpaw.models.User;

public class MessagesFragment extends Fragment {

    public static final String ARG_USER = "user";
    private User mUser;

    private FragmentMessagesBinding mBinding;

    private FirebaseFirestore mDb;

    private MessageListAdapter mMessageListAdapter;

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
            mUser = (User) getArguments().getSerializable(ARG_USER);
        }

        mDb = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMessagesBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getMessagesFromDb();
    }

    private void getMessagesFromDb() {
        mDb.collection(Message.MESSAGES)
                .whereEqualTo("receiverId", mUser.getUid())

    }
}