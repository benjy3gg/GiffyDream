package com.benjy3gg.giphydream;

import android.content.Context;
import android.content.SharedPreferences;
import android.service.dreams.DreamService;

public class GiphyDream extends DreamService {
	private GiphyView mGiphyView;
	private SharedPreferences mSharedPrefs;
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		setFullscreen(true);
		setContentView(R.layout.daydream);
		
		mGiphyView = (GiphyView) findViewById(R.id.aerial);
		mGiphyView.setSharedPrefs(getSharedPreferences(getPackageName(), Context.MODE_PRIVATE));
	}
	
	/* DreamService */
	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
	
	@Override
	public void onDreamingStarted() {
		super.onDreamingStarted();
	}
	
	public void onDreamingStopped() {
		mGiphyView.stop();

		
		super.onDreamingStopped();
	}
}
