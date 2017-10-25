package com.indexyear.jd.dispatch.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ManageState {

    private DatabaseReference mDatabase; //Firebase reference

    public ManageState(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
}
