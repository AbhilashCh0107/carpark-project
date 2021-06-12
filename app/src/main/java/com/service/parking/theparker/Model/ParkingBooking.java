package com.service.parking.theparker.Model;

import java.io.Serializable;

public class ParkingBooking implements Serializable {
    private String parkingId;
    private String transactionId;
    private Long timestamp;
    private String by;
    private String spotHost;
    private String slotNo;
    private String slotNumber;
    private String date;
    private String price;
    private String slot;
    private boolean payLater=false;
    private boolean completed=false;
    private String parkingArea;

    private String id;

    public ParkingBooking() {

    }

    public ParkingBooking(String parkingId, String transactionId, Long timestamp, String by, String spotHost, String slotNo, String parkingArea) {
        this.parkingId = parkingId;
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.by = by;
        this.spotHost = spotHost;
        this.slotNo = slotNo;
        this.parkingArea = parkingArea;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getSpotHost() {
        return spotHost;
    }

    public void setSpotHost(String spotHost) {
        this.spotHost = spotHost;
    }

    public String getSlotNo() {
        return slotNo;
    }

    public void setSlotNo(String slotNo) {
        this.slotNo = slotNo;
    }

    public String getParkingArea() {
        return parkingArea;
    }

    public void setParkingArea(String parkingArea) {
        this.parkingArea = parkingArea;
    }

    public boolean isPayLater() {
        return payLater;
    }

    public void setPayLater(boolean payLater) {
        this.payLater = payLater;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
