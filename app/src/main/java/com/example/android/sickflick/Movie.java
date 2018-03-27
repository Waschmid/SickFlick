package com.example.android.sickflick;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie {

    protected int mId;
    protected String mTitle;
    protected double mRating;
    protected String mPosterPath;


    public Movie() {
    }

    public Movie(int id, String title, double rating, String posterPath) {
        mId = id;
        mTitle = title;
        mRating = rating;
        mPosterPath = posterPath;
    }

    public String getTitle() {
        return mTitle;
    }

    public double getRating() {
        return mRating;
    }

    public void setId(int new_id) {
        mId = new_id;
    }

    public int getId() {
        return mId;
    }

    public String getPosterPath() {
        return mPosterPath;
    }
}
