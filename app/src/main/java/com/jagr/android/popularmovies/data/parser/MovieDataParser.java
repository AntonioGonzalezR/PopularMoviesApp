package com.jagr.android.popularmovies.data.parser;


import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.jagr.android.popularmovies.data.MovieContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Antonio on 15-08-26.
 * MovieDataParser extract data from a JSON string that represents a
 * collection of movies returned by The Movies API discover/movie resource.
 *
 */
public class MovieDataParser implements DataParser {

    private static String LOG_TAG = MovieDataParser.class.getSimpleName();

    public static final String OPI_PAGE = "page";
    public static final String OPI_TOTAL_PAGES = "total_pages";
    public static final String OPI_RESULTS = "results";

    public static final String OMI_MOVIE_ID = "id";
    public static final String OMI_ORIGINAL_TITLE = "original_title";
    public static final String OMI_OVERVIEW = "overview";
    public static final String OMI_RELEASE_DATE = "release_date";
    public static final String OMI_POSTER_PATH = "poster_path";
    public static final String OMI_POPULARITY = "popularity";
    public static final String OMI_VOTE_AVERAGE = "vote_average";


    @Override
    public ParseResult parse( Context context, String json ){

        ParseResult result = null;
        ArrayList<ContentValues> movies = new ArrayList<ContentValues>();
        int page =  -1;
        int totalPages = -1;
        try {


            JSONObject pageJson = new JSONObject(json);
            page = pageJson.getInt(OPI_PAGE);
            totalPages = pageJson.getInt(OPI_TOTAL_PAGES);
            JSONArray moviesArray = pageJson.getJSONArray(OPI_RESULTS);

            ContentValues cv;
            JSONObject movie = null;

            long movieId;
            String originalTitle;
            String overview;
            String releaseDate;
            String posterPath;
            double popularity;
            double voteAverage;

            for (int i = 0; i < moviesArray.length(); i++) {
                movie = moviesArray.getJSONObject(i);
                movieId = movie.getLong(OMI_MOVIE_ID);
                originalTitle = movie.getString(OMI_ORIGINAL_TITLE);
                overview = movie.getString(OMI_OVERVIEW);
                releaseDate = movie.getString(OMI_RELEASE_DATE);
                posterPath = movie.getString(OMI_POSTER_PATH);
                popularity = movie.getDouble(OMI_POPULARITY);
                voteAverage = movie.getDouble(OMI_VOTE_AVERAGE);

                cv = new ContentValues();
                cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                cv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
                cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, posterPath);
                cv.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
                cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);

                movies.add(cv);

            }
        }catch(JSONException jex){
            Log.e(LOG_TAG, jex.getMessage(), jex);
        }

        result = new ParseResult( ParseResult.OK, movies, null, page, totalPages );

        return result;
    }
}
