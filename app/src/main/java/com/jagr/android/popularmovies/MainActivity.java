package com.jagr.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jagr.android.popularmovies.data.model.Movie;
import com.jagr.android.popularmovies.util.Utility;

/**
 * App's main activity
 */
public class MainActivity extends AppCompatActivity implements MoviesFragment.Callback, DetailMovieFragment.SharedIntentCallBack {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAIL_FRAGMENT_TAG = "DTAG";
    private ShareActionProvider mShareActionProvider;
    public boolean mTwoPane;


    /**
     * Handles the selection of an item in the main grid
     * @param movie
     */
    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
            setSelectedItem( movie );
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(Movie.NAME, movie);
            startActivity(intent);
        }
    }

    /**
     * Set a default item for tables view
     * @param movie
     */
    @Override
    public void setDefaultItem(Movie movie) {
        if(mTwoPane) {
            setSelectedItem(movie);
        }
    }

    /**
     * Replace a the detail fragment after an item was selected
     * in the main grid
     * @param movie
     */
    public void setSelectedItem( Movie movie ){
        Bundle args = new Bundle();
        args.putParcelable(Movie.NAME, movie);
        DetailMovieFragment fragment = new DetailMovieFragment();
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, fragment, DETAIL_FRAGMENT_TAG)
                .commit();
    }

    /**
     * Set's up the main activity depending on the type od device we are using
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (null != findViewById(R.id.movie_detail_container)) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailMovieFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    /**
     * Create the main's activity menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if( mTwoPane ) {
            MenuItem shareItem = menu.findItem(R.id.action_share);
            mShareActionProvider = (ShareActionProvider)
                    MenuItemCompat.getActionProvider(shareItem);

            mShareActionProvider.setShareIntent(Utility.createSharedIntent(""));
        }
        return true;
    }

    /**
     * Handle item selection in main activity
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class ) );
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Allow detail fragment to set the intent to be lunched when the user wants
     * to share a trailer
     * @param text
     */
    @Override
    public void setSharedIntent(String text) {
        Intent sharedIntent = Utility.createSharedIntent(text);
        if(null != sharedIntent && null != mShareActionProvider ) {
            mShareActionProvider.setShareIntent( sharedIntent );
        }else{
            Log.d(LOG_TAG, "sharedIntent is null" + sharedIntent );
        }
    }
}
