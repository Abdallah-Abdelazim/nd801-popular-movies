package com.abdallah.popularmovies.api;


import android.net.Uri;
import android.util.Log;

import com.abdallah.popularmovies.BuildConfig;
import com.abdallah.popularmovies.models.Movie;
import com.abdallah.popularmovies.utils.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
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


    public static final class MoviesResponse {
        public int page;
        public int totalResults;
        public int totalPages;

        @SerializedName("results")
        public List<Movie> movies;
    }

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
            result = NetworkUtil.sendGetRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result != null) {
            // serialize the json to MoviesResponse object
            Gson gson = new Gson();
            MoviesResponse response = gson.fromJson(result, MoviesResponse.class);

            return response.movies;
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
            result = NetworkUtil.sendGetRequest(url);
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