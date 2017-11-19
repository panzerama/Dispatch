package com.indexyear.jd.dispatch.models;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String currentTeam;
    private String userID;
    private String currentRole;
    private String currentStatus;
    private String email;
    private String token;
    private float latitude; // TODO: 11/17/17 JD change to one Location object?
    private float longitude;

    public static User createFromIDAndEmail(String userID, String email) {
        return new User(userID, email);
    }

    public User(String currentTeam, String userID, String currentRole, String currentStatus, String email, String token, float latitude, float longitude) {
        this.currentTeam = currentTeam;
        this.userID = userID;
        this.currentRole = currentRole;
        this.currentStatus = currentStatus;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private User(String userID, String email){
        this("none", userID, "none", "none", email, "none", (float) -7.3430524, (float) 72.3588805);
    }

    // required for use with DataSnapshot getValue
    private User(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public User(String email){}

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCurrentTeam() {
        return currentTeam;
    }

    public void setCurrentTeam(String currentTeam) {
        this.currentTeam = currentTeam;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(String currentRole) {
        this.currentRole = currentRole;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /***
     * Turns current user into a shallow copy of another, or updates based on retrieved user object.
     * @param otherUser
     */
    public void updateUser(User otherUser){
        this.currentTeam = otherUser.currentTeam;
        this.userID = otherUser.userID;
        this.currentRole = otherUser.currentRole;
        this.currentStatus = otherUser.currentStatus;
        this.latitude = otherUser.latitude;
        this.longitude = otherUser.longitude;
    }

    public void createEmployee(){

    }

    public Map<String, Object> toMap() {
        Map<String, Object> employeeValues = new HashMap<>();

        employeeValues.put("currentMCT", currentTeam);
        employeeValues.put("currentRole", currentRole);
        employeeValues.put("currentStatus", currentStatus);
        employeeValues.put("latitude", latitude);
        employeeValues.put("longitude", longitude);
        employeeValues.put("userID", userID);

        return employeeValues;
    }


}
