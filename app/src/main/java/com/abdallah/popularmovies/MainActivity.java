package com.abdallah.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abdallah.popularmovies.adapter.EndlessRecyclerOnScrollListener;
import com.abdallah.popularmovies.adapter.MoviesAdapter;
import com.abdallah.popularmovies.api.TMDBServices;
import com.abdallah.popularmovies.entity.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.rv_movies) RecyclerView moviesRecyclerView;
    @BindView(R.id.loading_movies_pb) ProgressBar loadingMoviesProgressBar;
    @BindView(R.id.ll_no_connectivity) LinearLayout noConnectivityMsgLinearLayout;

    @BindInt(R.integer.grid_span_count) int gridSpanCount;

    private GridLayoutManager layoutManager;
    private MoviesAdapter adapter;

    private List<Movie> moviesList;

    private int moviesSortingMethod = TMDBServices.SORT_MOVIES_BY_POPULARITY; // The default is sorting by popularity
    private int currentPage = 1;

    private boolean noInternetConnectivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        // check whether there's network connectivity or not
        if (isOnline()) {

            // This setting improves performance since changes
            // in content do not change the layout size of the RecyclerView
            moviesRecyclerView.setHasFixedSize(true);

            layoutManager = new GridLayoutManager(this, gridSpanCount);
            moviesRecyclerView.setLayoutManager(layoutManager);

            moviesList = new ArrayList<>();
            adapter = new MoviesAdapter(moviesList);
            moviesRecyclerView.setAdapter(adapter);

            loadMovies(moviesSortingMethod, currentPage);

            int recyclerViewVisibleThreshold = 5;
            moviesRecyclerView.addOnScrollListener(
                    new EndlessRecyclerOnScrollListener(recyclerViewVisibleThreshold) {
                @Override
                public void onLoadMore() {
                    loadMovies(moviesSortingMethod, ++currentPage);
                }
            });
        }
        else {
            noInternetConnectivity = true;
            moviesRecyclerView.setVisibility(View.INVISIBLE);
            loadingMoviesProgressBar.setVisibility(View.INVISIBLE);
            noConnectivityMsgLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loadMovies(int sortingMethod, int page) {
        new MoviesQueryTask().execute(sortingMethod, page);
    }

    @OnClick(R.id.btn_retry_loading)
    public void retryLoadingMovies() {
        if (isOnline()) {
            noConnectivityMsgLinearLayout.setVisibility(View.INVISIBLE);

            // This setting improves performance since changes
            // in content do not change the layout size of the RecyclerView
            moviesRecyclerView.setHasFixedSize(true);

            layoutManager = new GridLayoutManager(this, gridSpanCount);
            moviesRecyclerView.setLayoutManager(layoutManager);

            loadMovies(moviesSortingMethod, currentPage);
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
                if (noInternetConnectivity) {
                    Toast.makeText(this, getString(R.string.no_internet_toast), Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }

                moviesSortingMethod = TMDBServices.SORT_MOVIES_BY_POPULARITY;
                item.setChecked(true);

                // reload the recycler view
                loadMovies(TMDBServices.SORT_MOVIES_BY_POPULARITY, currentPage);

                return true;

            case R.id.action_sort_by_rating:
                if (noInternetConnectivity) {
                    Toast.makeText(this, getString(R.string.no_internet_toast), Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }

                moviesSortingMethod = TMDBServices.SORT_MOVIES_BY_RATING;
                item.setChecked(true);

                // reload recycler view
                loadMovies(TMDBServices.SORT_MOVIES_BY_RATING, currentPage);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private class MoviesQueryTask extends AsyncTask<Integer, Void, List<Movie> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingMoviesProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(Integer... integers) {
            if (integers.length == 2) {
                int sortingMethod = integers[0];
                int page = integers[1];

                List<Movie> movies = TMDBServices.getMovies(sortingMethod, page);
                return movies;
            }
            else {
                Log.d(TAG, "doInBackground() :Wrong method parameters.");
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            loadingMoviesProgressBar.setVisibility(View.INVISIBLE);

            if (movies != null) {
                moviesList.addAll(movies);
                adapter.notifyDataSetChanged();
            }
            else {
                Log.d(TAG, "onPostExecute(): moviesList equals null");
                Toast.makeText(MainActivity.this
                        , R.string.load_movies_error_msg, Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }
}
