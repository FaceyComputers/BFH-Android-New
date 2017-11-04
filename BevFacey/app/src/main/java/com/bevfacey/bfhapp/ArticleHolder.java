package com.bevfacey.bfhapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

class ArticleHolder extends RecyclerView.ViewHolder {
    private final Activity context;
    private boolean loaded = false;
    private final LinearLayout layout;

    private GetSubPages gsp;

    ArticleHolder(Activity context, View itemView) {
        super(itemView);
        this.context = context;
        layout = itemView.findViewById(R.id.articleLayout);
    }

    @SuppressWarnings("deprecation")
    void bindArticle(String content) {
        if(!loaded) {

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

            WebView webView = new WebView(context);
            Display display = context.getWindowManager().getDefaultDisplay();
            float width = display.getWidth();
            float scale = webView.getScale();
            int size = (int)(width / scale);

            if(content.contains(":SCHOOL_NOTICE:")) {
                content = "<body bgcolor=\"#fff2c4\">" + content + "</body>";
                content = content.replace(":SCHOOL_NOTICE:", "");
            }

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
            webView.setWebViewClient(webViewClient);

            content = content.replaceAll(Pattern.quote("a href=\"/"), "a href=\"http://bevfacey.ca/");
            content = content.replaceAll(Pattern.quote("img alt=\"") + "(.*?)" +
                    Pattern.quote("\" src=\"/"), "img alt=\"\" style=\"margin: 0; " +
                    "padding: 0\" width=" + size + "px src=\"http://bevfacey.ca/");

            webView.loadData(content, "text/html; charset=utf-8", "UTF-8");

            layout.addView(webView);
            loaded = true;
        } else {
            Log.i("Loaded", "View already loaded");
        }
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