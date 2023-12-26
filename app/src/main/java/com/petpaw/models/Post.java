package com.petpaw.models;



import org.w3c.dom.Comment;

import java.util.List;

public class Post {
    private String title;
    private String content;
    private User author;
    private final int id;
    private List<String> images;
    private List<PostComment> comments;
    private int likes;


    private Post(Builder builder) {
        this.title = builder.title;
        this.content = builder.content;
        this.author = builder.author;
        this.id = builder.id;
        this.images = builder.images;
        this.comments = builder.comments;
        this.likes = builder.likes;
    }

    public static class Builder {
        private String title;
        private String content;
        private User author;
        private final int id;
        private List<String> images;
        private List<PostComment> comments;
        private int likes;

        public Builder(String title, String content, User author, int id) {
            this.title = title;
            this.content = content;
            this.author = author;
            this.id = id;
        }

        public Builder images(List<String> images) {
            this.images = images;
            return this;
        }

        public Builder comments(List<PostComment> comments) {
            this.comments = comments;
            return this;
        }

        public Builder likes(int likes) {
            this.likes = likes;
            return this;
        }

        public Post build() {
            return new Post(this);
        }
    }
}
