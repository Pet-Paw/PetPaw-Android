package com.petpaw.fragments.screens;

import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petpaw.R;
import com.petpaw.databinding.FragmentPostDetailBinding;
import com.petpaw.models.Post;
import com.petpaw.models.Comment;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostDetailFragment extends Fragment {

    private FragmentPostDetailBinding mBinding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String testPostId = "p0sAwLL9I6KjHqwaKNyB";

    private Post post;

    private String mParam1;
    private String mParam2;

    public PostDetailFragment() {
        // Required empty public constructor
    }

    public static PostDetailFragment newInstance(String param1, String param2) {
        PostDetailFragment fragment = new PostDetailFragment();
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
        // Inflate the layout for this fragment
        mBinding = FragmentPostDetailBinding.inflate(inflater, container, false);
        db.collection("Posts").document(testPostId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        post = doc.toObject(Post.class);
                        List<String> likes = post.getLikes();
                        mBinding.postCardView.postCardViewUserNameTextView.setText(post.getAuthorId());

                        //        ------- Formate Date ------------
                        Date date = post.getDateModified();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
                        mBinding.postCardView.postCardViewDate.setText(sdf.format(date));

                        //      ---------------  Update the like button start -----------
                        if (likes.contains(mAuth.getCurrentUser().getUid())) {
                            mBinding.postCardView.postCardViewLikeImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary), PorterDuff.Mode.SRC_IN);
                        } else {
                            mBinding.postCardView.postCardViewLikeImageView.clearColorFilter();
                        }

                        //      ---------------  Update the like button end -----------
                        mBinding.postCardView.postCardViewContentTextView.setText(post.getContent());
                        mBinding.postCardView.postCardViewLikeCountTextView.setText(String.valueOf(post.getLikes().size()));
                        mBinding.postCardView.postCardViewCommentCountTextView.setText(String.valueOf(post.getComments().size()));

                        //        ---------------  Load image start -----------
                        String imageUrl = post.getImageURL();
                        Picasso.get()
                                .load(imageUrl)
                                .tag(System.currentTimeMillis())
                                .into(mBinding.postCardView.postCardImageView, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("TAG", "Load image successfully");
                                    }
                                    @Override
                                    public void onError(Exception e) {
                                        Log.e("TAG", "Load image failed");
                                    }
                                });

                        //        --------------- add onClick like button -----------
                        mBinding.postCardView.postCardViewLikeImageView.setOnClickListener(view -> {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference postRef = db.collection("Posts").document(post.getPostId());
                            if(!(likes.contains(mAuth.getCurrentUser().getUid()))) {
                                // User hasn't liked
                                likes.add(mAuth.getCurrentUser().getUid());
                                // Update likes field
                                postRef.update("likes", likes)
                                        .addOnSuccessListener(aVoid -> {
                                            mBinding.postCardView.postCardViewLikeImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary), PorterDuff.Mode.SRC_IN);
                                            mBinding.postCardView.postCardViewLikeCountTextView.setText(String.valueOf(post.getLikes().size()));
                                        });
                            } else {
                                // User has already liked
                                Log.d("TAG", "You unlike this post ");
                                likes.remove(mAuth.getCurrentUser().getUid());
                                postRef.update("likes", likes)
                                        .addOnSuccessListener(aVoid -> {
                                            mBinding.postCardView.postCardViewLikeImageView.clearColorFilter();
                                            mBinding.postCardView.postCardViewLikeCountTextView.setText(String.valueOf(post.getLikes().size()));
                                        });
                            }

                        });
                    }
                });
        mBinding.createPostCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.createPostCommentEditText.getText().toString().length() != 0){
                    Comment cmt = new Comment(mBinding.createPostCommentEditText.getText().toString(), mAuth.getUid(), null, null);
                    db.collection("Comments")
                            .add(cmt.toDoc())
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()){
                                        DocumentReference doc = task.getResult();
                                        db.collection("Comments").document(doc.getId())
                                                        .update("commentId", doc.getId())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        db.collection("Posts").document(testPostId)
                                                                                .update("comments", FieldValue.arrayUnion(doc.getId()))
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        Log.d("TAG", "Comment posted successfully ");
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                    }
                                }
                            });

                }
            }
        });
        return mBinding.getRoot();
    }
}