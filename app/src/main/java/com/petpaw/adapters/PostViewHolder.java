package com.petpaw.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.petpaw.R;

public class PostViewHolder extends RecyclerView.ViewHolder {

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