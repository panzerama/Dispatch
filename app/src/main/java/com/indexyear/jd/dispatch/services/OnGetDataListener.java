package com.indexyear.jd.dispatch.services;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by jd on 11/9/17. Aids in database processing.
 */

    // A comment for Niko - JDP 12/12/2017

interface OnGetDataListener {
    public void onStart();
    public void onSuccess(DataSnapshot data);
    public void onFailed(DatabaseError databaseError);
}
