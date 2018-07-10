package com.abdallah.popularmovies.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * An OnScrollListener for the RecyclerView.
 * Loads more data in the RecyclerView as the user scrolls so that scrolling doesn't stop.
 * Works with LinearLayoutManager & GridLayoutManager.
 *
 * This class is based on the following tutorial: https://goo.gl/Thgjkw
 * @author Abdallah Abdelazim
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
    private boolean isLoading = true;

    private int visibleThreshold = 5;

    public EndlessRecyclerOnScrollListener() {}

    public EndlessRecyclerOnScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount(); // number of items currently being viewed (visible items)
        int totalItemCount = recyclerView.getLayoutManager().getItemCount();
        int firstVisibleItem =
                ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();


        // The below condition is used to prevent sending more requests for data
        // while the first one is still loading (network operations are usually slow)
        if (isLoading) {
            if (totalItemCount > previousTotalItemCount) {
                // if the dataset count has changed then we can conclude that the loading is finished.
                isLoading = false;
                previousTotalItemCount = totalItemCount;
            }
        }

        if (!isLoading) {
            int remainingItemCount = totalItemCount - (visibleItemCount + firstVisibleItem);
            if (remainingItemCount <= visibleThreshold) {
                // End has been reached

                onLoadMore();

                isLoading = true;
            }
        }
    }

    public void reset() {
        previousTotalItemCount = 0;
        isLoading = true;
    }

    public void setVisibleThreshold(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public abstract void onLoadMore();
}