package com.petpaw.adapters;

import android.annotation.SuppressLint;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.petpaw.databinding.ItemMessageListBinding;
import com.petpaw.interfaces.OnMessageClickListener;
import com.petpaw.models.Message;
import com.petpaw.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
    private List<Message> messageList;
    private Map<String, User> userMap;
    private OnMessageClickListener onClickListener;

    public MessageListAdapter(List<Message> messageList) {
        this.messageList = messageList;
        userMap = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnMessageClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMessageListBinding binding = ItemMessageListBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);

        holder.mBinding.tvContent.setText(message.getContent());

        if (userMap != null) {
            User user = userMap.get(message.getSenderId());
            // TODO: load user avatar and username
//            Picasso.get().load()
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemMessageListBinding mBinding;
        public ViewHolder(@NonNull ItemMessageListBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
