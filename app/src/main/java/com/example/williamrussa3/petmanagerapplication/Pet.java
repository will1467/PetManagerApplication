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
    private double _weight;
    private ArrayList<ScheduledTime> _feedingDates;
    private ArrayList<ScheduledTime> _walkingDates;


    public Pet(Parcel in) {
        _name = in.readString();
        _breed = in.readString();
        _weight = in.readDouble();
        _feedingDates = in.createTypedArrayList(ScheduledTime.CREATOR);
        _walkingDates = in.createTypedArrayList(ScheduledTime.CREATOR);
    }

    public Pet(String newName, String newBreed , double newWeight) {
        _name = newName;
        _breed = newBreed;
        _weight = newWeight;
        _feedingDates = new ArrayList<ScheduledTime>(0);
        _walkingDates = new ArrayList<ScheduledTime>(0);
    }

    public String GetName() {
        return _name;
    }

    public String GetBreed() {
        return _breed;
    }

    public double GetWeight() { return _weight; }

    public ScheduledTime GetFeedingTime(int index) {
        return _feedingDates.get(index);
    }

    public int GetFeedingTimeArrayLength() {
        return _feedingDates.size();
    }

    public void RemoveFeedingTime(int indexToBeDeleted) {
        _feedingDates.remove(indexToBeDeleted);
    }

    public int SetFeedingTime(Date date) {
        ScheduledTime scheduledTime = new ScheduledTime(date);
        _feedingDates.add(scheduledTime);
        return _feedingDates.indexOf(scheduledTime);

    }

    public ScheduledTime GetWalkingTime(int index) {
        return _walkingDates.get(index);
    }

    public int SetWalkingTime(Date date) {
        ScheduledTime scheduledTime = new ScheduledTime(date);
        _walkingDates.add(scheduledTime);
        return _walkingDates.indexOf(scheduledTime);
    }

    public void RemoveWalkingTime(int indexToBeDeleted) {
        _walkingDates.remove(indexToBeDeleted);
    }


    public int GetWalkingTimeArrayLength() {
        return _walkingDates.size();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_name);
        dest.writeString(_breed);
        dest.writeDouble(_weight);
        dest.writeTypedList(_feedingDates);
        dest.writeTypedList(_walkingDates);
    }

    public static final Parcelable.Creator<Pet> CREATOR = new Parcelable.Creator<Pet>() {
        public Pet createFromParcel(Parcel in) {
            return new Pet(in);
        }

        public Pet[] newArray(int size) {
            return new Pet[size];
        }
    };
}
