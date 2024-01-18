package com.petpaw.clients;

import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.annotations.concurrent.Background;
import com.petpaw.R;
import com.petpaw.database.NotificationCollection;
import com.petpaw.database.UserCollection;
import com.petpaw.fragments.screens.SideNavFragment;
import com.petpaw.models.NotificationPetPaw;
import com.petpaw.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NotiSender {
    OkHttpClient client = new OkHttpClient();
    String token;
    String currentUserId;
    MediaType mediaType = MediaType.parse("application/json");
    JSONObject jsonData = new JSONObject();
    JSONObject jsonNotif = new JSONObject();
    JSONObject wholeObj = new JSONObject();
    UserCollection userCollection = UserCollection.newInstance();
    NotificationCollection notificationCollection = NotificationCollection.newInstance();


    public NotiSender(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    private void callFCMSendAPI() {
        RequestBody requestBody = RequestBody.create(mediaType, wholeObj.toString());
        Request request = new Request.Builder().url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .addHeader("Authorization", "Bearer AAAAPTkn49U:APA91bGZKD-oZHLxPPI6EtzwhB5VtkOmxVlqAQGRIYaymeNW59Cix0rpM3EPSuLrN_vyX_hgIP0lHJo57JrTio6Iot_HVSO0xFGyyvfyhte8b7oeUdhVHxuVdHflc1-S2UVX-WGZEKgB")
                .addHeader("Content-Type", "application/json").build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());

            jsonNotif = new JSONObject();
            jsonData = new JSONObject();
            wholeObj = new JSONObject();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Background
    public void sendNotificationOnCurrentAccount(String body) throws IOException {
        Thread sendNotiThread =  new Thread(new Runnable() {
            @Override
            public void run() {
                userCollection.getUser(currentUserId, new UserCollection.Callback() {
                    @Override
                    public void onCallback(List<User> users) {

                    }

                    @Override
                    public void onCallBack(User user) {
                        String title = "You";
                        String token = user.getPhysicalDeviceToken();
                        Log.d("NotiSender", "token: " + token);
                        NotificationPetPaw notification = new NotificationPetPaw(
                                title,
                                body,
                                currentUserId,
                                currentUserId,
                                new Date(),
                                true
                        );
                        notificationCollection.createNotification(notification);

                        try {
                            jsonData.put("title", title);
                            jsonData.put("body", body);
                            jsonData.put("fromUid", currentUserId);
                            jsonData.put("toUid", currentUserId);
                            wholeObj.put("to", token);
                            wholeObj.put("data", jsonData);
                            wholeObj.put("notification", jsonData);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        callFCMSendAPI();
                    }
                });
            }
        });
        sendNotiThread.start();
    }
    @Background
    public void sendNotificationToDifferentAccount(String toUserId, String fromUserName, String body) {
        Thread sendNotiThread = new Thread(new Runnable() {
            @Override
            public void run() {

                userCollection.getUser(toUserId, new UserCollection.Callback() {
                    @Override
                    public void onCallback(List<User> users) {

                    }

                    @Override
                    public void onCallBack(User user) {
                        String title = fromUserName;
                        String token = user.getPhysicalDeviceToken();
                        NotificationPetPaw notification = new NotificationPetPaw(
                                title,
                                body,
                                currentUserId,
                                user.getUid(),
                                new Date(),
                                true
                        );
                        notificationCollection.createNotification(notification);

                        try {
                            jsonData.put("title", title);
                            jsonData.put("body", body);
                            jsonData.put("fromUid", currentUserId);
                            jsonData.put("toUid", user.getUid());
                            wholeObj.put("to", token);
                            wholeObj.put("data", jsonData);
                            wholeObj.put("notification", jsonData);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        callFCMSendAPI();
                    }
                });
            }
        });
        sendNotiThread.start();
    }
}
