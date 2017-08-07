package com.jmoore.bevfacey;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

class CustomListAdapterSubMenu extends ArrayAdapter<String>{ //This class is the list of Information
    private final Activity context;
    private final String[]subText;
    private final String[]subLink;

    CustomListAdapterSubMenu(Activity context,String[]subText,String[]subLink){
        super(context,R.layout.mylistsubmenu,subText);
        this.context=context;
        this.subText=subText;
        this.subLink=subLink;
    }

    @NonNull
    public View getView(int position,View view,@NonNull ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylistsubmenu,null,true);

        TextView tv = rowView.findViewById(R.id.subMenuText);
        tv.setTypeface(MainActivity.typefaceMenuItems);
        tv.setText(subText[position]);
        tv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                TextView tv2=(TextView)view;
                String value=tv2.getText().toString();
                int pos=MainActivity.globalSubTitles.indexOf(value);
                String subURL=MainActivity.globalSubText.get(pos);
                getSubPages(subURL);
            }
        });
        return rowView;
    }

    private void getSubPages(String url){
        new GetSubPages().execute(url);
    }

    private class GetSubPages extends AsyncTask<String,Integer,String> {
        Intent i;
        @Override
        protected String doInBackground(String[]params){
            String urlStr=MainActivity.globalURL+params[0];
            URL url;
            try{url=new URL(urlStr);}catch(MalformedURLException ex){return"bad";} //Convert the String URL into an actual URL
            try{MainActivity.docHome= Jsoup.parse(url,3000);}catch(IOException ex){return"bad";} //Try to download the URL (this only fails if the download is corrupted)
            return"good"; //Tell the post execution task that it worked
        }
        protected void onPostExecute(String result){
            i=new Intent(context,SubPageActivity.class);
            context.startActivity(i);
        }
    }
}