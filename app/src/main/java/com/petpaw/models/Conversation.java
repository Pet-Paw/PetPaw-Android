package com.petpaw.models;

import java.util.Comparator;
import java.util.List;

public class Conversation {
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

    public static class ConversationComparator implements Comparator<Conversation> {
        @Override
        public int compare(Conversation o1, Conversation o2) {
            return o1.lastMessage.getSentAt().compareTo(o2.lastMessage.getSentAt());
        }
    }
}
