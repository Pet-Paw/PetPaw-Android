package com.petpaw.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.petpaw.databinding.ItemConversationListBinding;
import com.petpaw.databinding.ItemMessageListBinding;
import com.petpaw.interfaces.OnConversationClickListener;
import com.petpaw.models.Conversation;
import com.petpaw.models.User;

import java.util.List;
import java.util.Map;

public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.ViewHolder> {
    private final FirebaseUser user;
    private List<Conversation> conversationList;
    private Map<String, User> userMap;
    private OnConversationClickListener onClickListener;

    public ConversationListAdapter(FirebaseUser user, List<Conversation> conversationList) {
        this.user = user;
        this.conversationList = conversationList;
        sortConversationList();
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
        Conversation conversation = conversationList.get(position);

        if (userMap != null) {
            return;
        }

        holder.mBinding.tvContent.setText(conversation.getLastMessage().getContent());


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

    }
}
