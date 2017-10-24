package com.indexyear.jd.dispatch.models;

/**
 * Created by karibullard on 10/23/17.
 */

public class Employee {
    private String firstName;
    private String lastName;
    private String phone;
    private MCT currentMCT;
    private UserRole currentRole;

    public Employee(String firstName, String lastName, String phone){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    private enum UserRole {
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
}
