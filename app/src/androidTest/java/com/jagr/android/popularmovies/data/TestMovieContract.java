package com.jagr.android.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Antonio on 15-08-23.
 */
public class TestMovieContract extends AndroidTestCase {

    public void testBuildMovieUri(){
        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(1);

        assertNotNull("Error: Null Uri returned. ", movieUri);
        assertEquals("Error: Movie movieId not properly appended to the end of the Uri" + movieUri.toString(),
                "1", movieUri.getLastPathSegment());
        assertEquals("Error: Weather location Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.jagr.android.popularmovies/movie/1");
    }
}
