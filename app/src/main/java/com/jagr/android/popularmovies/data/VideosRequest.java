package com.jagr.android.popularmovies.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.jagr.android.popularmovies.R;
import com.jagr.android.popularmovies.data.model.VideoResponse;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

/**
 * Created by Antonio on 15-09-08.
 * Create service that perform a REST request to fetch the videos information
 */
public class VideosRequest extends SpringAndroidSpiceRequest<VideoResponse> {

    private static final String LOG_TAG = VideosRequest.class.getSimpleName();
    private static final String REQUEST_BASE_URL = "movie/";
    private static final String REQUEST_VIDEOS_URL = "videos";
    private static final String API_KEY_PARAM = "api_key";

    private Context mContext;
    private long mMovieId;
    private String mKey;


    /**
     * Create an object VideoRequest that will perform a request to the API
     * @param context - context in which the request is being created
     * @param movieId - Id of the movie we want to get the trailers
     * @param key - security string necessary to perform the request
     */
    public VideosRequest(Context context, long movieId, String key) {
        super(VideoResponse.class);
        this.mContext = context;
        this.mMovieId = movieId;
        this.mKey = key;
    }


    /**
     * Initialize the service to perform the ReST request
     * @return a VideoResponse that represents the information fetched
     * @throws Exception
     */
    @Override
    public VideoResponse loadDataFromNetwork() throws Exception {

        Uri builtUri = Uri.parse( mContext.getString(R.string.api_base_url) + REQUEST_BASE_URL).buildUpon()
                .appendEncodedPath(String.valueOf( mMovieId ))
                .appendEncodedPath( REQUEST_VIDEOS_URL )
                .appendQueryParameter(API_KEY_PARAM, mKey)
                .build();


        Log.d(LOG_TAG, builtUri.toString());

        return getRestTemplate().getForObject(builtUri.toString(), VideoResponse.class);
    }
}
