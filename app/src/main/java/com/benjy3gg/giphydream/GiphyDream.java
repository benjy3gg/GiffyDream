package com.benjy3gg.giphydream;

import android.content.Context;
import android.content.SharedPreferences;
import android.service.dreams.DreamService;

public class GiphyDream extends DreamService {
	private GiphyView mGiphyView;
	private SharedPreferences mSharedPrefs;
	private String mTag;

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		setFullscreen(true);
		setContentView(R.layout.daydream);

		mTag = getApplicationContext().getSharedPreferences(getPackageName() + "_preferences", Context.MODE_PRIVATE).getString("tag", "cat");
		
		mGiphyView = (GiphyView) findViewById(R.id.aerial);
		mGiphyView.setTag(mTag);

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
