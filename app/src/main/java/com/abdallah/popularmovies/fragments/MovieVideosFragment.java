package com.abdallah.popularmovies.fragments;


import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import com.abdallah.popularmovies.R;
import com.abdallah.popularmovies.adapters.VideosAdapter;
import com.abdallah.popularmovies.api.TMDBServices;
import com.abdallah.popularmovies.models.Video;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieVideosFragment extends Fragment {

    private static final String TAG = MovieVideosFragment.class.getSimpleName();

    private static final String ARG_MOVIE_ID = "ARG_MOVIE_ID";

    @BindView(R.id.rv_videos) RecyclerView videosRecyclerView;
    @BindView(R.id.pb_loading_videos) ProgressBar loadingVideosProgressBar;
    @BindView(R.id.tv_videos_msg) TextView videosMsgTextView;

    private long movieId;

    private VideosAdapter videosAdapter;

    public MovieVideosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieId The ID of the movie to display its videos.
     * @return A new instance of fragment MovieVideosFragment.
     */
    public static MovieVideosFragment newInstance(long movieId) {
        MovieVideosFragment fragment = new MovieVideosFragment();
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
        View fragmentView = inflater.inflate(R.layout.fragment_movie_videos, container
                , false);
        ButterKnife.bind(this, fragmentView);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadMovieVideos();
    }

    public void loadMovieVideos() {

        loadingVideosProgressBar.setVisibility(View.VISIBLE);
        videosMsgTextView.setVisibility(View.INVISIBLE);

        TMDBServices.requestMovieVideos(getContext(), movieId
                , new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // serialize the json to Videos array
                        try {
                            Gson gson = new Gson();
                            Video[] videos = gson.fromJson(response.getJSONArray("results").toString()
                                    , Video[].class);

                            if (videos != null) {

                                if (videos.length > 0) {
                                    // display the videos in the recycler view

                                    videosRecyclerView.setHasFixedSize(false);

                                    videosRecyclerView.setNestedScrollingEnabled(false);

                                    LinearLayoutManager layoutManager = new LinearLayoutManager(
                                            MovieVideosFragment.this.getContext());
                                    videosRecyclerView.setLayoutManager(layoutManager);

                                    DividerItemDecoration dividerItemDecoration =
                                            new DividerItemDecoration(MovieVideosFragment.this.getContext(),
                                                    layoutManager.getOrientation());
                                    videosRecyclerView.addItemDecoration(dividerItemDecoration);

                                    videosAdapter = new VideosAdapter(videos
                                            , new VideosAdapter.RecyclerViewItemClickListener() {
                                        @Override
                                        public void onRecyclerViewItemClicked(Video video) {
                                            String videoLink =
                                                    getString(R.string.youtube_video_base_url, video.getKey());
                                            openVideoExternally(videoLink);
                                        }
                                    });
                                    videosRecyclerView.setAdapter(videosAdapter);
                                }
                                else {
                                    videosMsgTextView.setText(R.string.no_videos_msg);
                                    videosMsgTextView.setVisibility(View.VISIBLE);
                                }

                            }
                            else {
                                Log.d(TAG, "videos equals null");

                                videosMsgTextView.setText(R.string.error_loading_videos_msg);
                                videosMsgTextView.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            Log.d(TAG, e.toString());

                            videosMsgTextView.setText(R.string.error_loading_videos_msg);
                            videosMsgTextView.setVisibility(View.VISIBLE);
                        }

                        loadingVideosProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
                , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());

                        videosMsgTextView.setText(R.string.error_loading_videos_msg);
                        videosMsgTextView.setVisibility(View.VISIBLE);

                        loadingVideosProgressBar.setVisibility(View.INVISIBLE);
                    }
                });

    }

    public void openVideoExternally(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            Toast.makeText(getContext(), R.string.error_open_url, Toast.LENGTH_SHORT).show();
        }
    }

}
