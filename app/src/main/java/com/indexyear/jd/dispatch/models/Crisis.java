package com.indexyear.jd.dispatch.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Crisis {

    private String crisisID;
    private String crisisAddress;
    private String teamName;
    private String status;

    /*
    private Date crisisDate;
    private Calendar timeCallReceived;
    private Calendar timeArrived;
    private Boolean policeOnScene;
    private ReferringAgency referringAgency;
    private ReferralReason referralReason;
    private String callBackNumber;
    private String dispatchComments;
    private MCT responseTeam;
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

    public Map toMap(){
        Map<String, String> outputMap = new HashMap<String, String>();

        outputMap.put("crisisID", crisisID);
        outputMap.put("crisisAddress", crisisAddress);
        outputMap.put("teamName", teamName);
        outputMap.put("status", status);

        return outputMap;
    }

    /*
    public Date getCrisisDate() {
        return crisisDate;
    }

    public void setCrisisDate(Date crisisDate) {
        this.crisisDate = crisisDate;
    }

    public Calendar getTimeCallReceived() {
        return timeCallReceived;
    }

    public void setTimeCallReceived(Calendar timeCallReceived) {
        this.timeCallReceived = timeCallReceived;
    }

    public Calendar getTimeArrived() { return timeArrived; }

    public void setTimeArrived(Calendar timeArrived) {
        this.timeArrived = timeArrived;
    }

    public Boolean getPoliceOnScene() {
        return policeOnScene;
    }

    public void setPoliceOnScene(Boolean policeOnScene) {
        this.policeOnScene = policeOnScene;
    }

    public ReferringAgency getReferringAgency() {
        return referringAgency;
    }

    public void setReferringAgency(ReferringAgency referringAgency) {
        this.referringAgency = referringAgency;
    }

    public ReferralReason getReferralReason() {
        return referralReason;
    }

    public void setReferralReason(ReferralReason referralReason) {
        this.referralReason = referralReason;
    }

    public String getCallBackNumber() {
        return callBackNumber;
    }

    public void setCallBackNumber(String callBackNumber) {
        this.callBackNumber = callBackNumber;
    }

    public String getDispatchComments() {
        return dispatchComments;
    }

    public void setDispatchComments(String dispatchComments) {
        this.dispatchComments = dispatchComments;
    }

    public MCT getResponseTeam() {
        return responseTeam;
    }

    public void setResponseTeam(MCT responseTeam) {
        this.responseTeam = responseTeam;
    }

    public List<User> getResponseTeamMembers() {
        return responseTeamMembers;
    }

    public void setResponseTeamMembers(List<User> responseTeamMembers) {
        this.responseTeamMembers = responseTeamMembers;
    }
    */
}
