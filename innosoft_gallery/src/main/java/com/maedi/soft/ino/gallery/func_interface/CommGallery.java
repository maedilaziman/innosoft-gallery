package com.maedi.soft.ino.gallery.func_interface;

import android.support.v7.widget.RecyclerView;

public interface CommGallery {

    interface LoadImage<T> {

        void setImage(T t1, T t2);

        RecyclerView setListView(T t1);
    }
}
