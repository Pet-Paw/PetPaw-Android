package com.petpaw.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.petpaw.R;
import com.petpaw.database.UserCollection;
import com.petpaw.models.NotificationPetPaw;
import com.petpaw.models.User;
import com.petpaw.utils.ImageHelper;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotificationPetPaw> notifications = new ArrayList<>();


    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;
        public ImageView profilePic;
        public TextView createdDate;
        public NotificationViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(com.petpaw.R.id.title);
            content = itemView.findViewById(R.id.content);
            profilePic = itemView.findViewById(R.id.profilePic);
            createdDate = itemView.findViewById(R.id.createdDate);
        }
    }


    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.notification_cardview, null);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationPetPaw notification = notifications.get(position);

        String userId = notification.getFrom();
        UserCollection userCollection = UserCollection.newInstance();
        userCollection.getUser(userId, new UserCollection.Callback() {
            @Override
            public void onCallback(List<User> users) {

            }

            @Override
            public void onCallBack(User user) {
                if (user.getImageURL() != null) {
                    ImageHelper.loadImage(user.getImageURL(), holder.profilePic);
                } else {
                    holder.profilePic.setImageResource(R.drawable.default_avatar);
                }
                holder.title.setText(user.getName());
                holder.content.setText(notification.getContent());
                holder.createdDate.setText(notification.getCreatedDate().toString());

            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void setNotifications(List<NotificationPetPaw> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }
}
