package com.bevfacey.bfhnew;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class ShadowVerticalSpaceItemDecorator extends RecyclerView.ItemDecoration {
    private Drawable divider;

    ShadowVerticalSpaceItemDecorator(Context context, int resId) {
        this.divider = ContextCompat.getDrawable(context, resId);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        for (int childIdx = 0; childIdx < parent.getChildCount(); childIdx++) {
            View item = parent.getChildAt(childIdx);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) item.getLayoutParams();
            int top = item.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();
            this.divider.setBounds(left, top, right, bottom);
            this.divider.draw(c);
        }
    }
}