package com.abdallah.popularmovies.data;

import android.provider.BaseColumns;

public final class MovieDbContract {

    private MovieDbContract() {/* private constructor to prevent instantiation*/}

    public static class FavoriteMovies implements BaseColumns {
        public static final String TABLE_NAME = "favorite_movies";
        public static final String COLUMN_NAME_MOVIE_ID = "movie_id";
    }
}
