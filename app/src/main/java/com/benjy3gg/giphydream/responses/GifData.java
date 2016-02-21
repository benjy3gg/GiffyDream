package com.benjy3gg.giphydream.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GifData {

    //        @SerializedName("type")
//        @Expose
//        public String type;
    @SerializedName("id")
    @Expose
    public String id;
    //        @SerializedName("url")
//        @Expose
//        public String url;
//        @SerializedName("image_original_url")
//        @Expose
//        public String imageOriginalUrl;
    @SerializedName("image_url")
    @Expose
    public String imageUrl;
    //        @SerializedName("image_mp4_url")
//        @Expose
//        public String imageMp4Url;
//        @SerializedName("image_frames")
//        @Expose
//        public String imageFrames;
//        @SerializedName("image_width")
//        @Expose
//        public String imageWidth;
//        @SerializedName("image_height")
//        @Expose
//        public String imageHeight;
//        @SerializedName("fixed_height_downsampled_url")
//        @Expose
//        public String fixedHeightDownsampledUrl;
//        @SerializedName("fixed_height_downsampled_width")
//        @Expose
//        public String fixedHeightDownsampledWidth;
//        @SerializedName("fixed_height_downsampled_height")
//        @Expose
//        public String fixedHeightDownsampledHeight;
    @SerializedName("fixed_width_downsampled_url")
    @Expose
    public String fixedWidthDownsampledUrl;
    //        @SerializedName("fixed_width_downsampled_width")
//        @Expose
//        public String fixedWidthDownsampledWidth;
//        @SerializedName("fixed_width_downsampled_height")
//        @Expose
//        public String fixedWidthDownsampledHeight;
//        @SerializedName("fixed_height_small_url")
//        @Expose
//        public String fixedHeightSmallUrl;
//        @SerializedName("fixed_height_small_still_url")
//        @Expose
//        public String fixedHeightSmallStillUrl;
//        @SerializedName("fixed_height_small_width")
//        @Expose
//        public String fixedHeightSmallWidth;
//        @SerializedName("fixed_height_small_height")
//        @Expose
//        public String fixedHeightSmallHeight;
//        @SerializedName("fixed_width_small_url")
//        @Expose
//        public String fixedWidthSmallUrl;
//        @SerializedName("fixed_width_small_still_url")
//        @Expose
//        public String fixedWidthSmallStillUrl;
//        @SerializedName("fixed_width_small_width")
//        @Expose
//        public String fixedWidthSmallWidth;
//        @SerializedName("fixed_width_small_height")
//        @Expose
//        public String fixedWidthSmallHeight;
//        @SerializedName("username")
//        @Expose
//        public String username;
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
