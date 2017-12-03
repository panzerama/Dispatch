package com.indexyear.jd.dispatch.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by karibullard on 10/23/17.
 */

public class Team implements Parcelable{

    public String teamName;
    public String teamID;
    public Map<String, User> teamMembers;
    public float latitude;
    public float longitude;
    public String status;
    public float travelTime;
    public Map<String, String> tokens;

    public Team() {
    }

    public Team(String teamName, String teamID, Map<String, User> teamMembers, float latitude, float longitude, String status, float travelTime, Map<String, String> tokens) {
        this.teamName = teamName;
        this.teamID = teamID;
        this.teamMembers = teamMembers;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.travelTime = travelTime;
        this.tokens = tokens;
    }

    protected Team(Parcel in) {
        teamName = in.readString();
        teamID = in.readString();
        latitude = in.readFloat();
        longitude = in.readFloat();
        status = in.readString();
    }

    @Exclude
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
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }

    public Map<String, User> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(Map<String, User> teamMembers) {
        this.teamMembers = teamMembers;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(float travelTime) {
        this.travelTime = travelTime;
    }

    public Map<String, String> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, String> tokens) {
        this.tokens = tokens;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("teamMembers", teamMembers);
        result.put("teamID", teamID);
        result.put("teamName", teamName);
        result.put("status", status);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("travelTime", travelTime);
        result.put("tokens", tokens);

        return result;
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
        parcel.writeInt(this.teamMembers.size());
        for (Map.Entry<String, User> entry : this.teamMembers.entrySet()) {
            parcel.writeString(entry.getKey());
            parcel.writeParcelable(entry.getValue(), i);
        }
        parcel.writeInt(this.tokens.size());
        for (Map.Entry<String, String> entry : this.tokens.entrySet()) {
            parcel.writeString(entry.getKey());
            parcel.writeString(entry.getValue());
        }
    }
}
