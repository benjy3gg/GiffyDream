package com.benjy3gg.giffydream.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GifData {


    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("image_url")
    @Expose
    public String imageUrl;

    @SerializedName("caption")
    @Expose
    public String caption;

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCaption() {
        return caption;
    }

    @Override
    public String toString() {
        return "id: " + id + " caption: " + getCaption() + " url: " + getImageUrl();
    }
}
