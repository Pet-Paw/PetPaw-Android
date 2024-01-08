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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petpaw.R;

import com.petpaw.models.Comment;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CommentViewHolder> {

    Context context;
    List<Comment> commentList;



    public CommentListAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentListAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentListAdapter.CommentViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_comment_list,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CommentListAdapter.CommentViewHolder holder, int position) {
        Log.d("TAG", "*** post number: " + position);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        String commentId = commentList.get(position).getCommentId();
        Log.d("TAG", "commentId: " + commentId);
        List<String> likes = commentList.get(position).getLikes();

        db.collection("Users").document(commentList.get(position).getAuthor())
                        .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot doc = task.getResult();
                                            holder.commentUsernameTextView.setText(doc.get("name").toString());
                                            String imageUrl = doc.get("imageURL").toString();
                                            Picasso.get()
                                                    .load(imageUrl)
                                                    .tag(System.currentTimeMillis())
                                                    .into(holder.commentUserProfilePic, new com.squareup.picasso.Callback() {
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
                                });




//      ---------------  Update the like button start -----------

        if (likes.contains(currentUserId)) {
            holder.commentLikeBtn.setColorFilter(ContextCompat.getColor(context, R.color.primary), PorterDuff.Mode.SRC_IN);
        } else {
            holder.commentLikeBtn.clearColorFilter();
        }



//      ---------------  Update the like button end -----------
        holder.commentContentTextView.setText(commentList.get(position).getContent());
        holder.commentLikeCountTextView.setText(String.valueOf(commentList.get(position).getLikes().size()));

//        ---------------  Load image start -----------


//        --------------- add onClick like button -----------
        holder.commentLikeBtn.setOnClickListener(view -> {
            DocumentReference commentRef = db.collection("Comments").document(commentId);
            if(!(likes.contains(currentUserId))) {
                // User hasn't liked
                likes.add(currentUserId);
                // Update likes field
                commentRef.update("likes", likes)
                        .addOnSuccessListener(aVoid -> {
                            holder.commentLikeBtn.setColorFilter(ContextCompat.getColor(context, R.color.primary), PorterDuff.Mode.SRC_IN);
                            holder.commentLikeCountTextView.setText(String.valueOf(commentList.get(position).getLikes().size()));
                        });
            } else {
                // User has already liked
                Log.d("TAG", "You unlike this post ");
                likes.remove(currentUserId);
                commentRef.update("likes", likes)
                        .addOnSuccessListener(aVoid -> {
                            holder.commentLikeBtn.clearColorFilter();
                            holder.commentLikeCountTextView.setText(String.valueOf(commentList.get(position).getLikes().size()));
                        });
            }

        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
    public class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView commentUserProfilePic, commentLikeBtn;
        TextView commentContentTextView, commentLikeCountTextView, commentUsernameTextView;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentUserProfilePic = itemView.findViewById(R.id.commentUserProfilePicImageView);
            commentLikeBtn = itemView.findViewById(R.id.commentLikeBtn);
            commentContentTextView = itemView.findViewById(R.id.commentContentTextView);
            commentLikeCountTextView = itemView.findViewById(R.id.commentLikeCountTextView);
            commentUsernameTextView = itemView.findViewById(R.id.commentUsernameTextView);
        }
    }
}

class CommentViewHolder extends RecyclerView.ViewHolder {

    ImageView commentUserProfilePic, commentLikeBtn;
    TextView commentContentTextView, commentLikeCountTextView, commentUsernameTextView;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        commentUserProfilePic = itemView.findViewById(R.id.commentUserProfilePicImageView);
        commentLikeBtn = itemView.findViewById(R.id.commentLikeBtn);
        commentContentTextView = itemView.findViewById(R.id.commentContentTextView);
        commentLikeCountTextView = itemView.findViewById(R.id.commentLikeCountTextView);
        commentUsernameTextView = itemView.findViewById(R.id.commentUsernameTextView);

    }
}