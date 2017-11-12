package com.indexyear.jd.dispatch.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Crisis implements Parcelable{

    private String crisisID;
    private String crisisAddress;

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
    private List<Employee> responseTeamMembers;
    */

    public Crisis(String crisisID, String crisisAddress){
        this.crisisID = crisisID;
        this.crisisAddress = crisisAddress;
    }

    // jdp - requires that the fields be read in the same order that they are serialized elsewhere
    public Crisis(Parcel input){
        crisisID = input.readString();
        crisisAddress = input.readString();
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(crisisID);
        dest.writeString(crisisAddress);
    }

    public static final Parcelable.Creator<Crisis> CREATOR = new Parcelable.Creator<Crisis>(){
        public Crisis createFromParcel(Parcel in) {
            return new Crisis(in);
        }

        public Crisis[] newArray(int size) {
            return new Crisis[size];
        }
    };

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

    public List<Employee> getResponseTeamMembers() {
        return responseTeamMembers;
    }

    public void setResponseTeamMembers(List<Employee> responseTeamMembers) {
        this.responseTeamMembers = responseTeamMembers;
    }
    */

    public int describeContents(){
        return 0;
    }
}
