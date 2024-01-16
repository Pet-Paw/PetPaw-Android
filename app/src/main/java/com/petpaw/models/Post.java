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
//    private int likes;
    private Date dateModified;
    private boolean isModified;
    private String imageURL;
    private List<String> likes;

    private List<String> comments;
    private List<String> tags;
    private List<String> petIdList;
    private String communityId;
    private String postId;

    public Post() {
        this.dateModified = new Date();
        this.isModified = false;
        this.likes = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.petIdList = new ArrayList<>();
        this.postId = "";
        this.communityId = "";
    }

    public Post(String authorId, Date dateModified, String content,  boolean isModified, String imageURL, List<String> likes, List<String> comments, String postId, List<String> tags, List<String> petIdList, String communityId){
        this.authorId = authorId;
        this.dateModified = dateModified;
        this.isModified = isModified;
        this.content = content;
        this.imageURL = imageURL;
        this.likes = likes;
        this.comments = comments;
        this.postId = postId;
        this.tags = tags;
        this.petIdList = petIdList;
        this.communityId = communityId;
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
        this.petIdList = new ArrayList<>();
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

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

    public List<String> getPetIdList() {
        return petIdList;
    }

    public void setPetIdList(List<String> petIdList) {
        this.petIdList = petIdList;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
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
