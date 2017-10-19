package com.jmoore.bevfacey;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

class CustomListAdapter extends ArrayAdapter<String>{ //This class is the list of Information
    private final Activity context;
    private final String[]itemname;
    private final String[]imgid;
    private final String[]itemdesc;
    private boolean loaded = false;

    CustomListAdapter(Activity context,String[]itemname,String[]itemdesc,String[]imgid){ //Constructor
        super(context,R.layout.mylist,itemname);
        this.context=context;
        this.itemname=itemname;
        this.itemdesc=itemdesc;
        this.imgid=imgid;
    }

    @NonNull
    public View getView(final int position, View view, @NonNull ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist,null,true);
        parent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        String s=",,,";
        String fixDesc=itemdesc[position].replaceAll(s,"\n\n");
        fixDesc=fixDesc.replaceFirst("\n\n","");
        if(fixDesc.contains(itemname[position])){
            fixDesc=fixDesc.replaceAll(itemname[position],"");
        }

        ImageView imageView=rowView.findViewById(R.id.icon);
        TextView txtTitle=rowView.findViewById(R.id.itemTitle);
        TextView extratxt=rowView.findViewById(R.id.itemDesc);

        txtTitle.setTypeface(MainActivity.typeface);
        extratxt.setTypeface(MainActivity.typefaceBody);

        if(!itemname[position].isEmpty()){
            if(itemname[position].contains(":SCHOOLNOTICE:")){
                String replaceFirst=itemname[position];
                String replaceNotice=replaceFirst.replaceFirst(":SCHOOLNOTICE:","");
                txtTitle.setTextColor(ContextCompat.getColor(context,R.color.colorNotice));
                txtTitle.setText(replaceNotice.toUpperCase());
                rowView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorNoticeBkg));
            }else{txtTitle.setText(itemname[position]);}
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
            Picasso.with(context).load(imgid[position]).into(imageView, new com.squareup.picasso.Callback(){
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
            rowView.refreshDrawableState();
        }catch(Exception ignored){
            //We failed to load the image
        }
        return rowView;
    }
}