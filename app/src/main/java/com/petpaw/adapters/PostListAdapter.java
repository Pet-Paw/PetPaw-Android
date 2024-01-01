package com.petpaw.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petpaw.databinding.FragmentPostCardViewBinding;

import com.petpaw.R;

import com.google.firebase.firestore.auth.User;
import com.petpaw.interfaces.OnPostClickListener;
import com.petpaw.models.Post;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
                        R.layout.fragment_post_card_view,
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
            FirebaseFirestore db = FirebaseFirestore.getInstance();
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