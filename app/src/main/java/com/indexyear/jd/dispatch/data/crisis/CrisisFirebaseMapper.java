package com.indexyear.jd.dispatch.data.crisis;

import com.google.firebase.database.DataSnapshot;
import com.indexyear.jd.dispatch.models.Crisis;

public class CrisisFirebaseMapper    {
    // presumes that a single crisis node is being read
    Crisis fromDataSnapshot(DataSnapshot snapshot) {
        return new Crisis(
                snapshot.getKey(),
                snapshot.child("crisisAddress").getValue(String.class),
                snapshot.child("teamName").getValue(String.class),
                snapshot.child("status").getValue(String.class));
    }
}

