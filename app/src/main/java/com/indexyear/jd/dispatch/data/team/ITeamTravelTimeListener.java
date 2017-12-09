package com.indexyear.jd.dispatch.data.team;

import com.indexyear.jd.dispatch.models.Crisis;

import java.util.Map;

/**
 * Created by jd on 12/9/17.
 */

public interface ITeamTravelTimeListener {
    void onSuccess(Crisis source, Map<String, Integer> travelTimes);
    void onFailure();
}
