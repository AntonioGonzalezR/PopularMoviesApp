package com.jagr.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Antonio on 15-08-30.
 * Used to send information via an Intent to the detail activity of the app
 */
public class MovieParcelable implements Parcelable {


    public static final String NAME = MovieParcelable.class.getSimpleName();
    private String movieTitle = null;
    private String moviePoster = null;
    private String movieSynopsis = null;
    private double movieRating;
    private double movieVoteAverage;
    private long movieReleaseDate;

    public MovieParcelable(){ }

    /**
     * Create a MovieParcelable object
     * @param movieTitle - string containing the movie title
     * @param moviePoster - movie poster url
     * @param movieSynopsis - movie description
     * @param movieRating - movie rank vs other movies
     * @param movieReleaseDate - log number representing the movie release date
     */
    public MovieParcelable(String movieTitle, String moviePoster, String movieSynopsis, double movieRating, long movieReleaseDate,
                           double movieVoteAverage) {
        this.movieTitle = movieTitle;
        this.moviePoster = moviePoster;
        this.movieSynopsis = movieSynopsis;
        this.movieRating = movieRating;
        this.movieReleaseDate = movieReleaseDate;
        this.movieVoteAverage = movieVoteAverage;
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

    public long getMovieReleaseDate() {
        return movieReleaseDate;
    }

    /** Static field used to regenerate object, individually or as arrays */
    public static final Parcelable.Creator<MovieParcelable> CREATOR = new Parcelable.Creator<MovieParcelable>() {
        public MovieParcelable createFromParcel(Parcel pc) {
            return new MovieParcelable(pc);
        }
        public MovieParcelable[] newArray(int size) {
            return new MovieParcelable[size];
        }
    };

    /**Ctor from Parcel, reads back fields IN THE ORDER they were written */
    public MovieParcelable(Parcel pc){
        movieTitle         = pc.readString();
        moviePoster        = pc.readString();
        movieSynopsis      = pc.readString();
        movieRating        = pc.readDouble();
        movieVoteAverage   = pc.readDouble();
        movieReleaseDate   = pc.readLong();

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
        dest.writeDouble( movieRating );
        dest.writeDouble( movieVoteAverage );
        dest.writeLong( movieReleaseDate );
    }
}
