package com.jagr.android.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import static com.jagr.android.popularmovies.data.MovieContract.*;

/**
 * Created by Antonio on 15-08-23.
 * ContentProvider implementation that offers read and write access
 * to the movie db used by the app.
 */
public class MovieProvider extends ContentProvider {
    // The URI Matcher used by this content provider.

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;

    private static final SQLiteQueryBuilder sMovieByIdQueryBuilder;


    static{
        sMovieByIdQueryBuilder = new SQLiteQueryBuilder();
        sMovieByIdQueryBuilder.setTables( MovieEntry.TABLE_NAME );
    }


    //movie.movie_id = ?
    private static final String sMovieIdSelection =
            MovieEntry.TABLE_NAME+
                    "." + MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    /**
     * Initialize an Uri matcher object
     *
     */
    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority  =  CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, PATH_MOVIE, MOVIE );
        uriMatcher.addURI(authority, PATH_MOVIE + "/#", MOVIE_WITH_ID );

        return uriMatcher;
    }

    /**
     * Create an instance of the MovieDBHelper which is used to
     * obtain a readable or writable instances of the movie db.
     * @return operation status
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    /**
     * Determine the kind of URI we are trying to use and
     * returns if is gonna return a collection of objects or a single item
     * @param uri resource we want to use
     * @return constant that represent the type of resource we want to use
     */
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Queries the database with the given parameters
     * @param uri - resource we want to query
     * @param projection - results we want to get
     * @param selection - filter to apply
     * @param selectionArgs - values to use in the filter
     * @param sortOrder - ordering
     * @return cursor with the rows fetched after applying the parameters
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "movie/*"
            case MOVIE_WITH_ID:
            {
                retCursor = getMovieById(uri, projection, sortOrder);
                break;
            }
            // "movie"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                    MovieEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    /**
     * Get a movie by id
     * @param uri - uri that contains information of the resource we want to get.
     * @param projection - fields we would like to get from the query.
     * @param sortOrder - information order.
     * @return Cursor with the result of the query
     */
    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {

        long  movieId = MovieEntry.getMovieIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sMovieIdSelection;

        selectionArgs = new String[]{ String.valueOf( movieId ) };

        Log.d(LOG_TAG, "Searching in db for the movie" + movieId);

        return sMovieByIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }



    /**
     * Insert a new row in a table of the movie db
     * @param uri - Contain information of the resource we want to insert
     * @param values - values we want to insert.
     * @return uri of the new resource
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MOVIE: {

                Log.d( LOG_TAG,values.toString() );
                //normalizeDate(values);
                long _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    /**
     * Insert a new row in a table of the movie db
     * @param uri - Contain information of the resource we want to delete
     * @param selection - fields used to filter the information to be deleted.
     * @param selectionArgs - values of the fields userd to filter the information to be deleted .
     * @return operation status
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);

        if( null == selection ){
            selection = "1";
        }

        switch (match){
            case MOVIE:
                rowsDeleted = db.delete( MovieEntry.TABLE_NAME, selection, selectionArgs );
                break;
            case MOVIE_WITH_ID:
                long  movieId = MovieEntry.getMovieIdFromUri(uri);
                selectionArgs = new String[]{ String.valueOf( movieId ) };
                rowsDeleted = db.delete( MovieEntry.TABLE_NAME, sMovieIdSelection,  selectionArgs );
                Log.d( LOG_TAG, "rows deleted::" + rowsDeleted  );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if( rowsDeleted > 0 ){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * Update values in the databes
     * @param uri - information of the resource to be updated
     * @param values  - data used to update the resources
     * @param selection - fields used to filter the information to be updated.
     * @param selectionArgs - values of the fields userd to filter the information to be updated.
     * @return operation status
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;
        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIE:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if( rowsUpdated > 0 || selection.isEmpty() ){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }


    /**
     * Performs a massiver insertion of information
     * @param uri - information of the table where the information is going to be inserted
     * @param values information to be inserted
     * @return operation status
     */

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }


}
