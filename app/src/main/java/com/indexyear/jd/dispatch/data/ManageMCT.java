package com.indexyear.jd.dispatch.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by jd on 11/16/17.
 */

public class ManageMCT {

    private DatabaseReference mDatabase;

    public ManageMCT() { mDatabase = FirebaseDatabase.getInstance().getReference(); }

    public void addEmployeeAndToken(String teamName, String employeeID, String token) {
        mDatabase.child("teams").child(teamName).child("teamMembers").child(employeeID).setValue(token);
    }
}
