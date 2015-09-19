package com.jagr.android.popularmovies;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.jagr.android.popularmovies.data.MovieContract;
import com.jagr.android.popularmovies.data.MoviesRequest;
import com.jagr.android.popularmovies.data.model.Movie;
import com.jagr.android.popularmovies.data.model.MovieResponse;
import com.jagr.android.popularmovies.util.Utility;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Main fragment of the app, shows a GridView which contains posters of the
 * movies obteined from the API
 */
public class MoviesFragment extends Fragment implements OnMoviesScrollListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private static final String MOVIE_KEY = "MOVIE_KEY";
    private static final int MOVIES_LOADER = 1;

    @Bind(R.id.gridview_thumbnail)protected GridView mGridView;

    private ImageViewArrayAdapter mMoviesAdapter;
    private MoviesScrollListener mMoviesScrollListener;


    private SpiceManager spiceManager = new SpiceManager(
            JacksonSpringAndroidSpiceService.class);


    private WeakReference<MoviesFragment> mf =  new WeakReference<MoviesFragment>(this);
    private MoviesHandler mHandler = new MoviesHandler(mf);
    private MoviesObserver mMoviesObserver = new MoviesObserver();

    public MoviesFragment() {}


    /**
     * A callback interface that all activities containing this fragment must
     * implement.
     */
    public interface Callback {
        public void onItemSelected(Movie movie);
        public void setDefaultItem(Movie movie);
    }

    /**
     * Handles
     */
    private static class MoviesHandler extends Handler{

        private WeakReference<MoviesFragment> mf;

        public MoviesHandler( WeakReference<MoviesFragment> mf ){
            this.mf = mf;
        }

        @Override
        public void handleMessage(Message msg){
            mf.get().getCallBack().setDefaultItem((Movie)  mf.get().mMoviesAdapter.getItem(0));
        }
    }

    /**
     * Listen for changes in the db and updates the main grid accordingly
     */
    private class MoviesObserver extends ContentObserver{

        public MoviesObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            initFavoriteMoviesLoader();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            initFavoriteMoviesLoader();
        }
    }

    /**
     * Set up the main fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View moviesFragment = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this,moviesFragment);

        mMoviesScrollListener = new MoviesScrollListener(getActivity(), this);

        mMoviesAdapter = new ImageViewArrayAdapter(getActivity(),
                R.layout.grid_item_thumbnail,
                R.id.grid_item_thumbnail_imageView);


        /**
         * Setting up grid view
         */
        mGridView.setAdapter(mMoviesAdapter);
        mGridView.setOnScrollListener(mMoviesScrollListener);

        return moviesFragment;
    }

    /**
     * Handle the onStart movies fragment
     */
    @Override
    public void onStart() {
        spiceManager.start(getActivity());
        if( mMoviesAdapter.getCount() <= 0 ){
            updateMovies(MovieContract.DEFAULT_PAGE);
        }
        super.onStart();
    }

    /**
     * Manage  click events on items into the grid
     * @param position
     */
    @OnItemClick(R.id.gridview_thumbnail)
    void onItemClick(int position) {
        Movie item = (Movie) mMoviesAdapter.getItem(position);
        getCallBack().onItemSelected(item);

    }

    /**
     * Callback object to communicate with the activity that contains
     * this fragment
     * @return
     */
    private Callback getCallBack( ){
       return (Callback)getActivity();
    }


    /**
     * Check if there is an instance of this fragment saved an use the information
     * to reconstruct it
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if( null != savedInstanceState &&  savedInstanceState.containsKey(MOVIE_KEY) ){
            ArrayList<Movie> savedMovies = savedInstanceState.getParcelableArrayList(MOVIE_KEY);
            if( null != savedMovies && savedMovies.size() > 0 ) {
                mMoviesAdapter.addAll(savedMovies);
            }
        }
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Save the list already downloaded
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        ArrayList<Parcelable> movieList = new ArrayList<Parcelable>();
        for( int i = 0; i < mMoviesAdapter.getCount(); i++ ){
            movieList.add(mMoviesAdapter.getItem( i ) );
        }
        outState.putParcelableArrayList( MOVIE_KEY, movieList );
        super.onSaveInstanceState(outState);
    }

    /**
     * Fetch information from either a ReST API or a database
     */
    private void updateMovies( int page ){
        String orderBy = PreferenceManager.getDefaultSharedPreferences(getActivity()).
                getString(getActivity().getString(R.string.pref_key_movies_sort_order),
                        getActivity().getString(R.string.pref_values_movies_sort_order_default));

        if( !orderBy.equals( getActivity().getString( R.string.pref_values_movies_sort_order_favorites ) ) ) {

            MoviesRequest request = new MoviesRequest(getActivity(),String.valueOf(page), orderBy,
                    getActivity().getString(R.string.api_key));

            spiceManager.execute(request, new ListMoviesRequestListener());
        } else {//getting favorites movies from db
            if( page == MovieContract.DEFAULT_PAGE ) {
                initFavoriteMoviesLoader();
                getActivity().getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI,true,mMoviesObserver);
            }
        }
    }

    /**
     * Initialize a loader to fetch information from the movies database
     */
    private void initFavoriteMoviesLoader(){
        if( null == getLoaderManager().getLoader(MOVIES_LOADER) ) {
            getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        }else{
            getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        }
    }

    /**
     * Call fetchMovieTask to get more information from the api
     * @param nextPage page of movies we want to get
     */
    @Override
    public void onMoviesScrollReachedEnd(int nextPage) {
        updateMovies(nextPage);
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();

        super.onStop();
    }

    /**
     * Set all views to null
     */
    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        if( null != getLoaderManager().getLoader(MOVIES_LOADER) ){
            getActivity().getContentResolver().unregisterContentObserver( mMoviesObserver );
            getLoaderManager().destroyLoader(MOVIES_LOADER);
        }
        super.onDestroyView();
    }

    /**
     * Creates a new loader to fetch information from the movies database
     * @param id - identifier of the loader to be created
     * @param args - arguments used to create the loader
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
       return new CursorLoader( getActivity(), MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
    }

    /**
     * Handles the return of information from the loader
     * @param loader - loader to be handled
     * @param data - data returned
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int movieIdIndex;
        int moviePosterIndex;
        int movieTitleIndex;
        int movieReleaseDateIndex;
        int movieRatingIndex;
        int movieVoteAverageIndex;
        int movieSynopisIndex;
        mMoviesAdapter.clear();
        if ( data.moveToFirst() ) {
            movieIdIndex = data.getColumnIndex( MovieContract.MovieEntry.COLUMN_MOVIE_ID );
            moviePosterIndex = data.getColumnIndex( MovieContract.MovieEntry.COLUMN_MOVIE_POSTER );
            movieTitleIndex = data.getColumnIndex( MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE );
            movieReleaseDateIndex = data.getColumnIndex( MovieContract.MovieEntry.COLUMN_RELEASE_DATE );
            movieRatingIndex = data.getColumnIndex( MovieContract.MovieEntry.COLUMN_POPULARITY );
            movieVoteAverageIndex = data.getColumnIndex( MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE );
            movieSynopisIndex = data.getColumnIndex( MovieContract.MovieEntry.COLUMN_OVERVIEW );
            do {
                mMoviesAdapter.add(new Movie( data.getString(movieTitleIndex), data.getString(moviePosterIndex),
                        data.getString(movieSynopisIndex),data.getDouble(movieRatingIndex),
                        data.getString(movieReleaseDateIndex), data.getDouble(movieVoteAverageIndex),
                        data.getLong( movieIdIndex ) ));
            } while (data.moveToNext());
            if( mMoviesAdapter.getCount() > 0 ) {
                mHandler.sendMessage( new Message() );
            }

        }
        data.close();
        mMoviesAdapter.notifyDataSetChanged();
    }

    /**
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Listen for the response of the service that fetches the movies
     */
    private class ListMoviesRequestListener implements
            RequestListener<MovieResponse> {

        @Override
        public void onRequestFailure(SpiceException e) {
            Toast toast = Toast.makeText(getActivity(),
                    getActivity().getString(R.string.no_internet_connection) ,Toast.LENGTH_LONG);
            toast.show();

            Log.e(LOG_TAG, e.getMessage(), e);
        }

        @Override
        public void onRequestSuccess(MovieResponse movieResponse) {
            if (movieResponse == null || movieResponse.getMovies() == null ||
                    movieResponse.getMovies().size() < 1) {
                return;
            }

            mMoviesAdapter.addAll(movieResponse.getMovies());

            mMoviesAdapter.notifyDataSetChanged();

            getCallBack().setDefaultItem(movieResponse.getMovies().get(0) );

            Utility.setCurrentPage(getActivity(), movieResponse.getPage(),
                    movieResponse.getTotalPages());


        }
    }

}
