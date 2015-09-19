package com.jagr.android.popularmovies.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.jagr.android.popularmovies.R;
import com.jagr.android.popularmovies.data.model.MovieResponse;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

/**
 * Created by Antonio on 15-09-07.
 * Create service that perform a REST request to fetch the movies information
 */
public class MoviesRequest extends SpringAndroidSpiceRequest<MovieResponse> {

    private static final String LOG_TAG = MoviesRequest.class.getSimpleName();
    private static final String API_BASE_URL = "discover/movie";
    private static final String SORT_PARAM = "sort_by";
    private static final String API_KEY_PARAM = "api_key";
    private static final String PAGE_PARAM = "page";

    private Context mContext;
    private String mPage;
    private String mOrder;
    private String mKey;

    /**
     * Create an object MovieRequest that will perform a request to the API
     * @param context - context in which the request is being created
     * @param page - page we want to get
     * @param order - information's order
     * @param key - security string necesary to perform the request
     */
    public MoviesRequest( Context context, String page, String order, String key ) {
        super(MovieResponse.class);
        this.mContext = context;
        this.mPage = page;
        this.mOrder = order;
        this.mKey = key;
    }

    /**
     * Initialize the service to perform the ReST request
     * @return a MovieResponse that represents the information fetched
     * @throws Exception
     */
    @Override
    public MovieResponse loadDataFromNetwork() throws Exception {
        Uri builtUri = Uri.parse(mContext.getString(R.string.api_base_url) + API_BASE_URL).buildUpon()
                .appendQueryParameter(SORT_PARAM, mOrder)
                .appendQueryParameter(PAGE_PARAM, mPage)
                .appendQueryParameter(API_KEY_PARAM, mKey)
                .build();
        Log.d(LOG_TAG, builtUri.toString());
        return getRestTemplate().getForObject(builtUri.toString(), MovieResponse.class);
    }
}
