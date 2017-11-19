package com.indexyear.jd.dispatch.data.user;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.indexyear.jd.dispatch.models.Team;
import com.indexyear.jd.dispatch.models.User;

import java.util.List;

/**
 * Created by jd on 11/18/17.
 */

public class TeamFirebaseMapper {
    Team fromDataSnapshot(DataSnapshot snapshot){
        GenericTypeIndicator<List<User>> t = new GenericTypeIndicator<List<User>>() {};
        String latitude = (snapshot.child("latitude").getValue() != null) ? snapshot.child("latitude").getValue().toString() : "-7.3430524";
        String longitude = (snapshot.child("longitude").getValue() != null) ? snapshot.child("longitude").getValue().toString() : "72.3588805";

        return new Team(
                snapshot.child("teamName").getValue(String.class),
                snapshot.child("teamMembers").getValue(t),
                Float.parseFloat(latitude),
                Float.parseFloat(longitude)
        );
    }
}