package com.jagr.android.popularmovies.util;

import android.content.ContentValues;

import com.jagr.android.popularmovies.data.MovieContract;

/**
 * Created by Antonio on 15-08-30.
 */
public class MovieValues {

    /**
     *
     * @param cv
     * @return
     */
    public static String getMoviePoster( ContentValues cv ){
        return cv.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
    }

    /**
     *
     * @param cv
     * @return
     */
    public static String getMovieTitle( ContentValues cv ){
        return cv.getAsString(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
    }

    /**
     *
     * @param cv
     * @return
     */
    public static String getMovieSynopsis( ContentValues cv ){
        return cv.getAsString(MovieContract.MovieEntry.COLUMN_OVERVIEW);
    }

    /**
     *
     * @param cv
     * @return
     */
    public static double getMovieRating( ContentValues cv ){
        return cv.getAsDouble(MovieContract.MovieEntry.COLUMN_POPULARITY);
    }

    /**
     *
     * @param cv
     * @return
     */
    public static String getMovieReleaseDate( ContentValues cv ){
        return cv.getAsString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
    }


    /**
     *
     * @param cv
     * @return
     */
    public static double getMovieVoteAverage( ContentValues cv ){
        return cv.getAsDouble(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
    }

}
