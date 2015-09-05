package com.jagr.android.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.jagr.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by Antonio on 15-08-23.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }


    public void testGetType() {
        // content://com.jagr.android.popularmovies/movie/
        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.jagr.android.popularmovies/movie
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        int testMovieId = 94074;
        // content://com.jagr.android.popularmovies/movie/94074
        type = mContext.getContentResolver().getType(
                MovieEntry.buildMovieUri(testMovieId));
        // vnd.android.cursor.item/com.jagr.android.popularmovies/movie
        assertEquals("Error: the MovieEntry CONTENT_URI with movieId should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieEntry.CONTENT_ITEM_TYPE, type);

    }


    public void testBasicMovieQuery() {
        // insert our test records into the database
        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtil.createMovieValues();
        long movieRowId = db.insert(MovieEntry.TABLE_NAME, null, testValues);



        assertTrue("Unable to Insert MovieEntry into the Database", movieRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtil.validateCursor("testMovieQuery", movieCursor, testValues);
    }

    public void testBasicWeatherQuery() {

        ContentValues testValues = TestUtil.createMovieValues();

        // Test  content provider's query movie by id
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.buildMovieUri( 76341 ),
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtil.validateCursor("testBasicWeatherQuery", movieCursor, testValues);
    }

    public void testInsertReadProvider() {

        ContentValues testValues = TestUtil.createMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtil.TestContentObserver tco = TestUtil.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);


        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtil.validateCursor("testInsertReadProvider. Error validating MovieEntry.",
                cursor, testValues);

    }


    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our location delete.
        TestUtil.TestContentObserver movieObserver = TestUtil.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);


        deleteAllRecordsFromProvider();


        movieObserver.waitForNotificationOrFail();


        mContext.getContentResolver().unregisterContentObserver(movieObserver);

    }

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());

        cursor.close();

    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    /*
       This test uses the provider to insert and then update the data. Uncomment this test to
       see if your update location is functioning correctly.
    */
    public void testUpdateLocation() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtil.createMovieValues();

        Uri movieUri = mContext.getContentResolver().
                insert(MovieEntry.CONTENT_URI, values);
        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);
        Log.d(LOG_TAG, "New row id: " + movieRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MovieEntry._ID, movieRowId);
        updatedValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, "The god father");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor movieCursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null);

        TestUtil.TestContentObserver tco = TestUtil.getTestContentObserver();
        movieCursor.registerContentObserver(tco);



        int count = mContext.getContentResolver().update(
                MovieEntry.CONTENT_URI, updatedValues, MovieEntry._ID + "= ?",
                new String[] { Long.toString(movieRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        tco.waitForNotificationOrFail();

        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,   // projection
                MovieEntry._ID + " = " + movieRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtil.validateCursor("testUpdateLocation.  Error validating location entry update.",
                cursor, updatedValues);

        cursor.close();
    }



}
