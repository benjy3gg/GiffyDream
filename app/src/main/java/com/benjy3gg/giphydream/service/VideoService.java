package com.benjy3gg.giphydream.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;
import retrofit.http.GET;

public class VideoService {
	private final static String ENDPOINT = "api.giphy.com?api_key=dc6zaTOxFJmzC";
	
	private static AppleService createService() {
		return new RestAdapter.Builder()
				.setEndpoint(ENDPOINT)
				.setConverter(new JacksonConverter())
//                .setLogLevel(RestAdapter.LogLevel.FULL)
				.build()
				.create(AppleService.class);
	}
	
	public static List<AerialVideo> fetchVideos() {
		return joinVideos(createService().fetchWrappers());
	}
	
	public static void fetchVideos(final Callback<List<AerialVideo>> callback) {
		createService().fetchWrappers(new Callback<List<Wrapper>>() {
			@Override
			public void success(List<Wrapper> wrappers, Response response) {
				callback.success(joinVideos(wrappers), response);
			}
			
			@Override
			public void failure(RetrofitError error) {
				callback.failure(error);
			}
		});
	}
	
	private static List<AerialVideo> joinVideos(List<Wrapper> wrappers) {
		List<AerialVideo> videos = new LinkedList<>();
		for (Wrapper wrapper : wrappers) {
			videos.addAll(wrapper.assets);
		}
		return videos;
	}
	
	private interface AppleService {
		@GET("/v1/gifs/trending")
		List<Wrapper> fetchWrappers();
		
		@GET("/v1/gifs/trending")
		void fetchWrappers(Callback<List<Wrapper>> callback);
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Wrapper {
		@JsonProperty("assets")
		List<AerialVideo> assets;
	}
}
