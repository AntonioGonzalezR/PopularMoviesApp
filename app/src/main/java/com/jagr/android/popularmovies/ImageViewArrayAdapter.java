package com.jagr.android.popularmovies;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.jagr.android.popularmovies.data.model.Movie;
import com.jagr.android.popularmovies.util.Utility;
import com.squareup.picasso.Picasso;

/**
 * Created by Antonio on 15-08-25.
 * A Adapter that extends the functionality of ArrayAdapter in order to
 * create an imageView each time the method getView of this class gets called.
 */
public class ImageViewArrayAdapter extends  ArrayAdapter<Parcelable> {

    public ImageViewArrayAdapter(Context context, int resource, int gridViewResourceId){
        super(context, resource, gridViewResourceId);
    }

    /**
     * Create an image view which contains the a movie poster. This poster
     * was loaded into the image view using the Picasso library.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (ImageView)convertView;
        Movie mp = (Movie)getItem(position);

        if( imageView == null ) {
            LayoutInflater layoutInflator = LayoutInflater.from(getContext());
            convertView = layoutInflator.inflate(R.layout.grid_item_thumbnail, null);
            imageView = (ImageView)convertView;
            imageView.setLayoutParams(Utility.getGridViewLayOutParams(getContext()));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        Picasso.with( getContext() ).load( Utility.getImageUrl(getContext()) + mp.getMoviePoster() )
                .placeholder(R.drawable.placeholder)
                .into(imageView);
        return imageView;
    }




}
