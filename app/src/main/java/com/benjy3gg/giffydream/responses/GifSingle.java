package com.benjy3gg.giffydream.responses;

/**
 * Created by benjy3gg on 20.02.2016.
 */
public class GifSingle {
    public GifData data;

    @Override
    public String toString() {
        return data.toString();
    }

    public String getUrl() {
        return data.getImageUrl();
    }

    public String getCaption() {
        return data.getCaption() != null ? data.getCaption() : "None";
    }

    public String getId() {
        return data.id;
    }
}

