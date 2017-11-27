package com.indexyear.jd.dispatch.data.team;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.indexyear.jd.dispatch.data.user.TeamFirebaseMapper;
import com.indexyear.jd.dispatch.models.Team;
import com.indexyear.jd.dispatch.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jd on 11/16/17.
 */

public class TeamManager {

    private DatabaseReference mDatabase;
    private List<ITeamEventListener> mListeners;
    private List<Team> mCurrentTeamsList;

    public TeamManager() {
        mCurrentTeamsList = new ArrayList<>();
        mListeners = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // keeps a current list of teams in this object
        mDatabase.child("teams").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Team> currentTeams = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Team t = new TeamFirebaseMapper().fromDataSnapshot(snap);
                    currentTeams.add(t);
                }
                updateCurrentTeamsList(currentTeams);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // monitors changes to particular teams
        mDatabase.child("teams").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (mListeners.isEmpty()) { return; }
                for (ITeamEventListener mListener: mListeners) {
                    mListener.onTeamCreated(new TeamFirebaseMapper().fromDataSnapshot(dataSnapshot));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (mListeners.isEmpty()) { return; }
                for (ITeamEventListener mListener: mListeners) {
                    mListener.onTeamUpdated(new TeamFirebaseMapper().fromDataSnapshot(dataSnapshot));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (mListeners.isEmpty()) { return; }
                for (ITeamEventListener mListener: mListeners) {
                    mListener.onTeamRemoved(new TeamFirebaseMapper().fromDataSnapshot(dataSnapshot));
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

    public void addNewListener(ITeamEventListener newListener) { mListeners.add(newListener); }

    public void addEmployeeAndToken(Team team, User updateUser) {
        mDatabase.child("teams").child(team.getTeamID()).child("teamMembers").child(updateUser.getUserID()).updateChildren(updateUser.toMap());
    }

    public List<Team> getCurrentTeamsList() {
        return mCurrentTeamsList;
    }

    private void updateCurrentTeamsList(List<Team> currentList) {
        mCurrentTeamsList = currentList;
    }

}
