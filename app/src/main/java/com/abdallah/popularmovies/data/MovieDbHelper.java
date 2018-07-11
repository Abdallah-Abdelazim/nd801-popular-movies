package com.abdallah.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateFavoriteMoviesTable =
                "CREATE TABLE " + MovieDbContract.FavoriteMovies.TABLE_NAME + " (" +
                MovieDbContract.FavoriteMovies._ID + " INTEGER PRIMARY KEY," +
                MovieDbContract.FavoriteMovies.COLUMN_NAME_MOVIE_ID + " INTEGER)";
        db.execSQL(sqlCreateFavoriteMoviesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlDeleteFavoriteMoviesTable =
                "DROP TABLE IF EXISTS " + MovieDbContract.FavoriteMovies.TABLE_NAME;
        db.execSQL(sqlDeleteFavoriteMoviesTable);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
