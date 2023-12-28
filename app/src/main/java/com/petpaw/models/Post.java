package com.petpaw.models;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {
//    private String title;
    private String content;
    private String authorId;
    private String petId;
    private int likes;
    private Date dateModified;
    private boolean isModified;
    private String imageURL;
    private List<String> comments;
    private List<String> tags;

    public Post() {
        this.dateModified = new Date();
        this.isModified = false;
        this.comments = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public Post(String content, String author, String petId) {
        this.content = content;
        this.authorId = author;
        this.petId = petId;

//        default value -------------------------
        this.dateModified = new Date();
        this.isModified = false;
        this.comments = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
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

//    public List<String> getImages() {
//        return images;
//    }

//    public void setImages(List<String> images) {
//        this.images = images;
//    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
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
