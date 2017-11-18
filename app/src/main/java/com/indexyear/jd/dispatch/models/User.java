package com.indexyear.jd.dispatch.models;

import java.util.HashMap;
import java.util.Map;

public class User {

    public String firstName;
    public String lastName;
    public String phone;
    public String currentTeam;
    public String userID;
    public String currentRole;
    public String currentStatus;
    public float latitude;
    public float longitude;

    public User(String firstName, String lastName, String phone, String currentTeam, String userID, String currentRole, String currentStatus, float latitude, float longitude) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.currentTeam = currentTeam;
        this.userID = userID;
        this.currentRole = currentRole;
        this.currentStatus = currentStatus;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // TODO: 11/17/17 JD clean up the constructors

    public User(String userID, String firstName, String lastName, String phone){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public User(String firstName, String lastName, String phone){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public User(String userID, String role) {
        this.userID = userID;
        this.currentRole = role;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public void updateEmployee(){

    }

    public void createEmployee(){

    }

    public Map<String, Object> toMap() {
        Map<String, Object> employeeValues = new HashMap<>();

        employeeValues.put("firstName", firstName);
        employeeValues.put("lastName", lastName);
        employeeValues.put("phone", phone);
        employeeValues.put("currentMCT", currentTeam);
        employeeValues.put("currentRole", currentRole);
        employeeValues.put("currentStatus", currentStatus);
        employeeValues.put("latitude", latitude);
        employeeValues.put("longitude", longitude);
        employeeValues.put("userID", userID);

        return employeeValues;
    }


}
