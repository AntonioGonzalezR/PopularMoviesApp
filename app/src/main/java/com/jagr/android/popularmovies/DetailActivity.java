package com.jagr.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.jagr.android.popularmovies.data.model.Movie;
import com.jagr.android.popularmovies.util.Utility;

/**
 * App's detail activity
 */
public class DetailActivity extends AppCompatActivity implements DetailMovieFragment.SharedIntentCallBack {

    private ShareActionProvider mShareActionProvider;


    /**
     * Handle detail activity creation for tablets and phones
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(savedInstanceState == null){
            Intent intent = getIntent();
            Bundle args = new Bundle();
            Movie movie =  intent.getExtras().getParcelable(Movie.NAME);
            args.putParcelable(Movie.NAME,movie);
            DetailMovieFragment fragment = new DetailMovieFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container,fragment)
                    .commit();

        }
    }

    /**
     * Handle menu creation for detail activity
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);

        mShareActionProvider.setShareIntent( Utility.createSharedIntent("") );
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handle menu items selection
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = getParentActivityIntent();
                upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, upIntent);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
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
        if(mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(Utility.createSharedIntent(text));
        }
    }


}
