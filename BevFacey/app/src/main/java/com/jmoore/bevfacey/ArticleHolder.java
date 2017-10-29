package com.jmoore.bevfacey;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;

class ArticleHolder extends RecyclerView.ViewHolder {
    private final Activity context;
    private boolean loaded = false;
    private final LinearLayout layout;
    private final String[] imageUrlPattern = {"<img alt=\"\" src=\"", "\">"}; //Pattern for finding image URLs

    ArticleHolder(Activity context, View itemView) {
        super(itemView);
        this.context = context;
        layout = itemView.findViewById(R.id.articleLayout);
    }

    void bindArticle(String content) {
        if(!loaded) {
            final ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setPadding(0, 0, 0, 0);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setAdjustViewBounds(true);

            if(content.contains("<image alt")) {
                String imgId = MainActivity.getFromPatternStatic(imageUrlPattern, content);
                try {
                    Picasso.with(context).load(imgId).into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (!loaded) {
                                //loaded = true;
                                layout.addView(imageView);
                            }
                        }

                        @Override
                        public void onError() {
                            Log.w("ImageFailedToLoad", "An image failed to load properly");
                        }
                    });
                } catch (Exception ignored) {
                    Log.w("ImageFailedToLoad", "An image failed to load properly");
                }
            }

            WebView textView = new WebView(context);
            textView.getSettings().setLoadsImagesAutomatically(true);
            textView.loadData(content, "text/html; charset=utf-8", "UTF-8");
            layout.addView(textView);
            loaded = true;
        } else {
            Log.i("Loaded", "View already loaded");
        }
    }
}