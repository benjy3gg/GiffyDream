package com.benjy3gg.giphydream;

import com.benjy3gg.giphydream.responses.GifSingle;

/**
 * Created by benjy3gg on 20.02.2016.
 */
public interface SimpleCallback {

    void onNewGif(GiphyView.GifDrawableEx drawable, String caption);

    void onNewGifUrl(GifSingle gif);

    void onNeedNewGif();

    void onSetNewGif();

}
