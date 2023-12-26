package com.petpaw.models;

import java.util.List;

public abstract class FilterBy {
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String BIO = "bio";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String ADDRESS = "address";
    public static final String IMAGE_URL = "imageURL";
    public static final String FRIENDS = "friends";
    public static final String FOLLOWERS = "followers";
    public static final String FOLLOWING = "following";
    public static final String POSTS = "posts";
    public static final String PETS = "pets";
    public static final String IMAGES = "images";
    public static final String POSTED_BY = "postedBy";
    public static final String LIKED_BY = "likedBy";
    public static final String COMMENTS = "comments";
    public static final String LIKES = "likes";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String AUTHOR = "author";

    public static void startFilterBy(List<String> params) {
        for (String param : params) {
            switch (param) {
                case NAME:
                case ID:
                case BIO:
                case EMAIL:
                case PHONE:
                case ADDRESS:
                case IMAGE_URL:
                case FRIENDS:
                case FOLLOWERS:
                case FOLLOWING:
                case POSTS:
                case PETS:
                case IMAGES:
                case POSTED_BY:
                case LIKED_BY:
                case COMMENTS:
                case LIKES:
                case TITLE:
                case CONTENT:
                case AUTHOR:
            }
        }
    }



}
