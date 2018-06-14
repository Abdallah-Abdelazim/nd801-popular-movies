package com.abdallah.popularmovies.utils.network;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Common network utilities using OkHttp
 */
public final class OkHttpNetworkUtils {

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

}
