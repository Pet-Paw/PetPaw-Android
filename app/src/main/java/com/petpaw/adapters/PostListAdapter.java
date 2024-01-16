package com.petpaw.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import com.petpaw.R;


import com.petpaw.activities.PostCommentActivity;

import com.petpaw.activities.CreatePostActivity;

import com.petpaw.models.Post;
import com.petpaw.models.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {

    Context context;
    List<Post> postList;

    public PostListAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_post_card_view,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("TAG", "** post number: " + position);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        String postId = postList.get(position).getPostId();
        Log.d("TAG", "- postId: " + postId);
        List<String> likes = postList.get(position).getLikes();

//        -------------- String CommnunityId -------------
        String communityId = postList.get(position).getCommunityId();
        Log.d("TAG", "- communityId: " + communityId);

//        ------------ isModified -------------------
        if (postList.get(position).isModified()) {
            holder.postCardViewIsModified.setVisibility(View.VISIBLE);
        } else {
            holder.postCardViewIsModified.setVisibility(View.GONE);
        }

//        ----------- Show edit icon ----------------
        if (currentUserId.equals(postList.get(position).getAuthorId())) {
            holder.postCardViewEditImageView.setVisibility(View.VISIBLE);
        } else {
            holder.postCardViewEditImageView.setVisibility(View.GONE);
        }

        holder.postCardViewEditImageView.setOnClickListener(view -> {
            Intent intent = new Intent(context, CreatePostActivity.class);
            intent.putExtra("postId", postId);
            intent.putExtra("communityId", communityId);
            context.startActivity(intent);
        });

//        -------------- Format Date -------------
        Date date = postList.get(position).getDateModified();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
        holder.postCardViewDate.setText(sdf.format(date));

//        -------------- Show tags -------------
        if (postList.get(position).getTags().size() == 0) {
            holder.postCardViewTagsTextView.setVisibility(View.GONE);
        } else {
            holder.postCardViewTagsTextView.setVisibility(View.VISIBLE);
            StringBuilder tagList = new StringBuilder();
            for (String tag : postList.get(position).getTags()) {
                tagList.append("#").append(tag).append(" ");
            }
            holder.postCardViewTagsTextView.setText(tagList.toString());
        }

//      ---------------  Update the like button start -----------
        if (likes.contains(currentUserId)) {
            holder.postCardViewLikeImageView.setColorFilter(ContextCompat.getColor(context, R.color.primary), PorterDuff.Mode.SRC_IN);
        } else {
            holder.postCardViewLikeImageView.clearColorFilter();
        }

//      ---------------  Update the like button end -----------
        holder.postCardViewContentTextView.setText(postList.get(position).getContent());
        holder.postCardViewLikeCountTextView.setText(String.valueOf(postList.get(position).getLikes().size()));
        holder.postCardViewCommentCountTextView.setText(String.valueOf(postList.get(position).getComments().size()));
        holder.postCardViewCommentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostCommentActivity.class);
                intent.putExtra("postID", postId);
                context.startActivity(intent);
            }
        });

//        -------------- Show pet name -------------
        StringBuilder petNameList = new StringBuilder();
        if(postList.get(position).getPetIdList().size() == 0){
            holder.postCardViewPetNameLinearLayout.setVisibility(View.GONE);
        }else{
            for (String petId : postList.get(position).getPetIdList()) {
                db.collection("Pets").document(petId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String petName = documentSnapshot.getString("name");
                            if(petNameList.length() == 0) {
                                petNameList.append(petName + " ");
                            }else{
                                petNameList.append(", " + petName + " ");
                            }
                            holder.postCardViewPetNameTextView.setText(petNameList.toString());
                        }
                    } else {
                        Log.e("PostListAdapter", "Error fetching pet data", task.getException());
                    }
                });
            }
        }

//        ---------------  Load username and avatar start --------------
        Post post = postList.get(position);
        // Fetch the user's name and avatar URL from Firestore
        db.collection("users").document(post.getAuthorId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        // Set user's name
                        holder.postCardViewUserNameTextView.setText(user.getName());

                        // Set user's avatar
                        if (user.getImageURL() != null && !user.getImageURL().isEmpty()) {
                            Picasso.get()
                                    .load(user.getImageURL())
                                    .placeholder(R.drawable.default_avatar) // A default placeholder if needed
                                    .into(holder.postCardViewProfilePic);
                        } else {
                            holder.postCardViewProfilePic.setImageResource(R.drawable.default_avatar);
                        }
                    }
                }
            } else {
                Log.e("PostListAdapter", "Error fetching user data", task.getException());
            }
        });

//        ---------------  Load image start -----------
        String imageUrl = postList.get(position).getImageURL();
        if(imageUrl == null || imageUrl.isEmpty()) {
            holder.postCardImageView.setVisibility(View.GONE);
        } else {
            Picasso.get()
                    .load(imageUrl)
                    .tag(System.currentTimeMillis())
                    .into(holder.postCardImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            //Log.d("TAG", "Load image successfully at " + System.currentTimeMillis());
                        }
                        @Override
                        public void onError(Exception e) {
                            Log.d("TAG", "Load image successfully");
                        }
                    });
        }


//        --------------- add onClick like button -----------
        holder.postCardViewLikeImageView.setOnClickListener(view -> {
            DocumentReference postRef = db.collection("Posts").document(postId);
            if(!(likes.contains(currentUserId))) {
                // User hasn't liked
                likes.add(currentUserId);
                // Update likes field
                postRef.update("likes", likes)
                        .addOnSuccessListener(aVoid -> {
                            holder.postCardViewLikeImageView.setColorFilter(ContextCompat.getColor(context, R.color.primary), PorterDuff.Mode.SRC_IN);
                            holder.postCardViewLikeCountTextView.setText(String.valueOf(postList.get(position).getLikes().size()));
                        });
            } else {
                // User has already liked
                Log.d("TAG", "You unlike this post ");
                likes.remove(currentUserId);
                postRef.update("likes", likes)
                        .addOnSuccessListener(aVoid -> {
                            holder.postCardViewLikeImageView.clearColorFilter();
                            holder.postCardViewLikeCountTextView.setText(String.valueOf(postList.get(position).getLikes().size()));
                        });
            }

        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
    public class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView postCardViewProfilePic, postCardImageView, postCardViewLikeImageView, postCardViewCommentImageView, postCardViewEditImageView;
        TextView postCardViewIsModified, postCardViewUserNameTextView, postCardViewDate, postCardViewContentTextView, postCardViewLikeCountTextView, postCardViewCommentCountTextView, postCardViewTagsTextView, postCardViewPetNameTextView;
        LinearLayout postCardViewPetNameLinearLayout;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
//            --------TextView----------
            postCardViewUserNameTextView = itemView.findViewById(R.id.postCardViewUserNameTextView);
            postCardViewDate = itemView.findViewById(R.id.postCardViewDate);
            postCardViewContentTextView = itemView.findViewById(R.id.postCardViewContentTextView);
            postCardViewLikeCountTextView = itemView.findViewById(R.id.postCardViewLikeCountTextView);
            postCardViewCommentCountTextView = itemView.findViewById(R.id.postCardViewCommentCountTextView);
            postCardViewTagsTextView = itemView.findViewById(R.id.postCardViewTagsTextView);
            postCardViewPetNameTextView = itemView.findViewById(R.id.postCardViewPetNameTextView);
            postCardViewIsModified = itemView.findViewById(R.id.postCardViewIsModified);

//          --------ImageView----------
            postCardImageView = itemView.findViewById(R.id.postCardImageView);
            postCardViewProfilePic = itemView.findViewById(R.id.postCardViewProfilePic);
            postCardViewLikeImageView = itemView.findViewById(R.id.postCardViewLikeImageView);
            postCardViewCommentImageView = itemView.findViewById(R.id.postCardViewCommentImageView);
            postCardViewEditImageView = itemView.findViewById(R.id.postCardViewEditImageView);

//            --------LinearLayout----------
            postCardViewPetNameLinearLayout = itemView.findViewById(R.id.postCardViewPetNameLinearLayout);

            itemView.setOnClickListener(v -> {
                Log.d("PostListAdapter", "onClick: " + getAdapterPosition());
            });
        }
    }
}

//class PostViewHolder extends RecyclerView.ViewHolder {
//
//    ImageView postCardViewProfilePic, postCardImageView;
//    TextView postCardViewUserNameTextView, postCardViewDate, postCardViewLikeCountTextView, postCardViewCommentCountTextView;
//
//    public PostViewHolder(@NonNull View itemView) {
//        super(itemView);
//        postCardViewProfilePic = itemView.findViewById(R.id.postCardViewProfilePic);
//        postCardImageView = itemView.findViewById(R.id.postCardImageView);
//        postCardViewUserNameTextView = itemView.findViewById(R.id.postCardViewUserNameTextView);
//        postCardViewDate = itemView.findViewById(R.id.postCardViewDate);
//        postCardViewLikeCountTextView = itemView.findViewById(R.id.postCardViewLikeCountTextView);
//        postCardViewCommentCountTextView = itemView.findViewById(R.id.postCardViewCommentCountTextView);
//
//    }
//}