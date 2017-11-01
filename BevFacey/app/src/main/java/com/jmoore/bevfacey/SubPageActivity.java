package com.jmoore.bevfacey;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import org.jsoup.nodes.Document;
import java.util.regex.Pattern;

public class SubPageActivity extends AppCompatActivity {

    public Document doc = MainActivity.docSub;
    public Intent intent;
    public Activity context;
    public GridLayout grid;
    public LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_page);
        context = MainActivity.context;
        intent = getIntent();
        grid = findViewById(R.id.subLayout);
        layout = findViewById(R.id.subContent);
        createPage();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            ImageView bannerIV = findViewById(R.id.bannerImage2);
            int margin = bannerIV.getHeight();
            layout.setPadding(0, 0, 0, margin);
        }
    }

    private void createPage() {
        String content = doc.select("div.content-container").first().toString();
        WebView webView = new WebView(context);
        Display display = context.getWindowManager().getDefaultDisplay();
        float width = display.getWidth();
        float scale = webView.getScale();
        int size = (int)(width / scale);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);

        content = content.replaceAll(Pattern.quote(" alt=\"") + "(.*?)" + Pattern.quote("\""),"");
        content = content.replaceAll(Pattern.quote("img src=\""), "img style=\"margin: 0; padding: 0\" width=\"" + size + "px\" src=\"https://bevfacey.ca");
        Log.i("CONTDNT","content: " + content);

        webView.loadData(content, "text/html; charset=utf-8", "UTF-8");

        layout.addView(webView);
    }
}