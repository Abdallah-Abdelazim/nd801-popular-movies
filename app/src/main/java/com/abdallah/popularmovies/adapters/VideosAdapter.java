package com.abdallah.popularmovies.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abdallah.popularmovies.R;
import com.abdallah.popularmovies.models.Video;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder>{

    private final static String TAG = VideosAdapter.class.getSimpleName();

    private Video [] videos;
    private ListItemClickListener itemClickListener;

    public VideosAdapter(Video[] videos, ListItemClickListener itemClickListener) {
        this.videos = videos;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.videos_recyclerview_item, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video vid = videos[position];
        holder.videoNameTextView.setText(vid.getName());
    }

    @Override
    public int getItemCount() {
        return videos.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView videoNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            videoNameTextView = itemView.findViewById(R.id.tv_video_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedItemIndex = getAdapterPosition();
            Video video = videos[clickedItemIndex];
            itemClickListener.onListItemClicked(video);
        }
    }

    /**
     * Used in handling items clicks
     */
    public interface ListItemClickListener {
        void onListItemClicked(Video video);
    }
}
