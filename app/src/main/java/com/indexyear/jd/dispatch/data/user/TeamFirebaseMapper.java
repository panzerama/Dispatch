package com.indexyear.jd.dispatch.data.user;

import com.google.firebase.database.DataSnapshot;
import com.indexyear.jd.dispatch.models.Team;
import com.indexyear.jd.dispatch.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jd on 11/18/17.
 */

public class TeamFirebaseMapper {
    public Team fromDataSnapshot(DataSnapshot snapshot){

        String latitude = (snapshot.child("latitude").getValue() != null) ? snapshot.child("latitude").getValue().toString() : "-7.3430524";
        String longitude = (snapshot.child("longitude").getValue() != null) ? snapshot.child("longitude").getValue().toString() : "72.3588805";

        List<User> teamMembers = new ArrayList<>();
        for(DataSnapshot snap : snapshot.child("teamMembers").getChildren()){
            teamMembers.add(new UserFirebaseMapper().fromDataSnapshot(snap));
        }

        return new Team(
                snapshot.child("teamName").getValue(String.class),
                teamMembers,
                Float.parseFloat(latitude),
                Float.parseFloat(longitude)
        );
    }
}