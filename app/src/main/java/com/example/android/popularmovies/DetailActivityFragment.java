package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {


    TextView mRuntimeTextView;
    TextView mMovieTitleTextView;
    TextView mReleaseDateTextView;
    TextView mRatingTextView;
    TextView mOverviewTextView;
    ImageView mPosterImageView;
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private int mMovieID;

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
        Intent intent = getActivity().getIntent();

        Bundle bundle = getActivity().getIntent().getExtras();
        Movie movie  = bundle.getParcelable("Movie_Selected");

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mRuntimeTextView = (TextView) rootView.findViewById(R.id.movieRunTimeTextView);
        mReleaseDateTextView = (TextView) rootView.findViewById(R.id.movieReleaseYear);
        mRatingTextView = (TextView) rootView.findViewById(R.id.movieRatingTextView);
        mOverviewTextView = (TextView) rootView.findViewById(R.id.movieOverviewTextView);
        mMovieTitleTextView = (TextView) rootView.findViewById(R.id.movieTitleTextView);
        mPosterImageView = (ImageView) rootView.findViewById(R.id.detailMoviePosterImageView);

        String releaseDate = movie.getReleaseDate();
        String releaseYear = releaseDate.substring(0,4);
        mRuntimeTextView.setText(String.valueOf(movie.getRuntime()) + " minutes");
        mRatingTextView.setText(movie.getRating() + "/10");
        mReleaseDateTextView.setText(releaseYear);
        mOverviewTextView.setText(movie.getOverview());
        mMovieTitleTextView.setText(movie.getTitle());

        final String MOVIE_BASE_URL = " http://image.tmdb.org/t/p/";
        final String SIZE = "w185";
        final String POSTER_PATH = "poster_path";
        final String VALUE = movie.getPosterPath();
        Picasso.with(getActivity()).setLoggingEnabled(true);
        Picasso.with(getActivity()).load(movie.getPosterPath()).error(R.drawable.ic_info_black_24dp).into(mPosterImageView);

        mMovieID = movie.getMovieID();
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();

        return rootView;
    }

    public class FetchMovieTask extends AsyncTask<Void, Void, Movie> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private Movie getMovieDataFromJSONString(String movieJSONString)
                throws JSONException {
            Movie movieDetail = new Movie(new JSONObject(movieJSONString));

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

            String format = "json";

            //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            //String sortBy = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));
            //sortBy = sortBy + ".desc"; //vote_average.desc

            try {

                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendPath(String.valueOf(mMovieID))
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DATABASE_API_KEY)
                        .build();

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
                movieDetailJSONString = buffer.toString();

                Log.v(LOG_TAG, "Movie string: " + movieDetailJSONString);
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

            try {
                return getMovieDataFromJSONString(movieDetailJSONString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(Movie result) {
            if (result != null) {
                mRuntimeTextView.setText(String.valueOf(result.getRuntime()) + " minutes");
            }
        }
    }

}

