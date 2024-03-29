package com.krisyu.easybox.activity.bottom_navigation.fragments.message_fragment;

import java.io.Serializable;

public class MessageListItem implements Serializable {
    private int headImageId;
    private String content;
    private String userName;
    private String time;
    private int numberOfunread;
    private boolean isTop;
    private boolean isEditing;

    public MessageListItem(int headImageId, String userName, String content, String time, int numberOfunread) {
        this.content = content;
        this.headImageId = headImageId;
        this.userName = userName;
        this.time = time;
        this.numberOfunread = numberOfunread;
        this.isTop = false;
        this.isEditing = false;

    }

    public int getHeadImageId() {
        return headImageId;
    }

    public void setHeadImageId(int headImageId) {
        this.headImageId = headImageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNumberOfunread() {
        return numberOfunread;
    }

    public void setNumberOfunread(int numberOfunread) {
        this.numberOfunread = numberOfunread;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }
    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }
}
