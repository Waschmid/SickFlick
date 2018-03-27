package com.example.android.sickflick.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.sickflick.DetailedMovie;
import com.example.android.sickflick.util.QueryUtils;

public class DetailedMovieLoader extends AsyncTaskLoader<DetailedMovie> {

    private String mMovieUrl;

    public DetailedMovieLoader(Context context, String movie_url) {
        super(context);
        mMovieUrl = movie_url;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public DetailedMovie loadInBackground() {
        if (mMovieUrl == null)
            return null;
        return QueryUtils.fetchMovieDetails(mMovieUrl);
    }
}
