package com.abdallah.popularmovies.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abdallah.popularmovies.R;
import com.abdallah.popularmovies.api.TMDBServices;
import com.abdallah.popularmovies.models.Movie;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    private Movie movie;

    @BindView(R.id.tv_title) TextView titleTextView;
    @BindView(R.id.iv_movie_poster) ImageView moviePosterImageView;
    @BindView(R.id.tv_release_date) TextView releaseDateTextView;
    @BindView(R.id.tv_runtime) TextView runtimeTextView;
    @BindView(R.id.tv_rating) TextView ratingTextView;
    @BindView(R.id.btn_favorite) Button markAsFavoriteButton;
    @BindView(R.id.tv_overview) TextView overviewTextView;
    @BindView(R.id.movie_details_layout) ConstraintLayout movieDetailsLayout;
    @BindView(R.id.pb_loading_movie_details) ProgressBar loadingMovieDetailsProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        long movieId = intent.getLongExtra(MoviesActivity.EXTRA_MOVIE_ID, -1);

        loadMovieDetails(movieId);
    }

    @OnClick(R.id.btn_favorite)
    public void markMovieAsFavoriteOrUnfavorite() {

        // change button to become "unfavorite" button
        markAsFavoriteButton.setText(R.string.mark_as_unfavorite_button_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // API level 17 and above
            markAsFavoriteButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    getResources().getDrawable(R.drawable.ic_heart), null, null, null);
        }
        else {
            markAsFavoriteButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart)
                    , null, null, null);
        }


    }

    private void loadMovieDetails(long movieId) {

        movieDetailsLayout.setVisibility(View.INVISIBLE);
        loadingMovieDetailsProgressBar.setVisibility(View.VISIBLE);

        TMDBServices.requestMovieDetails(movieId, this
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // serialize the json to Movie object
                Gson gson = new Gson();
                movie = gson.fromJson(response.toString(), Movie.class);

                if (movie != null) {
                    titleTextView.setText(movie.getTitle());

                    String posterUrl = TMDBServices.IMG_BASE_URL + movie.getPosterPath();
                    Picasso.get()
                            .load(posterUrl)
                            .into(moviePosterImageView);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
                    releaseDateTextView.setText(simpleDateFormat.format(movie.getReleaseDate()));

                    runtimeTextView.setText(getString(R.string.movie_runtime, movie.getRuntime()));

                    ratingTextView.setText(Float.toString(movie.getVoteAverage()));

                    overviewTextView.setText(movie.getOverview());


                    movieDetailsLayout.setVisibility(View.VISIBLE);
                }
                else {
                    Log.d(TAG, "movie equals null");
                    Toast.makeText(MovieDetailsActivity.this, getString(R.string.load_movie_details_error_msg)
                            , Toast.LENGTH_SHORT).show();
                }

                loadingMovieDetailsProgressBar.setVisibility(View.INVISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                loadingMovieDetailsProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MovieDetailsActivity.this, getString(R.string.load_movie_details_error_msg)
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }
}
