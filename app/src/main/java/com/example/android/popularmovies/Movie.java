package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Movie implements Parcelable {
    private String mPosterPath;
    private String mOverview;
    private String mMovieTitle;
    private String mReleaseDate;
    private String mRating;
    private int mMovieID;
    private int mRuntime;
    private ArrayList<String> mTrailers = new ArrayList<String>();
    private ArrayList<Review> mReviews = new ArrayList<Review>();

    private String MOVIE_POSTER = "poster_path";
    private String MOVIE_OVERVIEW = "overview";
    private String MOVIE_TITLE = "title";
    private String MOVIE_RELEASE_DATE = "release_date";
    private String MOVIE_RATING = "vote_average";
    private String MOVIE_ID = "id";
    private String MOVIE_RUNTIME = "runtime";

    private String RESULTS = "results";
    private String TRAILER_YOUTUBE_KEY = "key";
    private String REVIEW_AUTHOR = "author";
    private String REVIEW_TEXT = "content";

    public Movie(JSONObject movieJSON) throws JSONException {
        mPosterPath = "http://image.tmdb.org/t/p/w500/" + movieJSON.getString(MOVIE_POSTER);
        mOverview = movieJSON.getString(MOVIE_OVERVIEW);
        mMovieTitle = movieJSON.getString(MOVIE_TITLE);
        mReleaseDate = movieJSON.getString(MOVIE_RELEASE_DATE);
        mRating = movieJSON.getString(MOVIE_RATING);
        mMovieID = Integer.parseInt(movieJSON.getString(MOVIE_ID));

        if(movieJSON.has(MOVIE_RUNTIME)) {
            mRuntime = Integer.parseInt(movieJSON.getString(MOVIE_RUNTIME));
        }

    }

    public int getNumTrailers(){
        return mTrailers.size();
    }

    public ArrayList<String> getTrailerList() {
        return mTrailers;
    }

    public void addTrailers(JSONObject trailerJSON) throws JSONException{
        JSONArray results = trailerJSON.getJSONArray(RESULTS);
        int length = results.length();

        for (int i = 0; i < length; i++) {
            JSONObject trailer = results.getJSONObject(i);
            mTrailers.add(trailer.getString(TRAILER_YOUTUBE_KEY));
        }
    }

    public void addReviews(JSONObject reviewsJSON) throws JSONException{
        JSONArray results = reviewsJSON.getJSONArray(RESULTS);
        int length = results.length();

        for (int i = 0; i < length; i++) {
            JSONObject result = results.getJSONObject(i);
            mReviews.add(new Review(result.getString(REVIEW_AUTHOR), result.getString(REVIEW_TEXT)));
        }
    }

    public String getPosterPath(){
        return mPosterPath;
    }

    public String getTitle(){
        return mMovieTitle;
    }

    public int getMovieID(){
        return mMovieID;
    }

    public int getRuntime(){
        return mRuntime;
    }

    public String getReleaseDate(){
        return mReleaseDate;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getRating() {
        return mRating;
    }

    protected Movie(Parcel in) {
        mPosterPath = in.readString();
        mOverview = in.readString();
        mMovieTitle = in.readString();
        mReleaseDate = in.readString();
        mRating = in.readString();
        mMovieID = in.readInt();
        mRuntime = in.readInt();
        MOVIE_POSTER = in.readString();
        MOVIE_OVERVIEW = in.readString();
        MOVIE_TITLE = in.readString();
        MOVIE_RELEASE_DATE = in.readString();
        MOVIE_RATING = in.readString();
        MOVIE_ID = in.readString();
        MOVIE_RUNTIME = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
        dest.writeString(mMovieTitle);
        dest.writeString(mReleaseDate);
        dest.writeString(mRating);
        dest.writeInt(mMovieID);
        dest.writeInt(mRuntime);
        dest.writeString(MOVIE_POSTER);
        dest.writeString(MOVIE_OVERVIEW);
        dest.writeString(MOVIE_TITLE);
        dest.writeString(MOVIE_RELEASE_DATE);
        dest.writeString(MOVIE_RATING);
        dest.writeString(MOVIE_ID);
        dest.writeString(MOVIE_RUNTIME);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public class Review {
        private String reviewerName;
        private String reviewText;

        public Review(String name, String text){
            this.reviewerName = name;
            this.reviewText = text;
        }
    }
}
