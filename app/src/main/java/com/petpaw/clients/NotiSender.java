package com.petpaw.clients;

import android.os.Environment;
import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.annotations.concurrent.Background;
import com.petpaw.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

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


    public NotiSender(String token, String currentUserId) {
        this.token = token;
        this.currentUserId = currentUserId;
    }


    @Background
    public void sendNotification(String body) throws IOException {
        Thread sendNotiThread =  new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    jsonNotif.put("title", token);
                    jsonNotif.put("body", body);
                    jsonData.put("userId", currentUserId);
                    wholeObj.put("to", token);
                    wholeObj.put("data", jsonData);
                    wholeObj.put("notification", jsonNotif);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                RequestBody requestBody = RequestBody.create(mediaType, wholeObj.toString());
                Request request = new Request.Builder().url("https://fcm.googleapis.com/fcm/send")
                        .post(requestBody)
                        .addHeader("Authorization", "Bearer AAAAPTkn49U:APA91bGZKD-oZHLxPPI6EtzwhB5VtkOmxVlqAQGRIYaymeNW59Cix0rpM3EPSuLrN_vyX_hgIP0lHJo57JrTio6Iot_HVSO0xFGyyvfyhte8b7oeUdhVHxuVdHflc1-S2UVX-WGZEKgB")
                        .addHeader("Content-Type", "application/json").build();
                try {
                    Response response = client.newCall(request).execute();
                    System.out.println(response.body().string());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        sendNotiThread.start();
    }
}
