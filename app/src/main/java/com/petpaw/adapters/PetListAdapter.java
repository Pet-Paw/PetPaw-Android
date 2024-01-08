package com.petpaw.adapters;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petpaw.R;
import com.petpaw.models.Pet;
import com.petpaw.models.Post;
import com.petpaw.models.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PetListAdapter extends RecyclerView.Adapter<PetListAdapter.PetViewHolder> {

    Context context;
    List<Pet> petList;

    public PetListAdapter(Context context, List<Pet> petList) {
        this.context = context;
        this.petList = petList;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PetViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_pet_list,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        String imageUrl = petList.get(position).getImageURL();
        if(imageUrl.length() != 0 && imageUrl != null) {
            Picasso.get()
                    .load(imageUrl)
                    .tag(System.currentTimeMillis())
                    .into(holder.petListProfilePic, new com.squareup.picasso.Callback() {
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
        holder.petListName.setText(petList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }
    public class PetViewHolder extends RecyclerView.ViewHolder {

        ImageView petListProfilePic;
        TextView petListName;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petListProfilePic = itemView.findViewById(R.id.petListProfilePic);
            petListName = itemView.findViewById(R.id.petListName);
        }
    }
}
