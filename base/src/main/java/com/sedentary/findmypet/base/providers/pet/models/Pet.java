package com.sedentary.findmypet.base.providers.pet.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rodrigo on 24/02/15.
 */
public class Pet implements Parcelable {
    public String name;
    public String age;

    public Pet() { }

    public Pet(Parcel in) {
        name = in.readString();
        age = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(age);
    }

    /**
     *
     */
    public static final Creator<Pet> CREATOR = new Creator<Pet>() {
        @Override
        public Pet createFromParcel(Parcel in) {
            return new Pet(in);
        }

        @Override
        public Pet[] newArray(int size) {
            return new Pet[size];
        }
    };
}