package com.jmoore.bevfacey;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.regex.Pattern;

class CustomListAdapterSubPage_TitleText extends ArrayAdapter<String>{ //This class is the list of Information
    private final Activity context;
    private final String[]title;
    private final String[]text;
    private String[]h3Titles=null;

    CustomListAdapterSubPage_TitleText(Activity context,String[]title,String[]text,String[]h3Titles){
        super(context,R.layout.mylistsubmenu,title);
        this.context=context;
        this.title=title;
        this.text=text;
        this.h3Titles=h3Titles;
    }

    CustomListAdapterSubPage_TitleText(Activity context,String[]title,String[]text){
        super(context,R.layout.mylistsubmenu,title);
        this.context=context;
        this.title=title;
        this.text=text;
    }

    @NonNull
    public View getView(int position,View view,@NonNull ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylistsubpage,null,true);

        String fixDesc=text[position];
        fixDesc=fixDesc.replaceFirst("\n\n","");
        if(fixDesc.contains(title[position])){
            fixDesc=fixDesc.replaceFirst(title[position],"");
        }

        String findStr=".,.";
        int lastIndex=0;
        //int h3TitleCount=0;
        while(lastIndex!=-1){
            lastIndex=text[position].indexOf(findStr,lastIndex);
            if(lastIndex!=-1){
                //h3TitleCount++;
                lastIndex+=findStr.length();
            }
        }

        if(h3Titles!=null) {
            for (String h3title : h3Titles) {
                System.out.println("H3TITLE: " + h3title);
                fixDesc = fixDesc.replaceFirst(Pattern.quote(findStr), "<h3>" + h3title + "</h3>");
            }
        }

        TextView titleTV=new TextView(context);
        TextView textTV=new TextView(context);
        textTV.setLinksClickable(true);
        textTV.setAutoLinkMask(Linkify.ALL);
        textTV.setLinkTextColor(ContextCompat.getColor(context,R.color.colorLinks));
        titleTV.setTypeface(MainActivity.typefaceMenuItems);
        textTV.setTypeface(MainActivity.typefaceBody);
        //noinspection deprecation
        titleTV.setText(Html.fromHtml("<h2>"+title[position].trim()+"</h2>"));
        fixDesc=fixDesc.replaceAll(",,snl,,","\n");//CONTACT PAGE ONLY
        String nl=fixDesc.replaceAll(",,,","\n\n");
        nl=nl.replaceAll(",,p,,","- ");
        nl=nl.trim()+"\n";
        if(nl.contains("mailto:")){
            nl=nl.replace("mailto:","");
            nl=nl.replace("http://www.bevfacey.ca","");
            nl=nl.replace("%20","");
        }

        //noinspection deprecation
        textTV.setText(Html.fromHtml(nl.replace("\n","<br />")));
        //textTV.setText(nl);

        int dp=10;
        float d=context.getResources().getDisplayMetrics().density;
        int margin=(int)(dp*d);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(margin,0,margin,0);
        titleTV.setLayoutParams(lp);
        textTV.setLayoutParams(lp);

        LinearLayout ll=rowView.findViewById(R.id.subPageTitleTextLinearLayout);
        ll.addView(titleTV);
        ll.addView(textTV);
        return rowView;
    }
}