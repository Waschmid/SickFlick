package com.example.android.sickflick.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.sickflick.Movie;
import com.example.android.sickflick.util.QueryUtils;

import java.util.ArrayList;

public class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private String mConfigUrl;
    private String mDiscoverUrl;

    public MovieLoader(Context context, String config_url, String discover_url) {
        super(context);
        mConfigUrl = config_url;
        mDiscoverUrl = discover_url;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        if (mConfigUrl == null || mDiscoverUrl == null)
            return null;
        QueryUtils.setConfigData(mConfigUrl);
        return QueryUtils.fetchMovieListData(mDiscoverUrl);
    }
}
