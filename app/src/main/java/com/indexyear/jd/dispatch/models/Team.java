package com.indexyear.jd.dispatch.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by karibullard on 10/23/17.
 */

public class Team implements Parcelable{

    private String teamName;
    private String teamID;
    private HashMap<String, User> teamMembers;
    private float latitude;
    private float longitude;
    private String status;
    private int travelTime;
    private String travelTimeReadable;
    private HashMap<String, String> tokens;

    /**
     * Paramaterless Constructor -- Required for Firebase Datasnapshot
     */
    public Team() {
    }

    public Team(String teamName, String teamID, HashMap<String, User> teamMembers, float latitude, float longitude, String status, int travelTime, String travelTimeReadable, HashMap<String, String> tokens) {
        this.teamName = teamName;
        this.teamID = teamID;
        this.teamMembers = teamMembers;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.travelTime = travelTime;
        this.travelTimeReadable = travelTimeReadable;
        this.tokens = tokens;
    }

    protected Team(Parcel in) {
        teamName = in.readString();
        teamID = in.readString();
        teamMembers = new HashMap<String, User>();
        in.readMap(teamMembers,User.class.getClassLoader());
        latitude = in.readFloat();
        longitude = in.readFloat();
        status = in.readString();
        travelTime = in.readInt();
        travelTimeReadable = in.readString();
        tokens = new HashMap<String, String>();
        in.readMap(tokens,String.class.getClassLoader());
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    public String getTeamName() {
        if(teamName == null){
            teamName = "";
        }
        return teamName;
    }

    public void setTeamName(String teamName) {
        if(teamName == null){
            this.teamName  = "";
        } else {
            this.teamName = teamName;
        }
    }

    public String getTeamID() {
        if(teamID == null){
            teamID = "";
        }
        return teamID;
    }

    public void setTeamID(String teamID) {
        if(teamID == null){
            this.teamID  = "";
        } else {
            this.teamID = teamID;
        }
    }

    public HashMap<String, User> getTeamMembers() {
        if(teamMembers == null){
            teamMembers = new HashMap<>();
        }
        return teamMembers;
    }

    public void setTeamMembers(HashMap<String, User> teamMembers) {
        if(teamMembers == null){
            this.teamMembers = new HashMap<>();
        } else {
            this.teamMembers = teamMembers;
        }
    }

    public float getLatitude() {
        return latitude;
    }
    public String getLatitudeAsString() { return String.valueOf(latitude); }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }
    public String getLongitudeAsString() { return String.valueOf(longitude); }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        if(status == null){
            status = "";
        }
        return status;
    }

    public void setStatus(String status) {
        if(status == null){
            this.status = "";
        } else {
            this.status = status;
        }
    }

    public int getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(int travelTime) {
        this.travelTime = travelTime;
    }

    public String getTravelTimeReadable() {
        if(travelTimeReadable == null){
            travelTimeReadable = "";
        }
        return travelTimeReadable;
    }

    public void setTravelTimeReadable(String travelTimeReadable) {
        if(travelTimeReadable == null){
            this.travelTimeReadable = "";
        } else {
            this.travelTimeReadable = travelTimeReadable;
        }
    }

    public HashMap<String, String> getTokens() {
        if(tokens == null){
            tokens = new HashMap<>();
        }
        return tokens;
    }

    public void setTokens(HashMap<String, String> tokens) {
        if(tokens == null){
            this.tokens = new HashMap<>();
        } else {
            this.tokens = tokens;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(teamName);
        parcel.writeString(teamID);
        parcel.writeFloat(latitude);
        parcel.writeFloat(longitude);
        parcel.writeString(status);
        parcel.writeInt(travelTime);
        parcel.writeString(travelTimeReadable);
        parcel.writeMap(teamMembers);
        parcel.writeMap(tokens);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("teamName", teamName);
        result.put("teamID", teamID);
        result.put("teamMembers", teamMembers);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("status", status);
        result.put("travelTime", travelTime);
        result.put("travelTime", travelTimeReadable);
        result.put("tokens", tokens);

        return result;
    }
}
