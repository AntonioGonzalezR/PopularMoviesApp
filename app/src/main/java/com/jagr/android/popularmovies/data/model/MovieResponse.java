package com.jagr.android.popularmovies.data.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Antonio on 15-09-07.
 * Object representation of the Json format returned by the movies API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class MovieResponse implements Serializable {

    private int page;
    @JsonProperty("results")
    private ArrayList<Movie> movies;
    @JsonProperty("total_pages")
    private int totalPages;
    @JsonProperty("total_results")
    private int totalResults;


    public int getPage() {
        return page;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }


    /**
     * Object's string representation.
     * @return
     */
    @Override
    public String toString(){
        return "MovieResponse [page=" + page + ", totalPages=" + totalPages + ", " +
                "totalResults=" + totalResults + "]";
    }

}
