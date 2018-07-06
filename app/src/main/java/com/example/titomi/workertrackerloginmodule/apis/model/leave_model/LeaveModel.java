package com.example.titomi.workertrackerloginmodule.apis.model.leave_model;

import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LeaveModel implements Serializable
{

    public static final String PENDING = "Pending";
    public static final String COMPLETED = "approved";
    private final static long serialVersionUID = -6305727652654605707L;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("approvedBy")
    @Expose
    private String approvedBy;
    @SerializedName("applicant")
    @Expose
    private User applicant;
    @SerializedName("fromDate")
    @Expose
    private String fromDate;
    @SerializedName("toDate")
    @Expose
    private String toDate;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("numDays")
    @Expose
    private Integer numDays;
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("updated")
    @Expose
    private String updated;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("id")
    @Expose
    private String id;

    /**
     * No args constructor for use in serialization
     *
     */
    public LeaveModel() {
    }

    /**
     * @param userId
     * @param approvedBy
     * @param fromDate
     * @param toDate
     * @param reason
     * @param date
     * @param comment
     * @param numDays
     * @param status
     * @param id
     */

    public LeaveModel(String userId, String approvedBy, String fromDate, String toDate, String reason, String date, String comment, Integer numDays, String status, String id) {
        super();
        this.userId = userId;
        this.approvedBy = approvedBy;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
        this.date = date;
        this.comment = comment;
        this.numDays = numDays;
        this.status = status;
        this.id = id;
    }

    /**
     *
     * @param status
     * @param reason
     * @param approvedBy
     * @param toDate
     * @param date
     * @param id
     * @param statusCode
     * @param message
     * @param fromDate
     * @param updated
     * @param created
     * @param description
     * @param numDays
     * @param name
     * @param userId
     * @param applicant
     * @param comment
     */
    public LeaveModel(String userId, String approvedBy, User applicant, String fromDate, String toDate, String reason, String date, String comment, Integer numDays, Integer statusCode, String message, String created, String updated, String status, String name, String description, String id) {
        super();
        this.userId = userId;
        this.approvedBy = approvedBy;
        this.applicant = applicant;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
        this.date = date;
        this.comment = comment;
        this.numDays = numDays;
        this.statusCode = statusCode;
        this.message = message;
        this.created = created;
        this.updated = updated;
        this.status = status;
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getNumDays() {
        return numDays;
    }

    public void setNumDays(Integer numDays) {
        this.numDays = numDays;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Object getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}