package com.maedi.soft.ino.gallery.utils;

import android.support.v7.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import timber.log.Timber;

public class PauseOnScrollListener extends RecyclerView.OnScrollListener {

    private final String TAG = this.getClass().getName()+"- PAUSE_SCROLL_LISTEN - ";

    private ImageLoader imageLoader;
    private final boolean pauseOnScroll;
    private final boolean pauseOnFling;

    public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
        this.imageLoader = imageLoader;
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnFling = pauseOnFling;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            if (this.pauseOnScroll) {
                this.imageLoader.pause();
            }
        }
        else if (newState == RecyclerView.SCROLL_STATE_IDLE) {//No scrolling is done
            this.imageLoader.resume();
        }
        else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
            if (this.pauseOnFling) {
                this.imageLoader.pause();
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
    }
}