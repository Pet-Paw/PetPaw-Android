package com.petpaw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.petpaw.R;
import com.petpaw.fragments.screens.ProfileFragment;
import com.petpaw.models.User;
import com.petpaw.utils.ImageHelper;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserFollowingAdapter extends RecyclerView.Adapter<UserFollowingAdapter.UserFollowingViewHolder>  {
    private List<User> users = new ArrayList<>();
    private String currentUserId;

    private boolean isFollowing;

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

    public UserFollowingAdapter(boolean isFollowing, String currentUserId) {
        this.isFollowing = isFollowing;
        this.currentUserId = currentUserId;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holder.itemView.getContext();
                String userId = users.get(position).getUid();

                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                if (isFollowing) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.profileFragmentLayout, ProfileFragment.newInstance(userId, R.id.userFollowingFragment, currentUserId))
                            .commitNow();
                } else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.profileFragmentLayout, ProfileFragment.newInstance(userId, R.id.useFollowerFragment, currentUserId))
                            .commitNow();
                }


//
//                ((FragmentActivity) context).findViewById(R.id.overlay_fragment_container).setVisibility(View.VISIBLE);
//                ((FragmentActivity) context).findViewById(R.id.userFollowingFragment).setVisibility(View.GONE);

                /*
                BottomNavigationView bottomNav = ((FragmentActivity) context).findViewById(R.id.bottomNav);
                int selectedItemId = bottomNav.getSelectedItemId();

                if (selectedItemId == R.id.searchFragment) {
                    ((FragmentActivity) context).findViewById(R.id.searchLayout).setVisibility(View.GONE);
                } else if (selectedItemId == R.id.profileFragment) {
                    ((FragmentActivity) context).findViewById(R.id.profileLayout).setVisibility(View.GONE);
                }
                 */
            }
        });

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
