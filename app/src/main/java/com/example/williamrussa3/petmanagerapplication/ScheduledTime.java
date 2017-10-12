package com.example.williamrussa3.petmanagerapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ScheduledTime implements Parcelable {
    private Date _date;

    public ScheduledTime(Date newDate){
        _date = newDate;
    }

    protected ScheduledTime(Parcel in) {
        _date = new Date(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_date.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScheduledTime> CREATOR = new Creator<ScheduledTime>() {
        @Override
        public ScheduledTime createFromParcel(Parcel in) {
            return new ScheduledTime(in);
        }

        @Override
        public ScheduledTime[] newArray(int size) {
            return new ScheduledTime[size];
        }
    };

    public Date GetDate(){
        return _date;
    }


}
