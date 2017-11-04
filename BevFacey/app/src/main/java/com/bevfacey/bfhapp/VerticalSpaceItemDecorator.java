package com.bevfacey.bfhapp;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class VerticalSpaceItemDecorator extends RecyclerView.ItemDecoration {
    private final int verticalSpaceHeight;

    VerticalSpaceItemDecorator(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1){
            outRect.bottom = verticalSpaceHeight;
        }
    }
}