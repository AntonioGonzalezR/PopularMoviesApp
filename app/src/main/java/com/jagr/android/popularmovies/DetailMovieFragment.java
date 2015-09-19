package com.jagr.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jagr.android.popularmovies.data.MovieContract;
import com.jagr.android.popularmovies.data.ReviewsRequest;
import com.jagr.android.popularmovies.data.VideosRequest;
import com.jagr.android.popularmovies.data.model.Movie;
import com.jagr.android.popularmovies.data.model.Review;
import com.jagr.android.popularmovies.data.model.ReviewResponse;
import com.jagr.android.popularmovies.data.model.Video;
import com.jagr.android.popularmovies.data.model.VideoResponse;
import com.jagr.android.popularmovies.util.Utility;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Antonio on 15-08-25.
 * A fragment that contains the information details of a movie.
 */
public class DetailMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailMovieFragment.class.getSimpleName();
    private static final String REVIEWS_DEFAULT_PAGE = "1";
    private static final int MOVIE_LOADER = 1;
    private static final String TRAILERS_KEY = "TRAILERS_KEY";
    private static final String REVIEWS_KEY = "REVIEWS_KEY";

    @Bind(R.id.thumbnail_imageView)ImageView mPoster;
    @Bind(R.id.movie_title_textView)TextView mMovieTitle;
    @Bind(R.id.synopsis_textView)TextView mSynopsis;
    @Bind(R.id.movie_release_date_textView)TextView mReleaseDate;
    @Bind(R.id.movie_vote_average_textView)TextView mVoteAverage;
    @Bind(R.id.favorite_imageButton)ImageButton mFavoriteImageButton;
    @Bind(R.id.trailers_linearLayout)LinearLayout mTrailersLinearLayout;
    @Bind(R.id.reviews_linearLayout)LinearLayout mReviewsLinearLayout;
    @Bind(R.id.reviews_textView)TextView mReviewsTextView;
    @Bind(R.id.trailers_textView)TextView mTrailersTextView;
    @Bind(R.id.trailers_lineView)View mTrailersLineView;
    @Bind(R.id.reviews_lineView)View mReviewsLineView;
    private Movie mMovie;
    private String mSharedTrailer = null;

    private boolean isFavorite = false;
    private ArrayList<Review> mReviews;
    private ArrayList<Video> mTrailers;

    private SpiceManager spiceManager = new SpiceManager(
            JacksonSpringAndroidSpiceService.class);

    public DetailMovieFragment() { }

    /**
     * Interface to allow communication between the activity containing
     * this fragment and the fragment
     */
    public interface SharedIntentCallBack{
        void setSharedIntent(String text);
    }

    /**
     * Handle the onStart event of the fragment
     */
    @Override
    public void onStart() {
        spiceManager.start(getActivity());
        if(null != mMovie ) {
            VideosRequest request = new VideosRequest(getActivity(), mMovie.getMovieId(),
                    getActivity().getString(R.string.api_key));
            spiceManager.execute(request, new ListVideosRequestListener());

            ReviewsRequest reviewsRequest = new ReviewsRequest( getActivity(), mMovie.getMovieId(),
                    REVIEWS_DEFAULT_PAGE, getActivity().getString(R.string.api_key));
            spiceManager.execute(reviewsRequest, new ListReviewsRequestListener());
        }
        super.onStart();
    }


    /**
     * Set up the detail view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, fragment);

        Bundle arguments = getArguments();
        if (arguments != null) {

            mMovie = arguments.getParcelable(Movie.NAME);
        }

        if( null != mMovie ) {
            Picasso.with(getActivity()).load(Utility.getImageUrl(getActivity()) + mMovie.getMoviePoster())
                    .placeholder(R.drawable.placeholder)
                    .into(mPoster);
            mPoster.setContentDescription(mMovie.getMovieTitle());
            mSynopsis.setText(mMovie.getMovieSynopsis());
            mMovieTitle.setText(mMovie.getMovieTitle());
            mReleaseDate.setText(Utility.getReleaseDate(mMovie.getMovieReleaseDate()));
            mVoteAverage.setText(getString(R.string.movie_vote_average_default_activity_detail, mMovie.getMovieVoteAverage()));
            initFavoriteMovieLoader();
            Log.d(LOG_TAG, "posterUrl:" + mMovie.getMoviePoster());
        }else{
            mPoster.setVisibility(View.GONE);
            mSynopsis.setVisibility(View.GONE);
            mMovieTitle.setVisibility(View.GONE);
            mReleaseDate.setVisibility(View.GONE);
            mVoteAverage.setVisibility(View.GONE);
            mFavoriteImageButton.setVisibility(View.GONE);
            mTrailersLinearLayout.setVisibility(View.GONE);
            mReviewsLinearLayout.setVisibility(View.GONE);
            mReviewsTextView.setVisibility(View.GONE);
            mTrailersTextView.setVisibility(View.GONE);
            mTrailersLineView.setVisibility(View.GONE);
            mReviewsLineView.setVisibility(View.GONE);
        }

        return fragment;
    }

    /**
     * Initialize the loader used to fetch the information about the movie
     */
    private void initFavoriteMovieLoader(){
        if( null == getLoaderManager().getLoader(MOVIE_LOADER) ) {
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        }else{
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        }
    }


    /**
     * Handle menu creation of detail movie fragment
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Set up ShareActionProvider's default share intent
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Save the list already downloaded
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(TRAILERS_KEY, mTrailers );
        outState.putParcelableArrayList(REVIEWS_KEY, mReviews);
        super.onSaveInstanceState(outState);
    }

    /**
     * Verify if there is an instance of this fragment saved if there is
     * an instance use that information to recreate the fragment
     * set up this frag
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated  (Bundle savedInstanceState) {
        if( null != savedInstanceState ){

            if( savedInstanceState.containsKey(TRAILERS_KEY) ) {
                ArrayList<Video> savedTrailers = savedInstanceState.getParcelableArrayList(TRAILERS_KEY);
                createTrailersView(savedTrailers);
            }

            if( savedInstanceState.containsKey(REVIEWS_KEY) ) {
                ArrayList<Review> savedReviews = savedInstanceState.getParcelableArrayList(REVIEWS_KEY);
                createReviewsView(savedReviews);
            }

        }


        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Set up the share intent to be launch when the user want to share
     * a trailer
     * @param v
     */
    private void setSharedIntent(Video v){
        Uri webPage = Uri.parse( getActivity().getString( R.string.api_movies_url ) ).buildUpon()
                .appendQueryParameter("v",v.getKey()).build();
        mSharedTrailer = webPage.toString();
        SharedIntentCallBack  callBack = (SharedIntentCallBack)getActivity();
        callBack.setSharedIntent(mSharedTrailer);
    }

    /**
     * Handle onStop activity event
     */
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
        super.onDestroyView();
        if( null != getLoaderManager().getLoader(MOVIE_LOADER) ){
            getLoaderManager().destroyLoader(MOVIE_LOADER);
        }
        ButterKnife.unbind(this);
    }

    /**
     * Save or remove a movie from the favorites database
     */
    @OnClick(R.id.favorite_imageButton)
    public void onFavoritImageButtonClicked(  ) {
        Log.d(LOG_TAG, "Saving as favorite....." + mMovie.getMovieTitle());

        if( !isFavorite ) {
            getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                    mMovie.parseToContentValues());
            isFavorite = true;
            mFavoriteImageButton.setBackgroundResource(R.drawable.favorite_on);
        }else{
            Uri uri = MovieContract.MovieEntry.buildMovieUri( mMovie.getMovieId() );
            int rowsDeleted = getActivity().getContentResolver().delete(uri, null, null);
            if( rowsDeleted > 0 ){
                isFavorite = false;
                mFavoriteImageButton.setBackgroundResource(0);
                mFavoriteImageButton.setBackgroundResource(R.drawable.favorite);
            }
        }
        Toast toast = Toast.makeText(getActivity(),
                isFavorite ? R.string.detail_activity_saved_favorite : R.string.detail_activity_removed_favorite,
                Toast.LENGTH_SHORT);
        toast.show();
    }


    /**
     * Listen for clicks on trailer play button
     */
    private class OnTrailerClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if( Utility.isNetworkAvailable( getActivity() ) ) {
                Video v = (Video) view.getTag();
                Uri webPage = Uri.parse(getActivity().getString(R.string.api_movies_url)).buildUpon()
                        .appendQueryParameter("v", v.getKey()).build();
                String title = getActivity().getString(R.string.chooser_title);
                Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                Intent chooser = Intent.createChooser(intent, title);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(chooser);
                }
            }else{
                Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT );
            }
        }
    }

    /**
     * Listen for the response of the service that fetches reviews information
     */
    private class ListReviewsRequestListener implements
            RequestListener<ReviewResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {

        }

        @Override
        public void onRequestSuccess(ReviewResponse reviewResponse) {

            if (reviewResponse == null || reviewResponse.getReviews() == null ||
                    reviewResponse.getReviews().size() < 1) {
                return;
            }


            if( mReviewsLinearLayout.getChildCount() > 1 ){
                mReviewsLinearLayout.removeViews(0, mReviewsLinearLayout.getChildCount()-1);
            }
            mReviews = reviewResponse.getReviews();
            createReviewsView(mReviews);

        }
    }

    /**
     * Add the review views to the fragment
     * @param reviews
     */
    private void createReviewsView( ArrayList<Review> reviews ){
        CardView layOut;
        TextView tv;
        Review r;
        LayoutInflater layoutInflator = LayoutInflater.from(getActivity());
        if( null != reviews && reviews.size() > 0 ) {
            for (int i = 0; i < reviews.size(); i++) {
                r = reviews.get(i);
                layOut = (CardView) layoutInflator.inflate(R.layout.list_item_review, null);
                tv = (TextView) layOut.findViewById(R.id.review_author_textView);
                tv.setText(r.getReviewAuthor());
                tv = (TextView) layOut.findViewById(R.id.review_content_textView);
                tv.setText(r.getReviewContent());
                mReviewsLinearLayout.addView(layOut, i);
                Log.d(LOG_TAG, reviews.get(i).toString());
            }
            mReviewsLinearLayout.getChildAt(mReviewsLinearLayout.getChildCount() - 1).setVisibility(View.GONE);
        }

    }

    /**
     * Listen for the response of the service that fetches videos information
     */
    private class ListVideosRequestListener implements
            RequestListener<VideoResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {

        }

        @Override
        public void onRequestSuccess(VideoResponse videoResponse) {
            if (videoResponse == null || videoResponse.getVideos() == null ||
                    videoResponse.getVideos().size() < 1) {
                return;
            }
            if( mTrailersLinearLayout.getChildCount() > 1 ){
                mTrailersLinearLayout.removeViews(0, mTrailersLinearLayout.getChildCount()-1);
            }
            mTrailers = videoResponse.getVideos();
            createTrailersView(mTrailers);
        }

    }

    /**
     * Add trailers to the fragment
     * @param videos
     */
    private void  createTrailersView( ArrayList<Video> videos ){
        RelativeLayout layOut;
        TextView tv;
        ImageButton ib;
        LayoutInflater layoutInflator = LayoutInflater.from(getActivity());
        View.OnClickListener listener = new OnTrailerClickListener();
        if( null != videos && videos.size() > 0 ) {
            setSharedIntent(videos.get(0));
            for(int i = 0;  i < videos.size(); i++ ){
                layOut = (RelativeLayout) layoutInflator.inflate(R.layout.list_item_trailer, null);
                tv = (TextView) layOut.findViewById(R.id.trailer_textView);
                ib = (ImageButton) layOut.findViewById(R.id.play_imageButton);
                ib.setOnClickListener(listener);
                ib.setTag(videos.get(i));
                tv.setText(getActivity().getString(R.string.detail_activity_trailer_number, i + 1));
                mTrailersLinearLayout.addView(layOut,i);
                Log.d(LOG_TAG, videos.get(i).toString());
            }
            mTrailersLinearLayout.getChildAt(mTrailersLinearLayout.getChildCount() - 1).setVisibility(View.GONE);
        }

    }

    /**
     * Create the loader to fetch information from db
     * @param id - loader id
     * @param args - arguments to create a loader
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieContract.MovieEntry.buildMovieUri( mMovie.getMovieId() );
        return new CursorLoader( getActivity(), uri, null, null, null, null);
    }

    /**
     * Handle the return of information from the loader
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int movieIdIndex;
        if ( data.moveToFirst() ) {
            movieIdIndex = data.getColumnIndex( MovieContract.MovieEntry.COLUMN_MOVIE_ID );
            if( data.getLong( movieIdIndex ) > 0 ){
                isFavorite = true;
                mFavoriteImageButton.setBackgroundResource(R.drawable.favorite_on);
            }
        }
        data.close();
    }

    /**
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

}
