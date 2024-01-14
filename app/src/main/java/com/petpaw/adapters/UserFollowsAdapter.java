package com.petpaw.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.petpaw.fragments.screens.UserFollowersFragment;
import com.petpaw.fragments.screens.UserFollowingFragment;

public class UserFollowsAdapter extends FragmentStateAdapter {
    String userId;
    public UserFollowsAdapter(@NonNull  FragmentActivity fragmentActivity, String userId) {
        super(fragmentActivity);
        this.userId = userId;
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: // followers
                return UserFollowersFragment.newInstance(userId);
            case 1:
                return UserFollowingFragment.newInstance(userId);

        }
        return  UserFollowersFragment.newInstance(userId);


    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
