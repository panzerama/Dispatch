package com.indexyear.jd.dispatch.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by karibullard on 10/23/17.
 */

public class MCT {

    public String teamID;
    public String teamName;
    public List<Employee> teamMembers;
    public float latitude;
    public float longitude;

    public MCT(){

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

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<Employee> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<Employee> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }

    public MCT(String teamName){
        this.teamName = teamName;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("teamID", teamID);
        result.put("teamMembers", teamMembers);
        result.put("teamName", teamName);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;
    }

}
