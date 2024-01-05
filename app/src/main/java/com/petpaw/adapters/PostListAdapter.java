package com.petpaw.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import com.petpaw.R;

import com.petpaw.models.Post;
import com.petpaw.models.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
        Log.d("TAG", "*** post number: " + position);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        String postId = postList.get(position).getPostId();
        Log.d("TAG", "postId: " + postId);
        List<String> likes = postList.get(position).getLikes();


        holder.postCardViewUserNameTextView.setText(postList.get(position).getAuthorId());

//        ------- Formate Date ------------
        Date date = postList.get(position).getDateModified();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
        holder.postCardViewDate.setText(sdf.format(date));

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

//        ---------------  Load avatar start -----------
        Post post = postList.get(position);
        // Fetch the user's name and avatar URL from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        Picasso.get()
                .load(imageUrl)
                .tag(System.currentTimeMillis())
                .into(holder.postCardImageView, new com.squareup.picasso.Callback() {
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
        holder.postCardViewLikeImageView.setOnClickListener(view -> {
            //FirebaseFirestore db = FirebaseFirestore.getInstance();
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

        ImageView postCardViewProfilePic, postCardImageView, postCardViewLikeImageView, postCardViewCommentImageView;
        TextView postCardViewUserNameTextView, postCardViewDate, postCardViewContentTextView, postCardViewLikeCountTextView, postCardViewCommentCountTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
//            --------TextView----------
            postCardViewUserNameTextView = itemView.findViewById(R.id.postCardViewUserNameTextView);
            postCardViewDate = itemView.findViewById(R.id.postCardViewDate);
            postCardViewContentTextView = itemView.findViewById(R.id.postCardViewContentTextView);
            postCardViewLikeCountTextView = itemView.findViewById(R.id.postCardViewLikeCountTextView);
            postCardViewCommentCountTextView = itemView.findViewById(R.id.postCardViewCommentCountTextView);

//          --------ImageView----------
            postCardImageView = itemView.findViewById(R.id.postCardImageView);
            postCardViewProfilePic = itemView.findViewById(R.id.postCardViewProfilePic);
            postCardViewLikeImageView = itemView.findViewById(R.id.postCardViewLikeImageView);
            postCardViewCommentImageView = itemView.findViewById(R.id.postCardViewCommentImageView);

            itemView.setOnClickListener(v -> {
                Log.d("PostListAdapter", "onClick: " + getAdapterPosition());
            });
        }
    }
}

class PostViewHolder extends RecyclerView.ViewHolder {

    ImageView postCardViewProfilePic, postCardImageView;
    TextView postCardViewUserNameTextView, postCardViewDate, postCardViewLikeCountTextView, postCardViewCommentCountTextView;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        postCardViewProfilePic = itemView.findViewById(R.id.postCardViewProfilePic);
        postCardImageView = itemView.findViewById(R.id.postCardImageView);
        postCardViewUserNameTextView = itemView.findViewById(R.id.postCardViewUserNameTextView);
        postCardViewDate = itemView.findViewById(R.id.postCardViewDate);
        postCardViewLikeCountTextView = itemView.findViewById(R.id.postCardViewLikeCountTextView);
        postCardViewCommentCountTextView = itemView.findViewById(R.id.postCardViewCommentCountTextView);

    }
}