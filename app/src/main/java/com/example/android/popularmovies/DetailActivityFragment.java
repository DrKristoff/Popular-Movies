package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import com.example.android.popularmovies.Movie.Review;

public class DetailActivityFragment extends Fragment {

    TextView mRuntimeTextView;
    TextView mMovieTitleTextView;
    TextView mReleaseDateTextView;
    TextView mRatingTextView;
    TextView mOverviewTextView;
    ImageView mPosterImageView;
    LinearLayout mTrailerLinearLayout;
    LinearLayout mReviewsLinearLayout;
    LayoutInflater mInflater;
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private int mMovieID;

    Movie mMovie;

    public DetailActivityFragment() {

        //setHasOptionsMenu(true);
    }

/*    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return null;
        } else {
            mMovie = arguments.getParcelable("Movie_Selected");
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mRuntimeTextView = (TextView) rootView.findViewById(R.id.movieRunTimeTextView);
        mReleaseDateTextView = (TextView) rootView.findViewById(R.id.movieReleaseYear);
        mRatingTextView = (TextView) rootView.findViewById(R.id.movieRatingTextView);
        mOverviewTextView = (TextView) rootView.findViewById(R.id.movieOverviewTextView);
        mMovieTitleTextView = (TextView) rootView.findViewById(R.id.movieTitleTextView);
        mPosterImageView = (ImageView) rootView.findViewById(R.id.detailMoviePosterImageView);
        mTrailerLinearLayout = (LinearLayout) rootView.findViewById(R.id.trailerLinearLayout);
        mReviewsLinearLayout = (LinearLayout) rootView.findViewById(R.id.reviewsLinearLayout);

        String releaseDate = mMovie.getReleaseDate();
        String releaseYear = releaseDate.substring(0,4);
        mRuntimeTextView.setText(String.valueOf(mMovie.getRuntime()) + " minutes");
        mRatingTextView.setText(mMovie.getRating() + "/10");
        mReleaseDateTextView.setText(releaseYear);
        mOverviewTextView.setText(mMovie.getOverview());
        mMovieTitleTextView.setText(mMovie.getTitle());

        final String MOVIE_BASE_URL = " http://image.tmdb.org/t/p/";
        final String SIZE = "w185";
        final String POSTER_PATH = "poster_path";
        final String VALUE = mMovie.getPosterPath();
        Picasso.with(getActivity()).setLoggingEnabled(true);
        Picasso.with(getActivity()).load(mMovie.getPosterPath()).error(R.drawable.ic_info_black_24dp).into(mPosterImageView);

        mMovieID = mMovie.getMovieID();
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();

        mInflater = inflater;

        return rootView;
    }

    public class FetchMovieTask extends AsyncTask<Void, Void, Movie> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private Movie getMovieDataFromJSONString(String movieJSONString, String trailerDetailJSONString, String reviewDetailJSONString)
                throws JSONException {
            Movie movieDetail = new Movie(new JSONObject(movieJSONString));
            movieDetail.addTrailers(new JSONObject(trailerDetailJSONString));
            movieDetail.addReviews(new JSONObject(reviewDetailJSONString));
            return movieDetail;
        }

        @Override
        protected Movie doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieDetailJSONString = null;
            String trailerDetailJSONString = null;
            String reviewDetailJSONString = null;

            String format = "json";

            //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            //String sortBy = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));
            //sortBy = sortBy + ".desc"; //vote_average.desc

            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String APPID_PARAM = "api_key";

            Uri movieUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(String.valueOf(mMovieID))
                    .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DATABASE_API_KEY)
                    .build();

            movieDetailJSONString = getRESTResponseFromURI(movieUri);

            movieUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(String.valueOf(mMovieID))
                    .appendPath("videos")
                    .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DATABASE_API_KEY)
                    .build();

            trailerDetailJSONString = getRESTResponseFromURI(movieUri);

            movieUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(String.valueOf(mMovieID))
                    .appendPath("reviews")
                    .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DATABASE_API_KEY)
                    .build();

            reviewDetailJSONString = getRESTResponseFromURI(movieUri);

            Log.v(LOG_TAG, "Movie string: " + movieDetailJSONString);
            Log.v(LOG_TAG, "Trailer string: " + trailerDetailJSONString);
            Log.v(LOG_TAG, "Review string: " + reviewDetailJSONString);


            try {
                return getMovieDataFromJSONString(movieDetailJSONString, trailerDetailJSONString, reviewDetailJSONString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the movie.
            return null;
        }

        @Override
        protected void onPostExecute(Movie result) {
            if (result != null) {

                mMovie = result;
                mRuntimeTextView.setText(String.valueOf(result.getRuntime()) + " minutes");

                final ArrayList<String> youtubeKeysArray = result.getTrailerList();

                int numTrailers = result.getNumTrailers();

                for (int i=0; i < numTrailers;i++) {
                    final int index = i;
                    View v = mInflater.inflate(R.layout.trailer_list_item, null);

                    // fill in any details dynamically here
                    TextView textView = (TextView) v.findViewById(R.id.trailerNumberTextView);
                    textView.setText("Trailer " + (i + 1));
                    ImageView playButton = (ImageView) v.findViewById(R.id.imageViewPlay);
                    playButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String keyID = youtubeKeysArray.get(index);
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + keyID)));
                        }
                    });

                    mTrailerLinearLayout.addView(v);
                }

                ArrayList<Review> reviews = result.getReviews();
                int numReviews = result.getNumReviews();

                for (int i=0; i < numReviews;i++){
                    Review review = reviews.get(i);

                    final int index = i;
                    View v = mInflater.inflate(R.layout.reviews_list_item, null);

                    // fill in any details dynamically here
                    TextView textViewAuthor = (TextView) v.findViewById(R.id.textViewAuthor);
                    textViewAuthor.setText(review.getReviewerName());
                    TextView textViewReview = (TextView) v.findViewById(R.id.textViewReview);
                    textViewReview.setText(review.getReviewText());

                    mReviewsLinearLayout.addView(v);
                }

            }
        }
    }

    public String getRESTResponseFromURI(Uri builtUri){

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String responseString = null;

        String format = "json";


        try {

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            responseString = buffer.toString();

            Log.v(LOG_TAG, "Movie string: " + responseString);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }


        return responseString;


    }

}

