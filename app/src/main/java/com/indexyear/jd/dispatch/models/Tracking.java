package com.indexyear.jd.dispatch.models;

/**
 * Created by NikoPC on 11/12/2017.
 */

public class Tracking {
    private String email,Uid,lat,lng;


    public Tracking() {
    }

    public Tracking(String email, String Uid, String lat, String lng) {
        this.email = email;
        this.Uid = Uid;
        this.lat = lat;
        this.lng = lng;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        this.Uid = Uid;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
