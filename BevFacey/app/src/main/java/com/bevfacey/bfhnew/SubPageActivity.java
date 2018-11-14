package com.bevfacey.bfhnew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class SubPageActivity extends AppCompatActivity {

    public Document doc = MainActivity.docSub;
    public Intent intent;
    public Activity context;
    public GridLayout grid;
    public LinearLayout layout;

    private GetSubPages gsp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_page);
        context = this;
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
        if (hasFocus) {
            ImageView bannerIV = findViewById(R.id.bannerImage2);
            int margin = bannerIV.getHeight();
            layout.setPadding(0, 0, 0, margin);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    private void createPage() {
        String content = doc.select("div.content-container").first().toString();
        String sidebarNavigation = doc.select("nav.sidebar-navigation").first().toString();
        sidebarNavigation = sidebarNavigation.replaceAll(Pattern.quote("href=\"/"), "href=\"http://bevfacey.ca/");

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.toLowerCase().contains("bevfacey.ca") && !url.toLowerCase().contains("download") && !url.toLowerCase().contains("uploads")) {
                    getSubPages(url);
                    return true;
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                    return true;
                }
            }
        };

        if (!sidebarNavigation.isEmpty() && intent.getStringExtra("url").toLowerCase().contains("eteacher")) {
            Log.i("Sidebar", "Sidebar found");
            WebView navView = new WebView(context);
            navView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            navView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            navView.loadData(sidebarNavigation, "text/html; charset=utf-8", "UTF-8");
            navView.setWebViewClient(webViewClient);
            layout.addView(navView);
        }

        WebView webView = new WebView(context);
        Display display = context.getWindowManager().getDefaultDisplay();
        float width = display.getWidth();
        float scale = webView.getScale();
        int size = (int) (width / scale);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(webViewClient);
        webView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        content = content.replaceAll(Pattern.quote("a href=\"/"), "a href=\"http://bevfacey.ca/");
        content = content.replaceAll(Pattern.quote(" alt=\"") + "(.*?)" + Pattern.quote("\""), "");
        content = content.replaceAll(Pattern.quote("img src=\""), "img style=\"margin: 0; padding: 0\" width=\"" + size + "px\" src=\"http://bevfacey.ca");

        webView.loadData(content, "text/html; charset=utf-8", "UTF-8");

        layout.addView(webView);
        //layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void getSubPages(String url) {
        gsp = new GetSubPages();
        gsp.execute(url);
    }

    private void stopSubPages() {
        gsp.cancel(true);
        gsp.isCancelled = true;
    }


    @SuppressWarnings("deprecation")
    private class GetSubPages extends AsyncTask<String, Integer, String> {
        private Intent i;
        private ProgressDialog progress = new ProgressDialog(context);
        private boolean isCancelled = false;

        protected void onPreExecute() {
            progress.setIndeterminate(true);
            progress.setTitle("Loading...");
            progress.setMessage("Please wait");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(true);
            progress.setCanceledOnTouchOutside(true);
            progress.setInverseBackgroundForced(true);
            progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    stopSubPages();
                }
            });
            progress.show();
        }

        @Override
        protected String doInBackground(String[] params) {
            URL url;
            try {
                url = new URL(params[0]); //Convert the String URL into an actual URL
            } catch (MalformedURLException ex) {
                return "bad";
            }
            try {
                MainActivity.docSub = Jsoup.parse(url, 15000); //Try to download the URL (this only fails if the download is corrupted)
            } catch (IOException ex) {
                return "bad";
            }
            i = new Intent(context, SubPageActivity.class);
            i.putExtra("url", params[0]);
            if (isCancelled) {
                i = null;
                return "bad";
            }
            context.startActivity(i);
            return "good"; //Tell the post execution task that it worked
        }

        protected void onPostExecute(String result) {
            if ("good".equals(result)) {
                progress.dismiss();
            } else {
                progress.dismiss();
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
                dlgAlert.setMessage("There was an error during loading. Try again later.");
                dlgAlert.setTitle("Oops! Sorry about that...");
                dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
        }
    }
}