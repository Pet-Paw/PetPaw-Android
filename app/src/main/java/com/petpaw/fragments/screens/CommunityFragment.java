package com.petpaw.fragments.screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.petpaw.R;
import com.petpaw.activities.CreateCommunityActivity;
import com.petpaw.activities.CreatePostActivity;
import com.petpaw.adapters.CommunityListAdapter;
import com.petpaw.adapters.PostListAdapter;
import com.petpaw.databinding.FragmentCommunityBinding;
import com.petpaw.models.Community;
import com.petpaw.models.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityFragment extends Fragment {
    FragmentCommunityBinding binding;
    private List<Community> ownedCommunityList = new ArrayList<>();
    private List<Community> joinedCommunityList = new ArrayList<>();
    private Boolean isOwned = true;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public CommunityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommunityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunityFragment newInstance(String param1, String param2) {
        CommunityFragment fragment = new CommunityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommunityBinding.inflate(inflater, container, false);

        setUnderline(binding.communityFragmentOwnedTextView);

        binding.communityFragmentOwnedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUnderline(binding.communityFragmentOwnedTextView);
                removeUnderline(binding.communityFragmentJoinedTextView);

//                binding.searchFragmentPostRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//                binding.searchFragmentPostRecyclerView.setAdapter(new PostListAdapter(requireContext(), postList));
                binding.communityFragmentOwnedRecyclerView.setVisibility(View.VISIBLE);

                //binding.searchFragmentUserRecyclerView.setAdapter(null);
                binding.communityFragmentJoinedRecyclerView.setVisibility(View.GONE);

            }
        });

        binding.communityFragmentJoinedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeUnderline(binding.communityFragmentOwnedTextView);
                setUnderline(binding.communityFragmentJoinedTextView);

                //binding.searchFragmentPostRecyclerView.setAdapter(null);
                binding.communityFragmentOwnedRecyclerView.setVisibility(View.GONE);
                binding.communityFragmentJoinedRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        return binding.getRoot();
    }

    public void setUnderline(TextView textView) {
        String text = textView.getText().toString();
        if(text.equals("OWNED")){
            isOwned = true;
        } else {
            isOwned = false;
        }
        SpannableString content = new SpannableString(textView.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
    }

    public void removeUnderline(TextView textView) {
        textView.setText(textView.getText().toString());
    }

    @Override
    public void onResume() {
        Log.d("TAG", "onResume: ");
        super.onResume();
        getCommunities();
    }

    private void getCommunities() {
        ownedCommunityList.clear();
        joinedCommunityList.clear();
        db.collection("Communities")
                .whereEqualTo("owner", auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Community community = document.toObject(Community.class);
                                ownedCommunityList.add(community);
                            }

                        if (context != null) {
                            Log.d("TAG", "Owned community List: " + ownedCommunityList.toString());
                            binding.communityFragmentOwnedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                            binding.communityFragmentOwnedRecyclerView.setAdapter(new CommunityListAdapter(requireContext(), ownedCommunityList, false, getFragmentManager()));
                        }
                    }
                }
        });
        db.collection("Communities")
                .whereArrayContains("members", auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Community community = document.toObject(Community.class);
                                joinedCommunityList.add(community);
                            }

                            if (context != null) {
                                Log.d("TAG", "Joined community List: " + joinedCommunityList.toString());
                                binding.communityFragmentJoinedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                binding.communityFragmentJoinedRecyclerView.setAdapter(new CommunityListAdapter(requireContext(), joinedCommunityList, false, getFragmentManager()));
                            }
                        }
                    }
                });
    }

}