package com.jagr.android.popularmovies;

/**
 * Created by Antonio on 15-08-30.
 * Define the contract to be used by classes that want to be notified when
 * a Scroll is reaching the last items.
 * See MoviesFragment and MoviesScrollListener.
 */
public interface OnMoviesScrollListener {
    void onMoviesScrollReachedEnd(int nextPage);
}
