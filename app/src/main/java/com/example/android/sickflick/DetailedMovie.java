package com.example.android.sickflick;

public class DetailedMovie extends Movie {

    private int mVoteCount;
    private String mReleaseDate;
    private String mGenres;
    private String mOverview;

    public DetailedMovie(int id, String title, double rating, String posterPath, int voteCount, String releaseDate, String genres, String overview) {
        mId = id;
        mTitle = title;
        mRating = rating;
        mPosterPath = posterPath;
        mVoteCount = voteCount;
        mReleaseDate = releaseDate;
        mGenres = genres;
        mOverview = overview;
    }

    public int getVoteCount() {
        return mVoteCount;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getGenres() {
        return mGenres;
    }

    public String getOverview() {
        return mOverview;
    }

    public static String formatDate(String toFormat) {
        if (toFormat.matches("^\\\\d{2}/\\\\d{2}/\\\\d{4}$"))
            return toFormat;
        else {
            String[] dates = toFormat.split("-");
            String formatted_date = dates[1].concat("/").concat(dates[2]).concat("/").concat(dates[0]);
            return formatted_date;
        }
    }
}
