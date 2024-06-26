package com.petpaw.fragments.screens;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.petpaw.R;
import com.petpaw.activities.MainActivity;
import com.petpaw.activities.MessageActivity;
import com.petpaw.adapters.UserFollowingAdapter;
import com.petpaw.database.FollowCollection;
import com.petpaw.database.UserCollection;
import com.petpaw.databinding.FragmentUserFollowersBinding;
import com.petpaw.databinding.FragmentUserFollowingBinding;
import com.petpaw.interfaces.OnBtnMessageClickListener;
import com.petpaw.models.Conversation;
import com.petpaw.models.FollowRecord;
import com.petpaw.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFollowersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFollowersFragment extends Fragment {

    private static final String TAG = "UserFollowersFragment";
    private FragmentUserFollowersBinding userFollowersBinding;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    private List<FollowRecord> followRecords = new ArrayList<>();
    private User currentUser;

    private User followingUser;

    private FollowCollection followCollection = FollowCollection.newInstance();

    private RecyclerView recyclerView;
    private UserFollowingAdapter userFollowingAdapter;
    private UserCollection userCollection = UserCollection.newInstance();

    private List<User> followerUsers = new ArrayList<>();

    private FirebaseFirestore mDb;



    public UserFollowersFragment() {

    }


    // TODO: Rename and change types and number of parameters
    public static UserFollowersFragment newInstance(String userId) {
        UserFollowersFragment fragment = new UserFollowersFragment();
        Bundle args = new Bundle();
        args.putString(ProfileFragment.USER_ID, userId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        userFollowersBinding = FragmentUserFollowersBinding.inflate(inflater, container, false);


        recyclerView = userFollowersBinding.followingRecyclerView;

        TextView msgNoFollowing = userFollowersBinding.msgNoFollowing;
        msgNoFollowing.setVisibility(TextView.INVISIBLE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        assert getArguments() != null;
        String userId = getArguments().getString(ProfileFragment.USER_ID);
        if (userId != null) {
//            getCurrentUserAndFollowingUser(firebaseUser.getUid(), "JhmTNRM4bjbykb92yOxAGoaIMH92");
            getUser(userId, new UserCollection.Callback() {
                @Override
                public void onCallback(List<User> users) {

                }

                @Override
                public void onCallBack(User user) {
                    currentUser = user;
                    followCollection.getAllFollowers(currentUser, new FollowCollection.Callback() {
                        @Override
                        public void onCallback(List<FollowRecord> res) {
                            followRecords = res;
                        }

                        @Override
                        public void onCallBackGetUsers(List<User> users) {
                            followerUsers = users;
                            userFollowingAdapter = new UserFollowingAdapter(false, userId);
                            userFollowingAdapter.setUsers(followerUsers);
                            recyclerView.setAdapter(userFollowingAdapter);

                            if (followerUsers.size() == 0) {
                                msgNoFollowing.setVisibility(TextView.VISIBLE);
                            }

                            userFollowingAdapter.setOnBtnMessageClick(new OnBtnMessageClickListener() {
                                @Override
                                public void onClick(User user) {
                                    String currentUserId = auth.getCurrentUser().getUid();
                                    String userId = user.getUid();

                                    mDb.collection(Conversation.CONVERSATIONS)
                                            .whereArrayContains("memberIdList", currentUserId)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    String conversationId = null;

                                                    for (DocumentSnapshot doc: queryDocumentSnapshots) {
                                                        Conversation conversation = doc.toObject(Conversation.class);
                                                        assert conversation != null;
                                                        List<String> memberIdList = conversation.getMemberIdList();

                                                        if (memberIdList.size() == 2
                                                                && memberIdList.contains(currentUserId)
                                                                && memberIdList.contains(userId)) {
                                                            conversationId = doc.getId();
                                                            break;
                                                        }
                                                    }

                                                    if (conversationId != null) {
                                                        moveToMessageActivity(conversationId);
                                                        return;
                                                    }

                                                    List<String> memberIdList = new ArrayList<>();
                                                    memberIdList.add(userId);
                                                    memberIdList.add(currentUserId);
                                                    createConversation(memberIdList);
                                                }
                                            });
                                }
                            });
                        }
                    });
                }
            });
        }

        userFollowersBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment profileFragment = ProfileFragment.newInstance(userId, 0, "");
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.profileFragmentLayout, profileFragment)
                        .commit();
            }
        });

        SearchView searchBar = userFollowersBinding.searchBar;
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });


        // Inflate the layout for this fragment
        return userFollowersBinding.getRoot();
    }

    private void getUser(String uid, UserCollection.Callback callback) {
        userCollection.getUser(uid, callback);
    }

    private void getFollowingUser(String uid, UserCollection.Callback callback) {
        userCollection.getUser(uid, callback);
    }

    private void filter(String text) {
        if (!text.isEmpty()) {
            List<User> filteredList = new ArrayList<>();

            for (User user : followerUsers) {
                if (user.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(user);
                }
            }
            userFollowingAdapter.filter(filteredList);
        } else {
            userFollowingAdapter.filter(followerUsers);
        }
    }

    private void moveToMessageActivity(String conversationId) {
        Intent intent = new Intent(requireActivity(), MessageActivity.class);
        intent.putExtra("conversationID", conversationId);
        startActivity(intent);
    }

    private void createConversation(List<String> userIdList){
        // Create new conversation
        Conversation conversation = new Conversation();
        conversation.setMemberIdList(userIdList);
        mDb.collection("Conversations")
                .add(conversation.toDoc())
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        DocumentReference docRef = task.getResult();
                        for (String user: userIdList){
                            Map<String, Object> map = new HashMap<>();
                            map.put("isSharing", false);
                            map.put("location", new GeoPoint(0,0));
                            docRef.collection("locations")
                                    .document(user)
                                    .set(map);
                        }
                        if(task.isSuccessful()){
                            moveToMessageActivity(task.getResult().getId());
                        }

                    }
                });
    }
}