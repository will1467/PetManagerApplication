package com.example.williamrussa3.petmanagerapplication;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class Pet implements Parcelable {

    private String _name;
    private String _breed;
    private ArrayList<ScheduledTime> _feedingDates;


    public Pet(Parcel in){
        _name = in.readString();
        _breed = in.readString();
        _feedingDates = in.createTypedArrayList(ScheduledTime.CREATOR);
    }

    public Pet(String newName, String newBreed){
        _name = newName;
        _breed = newBreed;
        _feedingDates = new ArrayList<ScheduledTime>(0);
    }

    public String GetName(){
        return _name;
    }

    public String GetBreed(){
        return _breed;
    }

    public ScheduledTime GetFeedingTime(int index){
        return _feedingDates.get(index);
    }

    public int GetFeedingTimeLength(){
        return _feedingDates.size();
    }

    public void SetFeedingTime(Date date) {
        ScheduledTime scheduledTime = new ScheduledTime(date);
        _feedingDates.add(scheduledTime);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_name);
        dest.writeString(_breed);
        dest.writeTypedList(_feedingDates);
    }

    public static final Parcelable.Creator<Pet> CREATOR = new Parcelable.Creator<Pet>(){
        public Pet createFromParcel(Parcel in){
            return new Pet(in);
        }

        public Pet[] newArray(int size){
            return new Pet[size];
        }
    };
}
