package com.example.titomi.workertrackerloginmodule.supervisor;

import java.util.Date;

/**
 * Created by NeonTetras on 13-Mar-18.
 */

public class Institutions extends Entity {

    public Institutions(String name,String state, String address,  String lga, String type,Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.state = state;
        this.lga = lga;
        this.type = type;
        this.name = name;

    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLga() {
        return lga;
    }

    public void setLga(String lga) {
        this.lga = lga;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private Double longitude;
    private Double latitude;
    private String address;
    private String state;
    private String lga;
    private Date dateAdded;
    private String type;
}
