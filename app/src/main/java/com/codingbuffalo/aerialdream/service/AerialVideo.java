package com.codingbuffalo.aerialdream.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AerialVideo {
	private String mUrl;
	private String mLocation;
	private String mTimeOfDay;
	private boolean isDownloaded;
	private String mFilePath;

	public AerialVideo(@JsonProperty("url") String url, @JsonProperty("accessibilityLabel") String location, @JsonProperty("timeOfDay") String timeOfDay) {
		mUrl = url;
		mLocation = location;
		mTimeOfDay = timeOfDay;
		isDownloaded = false;
		mFilePath = "";
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	public String getLocation() {
		return mLocation;
	}
	
	public String getTimeOfDay() {
		return mTimeOfDay;
	}

	public boolean isDownloaded() {
		return isDownloaded;
	}

	public void setDownloaded(boolean downloaded) {
		isDownloaded = downloaded;
	}


	public String getFilePath() {
		return mFilePath;
	}

	public void setFilePath(String mFilePath) {
		this.mFilePath = mFilePath;
	}
}
