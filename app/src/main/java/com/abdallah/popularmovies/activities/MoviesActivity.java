package com.abdallah.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abdallah.popularmovies.R;
import com.abdallah.popularmovies.adapters.EndlessRecyclerOnScrollListener;
import com.abdallah.popularmovies.adapters.MoviesAdapter;
import com.abdallah.popularmovies.api.TMDBServices;
import com.abdallah.popularmovies.models.Movie;
import com.abdallah.popularmovies.utils.network.NetworkUtils;
import com.android.volley.Response;
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
import butterknife.OnClick;

public class MoviesActivity extends AppCompatActivity implements MoviesAdapter.RecyclerViewItemClickListener {

    private static final String TAG = MoviesActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE_ID = "MovieID";

    @BindView(R.id.rv_movies) RecyclerView moviesRecyclerView;
    @BindView(R.id.loading_movies_pb) ProgressBar loadingMoviesProgressBar;
    @BindView(R.id.ll_no_connectivity) LinearLayout noConnectivityMsgLinearLayout;

    @BindInt(R.integer.grid_span_count) int gridSpanCount;

    private GridLayoutManager layoutManager;
    private MoviesAdapter adapter;

    private List<Movie> moviesList;

    private int moviesSortingMethod = TMDBServices.SORT_MOVIES_BY_POPULARITY; // The default is sorting by popularity
    private int currentPage = 1;

    private int recyclerViewVisibleThreshold = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);


        configureMoviesRecyclerView();
    }

    private void configureMoviesRecyclerView() {
        // This setting improves performance since changes
        // in content do not change the layout size of the RecyclerView
        moviesRecyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, gridSpanCount);
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

        TMDBServices.requestMovies(moviesSortingMethod, currentPage, this
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
                            Toast.makeText(MoviesActivity.this, getString(R.string.load_movies_error_msg)
                                    , Toast.LENGTH_SHORT).show();
                        }

                        loadingMoviesProgressBar.setVisibility(View.INVISIBLE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        loadingMoviesProgressBar.setVisibility(View.INVISIBLE);
                        noConnectivityMsgLinearLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(MoviesActivity.this, getString(R.string.load_movies_error_msg)
                                , Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resetMoviesRecyclerViewAfterChangingSorting() {
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

    @OnClick(R.id.btn_retry_loading)
    public void retryLoadingMovies() {
        if (NetworkUtils.isOnline(this)) {
            noConnectivityMsgLinearLayout.setVisibility(View.INVISIBLE);

            configureMoviesRecyclerView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify sortByPopularityMenuItem parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_sort_by_popularity:
                item.setChecked(true);

                moviesSortingMethod = TMDBServices.SORT_MOVIES_BY_POPULARITY;

                noConnectivityMsgLinearLayout.setVisibility(View.INVISIBLE);

                resetMoviesRecyclerViewAfterChangingSorting();

                return true;

            case R.id.action_sort_by_rating:
                item.setChecked(true);

                moviesSortingMethod = TMDBServices.SORT_MOVIES_BY_RATING;

                noConnectivityMsgLinearLayout.setVisibility(View.INVISIBLE);

                resetMoviesRecyclerViewAfterChangingSorting();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onRecyclerViewItemClicked(int clickedItemIndex) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);

        Movie movie = moviesList.get(clickedItemIndex);
        long movieId = movie.getId();
        intent.putExtra(EXTRA_MOVIE_ID, movieId);

        startActivity(intent);
    }


}
