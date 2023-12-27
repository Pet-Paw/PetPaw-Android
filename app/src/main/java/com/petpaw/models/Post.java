package com.petpaw.models;



import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private String title;
    private String content;
    private String author;
    private String petId;
    private int likes;
    private List<String> images;
    private List<String> comments;

    public Post() {
        this.images = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public Post(String title, String content, String author, String petId) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.petId = petId;
        this.images = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    //    private Post(Builder builder) {
//        this.title = builder.title;
//        this.content = builder.content;
//        this.author = builder.author;
//        this.id = builder.id;
//        this.images = builder.images;
//        this.comments = builder.comments;
//        this.likes = builder.likes;
//    }
//
//    public static class Builder {
//        private String title;
//        private String content;
//        private User author;
//        private final int id;
//        private List<String> images;
//        private List<PostComment> comments;
//        private int likes;
//
//        public Builder(String title, String content, User author, int id) {
//            this.title = title;
//            this.content = content;
//            this.author = author;
//            this.id = id;
//        }
//
//        public Builder images(List<String> images) {
//            this.images = images;
//            return this;
//        }
//
//        public Builder comments(List<PostComment> comments) {
//            this.comments = comments;
//            return this;
//        }
//
//        public Builder likes(int likes) {
//            this.likes = likes;
//            return this;
//        }
//
//        public Post build() {
//            return new Post(this);
//        }
//    }
}
