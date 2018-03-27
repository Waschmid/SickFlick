package com.example.android.sickflick.activities;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.sickflick.BuildConfig;
import com.example.android.sickflick.Movie;
import com.example.android.sickflick.R;
import com.example.android.sickflick.adapters.MovieAdapter;
import com.example.android.sickflick.loaders.MovieLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    public static final String LOG_TAG = MainActivity.class.getCanonicalName();
    private static final int MOVIE_LIST_LOADER_ID = 1;

    private String mDateRangePreference;

    private ListView mListView;
    private ProgressBar mProgressBar;
    private TextView mEmptyStateTextView;

    private MovieAdapter mMovieAdapter;


    private LoaderManager.LoaderCallbacks<ArrayList<Movie>> webQueryLoaderListener = new LoaderManager.LoaderCallbacks<ArrayList<Movie>>() {
        @Override
        public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
            String dateRange = mDateRangePreference;

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String upper_bound = tf.format(calendar.getTime());
            String lower_bound = null;

            if (dateRange.equals(getString(R.string.settings_one_week))) {
                calendar.add(Calendar.DATE, -7);
                lower_bound = tf.format((calendar.getTime()));
            } else if (dateRange.equals(getString(R.string.settings_two_weeks))) {
                calendar.add(Calendar.DATE, -14);
                lower_bound = tf.format((calendar.getTime()));
            } else if (dateRange.equals(getString(R.string.settings_four_weeks))) {
                calendar.add(Calendar.DATE, -28);
                lower_bound = tf.format((calendar.getTime()));
            } else if (dateRange.equals(getString(R.string.settings_six_weeks))) {
                calendar.add(Calendar.DATE, -42);
                lower_bound = tf.format((calendar.getTime()));
            }


            Uri baseUri = Uri.parse(getString(R.string.base_url));

            // Building the URL that's used to initialize future query arguments for image path URL and image size
            Uri.Builder uriBuilder_config = baseUri.buildUpon();
            uriBuilder_config.appendPath(getString(R.string.config_path));
            uriBuilder_config.appendQueryParameter("api_key", BuildConfig.ApiKey);

            // Builds the URL that gets the list of movies from TMDB
            Uri.Builder uriBuilder_list = baseUri.buildUpon();
            uriBuilder_list.appendPath(getString(R.string.discover_path));
            uriBuilder_list.appendPath(getString(R.string.movie_path));
            uriBuilder_list.appendQueryParameter("api_key", BuildConfig.ApiKey);
            uriBuilder_list.appendQueryParameter("language", "en-US");
            uriBuilder_list.appendQueryParameter("region", "US");
            uriBuilder_list.appendQueryParameter("sort_by", "vote_count.desc");
            uriBuilder_list.appendQueryParameter("include_adult", "false");
            uriBuilder_list.appendQueryParameter("include_video", "false");
            uriBuilder_list.appendQueryParameter("page", "1");
            uriBuilder_list.appendQueryParameter("primary_release_date.gte", lower_bound);
            uriBuilder_list.appendQueryParameter("primary_release_date.lte", upper_bound);

            return new MovieLoader(MainActivity.this, uriBuilder_config.toString(), uriBuilder_list.toString());
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
            mEmptyStateTextView.setText(getString(R.string.no_movies));

            if (mMovieAdapter == null) {
                mMovieAdapter = new MovieAdapter(MainActivity.this, data);
                mListView.setAdapter(mMovieAdapter);
            } else if (data != null && !data.isEmpty()) {
                mMovieAdapter.clear();
                mMovieAdapter.addAll(data);
            }
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
            mMovieAdapter.clear();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the saved preference for the date range query parameter if it's there
        PreferenceManager.setDefaultValues(this, R.xml.settings_main, false);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        mDateRangePreference = sharedPrefs.getString(getString(R.string.date_range_key), getString(R.string.settings_one_week));

        //Setting up UI
        setContentView(R.layout.activity_main);
        mListView = findViewById(R.id.activity_main_list);
        mProgressBar = findViewById(R.id.activity_main_progress_bar);
        mEmptyStateTextView = findViewById(R.id.activity_main_empty_view);
        mListView.setEmptyView(mEmptyStateTextView);

        // Check internet access, start loader if good to go
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        try {
            networkInfo = cm.getActiveNetworkInfo();
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Null ptr when trying to get network info", e);
        }
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            // Start getting the data
            getLoaderManager().initLoader(MOVIE_LIST_LOADER_ID, null, webQueryLoaderListener).forceLoad();
        } else {
            mEmptyStateTextView.setText(getString(R.string.no_network));
            mProgressBar.setVisibility(View.GONE);
        }

        // On a click, go to detailed movie activity
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                intent.putExtra("tmdb_id", ((Movie) parent.getItemAtPosition(position)).getId());
                startActivity(intent);
            }
        });
    }


    // Whenever the activity is brought back to the front, check the date range preference and reload using new value if it has changed
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String new_preference = sharedPrefs.getString(getString(R.string.date_range_key), getString(R.string.settings_one_week));
        if (!new_preference.equals(mDateRangePreference)) {
            mDateRangePreference = new_preference;
            getLoaderManager().restartLoader(MOVIE_LIST_LOADER_ID, null, webQueryLoaderListener).forceLoad();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.menu_main_watchlist:
                Intent watchlistIntent = new Intent(this, WatchlistActivity.class);
                startActivity(watchlistIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
