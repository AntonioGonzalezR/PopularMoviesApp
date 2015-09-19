package com.jagr.android.popularmovies.data.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Antonio on 15-09-09.
 * Object representation of the Json format returned by the movies API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class ReviewResponse implements Serializable {
    private int page;
    @JsonProperty("results")
    private ArrayList<Review> reviews;
    @JsonProperty("total_pages")
    private int totalPages;
    @JsonProperty("total_results")
    private int totalResults;


    public int getPage() {
        return page;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    @Override
    public String toString(){
        return "MovieResponse [page=" + page + ", totalPages=" + totalPages + ", " +
                "totalResults=" + totalResults + "]";
    }

}
