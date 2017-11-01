package com.jmoore.bevfacey;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

        ImageView imageView = findViewById(R.id.bannerImage2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            ImageView bannerIV = findViewById(R.id.bannerImage2);
            int margin = bannerIV.getHeight();
            layout.setPadding(0, 0, 0, margin);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
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
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);

        webView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith("/")) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse("http://bevfacey.ca" + url)));
                    return true;
                } else {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
            }
        });

        content = content.replaceAll(Pattern.quote("a href=\"/"), "a href=\"bevfacey.ca");
        content = content.replaceAll(Pattern.quote(" alt=\"") + "(.*?)" + Pattern.quote("\""),"");
        content = content.replaceAll(Pattern.quote("img src=\""), "img style=\"margin: 0; padding: 0\" width=\"" + size + "px\" src=\"http://bevfacey.ca");

        webView.loadData(content, "text/html; charset=utf-8", "UTF-8");

        layout.addView(webView);
    }
}