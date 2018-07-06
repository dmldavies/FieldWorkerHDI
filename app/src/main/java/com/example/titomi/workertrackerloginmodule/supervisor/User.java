package com.example.titomi.workertrackerloginmodule.supervisor;

import java.io.Serializable;

/**
 * Created by NeonTetras on 13-Feb-18.
 */
public class User extends Entity implements Serializable {


    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserLevelText() {
        return userLevelText;
    }

    public void setUserLevelText(String userLevelText) {
        this.userLevelText = userLevelText;
    }


    private String userLevelText;

    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public long getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(long supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private int userLevel;
    private String email;
    private int roleId;
    private String role;
    private String username;
    private String lineId;
    private long supervisorId;
    private String lastLogin;
    private boolean active;
    private String fullName;
    private String address;
    private String city;
    private String state;
    private String country;
    private String workType;
    private String status;


    public static final int ADMIN = 1;
    public static final int SUPERVISOR = 2;
    public static final int NURSE = 3;
    public static final int CLIENT = 4;
    public static final int DISTRIBUTOR = 5;
    public static final int MANAGER = 6;
    protected static final long serialVersionUID = 1l;
}
