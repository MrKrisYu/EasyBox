package com.krisyu.easybox.activity.ui.normal.home_fragment;

public class GroupItem {
    private String groupTitle;
    private int indicatorImageId;
    private int expandImageId;


    public GroupItem(String groupTitle, int indicatorImageId, int expandImageId) {
        this.groupTitle = groupTitle;
        this.indicatorImageId = indicatorImageId;
        this.expandImageId = expandImageId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public int getIndicatorImageId() {
        return indicatorImageId;
    }

    public void setIndicatorImageId(int indicatorImageId) {
        this.indicatorImageId = indicatorImageId;
    }

    public int getExpandImageId() {
        return expandImageId;
    }

    public void setExpandImageId(int expandImageId) {
        this.expandImageId = expandImageId;
    }
}
