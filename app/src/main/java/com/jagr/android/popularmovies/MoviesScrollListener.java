package com.jagr.android.popularmovies;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.AbsListView;

/**
 * Created by Antonio on 15-08-30.
 * Listen to the scrolling action of a gridView and allows to specify a
 * listener which is going to get notified when the scroll
 * is getting to the last items.
 */
public class MoviesScrollListener implements AbsListView.OnScrollListener {

    private static final String LOG_TAG = MoviesScrollListener.class.getSimpleName();

    private boolean mIsLoading = false;
    private int mPreviousTotal = 0;

    private Context mContext;
    private OnMoviesScrollListener mListener;


    public MoviesScrollListener(Context context, OnMoviesScrollListener listener){
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mIsLoading) {
            if (totalItemCount > mPreviousTotal) {
                mIsLoading = false;
                mPreviousTotal = totalItemCount;
            }
        }else if( mPreviousTotal > totalItemCount ){
            mPreviousTotal = totalItemCount;
        }


        if (!mIsLoading && (firstVisibleItem + visibleItemCount == totalItemCount)
            && totalItemCount > 0 && firstVisibleItem > 0 && (mPreviousTotal <= totalItemCount)
            && getCurrentPage() <= getTotalPages() ) {

            //Calling a callback function
            mListener.onMoviesScrollReachedEnd( getCurrentPage() + 1 );
            mIsLoading = true;
        }
    }

    private int getCurrentPage(){
        Log.d(LOG_TAG, mContext .toString() );
        return PreferenceManager.getDefaultSharedPreferences(mContext).
                getInt(mContext.getString(R.string.movies_page), Integer.parseInt(mContext.getString(R.string.movies_page_default)));
    }

    private int getTotalPages(){
        return PreferenceManager.getDefaultSharedPreferences(mContext).
                getInt(mContext.getString(R.string.movies_total_pages),
                        Integer.parseInt(mContext.getString(R.string.movies_total_pages_default)));
    }

}
