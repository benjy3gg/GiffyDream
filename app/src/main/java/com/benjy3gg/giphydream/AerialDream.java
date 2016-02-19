package com.benjy3gg.giphydream;

import android.content.Context;
import android.content.SharedPreferences;
import android.service.dreams.DreamService;

public class AerialDream extends DreamService {
	private AerialView mAerialView;
	private SharedPreferences mSharedPrefs;
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		setFullscreen(true);
		setContentView(R.layout.daydream);
		
		mAerialView = (AerialView) findViewById(R.id.aerial);
		mAerialView.setSharedPrefs(getSharedPreferences(getPackageName(), Context.MODE_PRIVATE));
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
		mAerialView.stop();
		
		super.onDreamingStopped();
	}
}
