package com.example.android.sickflick.util;

import android.util.Log;

import com.example.android.sickflick.DetailedMovie;
import com.example.android.sickflick.Movie;
import com.example.android.sickflick.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public final class QueryUtils {

    private static String image_base_url;
    private static String image_file_size;

    private QueryUtils() {

    }

    // First query to run when app starts - gets values for the class data members
    public static void setConfigData(String config_url) {
        String jsonResponse;

        try {
            jsonResponse = makeHttpRequest(createURL(config_url));
            extractJSON_config(jsonResponse);
        } catch (IOException e) {
            Log.e(MainActivity.LOG_TAG, "Error closing input stream on config");
        }
    }

    // Query for the list displayed in the main activity
    public static ArrayList<Movie> fetchMovieListData(String query_url) {
        URL url = createURL(query_url);
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(MainActivity.LOG_TAG, "Error closing input stream on list retrieval");
        }
        return extractJSON_movies(jsonResponse);
    }

    // Query for detailed information on one movie used by the DetailedMovies activity
    public static DetailedMovie fetchMovieDetails(String movie_url) {
        URL url = createURL(movie_url);
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(MainActivity.LOG_TAG, "Error closing input stream on movie details retrieval", e);
        }
        return extractJSON_detailedmovie(jsonResponse);
    }

    private static URL createURL(String stringUrl) {
        URL url;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(MainActivity.LOG_TAG, "Error forming URL", e);
            return null;
        }

        return url;
    }

    private static void extractJSON_config(String jsonString) {
        try {
            JSONObject config_data = new JSONObject(jsonString);
            JSONObject image_config = config_data.getJSONObject("images");
            image_base_url = image_config.getString("base_url");
            JSONArray image_sizes = image_config.getJSONArray("poster_sizes");
            image_file_size = image_sizes.getString(3);
        } catch (JSONException e) {
            Log.e(MainActivity.LOG_TAG, "Error parsing JSON config results", e);
        }
    }

    private static ArrayList<Movie> extractJSON_movies(String jsonString) {
        ArrayList<Movie> movies = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                movies.add(i, new Movie(jsonObject.getInt("id"), jsonObject.getString("title"), jsonObject.getDouble("vote_average"), image_base_url + image_file_size + jsonObject.getString("poster_path")));
            }

        } catch (JSONException e) {
            Log.e(MainActivity.LOG_TAG, "Error parsing JSON results", e);
        }

        return movies;
    }

    private static DetailedMovie extractJSON_detailedmovie(String jsonString) {
        StringBuilder stringBuilder = new StringBuilder();
        DetailedMovie movie = null;

        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            JSONArray jsonArray = jsonObject.getJSONArray("genres");
            for (int i = 0; i < jsonArray.length(); i++) {
                if (i == 0) {
                    stringBuilder.append(jsonArray.getJSONObject(i).getString("name"));
                } else {
                    stringBuilder.append(", ").append(jsonArray.getJSONObject(i).getString("name"));
                }
            }
            String genres = stringBuilder.toString();

            movie = new DetailedMovie(jsonObject.getInt("id"), jsonObject.getString("title"),
                    jsonObject.getDouble("vote_average"),
                    image_base_url + image_file_size + jsonObject.getString("poster_path"),
                    jsonObject.getInt("vote_count"),
                    DetailedMovie.formatDate(jsonObject.getString("release_date")),
                    genres,
                    jsonObject.getString("overview"));

        } catch (JSONException e) {
            Log.e(MainActivity.LOG_TAG, "Error parsing JSON results", e);
        }

        return movie;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null)
            return jsonResponse;

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.connect();

            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }

        } catch (IOException e) {
            Log.e(MainActivity.LOG_TAG, "I/O error trying to retrieve web data", e);
            return jsonResponse;
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
            if (inputStream != null)
                inputStream.close();
        }
        if (jsonResponse.isEmpty()) {
            Log.e(MainActivity.LOG_TAG, "JSON came back empty");
            return jsonResponse;
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        }

        return stringBuilder.toString();
    }
}
