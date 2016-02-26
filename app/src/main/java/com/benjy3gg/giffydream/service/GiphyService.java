package com.benjy3gg.giffydream.service;

import com.benjy3gg.giffydream.responses.GifSingle;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by bkopp on 19/02/16.
 */
public class GiphyService {
    private Retrofit retrofit;
    private Service service;

    public GiphyService(File dir) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.MILLISECONDS).cache(new Cache(dir, 1024)).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.GIPHY_ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit
                .create(Service.class);
    }

    public Call<GifSingle> getRandomTag(String tag) {
        return service.getRandomTag(Constants.GIPHY_KEY, tag);
    }

    public interface Service {
        @GET("/v1/gifs/random")
        Call<GifSingle> getRandomTag(
                @Query("api_key") String apiKey,
                @Query("tag") String tag
        );

    }
}
