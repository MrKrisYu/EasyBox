package com.krisyu.easybox.activity.bottom_navigation.normal.search_fragment;

public class DeliveryItem {
    private int boxId;
    private int deliveryWeight;
    private int deliveryPrice;
    private String trackingNum;
    private String orderTime;
    private String senderLocation;
    private String senderName;
    private String receiverLocation;
    private String receiverName;
    private String deliveryStatus;
    private String deliveryType;

    public DeliveryItem(int boxId, int deliveryWeight, int deliveryPrice, String trackingNum, String orderTime, String senderLocation, String senderName, String receiverLocation, String receiverName, String deliveryStatus, String deliveryType) {
        this.boxId = boxId;
        this.deliveryWeight = deliveryWeight;
        this.deliveryPrice = deliveryPrice;
        this.trackingNum = trackingNum;
        this.orderTime = orderTime;
        this.senderLocation = senderLocation;
        this.senderName = senderName;
        this.receiverLocation = receiverLocation;
        this.receiverName = receiverName;
        this.deliveryStatus = deliveryStatus;
        this.deliveryType = deliveryType;
    }

    public int getBoxId() {
        return boxId;
    }

    public String getBoxIdwithTitle(){
        return "柜子编号： " + boxId;
    }

    public void setBoxId(int boxId) {
        this.boxId = boxId;
    }

    public int getDeliveryWeight() {
        return deliveryWeight;
    }

    public void setDeliveryWeight(int deliveryWeight) {
        this.deliveryWeight = deliveryWeight;
    }

    public int getDeliveryPrice() {
        return deliveryPrice;
    }
    public String getDeliveryPricewithTitle(){
        return "合计：¥ " + getDeliveryPrice();
    }

    public void setDeliveryPrice(int deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public String getTrackingNum() {
        return trackingNum;
    }

    public String getTrackingNumwithTitle(){
        return "订单号： " + getTrackingNum();
    }

    public void setTrackingNum(String trackingNum) {
        this.trackingNum = trackingNum;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public String getOrderTimewithTitle() {
        return "下单时间： " + orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getSenderLocation() {
        return senderLocation;
    }

    public void setSenderLocation(String senderLocation) {
        this.senderLocation = senderLocation;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverLocation() {
        return receiverLocation;
    }

    public void setReceiverLocation(String receiverLocation) {
        this.receiverLocation = receiverLocation;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryInfo(){
        return getDeliveryType() + "  " + getDeliveryWeight() + " kg";
    }
}
