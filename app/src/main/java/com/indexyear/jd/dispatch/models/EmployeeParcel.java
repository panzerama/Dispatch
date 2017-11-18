package com.indexyear.jd.dispatch.models;

import android.os.Parcel;
import android.os.Parcelable;

public class EmployeeParcel implements Parcelable {

    private User mUser;

    protected EmployeeParcel(Parcel in) {
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

    public static final Creator<EmployeeParcel> CREATOR = new Creator<EmployeeParcel>() {
        @Override
        public EmployeeParcel createFromParcel(Parcel in) {
            return new EmployeeParcel(in);
        }

        @Override
        public EmployeeParcel[] newArray(int size) {
            return new EmployeeParcel[size];
        }
    };
}
