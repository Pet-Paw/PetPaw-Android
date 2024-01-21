package com.petpaw.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.petpaw.R;
import com.petpaw.databinding.ItemConversationListBinding;
import com.petpaw.interfaces.OnConversationClickListener;
import com.petpaw.models.Conversation;
import com.petpaw.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.ViewHolder> {
    private List<Conversation> conversationList;
    private Map<String, User> userMap;
    private OnConversationClickListener onClickListener;
    private FirebaseAuth mAuth;

    public ConversationListAdapter() {
        mAuth = FirebaseAuth.getInstance();
        conversationList = new ArrayList<>();
        userMap = null;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setConversationList(List<Conversation> conversationList) {
        this.conversationList = conversationList;
        sortConversationList();
        notifyDataSetChanged();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setUserMap(Map<String, User> userMap) {
        this.userMap = userMap;
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnConversationClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConversationListBinding binding = ItemConversationListBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (userMap == null) {
            return;
        }
        if (mAuth.getCurrentUser() == null){
            return;
        }

        Conversation conversation = conversationList.get(position);
        if (conversation.getMemberIdList().size() <= 2){
            User user = new User();
            for (String userId: conversation.getMemberIdList()) {
                if (!Objects.equals(userId, mAuth.getCurrentUser().getUid())) {
                    user = userMap.get(userId);
                }
            }
            String preview = "";
            if(conversation.getLastMessage().getSenderId().equals(mAuth.getCurrentUser().getUid())){
                preview += "You: ";
            }
            preview += conversation.getLastMessage().getContent();
            holder.mBinding.tvContent.setText(preview);
            holder.mBinding.tvUser.setText(user.getName());
            if(user.getImageURL() == null){
                holder.mBinding.iv.setImageResource(R.drawable.default_avatar);
            } else {
                Picasso.get()
                        .load(user.getImageURL())
                        .tag(System.currentTimeMillis())
                        .into(holder.mBinding.iv, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("TAG", "Load image successfully");
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("TAG", "Load image failed");
                            }
                        });
            }

        } else {
            holder.mBinding.iv.setImageResource(R.drawable.group_chat_image);
            String names = "";
            int cnt = 0;
            for (String userId: conversation.getMemberIdList()) {
                if (!Objects.equals(userId, mAuth.getCurrentUser().getUid())) {
                    names += userMap.get(userId).getName();
                    cnt++;

                    if (cnt == 3) {
                        break;
                    }

                    if(conversation.getMemberIdList().indexOf(userMap.get(userId).getUid()) != (conversation.getMemberIdList().size() - 1)){
                        names += ", ";
                    }
                }
            }
            String preview = "";
            if(conversation.getLastMessage().getSenderId().equals(mAuth.getCurrentUser().getUid())){
                preview += "You: ";
            }
            preview += conversation.getLastMessage().getContent();
            holder.mBinding.tvContent.setText(preview);
            holder.mBinding.tvUser.setText(names);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(conversation);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemConversationListBinding mBinding;
        public ViewHolder(@NonNull ItemConversationListBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    private void sortConversationList() {
        conversationList.sort(new Comparator<Conversation>() {
            @Override
            public int compare(Conversation o1, Conversation o2) {
                return o2.getLastMessage().getSentAt().compareTo(o1.getLastMessage().getSentAt());
            }
        });

    }
}
