package com.indexyear.jd.dispatch.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.indexyear.jd.dispatch.models.Crisis;

public class CrisisParcel implements Parcelable {

    private Crisis mCrisis;

    CrisisParcel(Parcel input) {
        String crisisID = input.readString();
        String crisisAddress = input.readString();
        String teamName = input.readString();
        String status = input.readString();

        mCrisis = new Crisis(crisisID, crisisAddress, teamName, status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCrisis.getCrisisID());
        dest.writeString(mCrisis.getCrisisAddress());
        dest.writeString(mCrisis.getTeamName());
        dest.writeString(mCrisis.getStatus());
    }

    public static final Creator<CrisisParcel> CREATOR = new Creator<CrisisParcel>(){
        public CrisisParcel createFromParcel(Parcel in) {
            return new CrisisParcel(in);
        }

        public CrisisParcel[] newArray(int size) {
            return new CrisisParcel[size];
        }
    };

    public Crisis getCrisis() {
        return mCrisis;

    }

}
