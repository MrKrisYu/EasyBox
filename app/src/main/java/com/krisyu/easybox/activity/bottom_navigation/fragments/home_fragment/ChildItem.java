package com.krisyu.easybox.activity.bottom_navigation.fragments.home_fragment;

public class ChildItem {
    public static final int BOX_STATUS_FREE = 1;
    public static final int BOX_STATUS_BUSY = 2;
    public static final int BOX_STATUS_DISCARD = 3;
    private String boxSequence;
    private int boxStatus;
    private int boxImageId;
    private boolean statusOfBind;


    public ChildItem(String boxSequence, int boxStatus, int boxImageId, boolean statusOfBind) {
        this.boxSequence = boxSequence;
        this.boxStatus = boxStatus;
        this.boxImageId = boxImageId;
        this.statusOfBind = statusOfBind;
    }

    public ChildItem(String boxSequence, int boxStatus, boolean statusOfBind) {
        this.boxSequence = boxSequence;
        this.boxStatus = boxStatus;
        this.boxImageId = 0;
        this.statusOfBind = statusOfBind;
    }

    public String getBoxSequence() {
        return boxSequence;
    }

    public void setBoxSequence(String boxSequence) {
        this.boxSequence = boxSequence;
    }

    public int getBoxStatus() {
        return boxStatus;
    }

    public void setBoxStatus(int boxStatus) {
        this.boxStatus = boxStatus;
    }

    public int getBoxImageId() {
        return boxImageId;
    }

    public void setBoxImageId(int boxImageId) {
        this.boxImageId = boxImageId;
    }

    public boolean isStatusOfBind() {
        return statusOfBind;
    }

    public void setStatusOfBind(boolean statusOfBind) {
        this.statusOfBind = statusOfBind;
    }
}
