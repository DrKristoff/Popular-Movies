package com.example.android.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie {
    private String mPosterPath;
    private String mOverview;
    private String mMovieTitle;
    private String mReleaseDate;
    private String mRating;
    private int mMovieID;

    final String MOVIE_POSTER = "poster_path";
    final String MOVIE_OVERVIEW = "overview";
    final String MOVIE_TITLE = "title";
    final String MOVIE_RELEASE_DATE = "release_date";
    final String MOVIE_RATING = "vote_average";
    final String MOVIE_ID = "id";

//    public Movie(String name, String posterPath, int ID)
//    {
//        mName = name;
//        mPosterPath = posterPath;
//        mMovieID = ID;
//
//    }

    public Movie(JSONObject movie) throws JSONException {
        mPosterPath = "http://image.tmdb.org/t/p/w185/" + movie.getString(MOVIE_POSTER);
        mOverview = movie.getString(MOVIE_OVERVIEW);
        mMovieTitle = movie.getString(MOVIE_TITLE);
        mReleaseDate = movie.getString(MOVIE_RELEASE_DATE);
        mRating = movie.getString(MOVIE_RATING);
        mMovieID = Integer.parseInt(movie.getString(MOVIE_ID));

    }

    public String getPosterPath(){
        return mPosterPath;
    }
}
