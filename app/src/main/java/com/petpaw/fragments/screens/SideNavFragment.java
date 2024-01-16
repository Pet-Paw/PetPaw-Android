package com.petpaw.fragments.screens;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.petpaw.R;
import com.petpaw.activities.SignInActivity;
import com.petpaw.database.UserCollection;
import com.petpaw.databinding.FragmentSideNavBinding;
import com.petpaw.models.User;
import com.petpaw.utils.ImageHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SideNavFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SideNavFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    FragmentSideNavBinding mBinding;

    DrawerLayout mDrawerLayout;

    public NavigationView mNavigationView;
    BottomNavigationView bottomNavigationView;

    FirebaseUser firebaseUser;

    private User currentUser;

    private UserCollection userCollection = UserCollection.newInstance();
    boolean isNotified = false;


    public SideNavFragment() {
        // Required empty public constructor
    }

    public static SideNavFragment newInstance() {
        return new SideNavFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = FragmentSideNavBinding.inflate(getLayoutInflater());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDrawerLayout = mBinding.drawerLayout;
        Toolbar toolbar = mBinding.toolbar;
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        // remove default title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        mNavigationView = mBinding.navView;
        mNavigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( requireActivity(), mDrawerLayout,
                toolbar,  R.string.open_nav, R.string.close_nav) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                mDrawerLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 180));

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // set layout params to match parent with transition
                mDrawerLayout.bringToFront();

            }


            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mDrawerLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


            }
        };


        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView = requireActivity().findViewById(R.id.bottomNav);
        if (savedInstanceState == null) {
            mDrawerLayout.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
            bottomNavigationView.setSelectedItemId(R.id.homeFragment);
            mNavigationView.setCheckedItem(R.id.homeFragment);
        }

        // update current user info in side nav header
        CircleImageView profileImage = mNavigationView.getHeaderView(0).findViewById(R.id.profileAvatar);
        TextView profileName = mNavigationView.getHeaderView(0).findViewById(R.id.profileName);

        if (firebaseUser != null) {
            userCollection.getUser(firebaseUser.getUid(), new UserCollection.Callback() {
                @Override
                public void onCallback(List<User> users) {

                }
                @Override
                public void onCallBack(User user) {
                    currentUser = user;
                    if (currentUser.getImageURL() != null) {
                            ImageHelper.loadImage(currentUser.getImageURL(), profileImage);
                    }
                    profileName.setText(user.getName());
                }
            });
        }





        return mBinding.getRoot();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        FrameLayout defaultContainer = requireActivity().findViewById(R.id.fr);
        defaultContainer.setVisibility(View.VISIBLE);



        if (itemId == R.id.homeFragment) {
            bottomNavigationView.setSelectedItemId(R.id.homeFragment);

        } else if (itemId == R.id.searchFragment) {
            bottomNavigationView.setSelectedItemId(R.id.searchFragment);

        } else if (itemId == R.id.notificationsFragment) {
            // get current bottom nav item id
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.overlay_notification_fragment, NotificationsFragment.newInstance("", ""))
                    .addToBackStack(null)
                    .commit();
            FrameLayout notificationContainer = requireActivity().findViewById(R.id.overlay_notification_fragment);
            defaultContainer.setVisibility(View.GONE);
            notificationContainer.setVisibility(View.VISIBLE);
        } else if (itemId == R.id.messagesFragment) {
            bottomNavigationView.setSelectedItemId(R.id.messagesFragment);
        } else if (itemId == R.id.communityFragment) {
            bottomNavigationView.setSelectedItemId(R.id.communityFragment);
        } else if (itemId == R.id.profileFragment) {
            bottomNavigationView.setSelectedItemId(R.id.profileFragment);
        } else if (itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireActivity(), SignInActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }
        mDrawerLayout.closeDrawers();
        return true;
    }

    private void popBackStack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}