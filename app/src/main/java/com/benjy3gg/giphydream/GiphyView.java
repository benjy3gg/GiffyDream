package com.benjy3gg.giphydream;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.benjy3gg.giphydream.responses.Gif;
import com.benjy3gg.giphydream.responses.GifSingle;
import com.benjy3gg.giphydream.responses.GiphyResponse;
import com.benjy3gg.giphydream.service.GifDownloader;
import com.benjy3gg.giphydream.service.GiphyService;

import java.io.IOException;
import java.util.ArrayList;
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
    public GifDrawable mCurrentDrawable;
    public GifDrawable mNextDrawable;
    private Context mContext;
    private Random            mRandom;
    private Handler           mHandler;
    private String            mVideoCachePath;
    private boolean           mPlayerReady;
    private boolean 		  mFirstPlay;
    private GiphyResponse     mResponse;
    private ArrayList<Gif>   mSafeGifs = new ArrayList<Gif>();
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
    private String mNextSlug;
    private String mCurrentSlug;
    private TextView mSlugText;
    private int mCurrentLoop;
    private int mMinLoop = 2;
    private int mNumGifsToGet = 10;
    private GiphyService mService;
    private GifSingle mCurrentRandomGif;
    private GifSingle mNextGif;
    private int mOffsetMulti = 0;
    private long start;

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
        mSlugText = (TextView) findViewById(R.id.slug);


        mRandom = new Random(System.currentTimeMillis());
        mHandler = new Handler(Looper.getMainLooper());
        mPlayerReady = false;

        mCurrentLoop = 0;


        mVideoCachePath = getContext().getExternalCacheDir().getPath() + "/videos";

        mGifView1 = (GifImageView) findViewById(R.id.gifView1);
        mGifView2 = (GifImageView) findViewById(R.id.gifView2);

        downloader = new GifDownloader();
        mService = new GiphyService();
        //loadNewRandom("cat");
        loadNewTrending(0);


        try {
            mCurrentDrawable = new GifDrawable(getResources(), R.drawable.api_giphy_header);
            mCurrentSlug = "kittey";
            mSlugText.setText(mCurrentSlug);
            mGifView1.setImageDrawable(mCurrentDrawable);
            mCurrentDrawable.start();
            addListener(mCurrentDrawable);
            /*mCurrentDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    Log.d("AnimationCompleted", loopNumber + " times already!");

                    if (mNextDrawable != null && mCurrentLoop >= 2) {
                        mCurrentDrawable.stop();
                        mCurrentDrawable.recycle();
                        mCurrentDrawable = mNextDrawable;
                        mCurrentSlug = mNextSlug;
                        mNextSlug = null;
                        mNextDrawable = null;
                        mGifView1.setImageDrawable(mCurrentDrawable);
                        mCurrentSlug = mCurrentSlug.substring(0,mCurrentSlug.lastIndexOf("-") != -1 ? mCurrentSlug.lastIndexOf("-") : mCurrentSlug.length());
                        mSlugText.setText(mCurrentSlug.replace("-", " "));
                        mCurrentDrawable.start();
                        mCurrentLoop = 0;
                    }else {
                        if(mSafeGifs.size() > 0) {
                            downloadNextGif();
                        }
                    }
                    mCurrentLoop++;
                }
            });*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadNewTrending(int offset) {

        Call<GiphyResponse> call = mService.catGifs(mNumGifsToGet, offset * mOffsetMulti);
        call.enqueue(new Callback<GiphyResponse>() {
            @Override
            public void onResponse(Call<GiphyResponse> call, Response<GiphyResponse> response) {
                mResponse = response.body();
                Log.e("Giphy-Response", mResponse.getData() + "");
                mGifData = mResponse.getData();
                mOffsetMulti++;
                gifDataTODrawables();
            }

            @Override
            public void onFailure(Call<GiphyResponse> call, Throwable t) {
                Log.e("Giphy-Failure", t.getCause() + " " + t.getStackTrace());
            }
        });
    }

    public void loadNewRandom(String tag) {
        start = System.nanoTime();
        Call<GifSingle> call = mService.getRandomTag(tag);
        call.enqueue(new Callback<GifSingle>() {
            @Override
            public void onResponse(Call<GifSingle> call, Response<GifSingle> response) {
                long endTime = System.nanoTime();
                mCurrentRandomGif = response.body();
                Log.e("Giphy-Response", "took: " + (endTime - start) + " " + mCurrentRandomGif.caption);
                loadNewRandom("cat");
            }

            @Override
            public void onFailure(Call<GifSingle> call, Throwable t) {
                Log.e("Giphy-Failure", t.getCause() + " " + t.getStackTrace());
            }
        });
    }



    public void gifDataTODrawables() {
        mSafeGifs.clear();
        for(Gif g: mGifData) {
            if(g.images.fixed_height.size <= 2500000) {
                mSafeGifs.add(g);
            }
        }
    }

    public void downloadNextGif() {
        Log.d("mSafeGifs", "We have " + mSafeGifs.size() + " GIFS in queue");

        String gifUrl = mSafeGifs.get(mIndex).images.fixed_height.url;

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
                            if(mLoadingView.getAlpha() >= 0) {
                                mLoadingView
                                        .animate()
                                        .alpha(0)
                                        .setDuration(1000)
                                        .setInterpolator(new DecelerateInterpolator())
                                        .start();
                            }
                            mNextDrawable = gifFromStream;
                            Log.d("Index: ", "" + mIndex);
                            mNextSlug = mSafeGifs.get(mIndex).slug;
                            if (mIndex > mSafeGifs.size() - 2) {
                                mIndex = 0;
                                loadNewTrending(mNumGifsToGet);
                                //get new trending urlarray?
                            } else {
                                mIndex++;
                            }


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
                if (mNextDrawable != null && mCurrentLoop >= 2) {
                    mCurrentDrawable.stop();
                    mCurrentDrawable.recycle();
                    mCurrentDrawable = mNextDrawable;
                    mNextDrawable = null;
                    mCurrentSlug = mNextSlug;
                    mNextSlug = null;
                    mGifView1.setImageDrawable(mCurrentDrawable);
                    mCurrentSlug = mCurrentSlug.substring(0,mCurrentSlug.lastIndexOf("-") != -1 ? mCurrentSlug.lastIndexOf("-") : mCurrentSlug.length());
                    mSlugText.animate().rotationXBy(360).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    mSlugText.setText(mCurrentSlug.replace("-", " "));
                    mCurrentDrawable.start();
                    mCurrentLoop = 0;
                }else {
                    if(mSafeGifs.size() > 0) {
                        downloadNextGif();
                    }
                }
                mCurrentLoop++;
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

    public void animateView() {

    }

    public void stop() {
        if(mNextDrawable != null) {
            mNextDrawable.stop();
        }

        if(mNextDrawable != null) {
            mCurrentDrawable.stop();
        }
    }

    public class Tuple<X,Y> {
        public final X slug;
        public final Y url;

        public Tuple(X x, Y y) {
            this.slug = x;
            this.url = y;
        }
    }

}
