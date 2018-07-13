package com.abdallah.popularmovies.api;


import android.content.Context;
import android.net.Uri;

import com.abdallah.popularmovies.BuildConfig;
import com.abdallah.popularmovies.utils.network.RequestQueueSingleton;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public final class TmdbServices {

    private static final String TAG = TmdbServices.class.getSimpleName();

    private final static String API_KEY = BuildConfig.TMDB_API_KEY;

    private final static String BASE_URL = "https://api.themoviedb.org/3";
    private final static String MOST_POPULAR_MOVIES_PATH = "movie/popular";
    private final static String HIGHEST_RATED_MOVIES_PATH = "movie/top_rated";
    private final static String MOVIE_DETAILS_PATH = "movie";
    private final static String MOVIE_VIDEOS_PATH = "videos";
    private final static String MOVIE_REVIEWS_PATH = "reviews";

    private final static String API_KEY_PARAM = "api_key";
    private final static String PAGE_PARAM = "page";


    public static final int SORT_MOVIES_BY_POPULARITY = 1;
    public static final int SORT_MOVIES_BY_RATING = 2;

    /**
     * API JSON response keys constants.
     */
    public static final class ResponseKeys {

        public static final String RESULTS = "results";

    }


    private TmdbServices() {/* prevent instantiation */};

    public static void requestMovies(Context ctx, int moviesSortingMethod, int page
            , Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {

        String path;
        switch (moviesSortingMethod) {
            case SORT_MOVIES_BY_POPULARITY:
                path = MOST_POPULAR_MOVIES_PATH;
                break;
            case SORT_MOVIES_BY_RATING:
                path = HIGHEST_RATED_MOVIES_PATH;
                break;
            default:
                throw new IllegalArgumentException("Undefined sorting method");
        }

        String url = Uri.parse(BASE_URL)
                .buildUpon()
                .appendEncodedPath(path)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(page))
                .build()
                .toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null
                , onSuccess, onError);
        request.setTag(ctx);
        RequestQueueSingleton.getInstance(ctx).addToRequestQueue(request);
    }

    public static void requestMovieDetails(Context ctx, long movieId
            , Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        String url = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(MOVIE_DETAILS_PATH)
                .appendEncodedPath(Long.toString(movieId))
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build()
                .toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null
                , onSuccess, onError);
        request.setTag(ctx);
        RequestQueueSingleton.getInstance(ctx).addToRequestQueue(request);
    }

    public static void requestMovieVideos(Context ctx, long movieId
            , Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        String url = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(MOVIE_DETAILS_PATH)
                .appendEncodedPath(Long.toString(movieId))
                .appendEncodedPath(MOVIE_VIDEOS_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build()
                .toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null
                , onSuccess, onError);
        request.setTag(ctx);
        RequestQueueSingleton.getInstance(ctx).addToRequestQueue(request);
    }

    public static void requestMovieReviews(Context ctx, long movieId
            , Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        String url = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(MOVIE_DETAILS_PATH)
                .appendEncodedPath(Long.toString(movieId))
                .appendEncodedPath(MOVIE_REVIEWS_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build()
                .toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null
                , onSuccess, onError);
        request.setTag(ctx);
        RequestQueueSingleton.getInstance(ctx).addToRequestQueue(request);
    }

    /**
     * Cancels all requests associated with the passed context.
     * @param ctx
     */
    public static void cancelOngoingRequests(Context ctx) {
        RequestQueueSingleton.getInstance(ctx).getRequestQueue().cancelAll(ctx);
    }

}