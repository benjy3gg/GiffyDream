package com.benjy3gg.giphydream.service;

import com.benjy3gg.giphydream.responses.GifSingle;
import com.benjy3gg.giphydream.responses.GiphyResponse;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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

    public GiphyService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.GIPHY_ENDPOINT)
                //.client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit
                .create(Service.class);
    }

    public Call<GiphyResponse> trendingGifs(int limit) {
        return service.getTrending(Constants.GIPHY_KEY, Integer.toString(limit));
    }

    public Call<GiphyResponse> catGifs(int limit, int offset) {
        return service.getCats(Constants.GIPHY_KEY, "cats", Integer.toString(limit), Integer.toString(offset));
    }

    public Call<GifSingle> getRandomTag(String tag) {
        return service.getRandomTag(Constants.GIPHY_KEY, tag);
    }

    public interface Service {
        @GET("/v1/gifs/trending")
        Call<GiphyResponse> getTrending(
                @Query("api_key") String apiKey,
                @Query("limit") String limit
        );

        @GET("/v1/gifs/search")
        Call<GiphyResponse> getCats(
                @Query("api_key") String apiKey,
                @Query("q") String query,
                @Query("limit") String limit,
                @Query("offset") String offset

        );

        @GET("/v1/gifs/random")
        Call<GifSingle> getRandomTag(
                @Query("api_key") String apiKey,
                @Query("tag") String tag
        );

    }
}
