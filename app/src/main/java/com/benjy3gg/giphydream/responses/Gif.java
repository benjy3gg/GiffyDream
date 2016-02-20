package com.benjy3gg.giphydream.responses;

/**
 * Created by bkopp on 19/02/16.
 */
public class Gif {
    public String type;
    public String id;
    public String url;
    public String bitly_gif_url;
    public String source;
    public String username;
    public GiphyImage images;

    @Override
    public String toString() {
        return id + " " + source;
    }
}
