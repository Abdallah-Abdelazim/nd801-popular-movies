package com.abdallah.popularmovies.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abdallah.popularmovies.R;
import com.abdallah.popularmovies.adapters.ReviewsAdapter;
import com.abdallah.popularmovies.api.TMDBServices;
import com.abdallah.popularmovies.models.Review;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MovieReviewsFragment extends Fragment {

    private static final String TAG = MovieReviewsFragment.class.getSimpleName();

    private static final String ARG_MOVIE_ID = "ARG_MOVIE_ID";

    @BindView(R.id.rv_reviews) RecyclerView reviewsRecyclerView;
    @BindView(R.id.pb_loading_reviews) ProgressBar loadingReviewsProgressBar;
    @BindView(R.id.tv_reviews_msg) TextView reviewsMsgTextView;
    private Unbinder unbinder;

    private long movieId;

    private ReviewsAdapter reviewsAdapter;

    public MovieReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieId The ID of the movie to display its reviews.
     * @return A new instance of fragment MovieReviewsFragment.
     */
    public static MovieReviewsFragment newInstance(long movieId) {
        MovieReviewsFragment fragment = new MovieReviewsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getLong(ARG_MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_movie_reviews, container
                , false);
        unbinder = ButterKnife.bind(this, fragmentView);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadMovieReviews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void loadMovieReviews() {

        loadingReviewsProgressBar.setVisibility(View.VISIBLE);
        reviewsMsgTextView.setVisibility(View.INVISIBLE);

        TMDBServices.requestMovieReviews(getContext(), movieId
                , new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // serialize the json into Review array
                        try {
                            Gson gson = new Gson();
                            Review[] reviews = gson.fromJson(
                                    response.getJSONArray(TMDBServices.ResponseKeys.RESULTS).toString()
                                    , Review[].class);

                            if (reviews != null) {

                                if (reviews.length > 0) {
                                    // display the reviews in the recycler view

                                    reviewsRecyclerView.setHasFixedSize(false);

                                    reviewsRecyclerView.setNestedScrollingEnabled(false);

                                    LinearLayoutManager layoutManager = new LinearLayoutManager(
                                            MovieReviewsFragment.this.getContext());
                                    reviewsRecyclerView.setLayoutManager(layoutManager);

                                    DividerItemDecoration dividerItemDecoration =
                                            new DividerItemDecoration(MovieReviewsFragment.this.getContext(),
                                            layoutManager.getOrientation());
                                    reviewsRecyclerView.addItemDecoration(dividerItemDecoration);

                                    reviewsAdapter = new ReviewsAdapter(reviews);
                                    reviewsRecyclerView.setAdapter(reviewsAdapter);

                                }
                                else {
                                    reviewsMsgTextView.setText(R.string.no_reviews_msg);
                                    reviewsMsgTextView.setVisibility(View.VISIBLE);
                                }
                            }
                            else {
                                Log.d(TAG, "reviews array equals null");

                                reviewsMsgTextView.setText(R.string.error_loading_reviews_msg);
                                reviewsMsgTextView.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            Log.d(TAG, e.toString());

                            reviewsMsgTextView.setText(R.string.error_loading_reviews_msg);
                            reviewsMsgTextView.setVisibility(View.VISIBLE);
                        }

                        loadingReviewsProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
                , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());

                        reviewsMsgTextView.setText(R.string.error_loading_reviews_msg);
                        reviewsMsgTextView.setVisibility(View.VISIBLE);

                        loadingReviewsProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

}
