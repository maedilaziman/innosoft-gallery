package com.maedi.soft.ino.gallery.utils;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.maedi.soft.ino.gallery.model.ListObject;

public class CommDiffCallback extends DiffUtil.Callback {

    private final ListObject mOldList;
    private final ListObject mNewList;

    public CommDiffCallback(ListObject oldList, ListObject newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).i2 == mNewList.get(newItemPosition).i2;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final ListObject oldData = mOldList.get(oldItemPosition);
        final ListObject newData =  mNewList.get(newItemPosition);

        return oldData.i2 == newData.i2;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}