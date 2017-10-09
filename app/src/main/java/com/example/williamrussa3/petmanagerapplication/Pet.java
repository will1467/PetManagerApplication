package com.example.williamrussa3.petmanagerapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Pet implements Parcelable {

    private String _name;
    private String _breed;


    public Pet(Parcel in){
        _name = in.readString();
        _breed = in.readString();
    }

    public Pet(String newName, String newBreed){
        _name = newName;
        _breed = newBreed;
    }

    public String GetName(){
        return _name;
    }

    public String GetBreed(){
        return _breed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_name);
        dest.writeString(_breed);
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
