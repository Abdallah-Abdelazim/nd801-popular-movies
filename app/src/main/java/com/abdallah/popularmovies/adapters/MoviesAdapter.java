package com.abdallah.popularmovies.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.abdallah.popularmovies.R;
import com.abdallah.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();

    private List<Movie> movies;

    private RecyclerViewItemClickListener itemClickListener;

    public MoviesAdapter(List<Movie> movies, RecyclerViewItemClickListener itemClickListener) {
        this.movies = movies;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);

        String posterUrl = holder.itemView.getResources()
                .getString(R.string.tmdb_img_url, movie.getPosterPath());
        Picasso.get()
                .load(posterUrl)
                .into(holder.moviePosterImageView);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_movie_poster) public ImageView moviePosterImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedItemIndex = getAdapterPosition();
            itemClickListener.onRecyclerViewItemClicked(clickedItemIndex);
        }
    }

    /**
     * Used in handling items clicks
     */
    public interface RecyclerViewItemClickListener {
        void onRecyclerViewItemClicked(int clickedItemIndex);
    }

}
