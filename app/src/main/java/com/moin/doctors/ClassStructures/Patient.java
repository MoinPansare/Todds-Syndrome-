package com.moin.doctors.ClassStructures;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by macpro on 9/15/16.
 */
public class Patient extends RealmObject implements Parcelable {
    public int id;
    public String patientName;
    public float result;

    public Patient(){

    }

    protected Patient(Parcel in) {
        id = in.readInt();
        patientName = in.readString();
        result = in.readFloat();
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(patientName);
        dest.writeFloat(result);
    }
}
