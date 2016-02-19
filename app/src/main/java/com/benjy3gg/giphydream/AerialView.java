package com.benjy3gg.giphydream;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.benjy3gg.giphydream.service.AerialVideo;
import com.benjy3gg.giphydream.service.VideoService;
import com.benjy3gg.giphydream.util.DownloadManager;
import com.benjy3gg.giphydream.util.SimpleCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class AerialView extends PercentRelativeLayout implements retrofit.Callback<List<AerialVideo>>, SimplePlayer.PlayerListener {
	private static final int FADE_DURATION = 5000;
	private Context mContext;
	private List<AerialVideo> mAerialVideos;
    private List<AerialVideo> mDownloadedAerialVideos = new ArrayList<AerialVideo>();
	private Random            mRandom;
	private Handler           mHandler;
	private DownloadManager   mDownloadManager;
	private String            mVideoCachePath;
	private boolean           mPlayerReady;
	private boolean 		  mFirstPlay;
	
	private PercentRelativeLayout mContainer;
	private View                  mLoadingView;
	
	private SimplePlayer mActivePlayer;
	private SimplePlayer mBufferPlayer;
    private ProgressBar mProgressBar;
	private TextView mLocation;
	private SharedPreferences mSharedPrefs;

	public AerialView(Context context) {
		super(context);
	}
	
	public AerialView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public AerialView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	public void setSharedPrefs(SharedPreferences prefs) {
		mSharedPrefs = prefs;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		LayoutInflater.from(getContext()).inflate(R.layout.daydream_view, this, true);
		
		mContainer = (PercentRelativeLayout) findViewById(R.id.container);
		mLoadingView = findViewById(R.id.loading);
		mActivePlayer = (SimplePlayer) findViewById(R.id.player1);
		mBufferPlayer = (SimplePlayer) findViewById(R.id.player2);
		mLocation = (TextView) findViewById(R.id.location_text);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mProgressBar.setVisibility(INVISIBLE);
		
		mActivePlayer.setPlayerListener(this);
		
		mRandom = new Random(System.currentTimeMillis());
		mHandler = new Handler();
		mDownloadManager = new DownloadManager(1);
		mPlayerReady = false;
		
		mVideoCachePath = getContext().getExternalCacheDir().getPath() + "/videos";
		
		VideoService.fetchVideos(this);
	}
	
	public void stop() {
		mHandler.removeCallbacks(mSwitcher);
		mActivePlayer.release();
		mBufferPlayer.release();
		mDownloadedAerialVideos.clear();
	}
	
	/* SimplePlayer.OnPlayerListener */
	@Override
	public void onPlayerInitialized(SimplePlayer simplePlayer) {
		mPlayerReady = true;
		startPlayingVideos();
	}
	
	@Override
	public void onVideoLoaded() {
		mActivePlayer.setPlayerListener(null);
		mActivePlayer.play();
		
		fadeOutLoadingView();
		
		loadNextVideo(mBufferPlayer);
		prepareNextVideo();
	}
	
	/* Retrofit.Callback */
	@Override
	public void success(List<AerialVideo> aerialVideos, Response response) {
		Log.d("Giphy", response + " " + aerialVideos);
		mAerialVideos = aerialVideos;
		//startPlayingVideos();
	}
	
	@Override
	public void failure(RetrofitError error) {
		Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
		error.printStackTrace();
	}
	
	/* Helpers */
	private void switchPlayer() {
		// Switch active and buffer players
		SimplePlayer tempPlayer = mActivePlayer;
		mActivePlayer = mBufferPlayer;
		mBufferPlayer = tempPlayer;
		
		mActivePlayer.play();
		
		// Fade out top player
		Animation animation = new AlphaAnimation(1, 0);
		animation.setDuration(FADE_DURATION);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// Put active player on top so we can fade it out in the next iteration
				mContainer.bringChildToFront(mActivePlayer);
				
				// Start buffering next video
				mBufferPlayer.setAlpha(1);
				loadNextVideo(mBufferPlayer);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		
		mBufferPlayer.startAnimation(animation);
		prepareNextVideo();
	}
	
	private void startPlayingVideos() {
        if(mPlayerReady && mAerialVideos != null) {
            setDownloadedVideos();
            if(mDownloadedAerialVideos != null && mDownloadedAerialVideos.size() > 0) {
                loadInitialVideo(mActivePlayer);
            }else {
                loadNextVideo(mActivePlayer);
            }
		}
	}
	
	private void prepareNextVideo() {
		mHandler.postDelayed(mSwitcher, mActivePlayer.getDuration() - FADE_DURATION);
	}

    private void loadInitialVideo(final SimplePlayer player) {

        long seed = System.nanoTime();
        Collections.shuffle(mDownloadedAerialVideos, new Random(seed));

        AerialVideo video = mDownloadedAerialVideos.get(0);
		String location = mDownloadedAerialVideos.get(0).getLocation();
		mLocation.setText(location);

        String url = video.getUrl();
        String filename = url.substring(url.lastIndexOf('/') + 1);
        String localPath = mVideoCachePath + "/" + filename;
        File file = new File(localPath);

        if(file.exists() && !file.isDirectory()) {
            Log.d("InitialVideo", "File was found");
            player.load(Uri.parse(file.toString()));
        }else {
            Log.d("InitialVideo", "File was not found");
            loadNextVideo(mActivePlayer);
        }
    }
	
	private void loadNextVideo(final SimplePlayer player) {
		final int randomID = mRandom.nextInt(mAerialVideos.size());
		final AerialVideo video = mAerialVideos.get(randomID);

		String tod = getTimeofday();
		// Get video filename
		String filename = video.getFilePath();
        Log.d("LoadNextVideo", "File " + filename + " should be loaded into player");
        File file = new File(filename);
		if (file.exists() && !file.isDirectory()) {
			player.load(Uri.parse(filename));
		} else {
            Log.d("LoadNextVideo", "File was not found! -> Downloading");
            final AerialVideo tempVideo = video;
			mDownloadManager.download(tempVideo, mVideoCachePath, new SimpleCallback() {
				@Override
				public void onDownloadComplete(File file) {
                    addDownloadedVideos(tempVideo);
                    tempVideo.setFilePath(file.getAbsolutePath());
                    Log.d("VideoDownloaded", tempVideo.getFilePath());
                    mSharedPrefs.edit().putBoolean(tempVideo.getUrl(), true).apply();
                    tempVideo.setDownloaded(true);
                    mProgressBar.setVisibility(INVISIBLE);
                    player.load(Uri.parse(file.toString()));
                    mAerialVideos.set(randomID, tempVideo);
				}

                @Override
                public void onDownloadStarted(long totalLength) {
                    mProgressBar.setVisibility(VISIBLE);
                    mProgressBar.setMax((int)totalLength);
                }

                @Override
                public void onDownloadProgress(long downloadedLength) {
                    mProgressBar.setIndeterminate(false);
                    mProgressBar.setProgress((int)downloadedLength);
                }
            });

		}
	}

	private String getTimeofday() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Log.d("PENIS", calendar.getTime().toString());
		if(calendar.get(Calendar.HOUR_OF_DAY) > 12)
			return "night";
		else
			return "day";

	}

    private void setDownloadedVideos() {
        int index = 0;
        Log.d("SetDownloadedVideos", "AerialVideos: " + mAerialVideos.size());
        for(AerialVideo video: mAerialVideos) {
			String url = video.getUrl();
			String filename = url.substring(url.lastIndexOf('/') + 1);
			String localPath = mVideoCachePath + "/" + filename;
            File f = new File(localPath);
            Log.d("SetDownloadedVideos", "Video: " + localPath + " exists: " + f.exists());
            boolean prefDownloaded = mSharedPrefs.getBoolean(video.getUrl(), false);
            if(f.exists() && !f.isDirectory() && prefDownloaded) {
                Log.d("VideoDownloaded", "Video " + filename +  " has been correctly downloaded");
                addDownloadedVideos(video);
            }else if(f.exists() && !f.isDirectory()) {
                removeDownloadedVideos(video);
                f.delete();
                Log.d("VideoDelete", "Video " + filename +  " was corrupted and is erased");
            }
            index++;
        }
    }

	private void addDownloadedVideos(AerialVideo video) {
		mDownloadedAerialVideos.add(video);

	}

	private void removeDownloadedVideos(AerialVideo video) {
		video.setDownloaded(false);
		mDownloadedAerialVideos.remove(video);
	}

	
	private Runnable mSwitcher = new Runnable() {
		@Override
		public void run() {
			switchPlayer();
		}
	};
	
	private void fadeOutLoadingView() {
		Animation animation = new AlphaAnimation(1, 0);
		animation.setDuration(FADE_DURATION);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mLoadingView.setVisibility(View.GONE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		
		mLoadingView.startAnimation(animation);
	}
}
