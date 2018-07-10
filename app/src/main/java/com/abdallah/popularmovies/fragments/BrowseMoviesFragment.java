package com.abdallah.popularmovies.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abdallah.popularmovies.R;
import com.abdallah.popularmovies.activities.MovieDetailsActivity;
import com.abdallah.popularmovies.adapters.EndlessRecyclerOnScrollListener;
import com.abdallah.popularmovies.adapters.MoviesAdapter;
import com.abdallah.popularmovies.api.TMDBServices;
import com.abdallah.popularmovies.models.Movie;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BrowseMoviesFragment extends Fragment implements MoviesAdapter.RecyclerViewItemClickListener {

    private static final String TAG = BrowseMoviesFragment.class.getSimpleName();

    private static final String STATE_SORTING_METHOD = "sorting_method";

    @BindView(R.id.rv_movies) RecyclerView moviesRecyclerView;
    @BindView(R.id.loading_movies_pb) ProgressBar loadingMoviesProgressBar;
    @BindView(R.id.ll_empty_list) LinearLayout emptyMoviesListLinearLayout;

    @BindInt(R.integer.grid_span_count) int gridSpanCount;

    private GridLayoutManager layoutManager;
    private MoviesAdapter adapter;

    private List<Movie> moviesList;

    private int moviesSortingMethod = TMDBServices.SORT_MOVIES_BY_POPULARITY; // The default is sorting by popularity
    private int currentPage;

    private int recyclerViewVisibleThreshold;

    private Snackbar errorSnackbar;

    public BrowseMoviesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment BrowseMoviesFragment.
     */
    public static BrowseMoviesFragment newInstance() {
        BrowseMoviesFragment fragment = new BrowseMoviesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_browse_movies
                , container, false);
        ButterKnife.bind(this, fragmentView);

        if (savedInstanceState != null) {
            moviesSortingMethod = savedInstanceState.getInt(STATE_SORTING_METHOD);

            Log.d(TAG, "restored sorting method = " + moviesSortingMethod);
        }

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        currentPage = 1;
        recyclerViewVisibleThreshold = 5*gridSpanCount; // always 5 rows of visible threshold

        configureMoviesRecyclerView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(STATE_SORTING_METHOD, moviesSortingMethod);

        super.onSaveInstanceState(outState);
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

        moviesRecyclerView.addOnScrollListener(
                new EndlessRecyclerOnScrollListener(recyclerViewVisibleThreshold) {
            @Override
            public void onLoadMore() {
                currentPage++;
                loadMovies();
            }
        });
    }

    private void loadMovies() {

        loadingMoviesProgressBar.setVisibility(View.VISIBLE);
        emptyMoviesListLinearLayout.setVisibility(View.INVISIBLE);
        if (errorSnackbar != null) {
            errorSnackbar.dismiss();
        }

        TMDBServices.requestMovies(getContext(), moviesSortingMethod, currentPage
                , new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // serialize the json response to Movies array
                        Gson gson = new Gson();
                        try {
                            Movie [] moviesArray = gson.fromJson(response.getJSONArray("results").toString()
                                    , Movie[].class);
                            List<Movie> movies = Arrays.asList(moviesArray);
                            moviesList.addAll(movies);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BrowseMoviesFragment.this.getContext()
                                    , getString(R.string.load_movies_error_msg)
                                    , Toast.LENGTH_SHORT).show();
                        }

                        loadingMoviesProgressBar.setVisibility(View.INVISIBLE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());

                        displayLoadError(error);

                    }
                });
    }

    private void displayLoadError(VolleyError error) {
        String errorMsg;
        if (error instanceof NoConnectionError) {
            errorMsg = getString(R.string.no_connection_error_message);
        }
        else if (error instanceof TimeoutError) {
            errorMsg = getString(R.string.connection_timeout_error_message);
        }
        else {
            errorMsg = getString(R.string.load_movies_error_msg);
        }

        loadingMoviesProgressBar.setVisibility(View.INVISIBLE);

        errorSnackbar = Snackbar.make(getActivity().findViewById(android.R.id.content)
                , errorMsg, Snackbar.LENGTH_INDEFINITE);
        errorSnackbar.setAction(R.string.retry_loading_text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMovies();
            }
        });
        errorSnackbar.show();

        if (adapter.getItemCount() == 0) {
            emptyMoviesListLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    public void resetMoviesRecyclerView() {
        currentPage = 1;
        moviesList.clear();
        adapter.notifyDataSetChanged();
        loadMovies();

        moviesRecyclerView.clearOnScrollListeners();
        moviesRecyclerView.addOnScrollListener(
                new EndlessRecyclerOnScrollListener(recyclerViewVisibleThreshold) {
            @Override
            public void onLoadMore() {
                currentPage++;
                loadMovies();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.fragment_browse_movies, menu);

        // check the menu item corresponding to the sorting method
        if (moviesSortingMethod == TMDBServices.SORT_MOVIES_BY_POPULARITY) {
            menu.findItem(R.id.action_sort_by_popularity).setChecked(true);
        }
        else if (moviesSortingMethod == TMDBServices.SORT_MOVIES_BY_RATING) {
            menu.findItem(R.id.action_sort_by_rating).setChecked(true);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_sort_by_popularity:
                item.setChecked(true);

                moviesSortingMethod = TMDBServices.SORT_MOVIES_BY_POPULARITY;

                resetMoviesRecyclerView();

                return true;

            case R.id.action_sort_by_rating:
                item.setChecked(true);

                moviesSortingMethod = TMDBServices.SORT_MOVIES_BY_RATING;

                resetMoviesRecyclerView();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

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