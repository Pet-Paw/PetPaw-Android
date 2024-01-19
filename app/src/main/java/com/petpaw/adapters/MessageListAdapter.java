package com.petpaw.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.petpaw.R;
import com.petpaw.databinding.ItemMessageListBinding;
import com.petpaw.interfaces.OnMessageClickListener;
import com.petpaw.models.Message;
import com.petpaw.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
    private List<Message> messageList;
    private Map<String, User> userMap;
    private OnMessageClickListener onClickListener;
    private Context context;
    FirebaseAuth auth;

    public MessageListAdapter(Context context) {
        auth = FirebaseAuth.getInstance();
        this.context = context;
        this.messageList = new ArrayList<>();
        userMap = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUserMap(Map<String, User> userMap) {
        this.userMap = userMap;
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

        if(message.getSenderId().equals(auth.getCurrentUser().getUid())){
            holder.mBinding.llMessageBox.setBackgroundTintList(context.getColorStateList(R.color.primary));
            holder.mBinding.tvContent.setTextColor(context.getColor(R.color.white));
            holder.mBinding.llMessage.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            holder.mBinding.llMessageBox.setBackgroundTintList(null);
            holder.mBinding.tvContent.setTextColor(context.getColor(R.color.black));
            holder.mBinding.llMessage.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        if (userMap != null) {
            User user = userMap.get(message.getSenderId());
            holder.mBinding.tvUser.setText(user.getName());
            if(userMap.size() < 3){
                holder.mBinding.tvUser.setVisibility(View.GONE);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(holder.mBinding.iv.getLayoutParams());
                params.setMargins(0, 40, 0, 0);
                holder.mBinding.iv.setLayoutParams(params);
            }
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
