package com.petpaw.models;

import com.petpaw.interfaces.FirebaseDoc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Community implements FirebaseDoc {
    private String id;
    private String name;
    private String description;
    private String owner;
    private String imageURL;
    private List<String> members;
    private String conversationId;

    public Community(String id, String name, String description, String owner, String imageURL, List<String> members) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.imageURL = imageURL;
        this.members = members;
    }

    public Community() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public Map<String, Object> toDoc() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("id", id);
        doc.put("name", name);
        doc.put("description", description);
        doc.put("owner", owner);
        doc.put("imageURL", imageURL);
        doc.put("members", members);
        return doc;
    }
}
