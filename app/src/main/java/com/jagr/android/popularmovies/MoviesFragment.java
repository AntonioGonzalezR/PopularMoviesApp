package com.jagr.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.jagr.android.popularmovies.data.FetchMoviesTask;
import com.jagr.android.popularmovies.data.MovieContract;
import com.jagr.android.popularmovies.util.MovieValues;

/**
 * Main fragment of the app, shows a GridView which contains posters of the
 * movies obteined from the API
 */
public class MoviesFragment extends Fragment implements OnMoviesScrollListener{

    private ImageViewArrayAdapter mMoviesAdapter;

    private MoviesScrollListener mMoviesScrollListener;

    /**
     *
     */
    public MoviesFragment() {}


    /**
     *
     */
    @Override
    public void onStart() {
        super.onStart();
        updateMovies(MovieContract.DEFAULT_PAGE);
    }

    /**
     * Set up the main fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View moviesFragment = inflater.inflate(R.layout.fragment_main, container, false);

        mMoviesAdapter = new ImageViewArrayAdapter(getActivity(),
                R.layout.grid_item_thumbnail,
                R.id.grid_item_thumbnail_imageView);

        GridView gridView = (GridView)moviesFragment.findViewById(R.id.gridview_thumbnail);
        gridView.setAdapter( mMoviesAdapter );

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContentValues item = mMoviesAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(MovieParcelable.NAME, new MovieParcelable(MovieValues.getMovieTitle(item),
                                MovieValues.getMoviePoster(item),
                                MovieValues.getMovieSynopsis(item),
                                MovieValues.getMovieRating(item),
                                MovieValues.getMovieReleaseDate(item),
                                MovieValues.getMovieVoteAverage(item)));
                startActivity(intent);
            }
        });

        mMoviesScrollListener = new MoviesScrollListener(getActivity(), this);

        gridView.setOnScrollListener(mMoviesScrollListener);

        return moviesFragment;
    }


    /**
     *
     */
    private void updateMovies( int page ){
        String orderBy = PreferenceManager.getDefaultSharedPreferences(getActivity() ).
                getString(getActivity().getString(R.string.pref_key_movies_sort_order),
                        getActivity().getString(R.string.pref_values_movies_sort_order_default));

        new FetchMoviesTask(getActivity(), mMoviesAdapter).execute( orderBy, String.valueOf(page),
                getActivity().getString(R.string.api_key) );
    }

    /**
     * Call fetchMovieTask to get more information from the api
     * @param nextPage page of movies we want to get
     */
    @Override
    public void onMoviesScrollReachedEnd(int nextPage) {
        updateMovies(nextPage);
    }
}
