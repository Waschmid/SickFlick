package com.example.android.sickflick.data;

import android.net.Uri;
import android.provider.BaseColumns;

// Database setup
public final class WatchlistContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.sickflick";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WATCHLIST = "watchlist";

    private WatchlistContract() {}


    public static class WatchlistEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WATCHLIST);

        public static final String TABLE_NAME = "watchlist";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TMDB_ID = "tmdb_id";
        public static final String COLUMN_MOVIE_NAME = "name";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";
        public static final String COLUMN_MOVIE_VOTE_COUNT = "vote_count";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_GENRES = "genres";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
    }
}

