package com.petpaw.utils;

import android.media.Image;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageHelper {
    public static void loadImage (String imageUrl, ImageView imageView) {
        if (imageUrl == null || imageUrl.isEmpty()) {
           return;
        }


        Picasso.get().load(imageUrl).tag(System.currentTimeMillis())
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG", "Load image successfully");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("TAG", "Load image successfully");
                    }
                });
    }

}
