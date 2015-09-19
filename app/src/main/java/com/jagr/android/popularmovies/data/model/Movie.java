package com.jagr.android.popularmovies.data.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.jagr.android.popularmovies.data.MovieContract;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Antonio on 15-08-30.
 * Used to send information via an Intent to the detail activity of the app
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class Movie implements Parcelable {


    public static final String NAME = Movie.class.getSimpleName();


    private long rowId;
    @JsonProperty("id")
    private long movieId;
    @JsonProperty("original_title")
    private String movieTitle = null;
    @JsonProperty("poster_path")
    private String moviePoster = null;
    @JsonProperty("overview")
    private String movieSynopsis = null;
    @JsonProperty("popularity")
    private double movieRating;
    @JsonProperty("vote_average")
    private double movieVoteAverage;
    @JsonProperty("release_date")
    private String movieReleaseDate;

    public Movie(){ }

    /**
     * Create a MovieParcelable object
     * @param movieTitle - string containing the movie title
     * @param moviePoster - movie poster url
     * @param movieSynopsis - movie description
     * @param movieRating - movie rank vs other movies
     * @param movieReleaseDate - log number representing the movie release date
     */
    public Movie(String movieTitle, String moviePoster, String movieSynopsis, double movieRating, String movieReleaseDate,
                 double movieVoteAverage, long movieId) {
        this.movieTitle = movieTitle;
        this.moviePoster = moviePoster;
        this.movieSynopsis = movieSynopsis;
        this.movieRating = movieRating;
        this.movieReleaseDate = movieReleaseDate;
        this.movieVoteAverage = movieVoteAverage;
        this.movieId = movieId;
    }


    public String getMovieTitle() {
        return movieTitle;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public String getMovieSynopsis() {
        return movieSynopsis;
    }

    public double getMovieRating() {
        return movieRating;
    }

    public double getMovieVoteAverage() {
        return movieVoteAverage;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    private void setMovieReleaseDate(String movieReleaseDate ){
        if( null != movieReleaseDate ){
            this.movieReleaseDate = movieReleaseDate;
        }else{
            this.movieReleaseDate = "1900-01-01";
        }

    }


    /**
     * parse a movie object to content values
     * @return
     */
    public ContentValues parseToContentValues(  ){
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, this.getMovieId());
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, this.getMoviePoster() );
        cv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, this.getMovieTitle() );
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, this.getMovieReleaseDate() );
        cv.put(MovieContract.MovieEntry.COLUMN_POPULARITY, this.getMovieRating());
        cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, this.getMovieVoteAverage() );
        cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, this.getMovieSynopsis());
        return cv;
    }


    public long getMovieId() {
        return movieId;
    }

    /** Static field used to regenerate object, individually or as arrays */
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel pc) {
            return new Movie(pc);
        }
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /**Ctor from Parcel, reads back fields IN THE ORDER they were written */
    public Movie(Parcel pc){
        movieTitle         = pc.readString();
        moviePoster        = pc.readString();
        movieSynopsis      = pc.readString();
        movieRating        = pc.readDouble();
        movieVoteAverage   = pc.readDouble();
        movieReleaseDate   = pc.readString();
        movieId            = pc.readLong();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString( movieTitle );
        dest.writeString( moviePoster );
        dest.writeString( movieSynopsis );
        dest.writeDouble(movieRating);
        dest.writeDouble( movieVoteAverage);
        dest.writeString(movieReleaseDate);
        dest.writeLong(movieId);
    }


    /**
     * String representation of movie object
     * @return
     */

    @Override
    public String toString() {
        return "Movie: [ movieTitle: " + movieTitle + ", " +
                "movieId: " + movieId  + ", " +
                "moviePoster: " + moviePoster  + ", " +
                "movieSynopsis: " + movieSynopsis  + ", " +
                "movieRating: " + movieRating  + ", " +
                "movieVoteAverage: " + movieVoteAverage  + ", " +
                "movieReleaseDate: " + movieReleaseDate +  "]";
    }
}
