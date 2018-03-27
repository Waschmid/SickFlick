package com.example.android.sickflick.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sickflick.R;
import com.example.android.sickflick.data.WatchlistContract.WatchlistEntry;
import com.squareup.picasso.Picasso;

public class MovieCursorAdapter extends CursorAdapter {

    public MovieCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        view.findViewById(R.id.list_item_rating).setVisibility(View.GONE);
        
        TextView title_view = view.findViewById(R.id.list_item_title);
        ImageView poster_view = view.findViewById(R.id.list_item_poster);

        String title = cursor.getString(cursor.getColumnIndex(WatchlistEntry.COLUMN_MOVIE_NAME));
        String poster_path = cursor.getString(cursor.getColumnIndex(WatchlistEntry.COLUMN_MOVIE_POSTER_PATH));

        title_view.setText(title);
        Picasso.with(context).load(poster_path).into(poster_view);
    }
}
