package com.benjy3gg.giphydream;

import android.app.Activity;
import android.os.Bundle;

public class TestActivity extends Activity {
	private GiphyView mGiphyView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.daydream);
		
		mGiphyView = (GiphyView) findViewById(R.id.aerial);
	}
	
	@Override
	protected void onStop() {
		mGiphyView.stop();
		
		super.onStop();
	}
}
