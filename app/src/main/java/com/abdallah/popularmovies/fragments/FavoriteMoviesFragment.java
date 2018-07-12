package com.abdallah.popularmovies.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.abdallah.popularmovies.R;
import com.abdallah.popularmovies.activities.MovieDetailsActivity;
import com.abdallah.popularmovies.adapters.EndlessRecyclerOnScrollListener;
import com.abdallah.popularmovies.adapters.FavoriteMoviesCursorAdapter;
import com.abdallah.popularmovies.adapters.MoviesAdapter;
import com.abdallah.popularmovies.data.MovieDbContract;
import com.abdallah.popularmovies.models.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteMoviesFragment extends Fragment
        implements FavoriteMoviesCursorAdapter.RecyclerViewItemClickListener
        , LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = FavoriteMoviesFragment.class.getSimpleName();

    private static final int FAVORITE_MOVIES_LOADER_ID = 0;

    @BindView(R.id.rv_movies) RecyclerView moviesRecyclerView;
    @BindView(R.id.ll_empty_list) LinearLayout emptyMoviesListLinearLayout;

    @BindInt(R.integer.grid_span_count) int gridSpanCount;

    private GridLayoutManager layoutManager;
    private FavoriteMoviesCursorAdapter adapter;

    public FavoriteMoviesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment FavoriteMoviesFragment.
     */
    public static FavoriteMoviesFragment newInstance() {
        FavoriteMoviesFragment fragment = new FavoriteMoviesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_favorite_movies
                , container, false);
        ButterKnife.bind(this, fragmentView);

        configureMoviesRecyclerView();

        getLoaderManager().initLoader(FAVORITE_MOVIES_LOADER_ID, null, this);

        return fragmentView;
    }

    private void configureMoviesRecyclerView() {
        // This setting improves performance since changes
        // in content do not change the layout size of the RecyclerView
        moviesRecyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(getContext(), gridSpanCount);
        moviesRecyclerView.setLayoutManager(layoutManager);

        adapter = new FavoriteMoviesCursorAdapter(getContext(), this);
        moviesRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onRecyclerViewItemClicked(long movieId) {

        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE_ID, movieId);

        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case FAVORITE_MOVIES_LOADER_ID:
                return new CursorLoader(getContext(), MovieDbContract.FavoriteMovie.CONTENT_URI
                        , null, null, null, null);
            default:
                throw new UnsupportedOperationException("Unknown id: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case FAVORITE_MOVIES_LOADER_ID:
                adapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch (loader.getId()) {
            case FAVORITE_MOVIES_LOADER_ID:
                adapter.swapCursor(null);
                break;
        }
    }
}
