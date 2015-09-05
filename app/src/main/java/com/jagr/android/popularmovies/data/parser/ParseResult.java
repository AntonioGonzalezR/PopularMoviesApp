package com.jagr.android.popularmovies.data.parser;

import android.content.ContentValues;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by Antonio on 15-08-26.
 * The ParseResult class contains data extracted from a JsonString object
 * and a status of that extraction after executing the DataParser.parse method
 */
public class ParseResult {

    @IntDef({OK, INVALID_RESPONSE, INVALID_LOCATION,NO_INTERNET_CONNECTION,SERVER_DOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ParsingStatus{}

    /**
     * available status
     */
    public static final int OK = 0;
    public static final int SERVER_DOWN = 1;
    public static final int INVALID_RESPONSE = 2;
    public static final int NO_INTERNET_CONNECTION = 3;
    public static final int INVALID_LOCATION = 4;


    private @ParsingStatus int status;
    private ContentValues header;
    private List<ContentValues> detail;
    private int page = -1;
    private int totalPages = -1;

    public ParseResult(@ParsingStatus int status){
        this.status = status;
    }

    public ParseResult(@ParsingStatus int status, List<ContentValues> detail, ContentValues header,
            int page, int totalPages) {
        this.status = status;
        this.detail = detail;
        this.header = header;
        this.page = page;
        this.totalPages = totalPages;
    }

    public @ParsingStatus int getStatus() {
        return status;
    }

    public List<ContentValues> getDetail() {
        return detail;
    }

    public ContentValues getHeader() {
        return header;
    }

    public int getPage(){
        return page;
    }

    public int getTotalPages(){
        return totalPages;
    }

}
