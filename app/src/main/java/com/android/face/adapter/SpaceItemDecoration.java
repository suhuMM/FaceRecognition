package com.android.face.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author suhu
 * @data 2018/5/15 0015.
 * @description
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration{
    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemCount = 2;
        int pos = parent.getChildAdapterPosition(view);
        outRect.left = 40;
        outRect.top = 40;
        outRect.bottom = 40;
        if (pos!=(itemCount-1)){
            outRect.right = 40;
        }else {
            outRect.right = 40;
        }

    }
}
