package com.jagr.android.popularmovies.util;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Antonio on 15-08-06.
 */
public class HttpConnection {

    private static final String LOG_TAG = HttpConnection.class.getSimpleName();

    private Context appContext;
    private static final HttpConnectionHelper connection = new HttpConnectionHelper();
    @IntDef({CONNECTION_OK, CONNECTION_DOWN, CONNECTION_UNKNOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ConnectionStatus{}
    public static final int CONNECTION_OK = 0;
    public static final int CONNECTION_DOWN = 1;
    public static final int CONNECTION_UNKNOWN = 3;

    public HttpConnection( Context appContext ){
        this.appContext = appContext;
    }

    public @ConnectionStatus int getStatus(){
        return this.connection.status;
    }

    /**
     *
     * @param url
     * @return
     */
    public String get( String url ){
        if( Utility.isNetworkAvailable(appContext) ) {
            return connection.downloadURL(url
                    , "GET"
                    , 10000);
        }else{
            return null;
        }

    }

    /**
     *
     * @param url
     * @param params
     * @return
     */
    public String post( String url, String... params ){
        if( Utility.isNetworkAvailable(appContext) ) {
            return connection.downloadURL(""
                    , "POST"
                    , 10000);
        }else{
            return null;
        }
    }

    private static class HttpConnectionHelper{

        @ConnectionStatus
        private int status;

        public HttpConnectionHelper(){ }

        /**
         * download JSON info from a given URL and return it as a String
         * @param urlParam - url which has the json info
         * @param method - type of request
         * @param timeOut - time waiting for answer
         * @return a string with json info
         */
        private String downloadURL( String urlParam, String method, int timeOut ){
            int responseCode = 0;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStr = null;
            try {
                URL url = new URL(urlParam);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(method);
                urlConnection.setReadTimeout(timeOut);
                urlConnection.setConnectTimeout(timeOut);

                urlConnection.connect();
                responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK ) {
                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder buffer = new StringBuilder();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        status = CONNECTION_DOWN;
                        return null;
                    }
                    jsonStr = buffer.toString();
                    status = CONNECTION_OK;
                }
            } catch (IOException e) {
                Log.e(LOG_TAG + " - downloadURL", "Error ", e);
                status = CONNECTION_DOWN;
                jsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG + " - downloadURL", "Error closing stream", e);
                    }
                }
            }
            return jsonStr;
        }
    }

}
