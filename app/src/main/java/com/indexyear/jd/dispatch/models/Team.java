package com.indexyear.jd.dispatch.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by karibullard on 10/23/17.
 */

public class Team implements Parcelable {

    public String teamName;
    public String teamID;
    public List<User> teamMembers;
    public float latitude;
    public float longitude;

    public Team(){

    }

    public Team(String teamName, String teamID, List<User> teamMembers, float latitude, float longitude){
        this.teamName = teamName;
        this.teamID = teamID;
        this.teamMembers = teamMembers;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Team(Parcel in) {
        teamName = in.readString();
        teamID = in.readString();
        teamMembers = in.createTypedArrayList(User.CREATOR);
        latitude = in.readFloat();
        longitude = in.readFloat();
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

    public List<User> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<User> teamMembers) {
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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("teamMembers", teamMembers);
        result.put("teamID", teamID);
        result.put("teamName", teamName);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeList(teamMembers);
        parcel.writeString(teamName);
        parcel.writeString(teamID);
        parcel.writeFloat(latitude);
        parcel.writeFloat(longitude);

    }
}
