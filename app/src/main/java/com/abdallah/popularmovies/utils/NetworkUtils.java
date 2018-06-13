package com.abdallah.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Common network utilities using OkHttp
 */
public final class NetworkUtils {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    /**
     * Makes a GET request to the given url and returns the response.
     * @param url
     * @return
     * @throws IOException
     */
    public static String sendGetRequest(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * Makes a POST request with the given json to the given url and returns the response.
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public String sendPostRequest(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * Utility function to return whether there's network connectivity or not.
     * @param ctx activity's context
     * @return true if the device has network connectivity, false otherwise.
     */
    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
