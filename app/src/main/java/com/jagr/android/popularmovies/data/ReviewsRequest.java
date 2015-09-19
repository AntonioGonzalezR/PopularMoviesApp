package com.jagr.android.popularmovies.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.jagr.android.popularmovies.R;
import com.jagr.android.popularmovies.data.model.ReviewResponse;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

/**
 * Created by Antonio on 15-09-12.
 * Create service that perform a REST request to fetch the reviews information
 */
public class ReviewsRequest extends SpringAndroidSpiceRequest<ReviewResponse> {

    private static final String LOG_TAG = ReviewsRequest.class.getSimpleName();
    private static final String REQUEST_BASE_URL = "movie/";
    private static final String REQUEST_REVIEWS_URL = "reviews";
    private static final String API_KEY_PARAM = "api_key";
    private static final String PAGE_PARAM = "page";


    private long mMovieId;
    private Context mContext;
    private String mPage;
    private String mKey;


    /**
     * Create an object ReviewRequest that will perform a request to the API
     * @param context - context in which the request is being created
     * @param movieId - Id of the movie we want to get the reviews
     * @param page - page we want to get
     * @param key - security string necessary to perform the request
     */
    public ReviewsRequest( Context context, long movieId, String page, String key ) {
        super(ReviewResponse.class);
        this.mContext = context;
        this.mPage = page;
        this.mMovieId = movieId;
        this.mKey = key;
    }


    /**
     * Initialize the service to perform the ReST request
     * @return a ReviewResponse that represents the information fetched
     * @throws Exception
     */
    @Override
    public ReviewResponse loadDataFromNetwork() throws Exception {
        Uri builtUri = Uri.parse( mContext.getString(R.string.api_base_url) + REQUEST_BASE_URL).buildUpon()
                .appendEncodedPath(String.valueOf(mMovieId))
                .appendEncodedPath(REQUEST_REVIEWS_URL)
                .appendQueryParameter(PAGE_PARAM, mPage)
                .appendQueryParameter(API_KEY_PARAM, mKey)
                .build();

        Log.d(LOG_TAG, builtUri.toString());

        return getRestTemplate().getForObject(builtUri.toString(), ReviewResponse.class);
    }

}
