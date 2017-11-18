package com.indexyear.jd.dispatch.data;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.indexyear.jd.dispatch.models.Crisis;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class CrisisManager { // TODO: 11/17/17 JD classes should be named after nouns, not verbs

    private Crisis mCrisis;
    private DatabaseReference mDatabase; //Firebase reference
    private List<ICrisisEventListener> mListeners;
    private ValueEventListener mCrisisValueChangeListener;

    public CrisisManager(){

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("crisis").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (ICrisisEventListener mListener: mListeners) {
                    mListener.onCrisisCreated(new CrisisFirebaseMapper().fromDataSnapshot(dataSnapshot));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (ICrisisEventListener mListener: mListeners) {
                    mListener.onCrisisUpdated(new CrisisFirebaseMapper().fromDataSnapshot(dataSnapshot));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for (ICrisisEventListener mListener: mListeners) {
                    mListener.onCrisisRemoved(new CrisisFirebaseMapper().fromDataSnapshot(dataSnapshot));
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Crisis createNewCrisis(String crisisAddress){
        String newKey = UUID.randomUUID().toString();
        Crisis newCrisis = new Crisis(newKey, crisisAddress, "Unspecified", "open");

        DatabaseReference newCrisisNode = mDatabase.child("crisis").child(newCrisis.getCrisisID());
        newCrisisNode.updateChildren(newCrisis.toMap());

        return newCrisis;

    }

    //GetAddress of crisis based on ID
    public String getCrisisAddress(DataSnapshot dataSnapshot, String crisisID) {

        for (DataSnapshot addressDataSnapshot : dataSnapshot.getChildren()) {
            String addressCheck = String.valueOf(addressDataSnapshot.child("address").getValue());
            if (crisisID.equals(addressDataSnapshot.child("crisisID").getValue())) {
               return addressCheck;
            }
        }
        return "address not found";
    }

    //set time automatically, to be called from MapActivity listener
    public void setArrivalTime(String crisisID){
        String currentDateTimeString = "" + Calendar.DATE + Calendar.getInstance().getTime();
        mDatabase.child("crisis").child(crisisID).child("arrivalTime").setValue(currentDateTimeString);
    }

    //set time in case of Listener failure,
    public void setArrivalTime(String crisisID, String time) {
        mDatabase.child("crisis").child(crisisID).child("arrivalTime").setValue(time);
    }

    public void setCrisisEventListener(ICrisisEventListener crisisEventListener){
        mListeners.add(crisisEventListener);
    }

}
