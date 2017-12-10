package com.indexyear.jd.dispatch.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Crisis {

    private String crisisID;
    private String crisisAddress;
    private String teamName;
    private String status;
    private double latitude;
    private double longitude;

    /*
    private Date crisisDate;
    private Calendar timeCallReceived;
    private Calendar timeArrived;
    private Boolean policeOnScene;
    private ReferringAgency referringAgency;
    private ReferralReason referralReason;
    private String callBackNumber;
    private String dispatchComments;
    private Team responseTeam;
    private List<User> responseTeamMembers;
    */

    public static Crisis createFromAddress(String address) {
        return new Crisis(address);
    }

    public Crisis(String crisisID, String crisisAddress, String teamName, String status) {
        this.crisisID = crisisID;
        this.crisisAddress = crisisAddress;
        this.teamName = teamName;
        this.status = status;
    }

    private Crisis(String crisisAddress){
        this.crisisAddress = crisisAddress;
        this.crisisID = UUID.randomUUID().toString();
        this.status = "open";
        this.teamName = "unset";
    }

    /*
    private enum ReferringAgency {
        DMHP, FIRE, POLICE, CC
    }

    private enum ReferralReason {
        MH, CD, MHCD, OTHER
    }
    */

    public String getCrisisID() { return crisisID; }

    public void setCrisisID(String crisisID) { this.crisisID = crisisID; }

    public String getCrisisAddress() {
        return crisisAddress;
    }

    public void setCrisisAddress(String crisisAddress) {
        this.crisisAddress = crisisAddress;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getLatitude() { return latitude; }
    public String getLatitudeAsString() { return String.valueOf(latitude); }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public String getLongitudeAsString() { return String.valueOf(longitude); }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public Map toMap(){
        Map<String, String> outputMap = new HashMap<String, String>();

        outputMap.put("crisisID", crisisID);
        outputMap.put("crisisAddress", crisisAddress);
        outputMap.put("teamName", teamName);
        outputMap.put("status", status);
        outputMap.put("latitude", "" + latitude);
        outputMap.put("longitude", "" + longitude);
        return outputMap;
    }

}
