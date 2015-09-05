package com.jagr.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jagr.android.popularmovies.util.Utility;
import com.squareup.picasso.Picasso;

/**
 * Created by Antonio on 15-08-25.
 * A fragment that contains the information details of a movie.
 */
public class DetailMovieFragment extends Fragment {

    private static final String LOG_TAG = DetailMovieFragment.class.getSimpleName();


    public DetailMovieFragment() {
    }


    /**
     * Set up the detail view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();

        Bundle data = intent.getExtras();
        MovieParcelable movie = data.getParcelable(MovieParcelable.NAME);


        ImageView poster = (ImageView)fragment.findViewById(R.id.thumbnail_imageView);
        TextView movieTitle = (TextView)fragment.findViewById(R.id.movie_title_textView);
        TextView synopsis = (TextView)fragment.findViewById(R.id.synopsis_textView);
        TextView releaseDate = (TextView)fragment.findViewById(R.id.movie_release_date_textView);
        TextView voteAverage = (TextView)fragment.findViewById(R.id.movie_vote_average_textView);
        if( null != movie ) {
            Picasso.with(getActivity()).load(Utility.getImageUrl(getActivity()) + movie.getMoviePoster())
                    .placeholder(R.drawable.placeholder)
                    .into(poster);
            poster.setContentDescription(movie.getMovieTitle());
            synopsis.setText(movie.getMovieSynopsis());
            movieTitle.setText(movie.getMovieTitle());
            releaseDate.setText(Utility.getReleaseDate(movie.getMovieReleaseDate()));
            voteAverage.setText(getString(R.string.movie_vote_average_default_activity_detail, movie.getMovieVoteAverage()));
            Log.d(LOG_TAG, "posterUrl:" + movie.getMoviePoster());
        }

        return fragment;
    }
}
