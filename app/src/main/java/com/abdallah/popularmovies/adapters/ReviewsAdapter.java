package com.abdallah.popularmovies.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abdallah.popularmovies.R;
import com.abdallah.popularmovies.models.Review;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private final static String TAG = ReviewsAdapter.class.getSimpleName();

    private Review [] reviews;

    public ReviewsAdapter(Review[] reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reviews_recyclerview_item, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review rev = reviews[position];

        holder.reviewContentTextView.setText(rev.getContent());
        holder.reviewAuthorTextView.setText(rev.getAuthor());
    }

    @Override
    public int getItemCount() {
        return reviews.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView reviewContentTextView;
        public TextView reviewAuthorTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            reviewContentTextView = itemView.findViewById(R.id.tv_review_content);
            reviewAuthorTextView = itemView.findViewById(R.id.tv_review_author);
        }
    }
}
