package com.abdallah.popularmovies.adapter;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.abdallah.popularmovies.R;
import com.abdallah.popularmovies.api.TMDBServices;
import com.abdallah.popularmovies.entity.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();

    private List<Movie> movies;

    public MoviesAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movies_recyclerview_item, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);

        String posterUrl = TMDBServices.IMG_BASE_URL + movie.getPosterPath();
        Picasso.get()
                .load(posterUrl)
                .into(holder.moviePosterImageView);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView moviePosterImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            moviePosterImageView = itemView.findViewById(R.id.iv_movie_poster);
        }
    }
}
