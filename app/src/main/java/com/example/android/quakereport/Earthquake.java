package com.example.android.quakereport;

import java.text.SimpleDateFormat;


public class Earthquake {
    final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

    private double mMagnitude;
    private String mQuakeCity;
    private long  mQuakeTime;
    private String mQuakeTimeString;


    public Earthquake(String vCity, double vMagnitude, long quaketime)
    {
        mQuakeCity = vCity;
        mMagnitude = vMagnitude;
        mQuakeTime = quaketime;
    }

    public double getMagnitude() {
        return mMagnitude;
    }
    public String getQuakeCity() {
        return mQuakeCity;
    }
    public long getQuakeTime(){
        return mQuakeTime;
    }
    public String getQuakeTimeString(){
        return  format1.format(mQuakeTime);
    }

}