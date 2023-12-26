package com.petpaw.models;

import java.util.List;

public class PostComment {
    private String content;
    private String author;
    private int likes;

    public PostComment() {
    }

    public PostComment(String content, String author, int likes) {
        this.content = content;
        this.author = author;
        this.likes = likes;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public int getLikes() {
        return likes;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

//    private final int id;
//    private List<Pet> pets;

//    private PostComment(Builder builder) {
//        this.content = builder.content;
//        this.author = builder.author;
//        this.id = builder.id;
//        this.likes = builder.likes;
//        this.pets = builder.pets;
//    }
//
//    public static class Builder {
//        private String content;
//        private User author;
//        private final int id;
//        private int likes;
//        private List<Pet> pets;
//
//        public Builder(String content, User author, int id) {
//            this.content = content;
//            this.author = author;
//            this.id = id;
//        }
//
//        public Builder likes(int likes) {
//            this.likes = likes;
//            return this;
//        }
//
//        public Builder pets(List<Pet> pets) {
//            this.pets = pets;
//            return this;
//        }
//
//        public PostComment build() {
//            return new PostComment(this);
//        }
//    }

}
