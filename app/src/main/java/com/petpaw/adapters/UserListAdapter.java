package com.petpaw.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.auth.User;
import com.petpaw.R;
import com.petpaw.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

    Context context;
    List<User> usersList;


    public UserListAdapter(Context context, List<User> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public UserListAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_user_card_view,
                        parent,
                        false
                ));
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.UserViewHolder holder, int position) {
        Log.d("TAG", "** user number: " + position);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        String userId = usersList.get(position).getUid();
        Log.d("TAG", "- user id: " + userId);

        if (!(userId.equals(currentUserId))) {
            holder.userCardViewUsername.setText(usersList.get(position).getName());
            Log.d("TAG", "-- user name: " + usersList.get(position).getName());
            if (usersList.get(position).getImageURL() == null) {
                holder.userCardViewProfilePic.setImageResource(R.drawable.default_avatar);
            } else {
                Picasso.get()
                        .load(usersList.get(position).getImageURL())
                        .placeholder(R.drawable.default_avatar) // A default placeholder if needed
                        .into(holder.userCardViewProfilePic);
            }
        }
    }

        @Override
        public int getItemCount() {
            return usersList.size();
        }


        public class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView userCardViewProfilePic;
        TextView userCardViewUsername;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userCardViewProfilePic = itemView.findViewById(R.id.userCardViewProfilePic);
            userCardViewUsername = itemView.findViewById(R.id.userCardViewUsername);
        }
    }
}

//    class UserViewHolder extends RecyclerView.ViewHolder {
//
//        ImageView userCardViewProfilePic;
//        TextView userCardViewUsername;
//
//        public UserViewHolder(@NonNull View itemView) {
//            super(itemView);
//            userCardViewProfilePic = itemView.findViewById(R.id.userCardViewProfilePic);
//            userCardViewUsername = itemView.findViewById(R.id.userCardViewUsername);
//        }
//    }


