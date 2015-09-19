package com.jagr.android.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.jagr.android.popularmovies.util.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by Antonio on 15-08-22.
 * Utility for using in test cases
 */
public class TestUtil extends AndroidTestCase {

    static final long TEST_DATE = 1431666000000L;

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createMovieValues( ){
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 76341);
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, "/kqjL17yufvn9OVLyXYpvtyrFfak.jpg");
        movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,"Mad Max: Fury Road");
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "An apocalyptic story set in the furthest" +
                "reaches of our planet, in a stark desert landscape where humanity is broken, and most" +
                " everyone is crazed fighting for the necessities of life. Within this world exist two" +
                " rebels on the run who just might be able to restore order. There's Max, a man of action" +
                " and a man of few words, who seeks peace of mind following the loss of his wife and child" +
                " in the aftermath of the chaos. And Furiosa, a woman of action and a woman who believes her" +
                " path to survival may be achieved if she can make it across the desert back to her childhood" +
                " homeland.");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 57.5001);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, TEST_DATE);
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,7.8);
        return movieValues;
    }




    static long insertMadMaxValues(Context context) {
        // insert our test records into the database
        MovieDBHelper dbHelper = new MovieDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = createMovieValues();

        long movieRowId;
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Movie Values", movieRowId != -1);

        db.close();
        return movieRowId;
    }



    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }

        assertFalse(valueCursor.moveToNext());

    }


    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }


}
