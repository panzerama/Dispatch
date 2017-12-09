package com.indexyear.jd.dispatch.data.team;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.indexyear.jd.dispatch.models.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jd on 11/16/17.
 */

public class TeamManager {

    // TODO: 12/9/17 JD we can remove ITeamEventListener
    private List<ITeamEventListener> mListeners;
    private List<Team> mCurrentTeamsList;
    private IGetTeamsListener mGetTeamsListener;
    private DatabaseReference dbRoot;

    public TeamManager() {
        mCurrentTeamsList = new ArrayList<>();
        mListeners = new ArrayList<>();
        dbRoot = FirebaseDatabase.getInstance().getReference();

        // keeps a current list of teams in this object
        dbRoot.child("teams").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Team> currentTeams = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Team t = snap.getValue(Team.class);
                    currentTeams.add(t);
                }
                updateCurrentTeamsList(currentTeams);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // monitors changes to particular teams
        dbRoot.child("teams").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (mListeners.isEmpty()) { return; }
                for (ITeamEventListener mListener: mListeners) {
                    mListener.onTeamCreated(dataSnapshot.getValue(Team.class));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (mListeners.isEmpty()) { return; }
                for (ITeamEventListener mListener: mListeners) {
                    mListener.onTeamUpdated(dataSnapshot.getValue(Team.class));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (mListeners.isEmpty()) { return; }
                for (ITeamEventListener mListener: mListeners) {
                    mListener.onTeamRemoved(dataSnapshot.getValue(Team.class));
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

//    public List<Team> getCurrentTeamsList() {
//        return mCurrentTeamsList;
//    }

    private void updateCurrentTeamsList(List<Team> currentList) {
        mCurrentTeamsList = currentList;
    }

    public void getTeams(IGetTeamsListener teamsListener){
        mGetTeamsListener = teamsListener;
        dbRoot.child("teams").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Team> currentTeams = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Team t = snap.getValue(Team.class);
                    currentTeams.add(t);
                }
                mGetTeamsListener.onGetTeams(currentTeams);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mGetTeamsListener.onFailedTeams();
            }
        });
    }
}
