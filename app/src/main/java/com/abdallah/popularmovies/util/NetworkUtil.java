package com.abdallah.popularmovies.util;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class NetworkUtil {

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

}
