package com.example.titomi.workertrackerloginmodule.supervisor;

import java.util.Date;

/**
 * Created by NeonTetras on 22-Feb-18.
 */

public class Task extends Entity {

    public Task() {
    }

    public Task(int id, User supervisor, User worker, Date dateGiven,
                Date dateDelivered, String name, String description, String timeGiven,
                String workType,
                String contactName, String contactNumber,
                String institution_name,
                String location, String lga, String state,
                String address, String sales, String images, int quantity,
                int inventoryBalance, int quantitySold, int participants, int status,int productId) {
        this.supervisor = supervisor;
        this.worker = worker;
        this.dateGiven = dateGiven;
        this.timeGiven = timeGiven;
        this.dateDelivered = dateDelivered;
        this.workType = workType;
        this.quantity = quantity;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.institution_name = institution_name;
        this.status = status;
        this.location = location;
        this.lga = lga;
        this.state = state;
        this.address = address;
        this.sales = sales;
        this.inventoryBalance = inventoryBalance;
        this.quantitySold = quantitySold;
        this.name =  name;
        this.description = description;
        this.images = images;
        this.participants = participants;
        this.id = id;
        this.productId = productId;

    }

    public User getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(User supervisor) {
        this.supervisor = supervisor;
    }

    public User getWorker() {
        return worker;
    }

    public void setWorker(User worker) {
        this.worker = worker;
    }

    public Date getDateGiven() {
        return dateGiven;
    }

    public void setDateGiven(Date dateGiven) {
        this.dateGiven = dateGiven;
    }

    public String getTimeGiven() {
        return timeGiven;
    }

    public void setTimeGiven(String timeGiven) {
        this.timeGiven = timeGiven;
    }

    public Date getDateDelivered() {
        return dateDelivered;
    }

    public void setDateDelivered(Date dateDelivered) {
        this.dateDelivered = dateDelivered;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getInstitution_name() {
        return institution_name;
    }

    public void setInstitution_name(String institution_name) {
        this.institution_name = institution_name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLga() {
        return lga;
    }

    public void setLga(String lga) {
        this.lga = lga;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }

    public int getInventoryBalance() {
        return inventoryBalance;
    }

    public void setInventoryBalance(int inventoryBalance) {
        this.inventoryBalance = inventoryBalance;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Double getStopLatitude() {
        return stopLatitude;
    }

    public void setStopLatitude(Double stopLatitude) {
        this.stopLatitude = stopLatitude;
    }

    public Double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public Double getStopLongitude() {
        return stopLongitude;
    }

    public void setStopLongitude(Double stopLongitude) {
        this.stopLongitude = stopLongitude;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getWorkerComment() {
        return workerComment;
    }

    public void setWorkerComment(String workerComment) {
        this.workerComment = workerComment;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    private User supervisor;
    private User worker;
    public Date dateGiven;
    public String timeGiven;
    public Date dateDelivered;
    public Date startTime;
    public Date stopTime;
    public String workType;
    public int quantity;
    public String contactName;
    public String contactNumber;
    public String institution_name;
    public Double latitude;
    public Double longitude;
    public Double startLatitude;
    public Double stopLatitude;
    public Double startLongitude;
    public Double stopLongitude;
    public int status;


    public String workerComment;
    public String location;
    public String lga;
    public String state;
    public String address;
    public String sales;
    private String description;
    private String images;
    private String video;
    private String audio;

    public static final int PENDING = 0;
    public static final int ONGOING = 1;
    public static final int PENDING_APPROVAL = 2;
    public static final int COMPLETED = 3;
    public int inventoryBalance;
    public int quantitySold;
    private int participants;
    private int productId;


}
