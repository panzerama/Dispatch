package com.indexyear.jd.dispatch.data.user;

import android.location.Location;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.indexyear.jd.dispatch.models.Team;
import com.indexyear.jd.dispatch.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserManager {

    private static final String TAG = "ManageUsers";

    private DatabaseReference mDatabase;
    private User mUser;
    private List<IUserEventListener> mListeners;
    private IGetUserListener mGetUserListener;

    public UserManager() {
        mListeners = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (mListeners.isEmpty()) {
                    return;
                }
                for (IUserEventListener mListener : mListeners) {
                    mListener.onUserCreated(dataSnapshot.getValue(User.class));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (mListeners.isEmpty()) {
                    return;
                }
                for (IUserEventListener mListener : mListeners) {
                    mListener.onUserUpdated(((dataSnapshot.getValue(User.class))));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (mListeners.isEmpty()) {
                    return;
                }
                for (IUserEventListener mListener : mListeners) {
                    mListener.onUserRemoved((dataSnapshot.getValue(User.class)));
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> currentUsers = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    User u = snap.getValue(User.class);
                    currentUsers.add(u);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addNewListener(IUserEventListener newListener) {
        mListeners.add(newListener);
    }

    public void addOrUpdateNewEmployee(User user) {
        mDatabase.child("users").child(user.getUserID()).setValue(user);
    }

    public void AddTeamNameNode(String teamName, String teamID) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("users").child(teamID).setValue(teamName);
    }

    public void setUserRole(String userID, String role) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("users").child(userID).child("currentRole").setValue(role);
    }

    public void setUserTeam(String userID, String team) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("users").child(userID).child("currentTeam").setValue(team);
    }

//    public static Team getTeam(final String teamName){
//        final Team[] team = {null};
//        try {
//            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
//            dbRef.child("teams").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for(DataSnapshot teamSnapshot : dataSnapshot.getChildren()){
//                        if(teamSnapshot.getKey().toString().equals((teamName.replaceAll("\\s+","")))){
//                            DatabaseReference ref = teamSnapshot.getRef().child("teamMembers");
//                            final Team team = dataSnapshot.getValue(Team.class);
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        } catch (Exception e) {
//            Log.d(TAG, e.toString());
//        }
//        return team[0];
//    }

//    public void setReturnEmployee(User user){
//         this.mUser = user;
//    }

    public void getUser(String userID, IGetUserListener userListener) {
        mGetUserListener = userListener;
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                mGetUserListener.onGetSingleUser(mUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mGetUserListener.onFailedSingleUser();
            }
        });
    }

//    public User getCurrentUser(){
//        return mUser;
//    }

    public void setUserStatus(String userID, String status) {
        mDatabase.child("users").child(userID).child("currentStatus").setValue(status);
    }

    public void updateUser(Map<String, Object> userMap) {
        mDatabase.child("users").child(userMap.get("userID").toString()).updateChildren(userMap);
    }

//    public void setUserNotificationToken(String userID, String token) {
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
//        dbRef.child("users").child(userID).child("notificationToken").setValue(token);
//    }

//    private void updateCurrentUsersList(List<User> currentUsers) {
//        mUserList = currentUsers;
//    }

//    public void setUserLocation(String userID, Location location){
//        try {
//            mDatabase.child("users").child(userID).child("latitude").setValue(location.getLatitude());
//            mDatabase.child("users").child(userID).child("longitude").setValue(location.getLongitude());
//        } catch (Exception e) {
//            Log.d("UserManager", "Error setting longitude and latitude");
//        }
//
//    }

}
