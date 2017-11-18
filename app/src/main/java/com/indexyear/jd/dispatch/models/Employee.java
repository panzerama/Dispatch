package com.indexyear.jd.dispatch.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.indexyear.jd.dispatch.activities.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class Employee implements Parcelable {

    public String firstName;
    public String lastName;
    public String phone;
    public String currentTeam;
    public String userID;
    public String currentRole;
    public MainActivity.UserStatus currentStatus;
    public float latitude;
    public float longitude;

    protected Employee(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        phone = in.readString();
        currentTeam = in.readString();
        userID = in.readString();
        currentRole = in.readString();
        latitude = in.readFloat();
        longitude = in.readFloat();
    }

    public static final Creator<Employee> CREATOR = new Creator<Employee>() {
        @Override
        public Employee createFromParcel(Parcel in) {
            return new Employee(in);
        }

        @Override
        public Employee[] newArray(int size) {
            return new Employee[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phone);
        dest.writeString(currentTeam);
        dest.writeString(userID);
        dest.writeString(currentRole);
        dest.writeFloat(latitude);
        dest.writeFloat(longitude);
    }

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

    public Employee(String userID, String role) {
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
