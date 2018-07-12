package com.abdallah.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MoviesContentProvider extends ContentProvider {

    private static final String TAG = MoviesContentProvider.class.getSimpleName();

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private MovieDbHelper movieDbHelper;

    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieDbContract.AUTHORITY, MovieDbContract.PATH_FAVORITE_MOVIES, MOVIES);
        uriMatcher.addURI(MovieDbContract.AUTHORITY
                , MovieDbContract.PATH_FAVORITE_MOVIES + "/#", MOVIE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection
            , @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();

        final Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                retCursor = db.query(MovieDbContract.FavoriteMovie.TABLE_NAME, projection
                        , selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String movieSelection = "_id=?";
                String[] movieSelectionArgs = new String[]{id};

                retCursor = db.query(MovieDbContract.FavoriteMovie.TABLE_NAME, projection
                        , movieSelection, movieSelectionArgs, null, null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // We are working with two types of data:
        // 1) a directory and 2) a single row of data.

        switch (uriMatcher.match(uri)) {
            case MOVIES:
                // directory
                return "vnd.android.cursor.dir" + "/" + MovieDbContract.AUTHORITY + "/"
                        + MovieDbContract.PATH_FAVORITE_MOVIES;
            case MOVIE_WITH_ID:
                // single item type
                return "vnd.android.cursor.item" + "/" + MovieDbContract.AUTHORITY + "/"
                        + MovieDbContract.PATH_FAVORITE_MOVIES;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        final Uri insertedItemUri;

        switch (uriMatcher.match(uri)) {
            case MOVIES:
                long id = db.insert(MovieDbContract.FavoriteMovie.TABLE_NAME, null, values);
                if (id != -1) {
                    insertedItemUri = ContentUris.withAppendedId(MovieDbContract.FavoriteMovie.CONTENT_URI, id);
                } else {
                    return null;
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        return insertedItemUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection
            , @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        final int itemsDeleted;

        switch (uriMatcher.match(uri)) {
            case MOVIES:
                itemsDeleted = db.delete(MovieDbContract.FavoriteMovie.TABLE_NAME
                        , "1", null);
                break;
            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String where = "_id=?";
                String [] whereArgs = new String[]{id};
                itemsDeleted = db.delete(MovieDbContract.FavoriteMovie.TABLE_NAME, where, whereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (itemsDeleted != 0) {
            // An item was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return itemsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values
            , @Nullable String selection, @Nullable String[] selectionArgs) {
        // update is not needed
        return 0;
    }
}
