package com.example.titomi.workertrackerloginmodule.shared_pref_manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.titomi.workertrackerloginmodule.supervisor.User;

/**
 * Created by Titomi on 2/16/2018.
 */

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "mysharedpref";
    private static final String KEY_INVENTORY_BALANCE = "inventory_balance";
    private static final String KEY_IS_LOGIN = "isLogin";
    //Keys for entire users table
    private static final String KEY_USER_ID = "userid";
    private static final String KEY_SUPERVISOR_ID = "supervisor_id";
    private static final String KEY_USER_EMAIL = "useremail";
    private static final String KEY_USER_LINE_ID = "line_id";
    private static final String KEY_USER_SALT = "salt";
    private static final String KEY_USER_ACTIVATION_CODE = "activation_code";
    private static final String KEY_USER_FORGOTTEN_PASSWORD_CODE = "forgotten_password_code";
    private static final String KEY_USER_FORGOTTEN_PASSWORD_TIME = "forgotten_password_time";
    private static final String KEY_USER_REMEMBER_CODE = "remember_code";
    private static final String KEY_USER_CREATED_ON = "created_on";
    private static final String KEY_USER_LAST_LOGIN_OLD = "last_login_old";
    private static final String KEY_USER_ACTIVE = "active";
    private static final String KEY_USER_FIRST_NAME = "first_name";
    private static final String KEY_USER_LAST_NAME = "last_name";

    private static final String KEY_USER_CITY = "user_city";
    private static final String KEY_USER_STATE = "user_state";
    private static final String KEY_USER_COUNTRY = "user_country";
    private static final String KEY_USER_COMPANY = "company";
    private static final String KEY_USER_PHONE_NUMBER = "phone_number";
    private static final String KEY_USER_PHOTO = "photo";
    private static final String KEY_USER_ROLE = "role";
    private static final String KEY_USER_ACTIVATION = "activation";
    private static final String KEY_USER_STATUS = "status";
    private static final String KEY_USER_ADDRESS = "user_address";
    private static final String KEY_USER_REMEMBER = "remember";
    private static final String KEY_USER_LAST_LOGIN = "last_login";
    private static final String KEY_USER_IP_ADDRESS = "ip_address";
    private static final String KEY_USER_LEAVE = "leave";
    private SharedPreferences.Editor editor;
    private static SharedPrefManager mInstance;
    private Context mCtx;
    private SharedPreferences pref;
    private int PRIVATE_MODE = 0;
    private String FULL_NAME = "fullname";
    private String SAVED_ROLE = "userRole";
    private String SAVED_ROLE_ID = "role_id";

    public SharedPrefManager(Context context) {
        this.mCtx = context;
        pref = this.mCtx.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }

    public void setUserFullname(String fullname) {
        editor.putString(FULL_NAME, fullname);
        editor.apply();
    }

    public String getSavedFullName() {
        return pref.getString(FULL_NAME, "");
    }


    public void setSavedLineId(String lineId) {
        editor.putString(KEY_USER_LINE_ID, lineId);
        editor.apply();
    }

    public String getSavedLineId() {
        return pref.getString(KEY_USER_LINE_ID, "");
    }

    public void setSavedFirstName(String first_name) {
        editor.putString(KEY_USER_FIRST_NAME, first_name);
        editor.apply();
    }

    public String getSavedFirstName() {
        return pref.getString(KEY_USER_FIRST_NAME, "");
    }

    public void setSavedLastName(String last_name) {
        editor.putString(KEY_USER_LAST_NAME, last_name);
        editor.apply();
    }

    public String getSavedLastName() {
        return pref.getString(KEY_USER_LAST_NAME, "");
    }

    public void setSavedEmail(String email) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public String getSavedEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public String getSavedPhoto() {
        return pref.getString(KEY_USER_PHOTO, "");
    }

    public void setSavedPhoto(String photo) {
        editor.putString(KEY_USER_PHOTO, photo);
        editor.apply();
    }

    public void setSavedRole(String role) {
        editor.putString(KEY_USER_ROLE, role);
        editor.apply();
    }

    public String getSavedRole() {
        return pref.getString(KEY_USER_ROLE, "");
    }


    public void setSavedPhoneNumber(String phone_number) {
        editor.putString(KEY_USER_PHONE_NUMBER, phone_number);
        editor.apply();
    }

    public String getSavedPhoneNumber() {
        return pref.getString(KEY_USER_PHONE_NUMBER, "");
    }


    public void setSavedState(String state) {
        editor.putString(KEY_USER_STATE, state);
        editor.apply();
    }

    public String getSavedState() {
        return pref.getString(KEY_USER_STATE, "");
    }


    public void setSavedCity(String city) {
        editor.putString(KEY_USER_CITY, city);
        editor.apply();
    }

    public String getSavedCity() {
        return pref.getString(KEY_USER_CITY, "");
    }



    public int getSavedUserId() {
        return pref.getInt(KEY_USER_ID, 0);
    }

    public boolean isUserLogin() {
        return pref.getBoolean(KEY_IS_LOGIN, false);
    }

    public void setUserLoggedIn(Boolean isLogin) {
        editor.putBoolean(KEY_IS_LOGIN, isLogin);
        editor.apply();
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public User getLoggedInUser() {
        User user = new User();
        user.setState(getSavedState());
        user.setFeaturedImage(getSavedPhoto());
        user.setId(getSavedUserId());
        user.setRoleId((getSavedRoleId()));
        user.setRole(getSavedRole());
        user.setFullName(getSavedFullName());
        user.setEmail(getSavedEmail());
        user.setAddress(getSavedAddress());
        user.setPhoneNumber(getSavedPhoneNumber());
        user.setSupervisorId(getSavedSupervisorId());
        return user;
    }


    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public boolean userLogin(int id, String email, String line_id, String salt, String activation_code, String forgotten_password_code, int forgotten_password_time, String remember_code, int created_on, int last_login_old, int active, String first_name, String last_name, String user_address, String user_city, String user_state, String user_country, String company, String phone_number, String photo, String role, String activation, String status, String remember, String last_login, String ip_address, String leave) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_LINE_ID, line_id);
        editor.putString(KEY_USER_SALT, salt);
        editor.putString(KEY_USER_ACTIVATION_CODE, activation_code);
        editor.putString(KEY_USER_FORGOTTEN_PASSWORD_CODE, forgotten_password_code);
        editor.putInt(KEY_USER_FORGOTTEN_PASSWORD_TIME, forgotten_password_time);
        editor.putString(KEY_USER_REMEMBER_CODE, remember_code);
        editor.putInt(KEY_USER_CREATED_ON, created_on);
        editor.putInt(KEY_USER_LAST_LOGIN_OLD, last_login_old);
        editor.putInt(KEY_USER_ACTIVE, active);
        editor.putString(KEY_USER_FIRST_NAME, first_name);
        editor.putString(KEY_USER_LAST_NAME, last_name);
        editor.putString(KEY_USER_ADDRESS, user_address);
        editor.putString(KEY_USER_CITY, user_city);
        editor.putString(KEY_USER_STATE, user_state);
        editor.putString(KEY_USER_COUNTRY, user_country);
        editor.putString(KEY_USER_COMPANY, company);
        editor.putString(KEY_USER_PHONE_NUMBER, phone_number);
        editor.putString(KEY_USER_PHOTO, photo);
        editor.putString(KEY_USER_ROLE, role);
        editor.putString(KEY_USER_ACTIVATION, activation);
        editor.putString(KEY_USER_STATUS, status);
        editor.putString(KEY_USER_REMEMBER, remember);
        editor.putString(KEY_USER_LAST_LOGIN, last_login);
        editor.putString(KEY_USER_IP_ADDRESS, ip_address);
        editor.putString(KEY_USER_LEAVE, leave);

        editor.apply();
        return true;
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_USER_EMAIL, null) != null) {
            return true;
        }
        return false;
    }

    public boolean logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public void setSavedRoleId(int userRole) {
        editor.putInt(SAVED_ROLE, userRole);
        editor.apply();
    }

    public int getSavedRoleId() {
        return pref.getInt(SAVED_ROLE, 0);
    }

    public void setSavedUserId(int id) {

    editor.putInt(KEY_USER_ID,id);
        editor.apply();
    }

    public void setSavedAddress(String address){
        editor.putString(KEY_USER_ADDRESS,address);
                editor.apply();
    }

    public String getSavedAddress(){
        return pref.getString(KEY_USER_ADDRESS, "");
    }

    public void setSavedSupervisorId(long savedSupervisorId) {
        editor.putLong(KEY_SUPERVISOR_ID, savedSupervisorId);
    }

    public long getSavedSupervisorId() {
        return pref.getLong(KEY_SUPERVISOR_ID, Long.parseLong("0"));
    }

    public void setSavedInventoryBalance(int balance) {
        editor.putInt(KEY_INVENTORY_BALANCE, balance);
    }

    public int getSavedInventoryBalance() {
        return pref.getInt(KEY_INVENTORY_BALANCE, 0);
    }
}
