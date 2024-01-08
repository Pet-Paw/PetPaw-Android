package com.petpaw.adapters;

import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.petpaw.fragments.screens.ProfileFragment;
import com.petpaw.fragments.screens.UserFollowersFragment;
import com.petpaw.fragments.screens.UserFollowingFragment;

import java.util.ArrayList;
import java.util.List;

public class UserFollowsAdapter extends FragmentStateAdapter {
    public UserFollowsAdapter(@NonNull  FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: // followers
                return  new UserFollowersFragment();
            case 1:
                return new UserFollowingFragment();

        }
        return  new UserFollowingFragment();


    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
