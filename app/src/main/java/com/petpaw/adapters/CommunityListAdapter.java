package com.petpaw.adapters;

import static androidx.core.os.BundleKt.bundleOf;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petpaw.R;
import com.petpaw.fragments.screens.CommunityDetailFragment;
import com.petpaw.models.Community;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class CommunityListAdapter extends RecyclerView.Adapter<CommunityListAdapter.CommunityViewHolder>{
    Context context;
    List<Community> communityList;

    boolean isSearch;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private FragmentManager fragmentManager;



    public CommunityListAdapter(Context context, List<Community> communityList, Boolean isSearch, FragmentManager fragmentManager) {
        this.context = context;
        this.communityList = communityList;
        this.isSearch = isSearch;
        this.fragmentManager = fragmentManager;
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
        if(isSearch){
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

                }
            });
        } else {
            holder.joinCommunityBtn.setVisibility(View.GONE);
            holder.joinedTextView.setVisibility(View.GONE);
        }
        holder.communityCardViewRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, communityId, Toast.LENGTH_SHORT).show();
                //chuyen do community detail
            }
        });


    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }


    public class CommunityViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout communityCardViewRelativeLayout;
        ImageView communityCardViewPic;
        TextView communityCardViewName;
        Button joinCommunityBtn;
        TextView joinedTextView;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            communityCardViewPic = itemView.findViewById(R.id.communityCardViewPic);
            communityCardViewName = itemView.findViewById(R.id.communityCardViewName);
            communityCardViewRelativeLayout = itemView.findViewById(R.id.communityCardViewRelativeLayout);
            joinCommunityBtn = itemView.findViewById(R.id.joinCommunityBtn);
            joinedTextView = itemView.findViewById(R.id.joinedTextView);
        }
    }
}
