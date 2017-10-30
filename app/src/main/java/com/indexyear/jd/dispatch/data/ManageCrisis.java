package com.indexyear.jd.dispatch.data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.indexyear.jd.dispatch.models.Crisis;

import java.util.Calendar;

/**
 * Created by lucasschwarz on 10/29/17.
 */

public class ManageCrisis {

    private DatabaseReference mDatabase; //Firebase reference

    public ManageCrisis(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void CreateNewCrisis(String crisisID, String address){
        Crisis crisis = new Crisis(crisisID, address);
        mDatabase.child("crisis").child(crisisID).setValue(crisis);
    }

    //GetAddress of crisis based on ID
    public String GetCrisisAddress(DataSnapshot dataSnapshot, String crisisID) {

        for (DataSnapshot addressDataSnapshot : dataSnapshot.getChildren()) {
            String addressCheck = String.valueOf(addressDataSnapshot.child("address").getValue());
            if (crisisID.equals(addressDataSnapshot.child("crisisID").getValue())) {
               return addressCheck;
            }
        }
        return "address not found";
    }

    //set time automatically, to be called from MapActivity listener
    public void SetArrivalTime(String crisisID){
        String currentDateTimeString = "" + Calendar.DATE + Calendar.getInstance().getTime();
        mDatabase.child("crisis").child(crisisID).child("arrivalTime").setValue(currentDateTimeString);
    }

    //set time in case of Listener failure,
    public void SetArrivalTime(String crisisID, String time) {
        mDatabase.child("crisis").child(crisisID).child("arrivalTime").setValue(time);
    }



}
