package com.indexyear.jd.dispatch.models;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by karibullard on 10/23/17.
 */

public class MCT {

    public String teamName;
    public List<Employee> teamMembers;
    public float latitude;
    public float longitude;
    public Date travelTime;

    public MCT(){

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

    public Date getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Date travelTime) {
        this.travelTime = travelTime;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("teamMembers", teamMembers);
        result.put("teamName", teamName);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("travelTime", travelTime);

        return result;
    }

}
