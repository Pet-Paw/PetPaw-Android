package com.petpaw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.petpaw.R;
import com.petpaw.models.User;
import com.petpaw.utils.ImageHelper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserFollowingAdapter extends RecyclerView.Adapter<UserFollowingAdapter.UserFollowingViewHolder>  {
    private List<User> users = new ArrayList<>();

    public class UserFollowingViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView country;
        public ImageView profilePic;

        public UserFollowingViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            country = itemView.findViewById(R.id.country);
            profilePic = itemView.findViewById(R.id.profilePic);
        }
    }





    @NonNull
    @Override
    public UserFollowingAdapter.UserFollowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_following_card, parent, false);
        return new UserFollowingAdapter.UserFollowingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserFollowingAdapter.UserFollowingViewHolder holder, int position) {
        User user = users.get(position);
        holder.username.setText(user.getName());
        holder.country.setText(user.getCountry());
        String imageUrl = user.getImageURL();

        ImageHelper.loadImage(imageUrl, holder.profilePic);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public void filter(List<User> filterList) {
        users = filterList;
        notifyDataSetChanged();
    }

}
