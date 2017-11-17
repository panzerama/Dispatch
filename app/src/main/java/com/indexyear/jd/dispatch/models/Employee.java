package com.indexyear.jd.dispatch.models;

import com.indexyear.jd.dispatch.activities.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class Employee {

    public String firstName;
    public String lastName;
    public String phone;
    public String currentTeam;
    public String userID;
    public UserRole currentRole;
    public MainActivity.UserStatus currentStatus;
    public float latitude;
    public float longitude;

    public enum UserRole { Dispatcher, MCTMEMBER }

    public Employee(){

    }

    public Employee(String userID, String firstName, String lastName, String phone){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public Employee(String firstName, String lastName, String phone){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
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

    public UserRole getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(UserRole currentRole) {
        this.currentRole = currentRole;
    }

    public MainActivity.UserStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(MainActivity.UserStatus currentStatus) {
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
