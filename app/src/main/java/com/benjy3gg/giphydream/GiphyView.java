package com.benjy3gg.giphydream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.benjy3gg.giphydream.responses.GifSingle;
import com.benjy3gg.giphydream.service.GifDownloader;
import com.benjy3gg.giphydream.service.GiphyService;

import java.io.IOException;
import java.util.ArrayList;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GiphyView extends PercentRelativeLayout implements SimpleCallback {
    private static final String TAG = "GiphyView";
    public GifDrawable mCurrentDrawable;
    public GifDrawable mNextDrawable;
    private View mLoadingView;

    private ProgressBar mProgressBar;

    private GifImageView mGifView1;
    private GifDownloader downloader;
    private TextView mSlugText;
    private GiphyService mService;
    private GifSingle mCurrentRandomGif;
    private long start;
    private GifDrawableEx mDrawable;
    private SpecialList mlist = new SpecialList();
    private String mTag;
    private PercentRelativeLayout mContainer;
    private SharedPreferences mSharedPrefs;
    private Handler mHandler;
    private int mCounterPlayed = 0;
    private int mCounterDownloaded = 0;

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

    public void setTag(String tag) {
        this.mTag = tag;
        mSlugText.setText(tag);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        LayoutInflater.from(getContext()).inflate(R.layout.daydream_view, this, true);

        mContainer = (PercentRelativeLayout) findViewById(R.id.container);
        mLoadingView = findViewById(R.id.loading);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mProgressBar.setVisibility(INVISIBLE);
        mSlugText = (TextView) findViewById(R.id.slug);

        mHandler = new Handler(Looper.getMainLooper());
        mGifView1 = (GifImageView) findViewById(R.id.gifView1);

        downloader = new GifDownloader();
        mService = new GiphyService();

        try {
            mDrawable = new GifDrawableEx(getResources(), R.drawable.api_giphy_header);
            onSetNewGif();
            onNeedNewGif();
            //loadNewRandom("cat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadNewRandom(String tag) {
        start = System.nanoTime();
        Call<GifSingle> call = mService.getRandomTag(tag);
        call.enqueue(new Callback<GifSingle>() {
            @Override
            public void onResponse(Call<GifSingle> call, Response<GifSingle> response) {
                long endTime = System.nanoTime();
                mCurrentRandomGif = response.body();
                Log.e("Giphy-Response", "took: " + (endTime - start) + " " + mCurrentRandomGif);
                GiphyView.this.onNewGifUrl(mCurrentRandomGif);
            }

            @Override
            public void onFailure(Call<GifSingle> call, Throwable t) {
                Log.e("Giphy-Failure", t.getCause() + " " + t.getStackTrace());
            }
        });
    }

    public void downloadGif(GifSingle gif) {
        String gifUrl = gif.getUrl();
        final String gifId = gif.getId();
        try {
            downloader.run(gifUrl, new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {}

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                    if (response.body().contentLength() <= 15000000 && !mlist.contains(gifId)) {
                        byte[] sourceIs = response.body().bytes();
                        final GifDrawableEx gifFromStream = new GifDrawableEx(sourceIs);
                        mlist.add(gifId);
                        onNewGif(gifFromStream, gifId);
                        mCounterDownloaded++;
                        Log.e(TAG, "downloaded: " + mCounterDownloaded);
                    } else {
                        Log.e(TAG, "size or duplicate: " + response.body().contentLength());
                        response.body().close();
                        onNeedNewGif();
                    }

                    Log.d(TAG, "" + mlist);
                }
            });
        }catch(IOException e) {
            Log.d("IOException: ", e.getMessage());
        }
    }

    public void stop() {
        if (mNextDrawable != null) {
            mNextDrawable.stop();
        }

        if (mNextDrawable != null) {
            mCurrentDrawable.stop();
        }
    }

    @Override
    public void onNewGif(final GifDrawableEx drawable, final String caption) {
        Log.d(TAG, "onNewGif");
        /*if(mDrawable != null) {
            mDrawable.stop();
            mDrawable.recycle();
        }*/
        mDrawable = drawable;
    }

    @Override
    public void onNewGifUrl(GifSingle gif) {
        Log.d(TAG, "onNewGifUrl");
        downloadGif(gif);
    }

    @Override
    public void onNeedNewGif() {
        Log.d(TAG, "onNeedNewGif");
        loadNewRandom(mTag);
    }

    @Override
    public void onSetNewGif() {
        Log.d(TAG, "onNewGif");
        if (mLoadingView.getAlpha() > 0) {
            mLoadingView
                    .animate()
                    .alpha(0)
                    .setDuration(1000)
                    .setInterpolator(new AccelerateInterpolator())
                    .start();
        }
        mGifView1.setImageDrawable(mDrawable);
        mDrawable.start();
        mCounterPlayed++;
        Log.e(TAG, "played: " + mCounterPlayed);
    }

    public class GifDrawableEx extends GifDrawable {

        public int mCurrentLoopCount = 0;
        public int mMinimumLoopCount = 2;
        public GifDrawableEx mNextGif;


        public GifDrawableEx(@NonNull byte[] bytes) throws IOException {
            super(bytes);
            this.calcMinimumLoops();
            this.addListener();
        }

        public GifDrawableEx(Resources resources, int api_giphy_header) throws IOException {
            super(resources, api_giphy_header);
            this.mMinimumLoopCount = 10;
            this.addListener();
            //this.start();
        }

        public void calcMinimumLoops() {
            int duration = this.getDuration();
            Log.d(TAG, "duration: " + duration);
            if (duration > 1800) {
                this.mMinimumLoopCount = 1;
            } else {
                double count = 1800.0f / duration;
                this.mMinimumLoopCount = (int) Math.ceil(count);
            }

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    onNeedNewGif();
                }
            }, duration);
        }

        public void addListener() {
            this.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    Log.d(TAG, "loop: " + mCurrentLoopCount + " of " + mMinimumLoopCount + " loops.");
                    if (mCurrentLoopCount == 0) {

                    }

                    if (mCurrentLoopCount >= mMinimumLoopCount - 1 && mDrawable != null) {
                        onSetNewGif();
                    }

                    mCurrentLoopCount++;
                }

            });
        }

    }

    public class SpecialList extends ArrayList<String> {

        @Override
        public boolean add(String string) {
            if (this.size() >= 20) {
                this.remove(0);
                return super.add(string);
            }

            return super.add(string);
        }
    }

}
