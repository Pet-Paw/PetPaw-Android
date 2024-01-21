package com.petpaw.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import com.petpaw.R;


import com.petpaw.activities.PostCommentActivity;

import com.petpaw.activities.CreatePostActivity;

import com.petpaw.clients.NotiSender;
import com.petpaw.database.NotificationCollection;
import com.petpaw.database.UserCollection;
import com.petpaw.fragments.screens.SideNavFragment;
import com.petpaw.models.NotificationPetPaw;
import com.petpaw.models.Post;
import com.petpaw.models.User;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {

    Context context;
    List<Post> postList;
    private NotificationCollection notificationCollection = NotificationCollection.newInstance();

    private UserCollection userCollection = UserCollection.newInstance();

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

//        ----------------- report post ------------------------
        if(currentUserId.equals(postList.get(position).getAuthorId())){
            holder.postCardViewReportImageView.setVisibility(View.GONE);
        }else{
        holder.postCardViewReportImageView.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Report this post?");
            builder.setMessage("Report notification will be sent to PetPaw admin.");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    DocumentReference postRef = db.collection("Admin").document("nolD8tefH4w9mwE9efzM");
                    postRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                //add to the "notifications" list a text "post reported"
                                if(documentSnapshot.get("notifications") != null){
                                    List<String> notifications = (List<String>) documentSnapshot.get("notifications");
                                    notifications.add("POST REPORT: User " + currentUserId + " has reported post " + postId);
                                    documentSnapshot.getReference().update("notifications", notifications);
                                }
                            }
                        } else {
                            Log.e("PostListAdapter", "Error fetching admin data", task.getException());
                        }
                    });
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

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

                // Send notification to the post's author
                String authorId = postList.get(position).getAuthorId();
                userCollection.getUser(authorId, new UserCollection.Callback() {
                    @Override
                    public void onCallback(List<User> users) {

                    }
                    @Override
                    public void onCallBack(User author) {
                        userCollection.getUser(currentUserId, new UserCollection.Callback() {
                            @Override
                            public void onCallback(List<User> users) {

                            }

                            @Override
                            public void onCallBack(User currentUser) {
                                String title = currentUser.getName();
                                NotiSender notiSender = new NotiSender(currentUserId);
                                if (!Objects.equals(currentUser.getUid(), authorId)) {
                                    notiSender.sendNotificationToDifferentAccount(authorId, title, "liked your post");
                                }
                                try {
                                    notiSender.sendNotificationOnCurrentAccount("You liked " + author.getName() + "'s post");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    }
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

        ImageView postCardViewProfilePic, postCardImageView, postCardViewLikeImageView, postCardViewCommentImageView, postCardViewEditImageView, postCardViewReportImageView;
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
            postCardViewReportImageView = itemView.findViewById(R.id.postCardViewReportImageView);

//            --------LinearLayout----------
            postCardViewPetNameLinearLayout = itemView.findViewById(R.id.postCardViewPetNameLinearLayout);

            itemView.setOnClickListener(v -> {
                Log.d("PostListAdapter", "onClick: " + getAdapterPosition());
            });


        }
    }

    private void updateNotificationBadge(String currentUserId, View view) {
        NavigationView mNavigationView = SideNavFragment.mNavigationView;
        TextView notificationCount = mNavigationView.getMenu().findItem(R.id.notificationsFragment).getActionView().findViewById(R.id.notificationsFragment);
        notificationCollection.getTotalNewNotification(currentUserId, new NotificationCollection.Callback() {
            @Override
            public void onCallback(List<NotificationPetPaw> notifications) {
                if (notifications.size() > 0) {
                    notificationCount.setText(String.valueOf(notifications.size()));
                } else {
                    notificationCount.setText("0");
                }
            }
        });
    }

}
