package com.petpaw.models;

import com.petpaw.interfaces.FirebaseDoc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Comment implements FirebaseDoc {
    private String commentId;
    private String content;
    private String author;
    private List<String> likes;

    public Comment() {
    }

    public Comment(String commentId, String content, String author, List<String> likes) {
        this.commentId = commentId;
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

    public List<String> getLikes() {
        return likes;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    @Override
    public Map<String, Object> toDoc() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("content", content);
        doc.put("author", author);
        doc.put("likes", likes);
        return doc;
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
