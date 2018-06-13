package com.abdallah.popularmovies.api;


import android.net.Uri;
import android.util.Log;

import com.abdallah.popularmovies.BuildConfig;
import com.abdallah.popularmovies.models.Movie;
import com.abdallah.popularmovies.utils.NetworkUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public final class TMDBServices {

    private static final String TAG = TMDBServices.class.getSimpleName();

    private final static String API_KEY = BuildConfig.TMDB_API_KEY;

    private final static String BASE_URL = "https://api.themoviedb.org/3";
    private final static String MOST_POPULAR_MOVIES_PATH = "movie/popular";
    private final static String HIGHEST_RATED_MOVIES_PATH = "movie/top_rated";
    private final static String MOVIE_DETAILS_PATH = "movie";

    private final static String API_KEY_PARAM = "api_key";
    private final static String PAGE_PARAM = "page";


    public static final int SORT_MOVIES_BY_POPULARITY = 1;
    public static final int SORT_MOVIES_BY_RATING = 2;

    public static final String IMG_BASE_URL = "http://image.tmdb.org/t/p/w185";


    public static List<Movie> getMovies(int moviesSortingMethod, int page) {

        String path;
        switch (moviesSortingMethod) {
            case SORT_MOVIES_BY_POPULARITY:
                path = MOST_POPULAR_MOVIES_PATH;
                break;
            case SORT_MOVIES_BY_RATING:
                path = HIGHEST_RATED_MOVIES_PATH;
                break;
            default:
                Log.d(TAG, "getMovies(): Undefined sorting method");
                return null;
        }

        String url = Uri.parse(BASE_URL)
                .buildUpon()
                .appendEncodedPath(path)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(page))
                .build()
                .toString();

        String result = null;
        try {
            result = NetworkUtils.sendGetRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result != null) {
            try {
                JSONObject resultJsonObject = new JSONObject(result);
                // serialize the json to Movies array
                Gson gson = new Gson();
                Movie [] movies = gson.fromJson(resultJsonObject.getJSONArray("results").toString()
                        , Movie[].class);

                return Arrays.asList(movies);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Movie getMovieDetails(long movieId) {
        String url = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(MOVIE_DETAILS_PATH)
                .appendEncodedPath(Long.toString(movieId))
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build()
                .toString();

        String result = null;
        try {
            result = NetworkUtils.sendGetRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result != null) {
            // serialize the json to Movie object
            Gson gson = new Gson();
            Movie movie = gson.fromJson(result, Movie.class);
            return movie;
        }

        return null;
    }

}