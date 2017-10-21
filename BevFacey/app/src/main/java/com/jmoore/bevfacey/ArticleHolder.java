package com.jmoore.bevfacey;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ArticleHolder extends RecyclerView.ViewHolder {
    private final Activity context;
    private boolean loaded = false;

    private final TextView txtTitle;
    private final TextView extratxt;
    private final ImageView imageView;

    public ArticleHolder(Activity CONTEXT, View itemView) {
        super(itemView);
        context = CONTEXT;
        txtTitle = itemView.findViewById(R.id.itemTitle);
        extratxt = itemView.findViewById(R.id.itemDesc);
        imageView = itemView.findViewById(R.id.icon);
    }

    public void bindArticle(String itemname,String itemdesc,String imgid){
        String s=",,,";
        String fixDesc=itemdesc.replaceAll(s,"\n\n");
        fixDesc=fixDesc.replaceFirst("\n\n","");
        if(fixDesc.contains(itemname)){
            fixDesc=fixDesc.replaceAll(itemname,"");
        }

        txtTitle.setTypeface(MainActivity.typeface);
        extratxt.setTypeface(MainActivity.typefaceBody);

        if(!itemname.isEmpty()){
            if(itemname.contains(":SCHOOLNOTICE:")){
                String replaceNotice=itemname.replaceFirst(":SCHOOLNOTICE:","");
                txtTitle.setTextColor(ContextCompat.getColor(context,R.color.colorNotice));
                txtTitle.setText(replaceNotice.toUpperCase());
                txtTitle.setBackgroundColor(ContextCompat.getColor(context,R.color.colorNoticeBkg));
                extratxt.setBackgroundColor(ContextCompat.getColor(context,R.color.colorNoticeBkg));
            }else{txtTitle.setText(itemname);}
        }else{
            txtTitle.setTextSize(0);
            txtTitle.setText("");
        }

        fixDesc=fixDesc.trim();
        String[]fixDescSplit=fixDesc.split(" ");
        for(String line : fixDescSplit){
            if(line.contains("mailto:")){
                String[]lineSplit=line.split(":");
                fixDesc=fixDesc.replace(line,lineSplit[lineSplit.length-1]);
            }
        }
        String nl=fixDesc+"\n";
        nl=nl.replace("\n","<br />");
        /*nl=nl.replace(",,b,,","<b>");
        nl=nl.replace(",,bb,,","<b />");
        nl=nl.replace(",,i,,","<i>");
        nl=nl.replace(",,ii,,","<i />");
        nl=nl.replace(",,u,,","<u>");
        nl=nl.replace(",,uu,,","<u />");*/
        //extratxt.setMovementMethod(LinkMovementMethod.getInstance());
        //noinspection deprecation
        extratxt.setText(Html.fromHtml(nl));
        System.out.println(imageView.getHeight());
        //extratxt.setText(nl);
        try {
            Picasso.with(context).load(imgid).into(imageView, new com.squareup.picasso.Callback(){
                @Override
                public void onSuccess(){
                    if(!loaded){
                        //MainActivity.updateItem(position);
                        loaded = true;
                    }
                }
                @Override
                public void onError(){
                    System.err.println("error");
                    loaded = true;
                }
            });
        }catch(Exception ignored){
            //We failed to load the image
        }
    }
}