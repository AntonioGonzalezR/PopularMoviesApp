package com.jagr.android.popularmovies.data.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Antonio on 15-09-08.
 * Object representation of the Json format returned by the movies API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class VideoResponse implements Serializable {
    private int page;
    @JsonProperty("results")
    private ArrayList<Video> videos;

    public int getPage() {
        return page;
    }

    public ArrayList<Video> getVideos() {
        return videos;
    }

    @Override
    public String toString(){
        return "MovieResponse [page=" + page + "]";
    }

}
