package com.jmoore.bevfacey;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import java.util.regex.Pattern;

class ArticleHolder extends RecyclerView.ViewHolder {
    private final Activity context;
    private boolean loaded = false;
    private final LinearLayout layout;

    ArticleHolder(Activity context, View itemView) {
        super(itemView);
        this.context = context;
        layout = itemView.findViewById(R.id.articleLayout);
    }

    void bindArticle(String content) {
        if(!loaded) {
            WebView webView = new WebView(context);
            Display display = context.getWindowManager().getDefaultDisplay();
            float width = display.getWidth();
            float scale = webView.getScale();
            int size = (int)(width / scale);

            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            webView.setVerticalScrollBarEnabled(false);
            webView.setHorizontalScrollBarEnabled(false);
            webView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return (event.getAction() == MotionEvent.ACTION_MOVE);
                }
            });

            content = content.replaceAll(Pattern.quote("img alt=\"") + "(.*?)" +
                    Pattern.quote("\" src=\"/"), "img alt=\"\" style=\"margin: 0; " +
                    "padding: 0\" width=" + size + "px src=\"https://bevfacey.ca/");

            webView.loadData(content, "text/html; charset=utf-8", "UTF-8");

            layout.addView(webView);
            loaded = true;
        } else {
            Log.i("Loaded", "View already loaded");
        }
    }
}