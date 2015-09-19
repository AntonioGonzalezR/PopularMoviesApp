package com.jagr.android.popularmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by Antonio on 15-09-08.
 * Object representation of the Json format returned by the movies API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class Video implements Parcelable {

    private String id;
    private String key;


    public Video() {
    }

    public Video(String id, String key) {
        this.id = id;
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    /** Static field used to regenerate object, individually or as arrays */
    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        public Video createFromParcel(Parcel pc) {
            return new Video(pc);
        }
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    /**Ctor from Parcel, reads back fields IN THE ORDER they were written */
    public Video(Parcel pc){
        id  = pc.readString();
        key = pc.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
    }

    @Override
    public String toString() {
        return "Video: [ id: " + id + ", " +
                "key: " + key +  "]";
    }
}
