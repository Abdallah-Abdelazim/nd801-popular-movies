package com.abdallah.popularmovies.activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abdallah.popularmovies.R;
import com.abdallah.popularmovies.api.TmdbServices;
import com.abdallah.popularmovies.data.MovieDbContract;
import com.abdallah.popularmovies.fragments.MovieReviewsFragment;
import com.abdallah.popularmovies.fragments.MovieVideosFragment;
import com.abdallah.popularmovies.models.Movie;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE_ID = "MovieID";

    private static final String STATE_MOVIE_ID = "STATE_MOVIE_ID";
    private static final String STATE_MOVIE = "STATE_MOVIE";
    private static final String STATE_IS_FAVORITE = "STATE_IS_FAVORITE";

    @BindView(R.id.tv_title) TextView titleTextView;
    @BindView(R.id.iv_movie_poster) ImageView moviePosterImageView;
    @BindView(R.id.tv_release_date) TextView releaseDateTextView;
    @BindView(R.id.tv_runtime) TextView runtimeTextView;
    @BindView(R.id.tv_rating) TextView ratingTextView;
    @BindView(R.id.btn_favorite) Button markAsFavoriteButton;
    @BindView(R.id.tv_overview) TextView overviewTextView;
    @BindView(R.id.tv_imdb_link) TextView imdbLinkTextView;

    @BindView(R.id.movie_details_layout) NestedScrollView movieDetailsLayout;
    @BindView(R.id.pb_loading_movie_details) ProgressBar loadingMovieDetailsProgressBar;

    @BindView(R.id.ll_load_error) LinearLayout loadErrorLinearLayout;
    @BindView(R.id.tv_error_msg) TextView errorMsgTextView;

    private long movieId;

    private Movie movie;

    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        movieId = getIntent().getLongExtra(EXTRA_MOVIE_ID, -1);

        if (movieId != -1) {

            if (savedInstanceState == null) {
                loadMovieDetails();
                checkIsFavorite();

                FragmentManager fragmentManager = getSupportFragmentManager();
                // add MovieVideosFragment
                fragmentManager.beginTransaction()
                        .add(R.id.movie_videos_fragment_container, MovieVideosFragment.newInstance(movieId))
                        .commit();
                // add MovieReviewsFragment
                fragmentManager.beginTransaction()
                        .add(R.id.movie_reviews_fragment_container, MovieReviewsFragment.newInstance(movieId))
                        .commit();
            }
            else {
                // restore data from savedInstanceState
                movieId = savedInstanceState.getLong(STATE_MOVIE_ID);
                movie = Parcels.unwrap(savedInstanceState.getParcelable(STATE_MOVIE));
                isFavorite = savedInstanceState.getBoolean(STATE_IS_FAVORITE);

                displayMovie();
                if (isFavorite) {
                    displayUnfavoriteButton();
                }
            }

        }
        else {
            Log.d(TAG, "movieId wasn't found in the intent extras!");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(STATE_MOVIE_ID, movieId);
        outState.putParcelable(STATE_MOVIE, Parcels.wrap(movie));
        outState.putBoolean(STATE_IS_FAVORITE, isFavorite);

    }

    private void checkIsFavorite() {
        markAsFavoriteButton.setVisibility(View.INVISIBLE);

        Uri movieUri = ContentUris.withAppendedId(MovieDbContract.FavoriteMovie.CONTENT_URI, movieId);
        Cursor cursor = getContentResolver().
                query(movieUri, null, null, null, null);
        if (cursor.getCount() != 0) {
            // movie exists in the favorite movies db
            displayUnfavoriteButton();
        }
        markAsFavoriteButton.setVisibility(View.VISIBLE);
    }

    private void displayUnfavoriteButton() {
        isFavorite = true;
        // change button to become "unfavorite" button
        markAsFavoriteButton.setText(R.string.mark_as_unfavorite_button_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // API level 17 and above
            markAsFavoriteButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    getResources().getDrawable(R.drawable.ic_heart), null, null, null);
        }
        else {
            markAsFavoriteButton.setCompoundDrawablesWithIntrinsicBounds(
                    getResources().getDrawable(R.drawable.ic_heart), null, null, null);
        }
    }

    private void displayFavoriteButton() {
        isFavorite = false;
        // change button to become "favorite" button
        markAsFavoriteButton.setText(R.string.mark_as_favorite_button_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // API level 17 and above
            markAsFavoriteButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    getResources().getDrawable(R.drawable.ic_heart_outline), null, null, null);
        }
        else {
            markAsFavoriteButton.setCompoundDrawablesWithIntrinsicBounds(
                    getResources().getDrawable(R.drawable.ic_heart_outline), null, null, null);
        }
    }

    @OnClick(R.id.btn_favorite)
    public void markMovieAsFavoriteOrUnfavorite() {

        if (!isFavorite) {
            // favorite the movie
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieDbContract.FavoriteMovie._ID, movie.getId());
            contentValues.put(MovieDbContract.FavoriteMovie.COLUMN_NAME_TITLE, movie.getTitle());
            contentValues.put(MovieDbContract.FavoriteMovie.COLUMN_NAME_POSTER_PATH, movie.getPosterPath());

            Uri insertedMovieUri = getContentResolver().insert(
                    MovieDbContract.FavoriteMovie.CONTENT_URI, contentValues);

            if (insertedMovieUri != null) {
                displayUnfavoriteButton();
            }
            else {
                Toast.makeText(this, R.string.movie_favorite_unfavorite_error
                        , Toast.LENGTH_SHORT).show();
            }
        }
        else {
            // unfavorite the movie
            Uri movieUri = ContentUris.withAppendedId(MovieDbContract.FavoriteMovie.CONTENT_URI, movieId);
            int moviesDeleted = getContentResolver().delete(movieUri, null, null);
            if (moviesDeleted != 0) {
                displayFavoriteButton();
            }
            else {
                Toast.makeText(this, R.string.movie_favorite_unfavorite_error
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadMovieDetails() {

        movieDetailsLayout.setVisibility(View.INVISIBLE);
        loadErrorLinearLayout.setVisibility(View.INVISIBLE);
        loadingMovieDetailsProgressBar.setVisibility(View.VISIBLE);

        TmdbServices.requestMovieDetails(this, movieId
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // serialize the json to Movie object
                Gson gson = new Gson();
                movie = gson.fromJson(response.toString(), Movie.class);

                if (movie != null) {
                    displayMovie();

                    movieDetailsLayout.setVisibility(View.VISIBLE);
                }
                else {
                    // null or empty response
                    Log.d(TAG, "Gson parsed movie equals null");

                    displayLoadError(getString(R.string.load_movie_details_error_msg));
                }

                loadingMovieDetailsProgressBar.setVisibility(View.INVISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());

                displayLoadError(error);
            }
        });
    }

    private void displayMovie() {
        titleTextView.setText(movie.getTitle());

        String posterUrl = getString(R.string.tmdb_img_url, movie.getPosterPath());
        Picasso.get()
                .load(posterUrl)
                .placeholder(R.drawable.img_download_placeholder)
                .into(moviePosterImageView);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", Locale.US);
        releaseDateTextView.setText(simpleDateFormat.format(movie.getReleaseDate()));

        runtimeTextView.setText(getString(R.string.movie_runtime, movie.getRuntime()));

        ratingTextView.setText(Float.toString(movie.getVoteAverage()));

        overviewTextView.setText(movie.getOverview());

        Spanned imdbHtmlLink = Html.fromHtml(
                String.format("<a href=\"%2$s\">%1$s</a>", getString(R.string.imdb_link_text)
                        , getString(R.string.imdb_movie_page_base_link, movie.getImdbId())));
        imdbLinkTextView.setText(imdbHtmlLink);
        imdbLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
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
            errorMsg = getString(R.string.load_movie_details_error_msg);
        }

        loadingMovieDetailsProgressBar.setVisibility(View.INVISIBLE);
        errorMsgTextView.setText(errorMsg);
        loadErrorLinearLayout.setVisibility(View.VISIBLE);
    }

    private void displayLoadError(String errorMsg) {
        loadingMovieDetailsProgressBar.setVisibility(View.INVISIBLE);
        errorMsgTextView.setText(errorMsg);
        loadErrorLinearLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_retry_load)
    void retryLoadMovieDetails() {
        loadMovieDetails();

        FragmentManager fragmentManager = getSupportFragmentManager();
        MovieVideosFragment movieVideosFragment =
                (MovieVideosFragment) fragmentManager.findFragmentById(R.id.movie_videos_fragment_container);
        MovieReviewsFragment movieReviewsFragment =
                (MovieReviewsFragment) fragmentManager.findFragmentById(R.id.movie_reviews_fragment_container);
        movieVideosFragment.loadMovieVideos();
        movieReviewsFragment.loadMovieReviews();
    }

    @Override
    protected void onDestroy() {
        // cancels ongoing requests (if any) that if continued will cause the app to crash when finished.
        TmdbServices.cancelOngoingRequests(this);
        Log.d(TAG, "Requests has been canceled!");
        super.onDestroy();
    }
}
