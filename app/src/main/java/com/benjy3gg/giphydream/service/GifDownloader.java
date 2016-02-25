package com.benjy3gg.giphydream.service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by bkopp on 20/02/16.
 */
public class GifDownloader {

    public OkHttpClient client;

    public GifDownloader(File dir) {
        client = new OkHttpClient.Builder().cache(new Cache(dir, 256 * 1024 * 1024)).build();
    }

    public Call run(String url, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}
