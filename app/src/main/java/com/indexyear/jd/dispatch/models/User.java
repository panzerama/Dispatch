package com.indexyear.jd.dispatch.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable{

    public String currentTeam;
    public String userID;
    public String currentRole;
    public String currentStatus;
    public String email;
    public String phone;
    public String token;
    public float latitude;
    public float longitude;
    public String firstName;
    public String lastName;


    // required for use with DataSnapshot getValue
    // must be public to work with DataSnapshot getValue
    public User(){

    }

    public User(String currentTeam, String userID, String currentRole, String currentStatus, String email, String phone, String token, float latitude, float longitude, String firstName, String lastName) {
        this.currentTeam = currentTeam;
        this.userID = userID;
        this.currentRole = currentRole;
        this.currentStatus = currentStatus;
        this.email = email;
        this.phone = phone;
        this.token = token;
        this.latitude = latitude;
        this.longitude = longitude;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    protected User(Parcel in) {
        currentTeam = in.readString();
        userID = in.readString();
        currentRole = in.readString();
        currentStatus = in.readString();
        email = in.readString();
        phone = in.readString();
        token = in.readString();
        latitude = in.readFloat();
        longitude = in.readFloat();
        firstName = in.readString();
        lastName = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getCurrentTeam() {
        return currentTeam;
    }

    public void setCurrentTeam(String currentTeam) {
        this.currentTeam = currentTeam;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("currentTeam", currentTeam);
        result.put("userID", userID);
        result.put("currentRole", currentRole);
        result.put("currentStatus", currentStatus);
        result.put("email", email);
        result.put("phone", phone);
        result.put("token", token);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("firstName", firstName);
        result.put("lastName", lastName);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(currentTeam);
        parcel.writeString(userID);
        parcel.writeString(currentRole);
        parcel.writeString(currentStatus);
        parcel.writeString(email);
        parcel.writeString(phone);
        parcel.writeString(token);
        parcel.writeFloat(latitude);
        parcel.writeFloat(longitude);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
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
        this.email = otherUser.email;
        this.phone = otherUser.email;
        this.token = otherUser.token;
        this.latitude = otherUser.latitude;
        this.longitude = otherUser.longitude;
        this.firstName = otherUser.firstName;
        this.lastName = otherUser.lastName;
    }

    public static User createFromIDAndEmail(String userID, String email) {
        User user =  new User();
        user.setEmail(email);
        user.setUserID(userID);

        return user;
    }
}
