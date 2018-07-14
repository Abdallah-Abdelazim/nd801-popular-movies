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
import com.abdallah.popularmovies.api.TmdbServices;
import com.abdallah.popularmovies.models.Movie;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BrowseMoviesFragment extends Fragment implements MoviesAdapter.RecyclerViewItemClickListener {

    private static final String TAG = BrowseMoviesFragment.class.getSimpleName();

    private static final String STATE_SORTING_METHOD = "STATE_SORTING_METHOD";
    private static final String STATE_CURRENT_PAGE = "STATE_CURRENT_PAGE";
    private static final String STATE_TOTAL_PAGES_NUM = "STATE_TOTAL_PAGES_NUM";
    private static final String STATE_MOVIES_LIST = "STATE_MOVIES_LIST";

    @BindView(R.id.rv_movies) RecyclerView moviesRecyclerView;
    @BindView(R.id.loading_movies_pb) ProgressBar loadingMoviesProgressBar;
    @BindView(R.id.ll_empty_list) LinearLayout emptyMoviesListLinearLayout;

    @BindInt(R.integer.grid_span_count) int gridSpanCount;
    private Unbinder unbinder;

    private GridLayoutManager layoutManager;
    private MoviesAdapter adapter;

    private List<Movie> moviesList;
    private int moviesSortingMethod = TmdbServices.SORT_MOVIES_BY_POPULARITY; // The default is sorting by popularity
    private int currentPage;
    private int totalPagesNum;

    private int recyclerViewVisibleThreshold;
    private EndlessRecyclerOnScrollListener endlessOnScrollListener;

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
        unbinder = ButterKnife.bind(this, fragmentView);

        if (savedInstanceState != null) {
            moviesSortingMethod = savedInstanceState.getInt(STATE_SORTING_METHOD);
            // the sorting menu item checking is restored automatically

            Log.d(TAG, "restored sorting method = " + moviesSortingMethod);
        }

        configureMoviesRecyclerView();

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            currentPage = totalPagesNum = 1;
            loadMovies();
        }
        else {
            // restore data from savedInstanceState
            currentPage = savedInstanceState.getInt(STATE_CURRENT_PAGE);
            totalPagesNum = savedInstanceState.getInt(STATE_TOTAL_PAGES_NUM);
            moviesList = Parcels.unwrap(savedInstanceState.getParcelable(STATE_MOVIES_LIST));
            adapter.changeMoviesList(moviesList);
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(STATE_SORTING_METHOD, moviesSortingMethod);
        outState.putInt(STATE_CURRENT_PAGE, currentPage);
        outState.putInt(STATE_TOTAL_PAGES_NUM, totalPagesNum);
        outState.putParcelable(STATE_MOVIES_LIST, Parcels.wrap(moviesList));

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void configureMoviesRecyclerView() {
        // This setting improves performance since changes
        // in content do not change the layout size of the RecyclerView
        moviesRecyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(getContext(), gridSpanCount);
        moviesRecyclerView.setLayoutManager(layoutManager);

        moviesList = new ArrayList<>();
        adapter = new MoviesAdapter(getContext(), moviesList, this);
        moviesRecyclerView.setAdapter(adapter);

        recyclerViewVisibleThreshold = 5*gridSpanCount; // always 5 rows of visible threshold
        endlessOnScrollListener = new EndlessRecyclerOnScrollListener(recyclerViewVisibleThreshold) {
            @Override
            public void onLoadMore() {
                if (currentPage < totalPagesNum) {
                    currentPage++;
                    loadMovies();
                }
                else {
                    Toast.makeText(BrowseMoviesFragment.this.getContext()
                            , R.string.endless_scrolling_end_msg, Toast.LENGTH_SHORT).show();
                }
            }
        };
        moviesRecyclerView.addOnScrollListener(endlessOnScrollListener);
    }

    private void loadMovies() {

        loadingMoviesProgressBar.setVisibility(View.VISIBLE);
        emptyMoviesListLinearLayout.setVisibility(View.INVISIBLE);
        if (errorSnackbar != null) {
            errorSnackbar.dismiss();
        }

        TmdbServices.requestMovies(getContext(), moviesSortingMethod, currentPage
                , new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            totalPagesNum = response.getInt("total_pages");
                            Log.d(TAG, "totalPagesNum = " + totalPagesNum);

                            // serialize the json response to Movies array
                            Gson gson = new Gson();
                            Movie [] moviesArray = gson.fromJson(
                                    response.getJSONArray(TmdbServices.ResponseKeys.RESULTS).toString()
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
        endlessOnScrollListener.reset();

        moviesList.clear();
        adapter.notifyDataSetChanged();
        currentPage = 1;
        loadMovies();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.fragment_browse_movies, menu);

        // check the menu item corresponding to the sorting method
        if (moviesSortingMethod == TmdbServices.SORT_MOVIES_BY_POPULARITY) {
            menu.findItem(R.id.action_sort_by_popularity).setChecked(true);
        }
        else if (moviesSortingMethod == TmdbServices.SORT_MOVIES_BY_RATING) {
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

                moviesSortingMethod = TmdbServices.SORT_MOVIES_BY_POPULARITY;

                resetMoviesRecyclerView();

                return true;

            case R.id.action_sort_by_rating:
                item.setChecked(true);

                moviesSortingMethod = TmdbServices.SORT_MOVIES_BY_RATING;

                resetMoviesRecyclerView();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onRecyclerViewItemClicked(int clickedItemIndex) {
        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);

        long movieId = moviesList.get(clickedItemIndex).getId();
        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE_ID, movieId);

        startActivity(intent);
    }

}
