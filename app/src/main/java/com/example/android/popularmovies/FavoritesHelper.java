package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ryand on 2/6/2016.
 */
public class FavoritesHelper {

    public FavoritesHelper(){

    }

    public static boolean saveFavorites(ArrayList<Movie> array, Context mContext) {
        String favoritesJSON = new Gson().toJson(array);
        Log.d("RCD", favoritesJSON);

        SharedPreferences prefs = mContext.getSharedPreferences("favorites", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("favorites", favoritesJSON);
        return editor.commit();
    }

    public static ArrayList<Movie> loadFavorites(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("favorites", 0);
        String movieFavorites = prefs.getString("favorites","");
        Type type = new TypeToken<ArrayList<Movie>>(){}.getType();
        ArrayList<Movie> favoriteMovies = new Gson().fromJson(movieFavorites, type);
        if(favoriteMovies!=null){
            return favoriteMovies;
        }
        else {
            return new ArrayList<Movie>();
        }

    }

    public static boolean isFavorite(String movieID, Context context){
        ArrayList<Movie> favorites = loadFavorites(context);
        ArrayList<String> ids = new ArrayList<>();
        for(Movie movie : favorites){
            String id = String.valueOf(movie.getMovieID());
            ids.add(id);
        }
        return ids.contains(movieID);

    }

    public static void addFavorite(Movie movie, Context context){
        ArrayList<Movie> favorites = loadFavorites(context);
        favorites.add(movie);
        saveFavorites(favorites,context);
    }

    public static void removeFavorite(Movie movie, Context context){
        ArrayList<Movie> favorites = loadFavorites(context);
        for(int i = 0; i < favorites.size();i++){
            if(favorites.get(i).getMovieID()==movie.getMovieID()){
                favorites.remove(i);
            }
        }
        saveFavorites(favorites,context);
    }


}
