package com.indexyear.jd.dispatch.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.indexyear.jd.dispatch.models.User;

public class UserParcel implements Parcelable {

    private User mUser;

    protected UserParcel(Parcel in) {
        String firstName = in.readString();
        String lastName = in.readString();
        String phone = in.readString();
        String currentTeam = in.readString();
        String userID = in.readString();
        String currentRole = in.readString();
        String currentStatus = in.readString();
        float latitude = in.readFloat();
        float longitude = in.readFloat();

        mUser = new User(firstName, lastName, phone, currentTeam, userID, currentRole, currentStatus, latitude, longitude);
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
        dest.writeString(mUser.firstName);
        dest.writeString(mUser.lastName);
        dest.writeString(mUser.phone);
        dest.writeString(mUser.currentTeam);
        dest.writeString(mUser.userID);
        dest.writeString(mUser.currentRole);
        dest.writeString(mUser.currentStatus);
        dest.writeFloat(mUser.latitude);
        dest.writeFloat(mUser.longitude);
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
