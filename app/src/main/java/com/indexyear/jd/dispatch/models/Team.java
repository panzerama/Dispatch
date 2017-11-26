package com.indexyear.jd.dispatch.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by karibullard on 10/23/17.
 */

public class Team {

    public String teamName;
    public List<User> teamMembers;
    public float latitude;
    public float longitude;

    public Team(){

    }

    public Team(String teamName, List<User> teamMembers, float latitude, float longitude){
        this.teamName = teamName;
        this.teamMembers = teamMembers;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
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
        result.put("teamName", teamName);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;
    }

}
