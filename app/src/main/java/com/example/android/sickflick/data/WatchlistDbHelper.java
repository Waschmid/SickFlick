package com.example.android.sickflick.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.sickflick.data.WatchlistContract.WatchlistEntry;

public class WatchlistDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "watchlist.db";

    public WatchlistDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_WATCHLIST_TABLE = "CREATE TABLE " + WatchlistEntry.TABLE_NAME + " ("
                + WatchlistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WatchlistEntry.COLUMN_TMDB_ID + " INTEGER NOT NULL, "
                + WatchlistEntry.COLUMN_MOVIE_NAME + " TEXT, "
                + WatchlistEntry.COLUMN_MOVIE_RATING + " REAL, "
                + WatchlistEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT, "
                + WatchlistEntry.COLUMN_MOVIE_VOTE_COUNT + " INTEGER, "
                + WatchlistEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT, "
                + WatchlistEntry.COLUMN_MOVIE_GENRES + " TEXT, "
                + WatchlistEntry.COLUMN_MOVIE_OVERVIEW + " TEXT);";
        db.execSQL(SQL_CREATE_WATCHLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
