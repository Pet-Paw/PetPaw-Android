package com.petpaw.models;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.petpaw.R;
import com.petpaw.activities.StartActivity;

import java.util.Objects;

public class NotificationChannelService extends FirebaseMessagingService {
    private static final String TAG = "NotificationChannelService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.d(TAG, "From: " + message.getFrom());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (message.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + message.getData());


            // sending message to current user
            if (message.getNotification() != null) {
                String notificationBody = message.getNotification().getBody();
                if (message.getNotification().getBody() == null) {
                    return;
                }
                sendNotification(notificationBody);
                return;
            }

            // sending message to other user
            String userId = message.getData().get("userId");
            String messageBody = message.getData().get("message");

            String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            assert userId != null;
            if (!userId.equals(currentUserId)) {
                return;
            }
            sendNotification(messageBody);
        }
    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }


    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);
        String channelId = getString(com.petpaw.R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.chat_icon)
                        .setContentTitle("PetPaw")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }

    private void sendRegistrationToServer(String token) {
        // TODO
    }
}
