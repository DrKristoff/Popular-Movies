package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryand on 12/26/2015.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    List<Movie> mMovies = new ArrayList<Movie>();
    Context mContext;

    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
        this.mMovies = movies;
        mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_grid_item, parent, false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.grid_item_imageView);
        //iconView.setImageResource(movie.image);

        final String MOVIE_BASE_URL = " http://image.tmdb.org/t/p/";
        final String SIZE = "w185";
        final String POSTER_PATH = "poster_path";
        final String VALUE = movie.getPosterPath();
        Picasso.with(mContext).setLoggingEnabled(true);
        Picasso.with(mContext).load(movie.getPosterPath()).error(R.drawable.ic_info_black_24dp).into(iconView);
        return convertView;
    }
}