package com.abdallah.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.abdallah.popularmovies.R;
import com.abdallah.popularmovies.data.MovieDbContract;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteMoviesCursorAdapter extends RecyclerView.Adapter<FavoriteMoviesCursorAdapter.ViewHolder> {

    private static final String TAG = FavoriteMoviesCursorAdapter.class.getSimpleName();

    Context ctx;
    private Cursor cursor;
    private RecyclerViewItemClickListener itemClickListener;

    public FavoriteMoviesCursorAdapter(Context ctx
            , RecyclerViewItemClickListener itemClickListener) {
        this.ctx = ctx;
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

        int idColIndex = cursor.getColumnIndex(MovieDbContract.FavoriteMovie._ID);
        int titleColIndex = cursor.getColumnIndex(MovieDbContract.FavoriteMovie.COLUMN_NAME_TITLE);
        int posterPathColIndex = cursor.getColumnIndex(MovieDbContract.FavoriteMovie.COLUMN_NAME_POSTER_PATH);

        cursor.moveToPosition(position); // get to the right location in the cursor

        long id = cursor.getLong(idColIndex);
        String title = cursor.getString(titleColIndex);
        String posterPath = cursor.getString(posterPathColIndex);

        holder.itemView.setTag(id);
        String posterUrl = ctx.getString(R.string.tmdb_img_url, posterPath);
        Picasso.get()
                .load(posterUrl)
                .into(holder.moviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (cursor == c) {
            return null; // nothing has changed
        }
        Cursor temp = cursor;
        this.cursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
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
            long movieId = (long) v.getTag();
            itemClickListener.onRecyclerViewItemClicked(movieId);
        }
    }

    /**
     * Used in handling items clicks
     */
    public interface RecyclerViewItemClickListener {
        void onRecyclerViewItemClicked(long movieId);
    }
}
