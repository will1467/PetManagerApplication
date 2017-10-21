package com.example.williamrussa3.petmanagerapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

/*Pet Class hold all pet related data. Field are : name, breed, weight, type, feeding Dates and Walking dates
   This class implements Parcelable, which allows the object to be passed via Intents to other activities
 */

public class Pet implements Parcelable {

    private String _name;
    private String _breed;
    private double _weight;
    private String _type;

    /*feedingDates and walkingDates
        are ArrayLists of ScheduledTime objects
        A scheduled time object contains a Date object.
        The scheduled time object exists to allow the whole class to be Parcelable because
        the Java built-in Date object cannot be made parcelable
     */
    private ArrayList<ScheduledTime> _feedingDates;
    private ArrayList<ScheduledTime> _walkingDates;

    //Constructor for class when instantiated using parcel

    public Pet(Parcel in) {
        _name = in.readString();
        _breed = in.readString();
        _weight = in.readDouble();
        _type = in.readString();
        _feedingDates = in.createTypedArrayList(ScheduledTime.CREATOR);
        _walkingDates = in.createTypedArrayList(ScheduledTime.CREATOR);
    }

    //Constructor for normal initialisation

    public Pet(String newName, String newBreed, double newWeight, String newType) {
        _name = newName;
        _breed = newBreed;
        _weight = newWeight;
        _type = newType;
        _feedingDates = new ArrayList<ScheduledTime>(0);
        _walkingDates = new ArrayList<ScheduledTime>(0);
    }

    //Getters for member variables

    public String GetName() {
        return _name;
    }

    public String GetBreed() {
        return _breed;
    }

    public double GetWeight() {
        return _weight;
    }

    public String GetType() {
        return _type;
    }

    //Methods for accessing the feeding times and the walking times arrayLists

    // Get ScheduledTime object using index
    public ScheduledTime GetFeedingTime(int index) {
        return _feedingDates.get(index);
    }

    //Get total size of feeding dates arrayList
    public int GetFeedingTimeArrayLength() {
        return _feedingDates.size();
    }

    //Remove a feeding time using index
    public void RemoveFeedingTime(int indexToBeDeleted) {
        _feedingDates.remove(indexToBeDeleted);
    }

    //Create new scheduled time object using Date object and add to feedingDates arrayList
    public int SetFeedingTime(Date date) {
        ScheduledTime scheduledTime = new ScheduledTime(date);
        _feedingDates.add(scheduledTime);

        //Return index of newly added feeding time
        return _feedingDates.indexOf(scheduledTime);

    }

    // Get ScheduledTime object using index
    public ScheduledTime GetWalkingTime(int index) {
        return _walkingDates.get(index);
    }

    //Get total size of walking dates arrayList

    public int GetWalkingTimeArrayLength() {
        return _walkingDates.size();
    }

    //Remove a walking time using index
    public void RemoveWalkingTime(int indexToBeDeleted) {
        _walkingDates.remove(indexToBeDeleted);
    }

    //Create new scheduled time object using Date object and add to feedingDates arrayList
    public int SetWalkingTime(Date date) {
        ScheduledTime scheduledTime = new ScheduledTime(date);
        _walkingDates.add(scheduledTime);
        return _walkingDates.indexOf(scheduledTime);
    }


    //Must be overridden for classes implementing Parcelable, not used

    @Override
    public int describeContents() {
        return 0;
    }

    //Write data to parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_name);
        dest.writeString(_breed);
        dest.writeDouble(_weight);
        dest.writeString(_type);
        dest.writeTypedList(_feedingDates);
        dest.writeTypedList(_walkingDates);
    }

    //Parcelable CREATOR
    public static final Parcelable.Creator<Pet> CREATOR = new Parcelable.Creator<Pet>() {
        public Pet createFromParcel(Parcel in) {
            return new Pet(in);
        }

        public Pet[] newArray(int size) {
            return new Pet[size];
        }
    };
}
