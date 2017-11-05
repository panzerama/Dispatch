package com.indexyear.jd.dispatch.models;

import com.indexyear.jd.dispatch.activities.MainActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by karibullard on 10/23/17.
 */

public class Employee {
    public String firstName;
    public String lastName;
    public String userID;
    public String phone;
    public MCT currentMCT;
    public UserRole currentRole;
    public MainActivity.UserStatus currentStatus;

    public Employee(){

    }

    public Employee(String userID, String firstName, String lastName, String phone){
        this.userID = userID;
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

    public enum UserRole {
        DISPATCHER, MCTMEMBER
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

    public MCT getCurrentMCT() {
        return currentMCT;
    }

    public void setCurrentMCT(MCT currentMCT) {
        this.currentMCT = currentMCT;
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

    public Map<String, Object> toMap() {
        Map<String, Object> employeeValues = new HashMap<>();

        employeeValues.put("firstName", firstName);
        employeeValues.put("lastName", lastName);
        employeeValues.put("uid", userID);
        employeeValues.put("phone", phone);

        return employeeValues;
    }

    public void updateEmployee(){

    }

    public void createEmployee(){

    }
}
