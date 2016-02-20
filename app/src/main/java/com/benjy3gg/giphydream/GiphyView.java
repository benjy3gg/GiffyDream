package com.benjy3gg.giphydream;

import android.animation.Animator;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.benjy3gg.giphydream.responses.Gif;
import com.benjy3gg.giphydream.responses.GiphyResponse;
import com.benjy3gg.giphydream.service.GifDownloader;
import com.benjy3gg.giphydream.service.GiphyService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GiphyView extends PercentRelativeLayout {
    private static final int GIF_DOWNLOADED = 1;
    private static final int FADE_DURATION = 5000;
    private Context mContext;
    private Random            mRandom;
    private Handler           mHandler;
    private String            mVideoCachePath;
    private boolean           mPlayerReady;
    private boolean 		  mFirstPlay;
    private GiphyResponse     mResponse;
    private ArrayList<String>      mGifUrls = new ArrayList<String>();
    public GifDrawable       mCurrentDrawable;
    public GifDrawable       mNextDrawable;
    private int               mIndex;
    private boolean           mFirstGif;

    private PercentRelativeLayout mContainer;
    private View                  mLoadingView;

    private SimplePlayer mActivePlayer;
    private SimplePlayer mBufferPlayer;
    private ProgressBar mProgressBar;
    private TextView mLocation;
    private SharedPreferences mSharedPrefs;
    private GifImageView mGifView1;
    private GifImageView mGifView2;
    private boolean mFirst = true;
    private ArrayList<Gif> mGifData  = new ArrayList<Gif>();
    private GifDownloader downloader;
    private int mLoop = 0;

    public GiphyView(Context context) {
        super(context);
    }

    public GiphyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GiphyView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mLocation = (TextView) findViewById(R.id.location_text);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mProgressBar.setVisibility(INVISIBLE);


        mRandom = new Random(System.currentTimeMillis());
        mHandler = new Handler(Looper.getMainLooper());
        mPlayerReady = false;



        mVideoCachePath = getContext().getExternalCacheDir().getPath() + "/videos";

        mGifView1 = (GifImageView) findViewById(R.id.gifView1);
        mGifView2 = (GifImageView) findViewById(R.id.gifView2);

        downloader = new GifDownloader();

        GiphyService service = new GiphyService();
        Call<GiphyResponse> call = service.trendingGifs();

        call.enqueue(new Callback<GiphyResponse>() {
            @Override
            public void onResponse(Call<GiphyResponse> call, Response<GiphyResponse> response) {
                mResponse = response.body();
                Log.e("Giphy-Response", mResponse.getData() + "");
                mGifData = mResponse.getData();
                gifDataTODrawables();
            }

            @Override
            public void onFailure(Call<GiphyResponse> call, Throwable t) {
                Log.e("Giphy-Failure", t.getCause() + " " + t.getStackTrace());
            }
        });

        try {
            mCurrentDrawable = new GifDrawable(getResources(), R.drawable.api_giphy_header);
            mGifView1.setImageDrawable(mCurrentDrawable);
            mCurrentDrawable.start();
            mLoadingView
                    .animate()
                    .alpha(0)
                    .setDuration(1000)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
            mCurrentDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    Log.d("AnimationCompleted", loopNumber + " times already!");
                    if (mNextDrawable != null) {
                        mCurrentDrawable.stop();
                        mCurrentDrawable.recycle();
                        mCurrentDrawable = mNextDrawable;
                        mNextDrawable = null;
                        mGifView1.setImageDrawable(mCurrentDrawable);
                        mCurrentDrawable.start();
                    }else {
                        if(mGifUrls.size() > 0) {
                            downloadNextGif();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gifDataTODrawables() {
        for(Gif g: mGifData) {
            if(g.images.fixed_height.size <= 2000000) {
                mGifUrls.add(g.images.fixed_height.url);
            }

        }
    }

    public void downloadNextGif() {
        if(mIndex >= mGifUrls.size()) {
            mIndex = 0;
            //get new trending urlarray?
        }
        String gifUrl = mGifUrls.get(mIndex);
        mIndex++;
        try {
            downloader.run(gifUrl, new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {}

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    Log.e("Content-Type: ", "" + response.body().contentType());
                    byte[] sourceIs = response.body().bytes();
                    final GifDrawable gifFromStream = new GifDrawable(sourceIs);
                    Message msg = Message.obtain();
                    msg.what = GIF_DOWNLOADED;
                    //mHandler.dispatchMessage(msg);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mNextDrawable = gifFromStream;
                            addListener(mNextDrawable);
                        }
                    });
                }
            });
        }catch(IOException e) {
            Log.d("IOException: ", e.getMessage());
        }

    }

    public void addListener(GifDrawable draw) {
        draw.addAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationCompleted(int loopNumber) {
                if (mNextDrawable != null) {
                    mCurrentDrawable.stop();
                    mCurrentDrawable.recycle();
                    mCurrentDrawable = mNextDrawable;
                    mNextDrawable = null;
                    mGifView1.setImageDrawable(mCurrentDrawable);
                    mCurrentDrawable.start();
                }else {
                    if(mGifUrls.size() > 0) {
                        downloadNextGif();
                    }
                }
            }
        });
    }

    public void setGif(final GifDrawable gif) {

        mNextDrawable = gif;

        if(mCurrentDrawable == null) {
            Log.d("setGif", "we have no mCurrentDrawable");

            mFirst = false;
            mCurrentDrawable = gif;
            mGifView1.setImageDrawable(mCurrentDrawable);
            mCurrentDrawable.start();

        }else {

        }

    }

    public void stop() {
        mNextDrawable.stop();
        mCurrentDrawable.stop();
    }

}
