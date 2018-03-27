package com.example.android.sickflick.activities;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sickflick.BuildConfig;
import com.example.android.sickflick.DetailedMovie;
import com.example.android.sickflick.data.WatchlistContract;
import com.example.android.sickflick.loaders.DetailedMovieLoader;
import com.example.android.sickflick.R;
import com.example.android.sickflick.data.WatchlistContract.WatchlistEntry;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    private DetailedMovie mMovie;
    private static int mMovieId;
    private Uri mDbUri;

    private LoaderManager.LoaderCallbacks<DetailedMovie> webQueryLoaderListener = new LoaderManager.LoaderCallbacks<DetailedMovie>() {
        @Override
        public Loader<DetailedMovie> onCreateLoader(int id, Bundle args) {

            Uri baseUri = Uri.parse(getString(R.string.base_url));
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendPath(getString(R.string.movie_path));
            uriBuilder.appendPath(Integer.toString(mMovieId));
            uriBuilder.appendQueryParameter("api_key", BuildConfig.ApiKey);
            return new DetailedMovieLoader(MovieDetailsActivity.this, uriBuilder.toString());
        }

        @Override
        public void onLoadFinished(Loader<DetailedMovie> loader, DetailedMovie data) {
            mMovie = data;
            mMovie.setId(mMovieId);
            setUI();
        }

        @Override
        public void onLoaderReset(Loader<DetailedMovie> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<Cursor> dbLoaderListener = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projection = {
                    WatchlistEntry._ID,
                    WatchlistEntry.COLUMN_TMDB_ID,
                    WatchlistEntry.COLUMN_MOVIE_NAME,
                    WatchlistEntry.COLUMN_MOVIE_RATING,
                    WatchlistEntry.COLUMN_MOVIE_POSTER_PATH,
                    WatchlistEntry.COLUMN_MOVIE_VOTE_COUNT,
                    WatchlistEntry.COLUMN_MOVIE_RELEASE_DATE,
                    WatchlistEntry.COLUMN_MOVIE_GENRES,
                    WatchlistEntry.COLUMN_MOVIE_OVERVIEW
            };

            return new CursorLoader(
                    MovieDetailsActivity.this,
                    mDbUri,
                    projection,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data == null || data.getCount() < 1)
                return;

            int id;
            String title, poster_path, release_date, genres, overview;

            if (data.moveToFirst()) {
                id = Integer.parseInt(data.getString(data.getColumnIndex(WatchlistEntry.COLUMN_TMDB_ID)));
                title = data.getString(data.getColumnIndex(WatchlistEntry.COLUMN_MOVIE_NAME));
                poster_path = data.getString(data.getColumnIndex(WatchlistEntry.COLUMN_MOVIE_POSTER_PATH));
                release_date = data.getString(data.getColumnIndex(WatchlistEntry.COLUMN_MOVIE_RELEASE_DATE));
                genres = data.getString(data.getColumnIndex(WatchlistEntry.COLUMN_MOVIE_GENRES));
                overview = data.getString(data.getColumnIndex(WatchlistEntry.COLUMN_MOVIE_OVERVIEW));

                mMovie = new DetailedMovie(id, title, -1, poster_path, -1, release_date, genres, overview);
                setUI();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private static final int DB_LOADER_ID = 2;
    private static final int MOVIE_LOADER_ID = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        mMovieId = i.getIntExtra("tmdb_id", 0);
        mDbUri = i.getData();

        LoaderManager loaderManager = getLoaderManager();

        // Either load from the database, or query TMDB for the movie info
        if (mDbUri != null)
            loaderManager.initLoader(DB_LOADER_ID, null, dbLoaderListener);
        else
            loaderManager.initLoader(MOVIE_LOADER_ID, null, webQueryLoaderListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_details, menu);
        return true;
    }

    // Decide what menu options to display based on whether movie is saved or not, or if it's selected from the watchlist
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mDbUri == null) {
            menu.findItem(R.id.menu_detailed_movie_delete).setVisible(false);
            if (checkIfInDb(mMovieId))
                menu.findItem(R.id.menu_detailed_movie_save).setVisible(false);
            else
                menu.findItem(R.id.menu_detailed_movie_saved).setVisible(false);
        } else {
            menu.findItem(R.id.menu_detailed_movie_save).setVisible(false);
            menu.findItem(R.id.menu_detailed_movie_saved).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_detailed_movie_save:
                saveMovieToWatchlist();
                finish();
                return true;
            case R.id.menu_detailed_movie_delete:
                deleteMoviefromWatchlist();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUI() {
        ImageView poster = findViewById(R.id.details_poster);
        Picasso.with(MovieDetailsActivity.this).load(mMovie.getPosterPath()).into(poster);

        TextView textView = findViewById(R.id.details_title);
        textView.setText(mMovie.getTitle());

        if (((int) mMovie.getRating()) < 0 || mMovie.getVoteCount() < 0) {
            findViewById(R.id.details_rating).setVisibility(View.GONE);
            findViewById(R.id.details_votecount).setVisibility(View.GONE);
        } else {
            textView = findViewById(R.id.details_rating);
            textView.append(" " + Double.toString(mMovie.getRating()));
            textView = findViewById(R.id.details_votecount);
            textView.append(" " + Integer.toString(mMovie.getVoteCount()));
        }

        textView = findViewById(R.id.details_release_date);
        textView.append(" " + mMovie.getReleaseDate());

        textView = findViewById(R.id.details_genre);
        textView.append(mMovie.getGenres());

        textView = findViewById(R.id.details_overview);
        textView.setText(mMovie.getOverview());
    }

    // Called when the save button is pressed, adds the movies info to the watchlist database
    private void saveMovieToWatchlist() {
        ContentValues values = new ContentValues();

        values.put(WatchlistEntry.COLUMN_TMDB_ID, mMovie.getId());
        values.put(WatchlistEntry.COLUMN_MOVIE_NAME, mMovie.getTitle());
        values.put(WatchlistEntry.COLUMN_MOVIE_POSTER_PATH, mMovie.getPosterPath());
        values.put(WatchlistEntry.COLUMN_MOVIE_RATING, mMovie.getRating());
        values.put(WatchlistEntry.COLUMN_MOVIE_VOTE_COUNT, mMovie.getVoteCount());
        values.put(WatchlistEntry.COLUMN_MOVIE_RELEASE_DATE, mMovie.getReleaseDate());
        values.put(WatchlistEntry.COLUMN_MOVIE_GENRES, mMovie.getGenres());
        values.put(WatchlistEntry.COLUMN_MOVIE_OVERVIEW, mMovie.getOverview());

        Uri uri = getContentResolver().insert(WatchlistEntry.CONTENT_URI, values);
        if (uri == null)
            Toast.makeText(this, "Error saving movie", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Movie saved to watchlist", Toast.LENGTH_SHORT).show();
    }

    // Called when the delete button is pressed, deletes a movie's row from the watchlist database
    private void deleteMoviefromWatchlist() {
        int success = getContentResolver().delete(mDbUri, null, null);

        if (success == 0) {
            Toast.makeText(this, "Error deleting movie", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Movie deleted successfully", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method used to check if a movie is already on the watchlist
    private boolean checkIfInDb(int tmdb_id) {
        String[] projection = {
                WatchlistEntry._ID,
                WatchlistEntry.COLUMN_TMDB_ID,
        };
        String id = (Integer.toString(mMovieId));
        Cursor c = getContentResolver().query(WatchlistEntry.CONTENT_URI, projection, WatchlistEntry.COLUMN_TMDB_ID + " = " + id, null, null);
        if (c.getCount() == 0) {
            c.close();
            return false;
        }
        c.close();
        return true;
    }
}