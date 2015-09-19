package com.jagr.android.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Antonio on 15-08-23.
 */
public class TestUriMatcher extends AndroidTestCase {

    public static final int TEST_MOVIE_ID = 1;

    // content://com.example.android.sunshine.app/movie"
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;

    // content://com.example.android.sunshine.app/movie/#"
    private static final Uri TEST_MOVIE_WITH_ID = MovieContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);

    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);
        assertEquals("Error: The MOVIE WITH ID URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_ID), MovieProvider.MOVIE_WITH_ID);

    }


}
