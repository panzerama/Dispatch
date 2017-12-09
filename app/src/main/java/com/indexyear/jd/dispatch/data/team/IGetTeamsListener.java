package com.indexyear.jd.dispatch.data.team;

import com.indexyear.jd.dispatch.models.Team;

import java.util.List;

/**
 * Created by karibullard on 12/5/17.
 */
public interface IGetTeamsListener {
    void onGetTeams(List<Team> retrievedTeams);
    void onFailedTeams();
}
