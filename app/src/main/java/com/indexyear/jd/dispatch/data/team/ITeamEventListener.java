package com.indexyear.jd.dispatch.data.team;

import com.indexyear.jd.dispatch.models.Team;

/**
 * Created by jd on 11/18/17.
 */

public interface ITeamEventListener {
    void onTeamCreated(Team newTeam);
    void onTeamRemoved(Team removedTeam);
    void onTeamUpdated(Team updatedTeam);
}
