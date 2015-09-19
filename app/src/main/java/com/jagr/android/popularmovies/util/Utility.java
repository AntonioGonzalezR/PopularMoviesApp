package com.jagr.android.popularmovies.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.GridView;

import com.jagr.android.popularmovies.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Antonio on 15-08-17.
 */
public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();
    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat orgiginal_sf = new SimpleDateFormat("yyyy-MM-dd");
    private static final String URL_MOVIE_BASE  = "http://image.tmdb.org/t/p/";

    /**
     * Check if the network is available
     * @param c - context in which the method was invoked
     * @return true if network is available, false otherwise.
     */
    public static boolean isNetworkAvailable(Context c){
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    /**
     *
     * @return
     */
    public static String getImageUrl(Context c){
        return URL_MOVIE_BASE + c.getString(R.string.api_image_path) + "/";
    }

    public static String getImageDetailUrl(){
        return "http://image.tmdb.org/t/p/w185/";
    }

    public static String getReleaseDate(String date){
        String releaseDate = "1900";
        try{
            releaseDate = sf.format( orgiginal_sf.parse(date) );
        }catch (ParseException pEx){
            Log.e(LOG_TAG, pEx.getMessage(), pEx);
        }
        return releaseDate;
    }

    private static boolean isLandscape( Context c){
        Resources r = Resources.getSystem();
        return r.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }


    /**
     *
     * @param c
     * @return
     */
    public static GridView.LayoutParams getGridViewLayOutParams(Context c){
        int screenHeight = (int) c.getResources().getDimension(R.dimen.activity_movie_poster_height);
        return new GridView.LayoutParams( GridView.LayoutParams.MATCH_PARENT  , screenHeight );
    }


    /**
     * Save the last page downloaded by the app
     * @param c - context from where the method was called
     * @param page - last page downloaded
     * @param totalPages - total number of pages in the API
     */
    public static void setCurrentPage(Context c, int page, int totalPages){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(c).edit();

        editor.putInt(c.getString(R.string.movies_page), page);
        editor.putInt(c.getString(R.string.movies_total_pages), totalPages);
        editor.commit();

    }

    public static Intent createSharedIntent( String sharedTrailer ) {
        Intent sharedIntent = new Intent();
        sharedIntent.setAction(Intent.ACTION_SEND);
        sharedIntent.putExtra(Intent.EXTRA_TEXT, null != sharedTrailer ? sharedTrailer : "No Trailer Available");
        sharedIntent.setType("text/plain");
        return sharedIntent;
    }


}
