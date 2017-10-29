package com.indexyear.jd.dispatch.models;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Crisis {

    private String crisisAddress;
    private Date crisisDate;
    private Calendar timeCallReceived;
    private Calendar timeArrived;
    private Boolean policeOnScene;
    private ReferringAgency referringAgency;
    private ReferralReason referralReason;
    private String callBackNumber;
    private String dispatchComments;
    private MCT responseTeam;
    private List<Employee> responseTeamMembers;

    public Crisis(String crisisAddress){
        this.crisisAddress = crisisAddress;
    }

    private enum ReferringAgency {
        DMHP, FIRE, POLICE, CC
    }

    private enum ReferralReason {
        MH, CD, MHCD, OTHER
    }

    public String getCrisisAddress() {
        return crisisAddress;
    }

    public void setCrisisAddress(String crisisAddress) {
        this.crisisAddress = crisisAddress;
    }

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

    public List<Employee> getResponseTeamMembers() {
        return responseTeamMembers;
    }

    public void setResponseTeamMembers(List<Employee> responseTeamMembers) {
        this.responseTeamMembers = responseTeamMembers;
    }
}
