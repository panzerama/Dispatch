package com.indexyear.jd.dispatch.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable{

    private String currentTeam;
    private String userID;
    private String currentRole;
    private String currentStatus;
    private String email;
    private String token;
    private float latitude;
    private float longitude;


    // required for use with DataSnapshot getValue
    // must be public to work with DataSnapshot getValue
    public User(){

    }

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
        this("none", userID, "none", "none", email, "none", (float)0, (float)0);
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
        this.token = otherUser.token;
        this.latitude = otherUser.latitude;
        this.longitude = otherUser.longitude;
    }

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

    /**
     * Parcelable Implementation
     */
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

    protected User(Parcel in) {
        currentTeam = in.readString();
        userID = in.readString();
        currentRole = in.readString();
        currentStatus = in.readString();
        email = in.readString();
        token = in.readString();
        latitude = in.readFloat();
        longitude = in.readFloat();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> employeeValues = new HashMap<>();

        employeeValues.put("currentMCT", currentTeam);
        employeeValues.put("userID", userID);
        employeeValues.put("currentRole", currentRole);
        employeeValues.put("currentStatus", currentStatus);
        employeeValues.put("email", email);
        employeeValues.put("token", token);
        employeeValues.put("latitude", latitude);
        employeeValues.put("longitude", longitude);

        return employeeValues;
    }


    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currentTeam);
        dest.writeString(userID);
        dest.writeString(currentRole);
        dest.writeString(currentStatus);
        dest.writeString(email);
        dest.writeString(token);
        dest.writeFloat(latitude);
        dest.writeFloat(longitude);
    }
}
