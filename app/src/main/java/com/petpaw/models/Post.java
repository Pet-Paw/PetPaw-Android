package com.petpaw.models;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

//    @Override
//    public Map<String, Object> toDoc(){
//        Map<String, Object> doc = new HashMap<>();
//        doc.put("content", this.content);
//        doc.put("authorId", this.authorId);
//        doc.put("petId", this.petId);
//        doc.put("likes", this.likes);
//        doc.put("dateModified", this.dateModified);
//        doc.put("isModified", this.isModified);
//        doc.put("imageURL", this.imageURL);
//        doc.put("comments", this.comments);
//        doc.put("tags", this.tags);
//        return doc;
//    }
}
