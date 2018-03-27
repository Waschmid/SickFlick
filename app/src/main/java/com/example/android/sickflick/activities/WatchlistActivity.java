package com.example.android.sickflick.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.sickflick.DetailedMovie;
import com.example.android.sickflick.Movie;
import com.example.android.sickflick.R;
import com.example.android.sickflick.adapters.MovieAdapter;
import com.example.android.sickflick.adapters.MovieCursorAdapter;
import com.example.android.sickflick.data.WatchlistContract.WatchlistEntry;

public class WatchlistActivity extends AppCompatActivity {

    private static final int MOVIE_LIST_LOADER_ID = 4;

    private ProgressBar mProgressBar;
    private MovieCursorAdapter mMovieCursorAdapter;

    private LoaderManager.LoaderCallbacks<Cursor> dbLoaderListener = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projection = {
                    WatchlistEntry._ID,
                    WatchlistEntry.COLUMN_MOVIE_NAME,
                    WatchlistEntry.COLUMN_MOVIE_RATING,
                    WatchlistEntry.COLUMN_MOVIE_POSTER_PATH,
            };

            return new CursorLoader(
                    WatchlistActivity.this,
                    WatchlistEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mMovieCursorAdapter.swapCursor(data);
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mMovieCursorAdapter.swapCursor(null);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting up UI
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.activity_watchlist_title);
        setContentView(R.layout.activity_watchlist);
        ListView listView = findViewById(R.id.activity_watchlist_list);
        mProgressBar = findViewById(R.id.activity_watchlist_progress_bar);

        mMovieCursorAdapter = new MovieCursorAdapter(this, null);
        listView.setAdapter(mMovieCursorAdapter);

        getLoaderManager().initLoader(MOVIE_LIST_LOADER_ID, null, dbLoaderListener).forceLoad();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(WatchlistActivity.this, MovieDetailsActivity.class);
                intent.setData(Uri.withAppendedPath(WatchlistEntry.CONTENT_URI, Long.toString(id)));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
