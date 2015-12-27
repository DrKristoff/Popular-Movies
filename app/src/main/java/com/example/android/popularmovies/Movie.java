package com.example.android.popularmovies;

/**
 * Created by ryand on 12/26/2015.
 */
public class Movie {
    private String mName;
    private int mImage;
    private String mLink;

    public Movie(String name, int image)
    {
        mName = name;
        this.mImage = image;

    }

    public Movie(String name, String link)
    {
        mName = name;
        mLink = link;

    }

    public String getMovieLink(){return mLink;}
}
