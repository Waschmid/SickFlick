package com.example.android.sickflick.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.sickflick.data.WatchlistContract.WatchlistEntry;

public class WatchlistProvider extends ContentProvider {

    private static final String LOG_TAG = WatchlistProvider.class.getSimpleName();
    private WatchlistDbHelper mDbHelper;

    private static final int WATCHLIST = 0;
    private static final int MOVIE_TMDB_ID = 1;

    // Creation and initialization of UriMatcher
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(WatchlistContract.CONTENT_AUTHORITY, WatchlistContract.PATH_WATCHLIST, WATCHLIST);
        sUriMatcher.addURI(WatchlistContract.CONTENT_AUTHORITY, WatchlistContract.PATH_WATCHLIST + "/#", MOVIE_TMDB_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new WatchlistDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case WATCHLIST:
                cursor = db.query(WatchlistEntry.TABLE_NAME, projection, selection, null, null, null, sortOrder);
                break;
            case MOVIE_TMDB_ID:
                selection = WatchlistContract.WatchlistEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(WatchlistEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unkown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);

        if (match == WATCHLIST) {
            return insertMovie(uri, values);
        } else {
            throw new IllegalArgumentException("Could not insert into : " + uri);
        }
    }

    private Uri insertMovie(Uri uri, ContentValues values) {
        //Making sure tmdb_id is unique so there are no repeats on the watchlist

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(WatchlistEntry.TABLE_NAME, null, values);

        if (id < 0) {
            Log.e(LOG_TAG, "Could not insert row : " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);

        if (match == MOVIE_TMDB_ID) {
            return deleteMovie(uri, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Could not delete : " + uri);
        }
    }

    private int deleteMovie(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        selection = WatchlistEntry._ID + "=?";
        selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
        rowsDeleted = db.delete(WatchlistEntry.TABLE_NAME, selection, selectionArgs);

        if (rowsDeleted > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    // Not used in the app so left blank (for now)
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
