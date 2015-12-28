package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import static android.widget.ImageView.ScaleType.CENTER_CROP;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostersFragment extends Fragment {

    GridView mGridView;
    private MovieAdapter mMovieAdapter;

    public PostersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //mGridView.setAdapter(new ImageAdapter(get));

        //mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //public void onItemClick(AdapterView<?> parent, View v,
        //                    int position, long id) {
        //Toast.makeText(getActivity(), "" + position,
        //        Toast.LENGTH_SHORT).show();
        //}
        //});
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_posters, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridView);

        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        mGridView.setAdapter(mMovieAdapter);

        updateMovies();
        return rootView;
    }

    @Override
    public void onResume() {
        updateMovies();  //update posters after settings preference changed
        super.onResume();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void updateMovies(){
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //String loc = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        moviesTask.execute();
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private ArrayList<Movie> getMovieDataFromJSONString(String movieJSONString)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MOVIE_LIST = "results";
            final String MOVIE_POSTER = "poster_path";
            final String MOVIE_OVERVIEW = "overview";
            final String MOVIE_TITLE = "title";
            final String MOVIE_RELEASE_DATE = "release_date";
            final String MOVIE_RATING = "vote_average";
            final String MOVIE_ID = "id";
            //final String MOVIE_LENGTH = "??"; get from different query?

            ArrayList<Movie> resultsList = new ArrayList<>();

            JSONObject movieJson = new JSONObject(movieJSONString);
            JSONArray moviesArray = movieJson.getJSONArray(MOVIE_LIST);

            for(int i =0; i < moviesArray.length(); i++){
                JSONObject movie = moviesArray.getJSONObject(i);
                resultsList.add(i, new Movie(movie));
            }

            return resultsList;

        }
        @Override
        protected ArrayList<Movie> doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String popularMoviesJsonString = null;

            String format = "json";

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortBy = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));
            sortBy = sortBy + ".desc"; //vote_average.desc

            try {

                //final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/popular";
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
                //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=
                final String APPID_PARAM = "api_key";
                final String SORT_BY_PARAM = "sort_by";
                    final String MIN_VOTES_PARAM = "vote_count.gte";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, sortBy)
                        .appendQueryParameter(MIN_VOTES_PARAM, "200")
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
                popularMoviesJsonString = buffer.toString();

                Log.v(LOG_TAG, "Movie string: " + popularMoviesJsonString);
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
                return getMovieDataFromJSONString(popularMoviesJsonString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            if (result != null) {
                mMovieAdapter.clear();
                for(Movie movie : result) {
                    mMovieAdapter.add(movie);
                }
              // New data is back from the server.  Hooray!
            }
        }
    }

}
