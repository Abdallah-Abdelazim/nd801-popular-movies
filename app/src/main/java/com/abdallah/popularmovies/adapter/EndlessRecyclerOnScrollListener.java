package com.abdallah.popularmovies.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * An OnScrollListener for the Grid Recycler View.
 * Loads more data in the Recycler View as the user scrolls so that scrolling doesn't stop.
 *
 * @author Abdallah Abdelazim
 * This class is based on the following tutorial: https://goo.gl/Thgjkw
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    /**
     * The total number of items in the dataset after the last load
     */
    private int previousTotalItemCount = 0;

    /**
     * True if we are still waiting for the last set of data to load.
     */
    private boolean loading = true;

    private int visibleThreshold = 5;

    public EndlessRecyclerOnScrollListener() {
    }

    public EndlessRecyclerOnScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount(); // number of items already been viewed
        int totalItemCount = recyclerView.getLayoutManager().getItemCount();
        int firstVisibleItem = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();


        // The below condition is used to prevent sending more requests for data
        // while the first one is still loading (network operations are usually slow)
        if (loading) {
            if (totalItemCount > previousTotalItemCount) {
                // if the dataset count has changed then we can conclude that the loading is finished.
                loading = false;
                previousTotalItemCount = totalItemCount;
            }
        }

        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            onLoadMore();

            loading = true;
        }
    }

    public abstract void onLoadMore();
}