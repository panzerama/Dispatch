package com.indexyear.jd.dispatch.models;

import com.google.firebase.database.Exclude;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by karibullard on 10/29/17.
 */

public class Message {

    public Date timeSent;
    public String userID;
    public String userName;
    public String text;

    public Date getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = timeSent;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Team getUserTeam() {
        return userTeam;
    }

    public void setUserTeam(Team userTeam) {
        this.userTeam = userTeam;
    }

    public Team userTeam;

    public Message(){

    }

    public Message(String messageText, String messageSender, Team userTeam) {
        this.text = messageText;
        this.userID = messageSender;
        this.userTeam = userTeam;

        // Initialize to current time
        timeSent = Calendar.getInstance().getTime();
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userID", userID);
        result.put("author", userName);
        result.put("text", text);
        result.put("timeSent", timeSent);
        result.put("userTeam", userTeam);

        return result;
    }
}
