package com.example.titomi.workertrackerloginmodule.apis.model.leave_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Applicant implements Serializable
{

    private final static long serialVersionUID = -3398890361468804782L;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("verificationCode")
    @Expose
    private Object verificationCode;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("roleId")
    @Expose
    private Object roleId;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("connection")
    @Expose
    private Connection connection;
    @SerializedName("sql")
    @Expose
    private Object sql;
    @SerializedName("ip_address")
    @Expose
    private Object ipAddress;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("line_id")
    @Expose
    private String lineId;
    @SerializedName("supervisor_id")
    @Expose
    private String supervisorId;
    @SerializedName("salt")
    @Expose
    private Object salt;
    @SerializedName("activation_code")
    @Expose
    private Object activationCode;
    @SerializedName("forgot_password_code")
    @Expose
    private Object forgotPasswordCode;
    @SerializedName("forgot_password_time")
    @Expose
    private Object forgotPasswordTime;
    @SerializedName("remember_code")
    @Expose
    private Object rememberCode;
    @SerializedName("created_on")
    @Expose
    private Object createdOn;
    @SerializedName("last_login_old")
    @Expose
    private Object lastLoginOld;
    @SerializedName("active")
    @Expose
    private Object active;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("user_address")
    @Expose
    private String userAddress;
    @SerializedName("user_city")
    @Expose
    private String userCity;
    @SerializedName("user_state")
    @Expose
    private String userState;
    @SerializedName("user_country")
    @Expose
    private String userCountry;
    @SerializedName("work_type")
    @Expose
    private String workType;
    @SerializedName("company")
    @Expose
    private Object company;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("activation")
    @Expose
    private Object activation;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("remember")
    @Expose
    private String remember;
    @SerializedName("last_login")
    @Expose
    private String lastLogin;
    @SerializedName("user_leave")
    @Expose
    private String userLeave;
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private Object message;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("updated")
    @Expose
    private Object updated;
    @SerializedName("name")
    @Expose
    private Object name;
    @SerializedName("description")
    @Expose
    private Object description;
    @SerializedName("id")
    @Expose
    private String id;

    /**
     * No args constructor for use in serialization
     *
     */
    public Applicant() {
    }

    /**
     *
     * @param remember
     * @param forgotPasswordCode
     * @param userCity
     * @param userLeave
     * @param workType
     * @param password
     * @param id
     * @param statusCode
     * @param userAddress
     * @param userState
     * @param username
     * @param rememberCode
     * @param phoneNumber
     * @param updated
     * @param created
     * @param description
     * @param userCountry
     * @param name
     * @param activationCode
     * @param role
     * @param verificationCode
     * @param firstName
     * @param activation
     * @param lineId
     * @param ipAddress
     * @param roleId
     * @param lastName
     * @param connection
     * @param lastLoginOld
     * @param status
     * @param forgotPasswordTime
     * @param photo
     * @param message
     * @param createdOn
     * @param supervisorId
     * @param lastLogin
     * @param sql
     * @param email
     * @param company
     * @param active
     * @param salt
     */
    public Applicant(String email, Object verificationCode, String role, Object roleId, String password, Connection connection, Object sql, Object ipAddress, String username, String lineId, String supervisorId, Object salt, Object activationCode, Object forgotPasswordCode, Object forgotPasswordTime, Object rememberCode, Object createdOn, Object lastLoginOld, Object active, String firstName, String lastName, String userAddress, String userCity, String userState, String userCountry, String workType, Object company, String phoneNumber, String photo, Object activation, String status, String remember, String lastLogin, String userLeave, Integer statusCode, Object message, String created, Object updated, Object name, Object description, String id) {
        super();
        this.email = email;
        this.verificationCode = verificationCode;
        this.role = role;
        this.roleId = roleId;
        this.password = password;
        this.connection = connection;
        this.sql = sql;
        this.ipAddress = ipAddress;
        this.username = username;
        this.lineId = lineId;
        this.supervisorId = supervisorId;
        this.salt = salt;
        this.activationCode = activationCode;
        this.forgotPasswordCode = forgotPasswordCode;
        this.forgotPasswordTime = forgotPasswordTime;
        this.rememberCode = rememberCode;
        this.createdOn = createdOn;
        this.lastLoginOld = lastLoginOld;
        this.active = active;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userAddress = userAddress;
        this.userCity = userCity;
        this.userState = userState;
        this.userCountry = userCountry;
        this.workType = workType;
        this.company = company;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
        this.activation = activation;
        this.status = status;
        this.remember = remember;
        this.lastLogin = lastLogin;
        this.userLeave = userLeave;
        this.statusCode = statusCode;
        this.message = message;
        this.created = created;
        this.updated = updated;
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(Object verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Object getRoleId() {
        return roleId;
    }

    public void setRoleId(Object roleId) {
        this.roleId = roleId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Object getSql() {
        return sql;
    }

    public void setSql(Object sql) {
        this.sql = sql;
    }

    public Object getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(Object ipAddress) {
        this.ipAddress = ipAddress;
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

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    public Object getSalt() {
        return salt;
    }

    public void setSalt(Object salt) {
        this.salt = salt;
    }

    public Object getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(Object activationCode) {
        this.activationCode = activationCode;
    }

    public Object getForgotPasswordCode() {
        return forgotPasswordCode;
    }

    public void setForgotPasswordCode(Object forgotPasswordCode) {
        this.forgotPasswordCode = forgotPasswordCode;
    }

    public Object getForgotPasswordTime() {
        return forgotPasswordTime;
    }

    public void setForgotPasswordTime(Object forgotPasswordTime) {
        this.forgotPasswordTime = forgotPasswordTime;
    }

    public Object getRememberCode() {
        return rememberCode;
    }

    public void setRememberCode(Object rememberCode) {
        this.rememberCode = rememberCode;
    }

    public Object getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Object createdOn) {
        this.createdOn = createdOn;
    }

    public Object getLastLoginOld() {
        return lastLoginOld;
    }

    public void setLastLoginOld(Object lastLoginOld) {
        this.lastLoginOld = lastLoginOld;
    }

    public Object getActive() {
        return active;
    }

    public void setActive(Object active) {
        this.active = active;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public Object getCompany() {
        return company;
    }

    public void setCompany(Object company) {
        this.company = company;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Object getActivation() {
        return activation;
    }

    public void setActivation(Object activation) {
        this.activation = activation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemember() {
        return remember;
    }

    public void setRemember(String remember) {
        this.remember = remember;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getUserLeave() {
        return userLeave;
    }

    public void setUserLeave(String userLeave) {
        this.userLeave = userLeave;
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

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Object getUpdated() {
        return updated;
    }

    public void setUpdated(Object updated) {
        this.updated = updated;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}