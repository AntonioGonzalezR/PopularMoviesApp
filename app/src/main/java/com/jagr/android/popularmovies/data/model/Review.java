package com.jagr.android.popularmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Antonio on 15-09-09.
 * Object representation of the Json format returned by the movies API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Review implements Parcelable {

    @JsonProperty("id")
    private String reviewId;
    @JsonProperty("author")
    private String reviewAuthor = null;
    @JsonProperty("content")
    private String reviewContent = null;
    @JsonProperty("url")
    private String reviewUrl = null;

    public Review() {
    }

    public Review(String reviewId, String reviewAuthor, String reviewContent, String reviewUrl) {
        this.reviewId = reviewId;
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;
        this.reviewUrl = reviewUrl;
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    /** Static field used to regenerate object, individually or as arrays */
    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel pc) {
            return new Review(pc);
        }
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };


    /**Ctor from Parcel, reads back fields IN THE ORDER they were written */
    public Review(Parcel pc){
        reviewId         = pc.readString();
        reviewAuthor     = pc.readString();
        reviewContent    = pc.readString();
        reviewUrl        = pc.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reviewId);
        dest.writeString(reviewAuthor);
        dest.writeString(reviewContent);
        dest.writeString(reviewUrl);

    }

    @Override
    public String toString() {
        return "Review: [ id: " + reviewId + ", " +
                "author: " + reviewAuthor  + ", " +
                "content: " + reviewContent  + ", " +
                "url: " + reviewUrl +  "]";
    }
}
