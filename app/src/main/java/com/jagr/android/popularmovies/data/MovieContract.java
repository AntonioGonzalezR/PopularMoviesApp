package com.jagr.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Antonio on 15-08-20.
 * Specify the database layout.
 +
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.jagr.android.popularmovies";


    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE  =  "movie";

   private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    static final long DEFAULT_DATE = 0L;

    public static final int DEFAULT_PAGE = 1;


    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        //Movie's original title, as returned by the API
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        //Movie Id, as returned by API, to identify the movie to be shown
        public static final String COLUMN_MOVIE_ID = "movie_id";

        //URL of movie poster, as returned by the API
        public static final String COLUMN_MOVIE_POSTER = "movie_poster";

        //A plot synopsis, as returned by the API
        public static final String COLUMN_OVERVIEW = "overview";

        //User rating, stored as a float representing an average as returned by the API
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        //popularity, stored as a float representing an average as returned by the API
        public static final String COLUMN_POPULARITY = "popularity";

        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId( CONTENT_URI, id);
        }

        public static int getMovieIdFromUri(Uri uri) {
            return Integer.parseInt( uri.getPathSegments().get(1) );
        }
    }

    public static long formatDate(String date)throws ParseException{
        return FORMAT.parse(date).getTime();
    }

}
