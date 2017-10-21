package com.example.williamrussa3.petmanagerapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

//Class ScheduledTime implements the Parcelable interface to allow transfer between
// activities as part of the Pet class
public class ScheduledTime implements Parcelable {

    //Date object used to hold the scheduled time
    private Date _date;

    //Getter for date object

    public Date GetDate(){
        return _date;
    }


    //Constructor for normal initialisation

    public ScheduledTime(Date newDate){
        _date = newDate;
    }

    //Constructor for class when instantiated using parce. Read in data object as a long

    protected ScheduledTime(Parcel in) {
        _date = new Date(in.readLong());
    }


    //Write data to parcel. Write date as a long
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_date.getTime());
    }

    //Must be overridden for classes implementing Parcelable, not used
    @Override
    public int describeContents() {
        return 0;
    }

    //Parcelable CREATOR

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

}
