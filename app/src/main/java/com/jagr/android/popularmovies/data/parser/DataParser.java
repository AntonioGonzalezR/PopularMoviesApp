package com.jagr.android.popularmovies.data.parser;

import android.content.Context;

/**
 * Created by Antonio on 15-08-26.
 * DataParser provides a mechanism to extract data from different Json strings into a
 * ContentValues object or ContentValues List.
 *
 * The interface provides a parse method responsible of making the transformation.
 */
public interface DataParser {

    /**
     *
     * Parse method is responsible of mapping out the json string into a
     * ContentValues object or a List of ContentValues.
     * @param context - context in which this method was invoked.
     * @param json - string to parse.
     * @return ParseResult which contain the result of parsing the json string.
     */

    ParseResult parse(Context context, String json);

}
