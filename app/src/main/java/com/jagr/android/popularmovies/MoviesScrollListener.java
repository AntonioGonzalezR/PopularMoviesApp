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
    private int scrollState;

    private Context mContext;
    private OnMoviesScrollListener mListener;

    private int mCurrentFirstVisibleItem = 0;
    private int mCurrentVisibleItemCount = 0;
    private int mCurrentTotalItemCount = 0;


    public MoviesScrollListener(Context context, OnMoviesScrollListener listener){
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
        this.isScrollCompleted();
    }


    private void isScrollCompleted(  ) {
        if (this.mCurrentVisibleItemCount > 0 && this.scrollState == SCROLL_STATE_IDLE) {

            if (mIsLoading) {
                if (this.mCurrentTotalItemCount > mPreviousTotal) {
                    mIsLoading = false;
                    mPreviousTotal = this.mCurrentTotalItemCount;
                }
            } else if (mPreviousTotal > this.mCurrentTotalItemCount) {
                mPreviousTotal = this.mCurrentTotalItemCount;
            }
            if (!mIsLoading && (this.mCurrentFirstVisibleItem + this.mCurrentVisibleItemCount == this.mCurrentTotalItemCount)
                    && this.mCurrentTotalItemCount > 0 && this.mCurrentFirstVisibleItem > 0 && (mPreviousTotal <= this.mCurrentTotalItemCount)
                    && getCurrentPage() <= getTotalPages()) {
                mIsLoading = true;
                mListener.onMoviesScrollReachedEnd(getCurrentPage() + 1);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.mCurrentFirstVisibleItem = firstVisibleItem;
        this.mCurrentVisibleItemCount = visibleItemCount;
        this.mCurrentTotalItemCount = totalItemCount;
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
