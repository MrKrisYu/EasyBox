package com.krisyu.easybox.mode;

import java.io.Serializable;

public class ChatItem implements Serializable {

    public static int MESSAGE_SEND = 0;
    public static int MESSAGE_RECEIVED = 1;

    private String time;
    private int headImgId;
    private String content;
    private int type;
    private String friendName;
    private boolean isTop;
    private boolean isEditing;

    public ChatItem(String time, int headImgId, String content, int type, String friendName) {
        this.time = time;
        this.headImgId = headImgId;
        this.content = content;
        this.type = type;
        this.friendName = friendName;
        this.isTop = false;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getHeadImgId() {
        return headImgId;
    }

    public void setHeadImgId(int headImgId) {
        this.headImgId = headImgId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
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
