package com.petpaw.models;

import com.petpaw.interfaces.FirebaseDoc;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conversation implements FirebaseDoc {
    public static final String CONVERSATIONS = "Conversations";

    private String uid;
    private List<String> memberIdList;

    private Message lastMessage;

    public Conversation() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<String> getMemberIdList() {
        return memberIdList;
    }

    public void setMemberIdList(List<String> memberIdList) {
        this.memberIdList = memberIdList;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public Map<String, Object> toDoc() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("memberIdList", memberIdList);
        return doc;
    }

    public static class ConversationComparator implements Comparator<Conversation> {
        @Override
        public int compare(Conversation o1, Conversation o2) {
            return o1.lastMessage.getSentAt().compareTo(o2.lastMessage.getSentAt());
        }
    }

}
