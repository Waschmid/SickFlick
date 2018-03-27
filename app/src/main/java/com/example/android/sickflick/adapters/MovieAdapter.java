package com.example.android.sickflick.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sickflick.Movie;
import com.example.android.sickflick.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Context context, ArrayList<Movie> movies){
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        ImageView poster = convertView.findViewById(R.id.list_item_poster);
        TextView title = convertView.findViewById(R.id.list_item_title);
        TextView rating = convertView.findViewById(R.id.list_item_rating);

        Movie currentMovie = getItem(position);

        title.setText(currentMovie.getTitle());
        rating.setText(String.format(Locale.US,"%.1f", currentMovie.getRating()));
        Picasso.with(this.getContext()).load(currentMovie.getPosterPath()).into(poster);

        return convertView;
    }
}
