package com.abdallah.popularmovies.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.abdallah.popularmovies.adapters.MoviesAdapter;
import com.abdallah.popularmovies.models.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteMoviesFragment extends Fragment implements MoviesAdapter.RecyclerViewItemClickListener {

    private static final String TAG = FavoriteMoviesFragment.class.getSimpleName();

    @BindView(R.id.rv_movies) RecyclerView moviesRecyclerView;
    @BindView(R.id.ll_empty_list) LinearLayout emptyMoviesListLinearLayout;

    @BindInt(R.integer.grid_span_count) int gridSpanCount;

    private GridLayoutManager layoutManager;
    private MoviesAdapter adapter;

    private List<Movie> moviesList;

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

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        configureMoviesRecyclerView();
    }

    private void configureMoviesRecyclerView() {
        // This setting improves performance since changes
        // in content do not change the layout size of the RecyclerView
        moviesRecyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(getContext(), gridSpanCount);
        moviesRecyclerView.setLayoutManager(layoutManager);

        moviesList = new ArrayList<>();
        adapter = new MoviesAdapter(moviesList, this);
        moviesRecyclerView.setAdapter(adapter);

        loadMovies();
    }

    private void loadMovies() {

    }

    @Override
    public void onRecyclerViewItemClicked(int clickedItemIndex) {
        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);

        Movie movie = moviesList.get(clickedItemIndex);
        long movieId = movie.getId();
        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE_ID, movieId);

        startActivity(intent);
    }
}
