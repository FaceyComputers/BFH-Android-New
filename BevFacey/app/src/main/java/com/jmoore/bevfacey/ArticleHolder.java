package com.jmoore.bevfacey;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

class ArticleHolder extends RecyclerView.ViewHolder {
    private final Activity context;
    private boolean loaded = false;
    private final LinearLayout layout;
    private final String[] imageUrlPattern = {"<img alt=\"\" src=\"", "\">"}; //Pattern for finding image URLs
    private boolean imageLoaded = false;

    ArticleHolder(Activity context, View itemView) {
        super(itemView);
        this.context = context;
        layout = itemView.findViewById(R.id.articleLayout);
    }

    private void updateImage() {

    }

    void bindArticle(String content) {
        if(!loaded) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setPadding(0, 0, 0, 0);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setAdjustViewBounds(true);
            imageView.setBackgroundColor(Color.WHITE);

            if(content.contains("<img alt")) {
                String imgId = MainActivity.getFromPatternStatic(imageUrlPattern, content);
                if(!imgId.contains("bevfacey.ca")) {
                    imgId = "https://bevfacey.ca" + imgId;
                }
                Log.e("IMGID",imgId);
                try {
                    Picasso.with(context).load(imgId).into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            //layout.addView(imageView);
                            Log.i("ImageLoaded", "Image loaded successfully");
                        }

                        @Override
                        public void onError() {
                            Log.e("ImageFailedToLoad", "An image failed to load properly");
                        }
                    });
                } catch (Exception ignored) {
                    Log.e("ImageFailedToLoad", "An image failed to load properly: " + ignored.getMessage());
                }
            }

            layout.addView(imageView);

            content = content.replaceAll(Pattern.quote("img alt=\"\""), "img");
            System.out.println(content);
            WebView textView = new WebView(context);
            textView.getSettings().setLoadsImagesAutomatically(true);
            textView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            textView.loadData(content, "text/html; charset=utf-8", "UTF-8");
            layout.addView(textView);
            loaded = true;
        } else {
            Log.i("Loaded", "View already loaded");
        }
    }
}