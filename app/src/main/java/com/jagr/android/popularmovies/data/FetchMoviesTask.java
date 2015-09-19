package com.jagr.android.popularmovies.data;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.jagr.android.popularmovies.ImageViewArrayAdapter;
import com.jagr.android.popularmovies.R;
import com.jagr.android.popularmovies.data.parser.DataParser;
import com.jagr.android.popularmovies.data.parser.MovieDataParser;
import com.jagr.android.popularmovies.data.parser.ParseResult;
import com.jagr.android.popularmovies.util.HttpConnection;
import com.jagr.android.popularmovies.util.Utility;

/**
 * Created by Antonio on 15-08-18.
 * Create a background operation to request data from The Movies API
 * and save in a cache database and an array adapter(This implementation
 * is gonna be modified in future implementations).
 *
 */
public class FetchMoviesTask extends AsyncTask<String, Void, ParseResult> {

    private static final String API_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
    private static final String SORT_PARAM = "sort_by";
    private static final String API_KEY_PARAM = "api_key";
    private static final String PAGE_PARAM = "page";
    private static Context mContext;
    private static ImageViewArrayAdapter mMovieAdapter;


    /**
     * Create a new moviesTask
     * @param context - context from which the task was created
     * @param movieAdapter - adapter we want to fill with the results
     */
    public FetchMoviesTask(Context context, ImageViewArrayAdapter movieAdapter){
        mContext =  context;
        mMovieAdapter = movieAdapter;
    }

    /**
     * background operation that request information and
     * save it in a cache db and array adapter
     * @param params - Strings containing query params used to call the API
     * @return result from parsing the date returned from the api
     */
    @Override
    protected ParseResult doInBackground(String... params) {
        ParseResult result = null;
        Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendQueryParameter(SORT_PARAM, params[0])
                .appendQueryParameter(PAGE_PARAM, params[1])
                .appendQueryParameter(API_KEY_PARAM, params[2])
                .build();

        if(Utility.isNetworkAvailable( mContext )) {

            HttpConnection con = new HttpConnection(mContext);
            String json = con.get(builtUri.toString());
            DataParser parser = new MovieDataParser();
            if( con.getStatus() == HttpConnection.CONNECTION_OK ) {
                result = parser.parse(mContext, json);
            }else{
                //TODO timeOut
                result = new ParseResult( ParseResult.NO_INTERNET_CONNECTION );
            }
        }else{
            result = new ParseResult( ParseResult.NO_INTERNET_CONNECTION );
        }
        return result;
    }


    /**
     * Process the result of the execution
     * @param result - ParseResult object that contains the status and data after
     *               the execution of the method DataParser.parse
     */
    @Override
    protected void onPostExecute(ParseResult result){
        if( result.getStatus() == ParseResult.OK ) {
            if(  null != result.getDetail() && !result.getDetail().isEmpty() ){
                Utility.setCurrentPage( mContext, result.getPage(), result.getTotalPages() );
                mMovieAdapter.addAll( result.getDetail() );
            }
        }else{
            switch( result.getStatus() ){
                case ParseResult.NO_INTERNET_CONNECTION:
                    Toast toast = Toast.makeText(mContext,
                            mContext.getString( R.string.no_internet_connection ),
                            Toast.LENGTH_LONG );
                    toast.show();
                    break;
                default:

            }
        }
    }
}
