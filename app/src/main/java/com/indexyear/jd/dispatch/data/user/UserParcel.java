package com.indexyear.jd.dispatch.data.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.indexyear.jd.dispatch.models.User;

public class UserParcel implements Parcelable {

    private User mUser;

    protected UserParcel(Parcel in) {
        String currentTeam = in.readString();
        String userID = in.readString();
        String currentRole = in.readString();
        String currentStatus = in.readString();
        String email = in.readString();
        String token = in.readString();
        float latitude = in.readFloat();
        float longitude = in.readFloat();

        mUser = new User(currentTeam, userID, currentRole, currentStatus, email, token, latitude, longitude);
    }

    public UserParcel(User inUser){
        mUser = inUser;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUser.getCurrentTeam());
        dest.writeString(mUser.getUserID());
        dest.writeString(mUser.getCurrentRole());
        dest.writeString(mUser.getCurrentStatus());
        dest.writeString(mUser.getEmail());
        dest.writeString(mUser.getToken());
        dest.writeFloat(mUser.getLatitude());
        dest.writeFloat(mUser.getLongitude());
    }

    public static final Creator<UserParcel> CREATOR = new Creator<UserParcel>() {
        @Override
        public UserParcel createFromParcel(Parcel in) {
            return new UserParcel(in);
        }

        @Override
        public UserParcel[] newArray(int size) {
            return new UserParcel[size];
        }
    };
}
