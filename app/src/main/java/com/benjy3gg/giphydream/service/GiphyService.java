package com.benjy3gg.giphydream.service;

import com.benjy3gg.giphydream.responses.GiphyResponse;

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

    public interface Service {
        @GET("/v1/gifs/trending")
        Call<GiphyResponse> getTrending(
                @Query("api_key") String apiKey
        );
    }
    
    public GiphyService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.GIPHY_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit
                .create(Service.class);
    }

    public Call<GiphyResponse> trendingGifs() {
        return service.getTrending(Constants.GIPHY_KEY);
    }
}
