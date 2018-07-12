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
        final String SQL_CREATE_TABLE_FAVORITE_MOVIE =
                "CREATE TABLE " + MovieDbContract.FavoriteMovie.TABLE_NAME + " (" +
                        MovieDbContract.FavoriteMovie._ID + " INTEGER PRIMARY KEY, " +
                        MovieDbContract.FavoriteMovie.COLUMN_NAME_TMDB_ID + " INTEGER NOT NULL, " +
                        MovieDbContract.FavoriteMovie.COLUMN_NAME_TITLE + "TEXT NOT NULL, " +
                        MovieDbContract.FavoriteMovie.COLUMN_NAME_POSTER_PATH + "TEXT NOT NULL)";

        db.execSQL(SQL_CREATE_TABLE_FAVORITE_MOVIE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Since these are development releases I don't care about the data.
        // Drop the database tables and recreate them.
        final String SQL_DELETE_TABLE_FAVORITE_MOVIE =
                "DROP TABLE IF EXISTS " + MovieDbContract.FavoriteMovie.TABLE_NAME;

        db.execSQL(SQL_DELETE_TABLE_FAVORITE_MOVIE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Since these are development release I don't care about the data.
        // Drop the database tables and recreate them. Same as onUpgrade
        onUpgrade(db, oldVersion, newVersion);
    }
}
