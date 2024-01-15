package com.petpaw.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petpaw.R;
import com.petpaw.fragments.screens.ProfileFragment;
import com.petpaw.models.Community;
import com.petpaw.models.User;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

public class CommunityListAdapter extends RecyclerView.Adapter<CommunityListAdapter.CommunityViewHolder>{
    Context context;
    List<Community> communityList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();


    public CommunityListAdapter(Context context, List<Community> communityList) {
        this.context = context;
        this.communityList = communityList;
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommunityViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_community_card_view,
                        parent,
                        false
                ));
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        Log.d("TAG", "** user number: " + position);
        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        String communityId = communityList.get(position).getId();

        holder.communityCardViewName.setText(communityList.get(position).getName());

        Picasso.get()
                .load(communityList.get(position).getImageURL())
                .placeholder(R.drawable.default_avatar)
                .into(holder.communityCardViewPic);

        if(communityList.get(position).getMembers().contains(currentUserId)){
            holder.joinCommunityBtn.setVisibility(View.GONE);
            holder.joinedTextView.setVisibility(View.VISIBLE);
        }

        holder.joinCommunityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    db.collection("Communities").document(communityId)
                            .update("members", FieldValue.arrayUnion(currentUserId))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        holder.joinCommunityBtn.setVisibility(View.GONE);
                                        holder.joinedTextView.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
//                int currentPosition = holder.getAdapterPosition();
//                if (currentPosition != RecyclerView.NO_POSITION) {
//                    String selectedUserId = usersList.get(currentPosition).getUid();
////                    ProfileFragment profileFragment = ProfileFragment.newInstance(selectedUserId, null);
//
//                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
//                    fragmentManager.beginTransaction()
//                            .replace(R.id.overlay_profile_fragment, ProfileFragment.newInstance(selectedUserId, R.id.searchFragment, "")).commit();
//
//                    ((FragmentActivity) context).findViewById(R.id.overlay_profile_fragment).setVisibility(View.VISIBLE);
//                    ((FragmentActivity) context).findViewById(R.id.searchLayout).setVisibility(View.GONE);
//
//                    /*
//                    BottomNavigationView bottomNav = ((FragmentActivity) context).findViewById(R.id.bottomNav);
//                    int selectedItemId = bottomNav.getSelectedItemId();
//
//                    if (selectedItemId == R.id.searchFragment) {
//                        ((FragmentActivity) context).findViewById(R.id.searchLayout).setVisibility(View.GONE);
//                    } else if (selectedItemId == R.id.profileFragment) {
//                        ((FragmentActivity) context).findViewById(R.id.profileLayout).setVisibility(View.GONE);
//                    }
//                     */
//
//                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }


    public class CommunityViewHolder extends RecyclerView.ViewHolder {
        LinearLayout communityCardViewLinearLayout;
        ImageView communityCardViewPic;
        TextView communityCardViewName;
        Button joinCommunityBtn;
        TextView joinedTextView;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            communityCardViewPic = itemView.findViewById(R.id.communityCardViewPic);
            communityCardViewName = itemView.findViewById(R.id.communityCardViewName);
            communityCardViewLinearLayout = itemView.findViewById(R.id.communityCardViewLinearLayout);
            joinCommunityBtn = itemView.findViewById(R.id.joinCommunityBtn);
            joinedTextView = itemView.findViewById(R.id.joinedTextView);
        }
    }
}
