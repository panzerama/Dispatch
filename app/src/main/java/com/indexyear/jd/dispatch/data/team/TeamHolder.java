package com.indexyear.jd.dispatch.data.team;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.indexyear.jd.dispatch.R;

/**
 * Created by kbullard on 12/4/2017.
 */

public class TeamHolder extends RecyclerView.ViewHolder  {

    private TextView teamName;
    private TextView teamStatus;
    private TextView travelTime;
    private Color availableColor;
    private Color unavailableColor;

    public TeamHolder(View itemView) {
        super(itemView);
    }

    private void findViews(View itemView){
        teamName = (TextView) itemView.findViewById(R.id.team_name_text);
        teamStatus = (TextView) itemView.findViewById(R.id.team_status_text);
        travelTime = (TextView) itemView.findViewById(R.id.team_travel_time);
    }
}
