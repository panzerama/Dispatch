package com.indexyear.jd.dispatch.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Crisis implements Parcelable{

    private String crisisID;
    private String crisisAddress;
    private String timeDispatchReceivedCall;
    private String dispatcherComments;
    private String callBackNumber;
    private String timeTeamDispatched;
    private String teamName;
    private HashMap<String, String> firstResponders;
    private String timeArrivedOnScene;
    private String timeResponseComplete;
    private String status;
    private double latitude;
    private double longitude;

    public Crisis(){

    }

    public Crisis(String crisisID, String crisisAddress, String timeDispatchReceivedCall, String dispatcherComments, String callBackNumber, String timeTeamDispatched, String teamName, HashMap<String, String> firstResponders, String timeArrivedOnScene, String timeResponseComplete, String status, double latitude, double longitude) {
        this.crisisID = crisisID;
        this.crisisAddress = crisisAddress;
        this.timeDispatchReceivedCall = timeDispatchReceivedCall;
        this.dispatcherComments = dispatcherComments;
        this.callBackNumber = callBackNumber;
        this.timeTeamDispatched = timeTeamDispatched;
        this.teamName = teamName;
        this.firstResponders = firstResponders;
        this.timeArrivedOnScene = timeArrivedOnScene;
        this.timeResponseComplete = timeResponseComplete;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Crisis createFromAddress(String address) {
        Crisis crisis = new Crisis();
        crisis.crisisAddress = address;
        crisis.crisisID =  UUID.randomUUID().toString();
        crisis.status = "unset";
        crisis.teamName = "unset";
        return crisis;
    }

    protected Crisis(Parcel in) {
        crisisID = in.readString();
        crisisAddress = in.readString();
        timeDispatchReceivedCall = in.readString();
        dispatcherComments = in.readString();
        callBackNumber = in.readString();
        timeTeamDispatched = in.readString();
        teamName = in.readString();
        timeArrivedOnScene = in.readString();
        timeResponseComplete = in.readString();
        status = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<Crisis> CREATOR = new Creator<Crisis>() {
        @Override
        public Crisis createFromParcel(Parcel in) {
            return new Crisis(in);
        }

        @Override
        public Crisis[] newArray(int size) {
            return new Crisis[size];
        }
    };

    public String getCrisisID() {
        if(crisisID.equals(null)){
            crisisID =  "none_set";
        }
        return crisisID;
    }

    public void setCrisisID(String crisisID) {
        if(crisisID.equals(null)){
            crisisID = "none_set";
        } else {
            this.crisisID = crisisID;
        }
    }

    public String getCrisisAddress() {
        if(crisisAddress.equals(null)){
            crisisAddress =  "none_set";
        }
        return crisisAddress;
    }

    public void setCrisisAddress(String crisisAddress) {
        if(crisisAddress.equals(null)){
            this.crisisAddress =  "none_set";
        } else {
            this.crisisAddress = crisisAddress;
        }
    }

    public String getTimeDispatchReceivedCall() {
        if(timeDispatchReceivedCall.equals(null)){
            timeDispatchReceivedCall =  "none_set";
        }
        return timeDispatchReceivedCall;
    }

    public void setTimeDispatchReceivedCall(String timeDispatchReceivedCall) {
        if(timeDispatchReceivedCall.equals(null)){
            this.timeDispatchReceivedCall =  "none_set";
        } else {
            this.timeDispatchReceivedCall = timeDispatchReceivedCall;
        }
    }

    public String getDispatcherComments() {
        if(dispatcherComments.equals(null)){
            dispatcherComments =  "none_set";
        }
        return dispatcherComments;
    }

    public void setDispatcherComments(String dispatcherComments) {
        if(dispatcherComments.equals(null)){
            this.dispatcherComments =  "none_set";
        } else {
            this.dispatcherComments = dispatcherComments;
        }
    }

    public String getCallBackNumber() {
        if(callBackNumber.equals(null)){
            callBackNumber =  "none_set";
        }
        return callBackNumber;
    }

    public void setCallBackNumber(String callBackNumber) {
        if(callBackNumber.equals(null)){
            this.callBackNumber =  "none_set";
        } else {
            this.callBackNumber = callBackNumber;
        }
    }

    public String getTimeTeamDispatched() {
        if(timeTeamDispatched.equals(null)){
            timeTeamDispatched =  "none_set";
        }
        return timeTeamDispatched;
    }

    public void setTimeTeamDispatched(String timeTeamDispatched) {
        if(timeTeamDispatched.equals(null)){
            this.timeTeamDispatched =  "none_set";
        } else {
            this.timeTeamDispatched = timeTeamDispatched;
        }
    }

    public String getTeamName() {
        if(teamName.equals(null)){
            teamName =  "none_set";
        }
        return teamName;
    }

    public void setTeamName(String teamName) {
        if(teamName.equals(null)){
            this.teamName =  "none_set";
        } else {
            this.teamName = teamName;
        }
    }

    public HashMap<String, String> getFirstResponders() {
        if(firstResponders.equals(null)){
            firstResponders =  new HashMap<>();
        }
        return firstResponders;
    }

    public void setFirstResponders(HashMap<String, String> firstResponders) {
        if(firstResponders.equals(null)){
            this.firstResponders =  new HashMap<>();
        } else {
            this.firstResponders = firstResponders;
        }
    }

    public String getTimeArrivedOnScene() {
        if(timeArrivedOnScene.equals(null)){
            timeArrivedOnScene =  "none_set";
        }
        return timeArrivedOnScene;
    }

    public void setTimeArrivedOnScene(String timeArrivedOnScene) {
        if(timeArrivedOnScene.equals(null)){
            this.timeArrivedOnScene =  "none_set";
        } else {
            this.timeArrivedOnScene = timeArrivedOnScene;
        }
    }

    public String getTimeResponseComplete() {
        if(timeResponseComplete.equals(null)){
            timeResponseComplete =  "none_set";
        }
        return timeResponseComplete;
    }

    public void setTimeResponseComplete(String timeResponseComplete) {
        if(timeResponseComplete.equals(null)){
            this.timeResponseComplete =  "none_set";
        } else {
            this.timeResponseComplete = timeResponseComplete;
        }
    }

    public String getStatus() {
        if(status.equals(null)){
            status =  "none_set";
        }
        return status;
    }

    public void setStatus(String status) {
        if(status.equals(null)){
            this.status =  "none_set";
        } else {
            this.status = status;
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Map toMap(){
        HashMap<String, Object> outputMap = new HashMap<String, Object>();

        outputMap.put("crisisID", crisisID);
        outputMap.put("crisisAddress", crisisAddress);
        outputMap.put("timeDispatchReceivedCall", timeDispatchReceivedCall);
        outputMap.put("dispatcherComments", dispatcherComments);
        outputMap.put("callBackNumber", callBackNumber);
        outputMap.put("timeTeamDispatched", timeTeamDispatched);
        outputMap.put("teamName", teamName);
        outputMap.put("firstResponders", firstResponders);
        outputMap.put("timeResponseComplete", timeResponseComplete);
        outputMap.put("status", status);
        outputMap.put("latitude", "" + latitude);
        outputMap.put("longitude", "" + longitude);
        return outputMap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(crisisID);
        parcel.writeString(crisisAddress);
        parcel.writeString(timeDispatchReceivedCall);
        parcel.writeString(dispatcherComments);
        parcel.writeString(callBackNumber);
        parcel.writeString(timeTeamDispatched);
        parcel.writeString(teamName);
        parcel.writeString(timeArrivedOnScene);
        parcel.writeString(timeResponseComplete);
        parcel.writeString(status);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }
}
