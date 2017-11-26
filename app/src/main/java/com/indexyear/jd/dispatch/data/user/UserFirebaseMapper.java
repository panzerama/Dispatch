package com.indexyear.jd.dispatch.data.user;

import com.google.firebase.database.DataSnapshot;
import com.indexyear.jd.dispatch.models.User;

/**
 * Created by jd on 11/17/17.
 */

class UserFirebaseMapper {
    // presumes that a single crisis node is being read
    User fromDataSnapshot(DataSnapshot snapshot) {
        String latitude = (snapshot.child("latitude").getValue() != null) ? snapshot.child("latitude").getValue().toString() : "-7.3430524";
        String longitude = (snapshot.child("longitude").getValue() != null) ? snapshot.child("longitude").getValue().toString() : "72.3588805";

        return new User(
                snapshot.child("currentTeam").getValue(String.class),
                snapshot.child("userID").getValue(String.class),
                snapshot.child("currentRole").getValue(String.class),
                snapshot.child("currentStatus").getValue(String.class),
                snapshot.child("email").getValue(String.class),
                snapshot.child("token").getValue(String.class),
                Float.parseFloat(latitude),
                Float.parseFloat(longitude)
        );
    }
}