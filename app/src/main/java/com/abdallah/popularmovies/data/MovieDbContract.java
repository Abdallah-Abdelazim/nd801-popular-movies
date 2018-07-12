package com.abdallah.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MovieDbContract {

    public static final String AUTHORITY = "com.abdallah.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVORITE_MOVIES = "favorite_movies";


    private MovieDbContract() {/* private constructor to prevent instantiation*/}

    public static final class FavoriteMovie implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_MOVIES).build();

        public static final String TABLE_NAME = "favorite_movie";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_POSTER_PATH = "poster_path";

        /*
        The above table structure looks something like the sample table below.
        With the name of the table and columns on top, and potential contents in rows

        Note: Because this implements BaseColumns, the _id column is generated automatically

        favorite_movie
         - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        |      _id         |     title    |      poster_path     |
         - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        |     7213671      |    Zootopia  |     /dfljsldf.jpg    |
          - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        |      4134        |    Titanic   |     /fddafsd.jpg     |
         - - - - - - - - - - - - - - - - - - - - - - - - - - - -

         */
    }
}
