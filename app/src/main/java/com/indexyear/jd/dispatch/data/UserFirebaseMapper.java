package com.indexyear.jd.dispatch.data;

import com.google.firebase.database.DataSnapshot;
import com.indexyear.jd.dispatch.models.User;

/**
 * Created by jd on 11/17/17.
 */

class UserFirebaseMapper {
    // presumes that a single crisis node is being read
    User fromDataSnapshot(DataSnapshot snapshot) {
        return new User(
                snapshot.child("firstName").getValue(String.class),
                snapshot.child("lastName").getValue(String.class),
                snapshot.child("phone").getValue(String.class),
                snapshot.child("currentTeam").getValue(String.class),
                snapshot.child("userID").getValue(String.class),
                snapshot.child("currentRole").getValue(String.class),
                snapshot.child("currentStatus").getValue(String.class),
                (float) snapshot.child("latitude").getValue(),
                (float) snapshot.child("longitude").getValue()
        );
    }
}
