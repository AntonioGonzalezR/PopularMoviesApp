package com.jagr.android.popularmovies.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.GridView;

import com.jagr.android.popularmovies.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Antonio on 15-08-17.
 */
public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();
    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy");
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

    public static String getReleaseDate(long date){
        return sf.format( new Date( date ) );
    }

    private static boolean isLandscape( Context c){
        Resources r = Resources.getSystem();
        return r.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }



    public static GridView.LayoutParams getGridViewLayOutParams(Context c){
        //Resources r = Resources.getSystem();
        //DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        //int screenWidth = metrics.widthPixels / 2;
        //int screenHeight = metrics.heightPixels / 2;
        //Log.d(LOG_TAG, screenWidth + "");
        //Log.d(LOG_TAG, screenHeight + "");
        //px = dp * (dpi / 160)
        //final float scale = r.getDisplayMetrics().density;
        //float dp = 435/scale;
        //float dp = c.getResources().getDimension( R.dimen.activity_movie_poster_height );
        //int screenHeight = (int)(dp * scale);
        //Log.d(LOG_TAG, "Scale " + scale);
        //Log.d( LOG_TAG,"Generic size " + dp  );
        //Log.d( LOG_TAG,"screenHeight " + screenHeight  );
        //Log.d( LOG_TAG,"valueInPixels " + valueInPixels  );
        //Log.d(LOG_TAG, "Landscape " + isLandscape( c ) );
        /*if( !isLandscape( c ) )
            return new GridView.LayoutParams( GridView.LayoutParams.MATCH_PARENT, 400 );
        else*/
            //return new GridView.LayoutParams( GridView.LayoutParams.MATCH_PARENT, screenHeight );

        //Log.d( LOG_TAG, "dp LandScape: " + dp );
        int screenHeight = (int) c.getResources().getDimension(R.dimen.activity_movie_poster_height);
        return new GridView.LayoutParams( GridView.LayoutParams.MATCH_PARENT  , screenHeight );
    }

}
