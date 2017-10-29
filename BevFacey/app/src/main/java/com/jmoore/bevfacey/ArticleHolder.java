package com.jmoore.bevfacey;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

class ArticleHolder extends RecyclerView.ViewHolder {
    private final Activity context;
    private boolean loaded = false;

    /*private final TextView txtTitle;
    private final TextView extratxt;
    private final ImageView imageView;*/
    private final LinearLayout layout;

    private String[] noteiceTitlePattern = {"<h3 class=\"notice-subtitle\">", "</h3>"}; //Pattern to use for finding the Title
    private String[] articleTitlePattern = {"<h2 class=\"article-title\">", "</h2>"}; //Pattern for finding Titles of text-based articles
    private String[] imageUrlPattern = {"<img alt=\"\" src=\"", "\">"}; //Pattern for finding image URLs

    ArticleHolder(Activity context, View itemView) {
        super(itemView);
        this.context = context;
        /*txtTitle = itemView.findViewById(R.id.itemTitle);
        extratxt = itemView.findViewById(R.id.itemDesc);
        imageView = itemView.findViewById(R.id.icon);*/
        layout = itemView.findViewById(R.id.articleLayout);
    }

    void bindArticle(String itemname, String itemdesc, String imgid, String content) {
        final ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setPadding(0,0,0,0);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setAdjustViewBounds(true);

        String newLineDelimiter = ",,,";

        if(content.contains("<image alt")) {
            try {
                Picasso.with(context).load(imgid).into(imageView, new com.squareup.picasso.Callback(){
                    @Override
                    public void onSuccess() {
                        if(!loaded) {
                            loaded = true;
                            layout.addView(imageView);
                        }
                    }
                    @Override
                    public void onError() {
                        loaded = true;
                    }
                });
            } catch(Exception ignored) {
                Log.w("ImageFailedToLoad", "An image failed to load properly");
            }
        }

        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setAutoLinkMask(Linkify.ALL);
        textView.setLinksClickable(true);
        textView.setLinkTextColor(ContextCompat.getColor(context, R.color.colorLinks));
        textView.setText(Html.fromHtml(content));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setLinksClickable(true);
        textView.setAutoLinkMask(Linkify.ALL);

        layout.addView(textView);

        /*String s = ",,,";
        String fixDesc = itemdesc.replaceAll(s, "\n\n");
        fixDesc = fixDesc.replaceFirst("\n\n", "");
        if(fixDesc.contains(itemname)) {
            fixDesc = fixDesc.replaceAll(itemname, "");
        }

        txtTitle.setTypeface(MainActivity.typeface);
        extratxt.setTypeface(MainActivity.typefaceBody);

        if(!itemname.isEmpty()) {
            if(itemname.contains(":SCHOOLNOTICE:")) {
                String replaceNotice = itemname.replaceFirst(":SCHOOLNOTICE:", "");
                txtTitle.setTextColor(ContextCompat.getColor(context, R.color.colorNotice));
                txtTitle.setText(replaceNotice.toUpperCase());
                txtTitle.setBackgroundColor(ContextCompat.getColor(context, R.color.colorNoticeBkg));
                extratxt.setBackgroundColor(ContextCompat.getColor(context, R.color.colorNoticeBkg));
            } else {
                txtTitle.setText(itemname);
            }
        } else {
            txtTitle.setTextSize(0);
            txtTitle.setText("");
        }

        fixDesc = fixDesc.trim();
        String[] fixDescSplit = fixDesc.split(" ");
        for(String line : fixDescSplit) {
            if(line.contains("mailto:")) {
                String[] lineSplit = line.split(":");
                fixDesc = fixDesc.replace(line, lineSplit[lineSplit.length - 1]);
            }
        }
        String nl = fixDesc + "\n";
        nl = nl.replace("\n", "<br />");
        /*nl=nl.replace(",,b,,","<b>");
        nl=nl.replace(",,bb,,","<b />");
        nl=nl.replace(",,i,,","<i>");
        nl=nl.replace(",,ii,,","<i />");
        nl=nl.replace(",,u,,","<u>");
        nl=nl.replace(",,uu,,","<u />");
        Log.i("LINKTEXT",nl);
        nl = nl.replace(",a,","<a href=\"");
        nl = nl.replace(",aa,","\">");
        nl = nl.replace(",aaa,","</a>");/

        //noinspection deprecation
        extratxt.setText(Html.fromHtml(nl));
        extratxt.setMovementMethod(LinkMovementMethod.getInstance());
        extratxt.setLinksClickable(true);
        extratxt.setAutoLinkMask(Linkify.ALL);
        //extratxt.setText(nl);*/
    }
}