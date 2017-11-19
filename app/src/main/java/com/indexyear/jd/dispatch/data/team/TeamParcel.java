package com.indexyear.jd.dispatch.data.team;

import android.os.Parcel;
import android.os.Parcelable;

import com.indexyear.jd.dispatch.models.Team;
import com.indexyear.jd.dispatch.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jd on 11/18/17.
 */

public class TeamParcel implements Parcelable {

    private Team mTeam;

    /*
    public String teamName;
    public List<User> teamMembers;
    public float latitude;
    public float longitude;

     */

    protected TeamParcel(Parcel in) {
        String teamName = in.readString();
        List<User> teamMembers = new ArrayList<>();
        in.readList(teamMembers, List.class.getClassLoader());
        float latitude = in.readFloat();
        float longitude = in.readFloat();

        mTeam = new Team(teamName, teamMembers, latitude, longitude);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTeam.teamName);
        dest.writeList(mTeam.teamMembers);
        dest.writeFloat(mTeam.latitude);
        dest.writeFloat(mTeam.longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TeamParcel> CREATOR = new Creator<TeamParcel>() {
        @Override
        public TeamParcel createFromParcel(Parcel in) {
            return new TeamParcel(in);
        }

        @Override
        public TeamParcel[] newArray(int size) {
            return new TeamParcel[size];
        }
    };
}
