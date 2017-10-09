package com.jmoore.bevfacey;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
    private GetSubPages gsp;

    CustomListAdapterSubMenu(Activity context,String[]subText){
        super(context,R.layout.mylistsubmenu,subText);
        this.context=context;
        this.subText=subText;
    }

    @NonNull
    public View getView(int position,View view,@NonNull ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylistsubmenu,null,true);

        TextView tv=rowView.findViewById(R.id.subMenuText);
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
        gsp = new GetSubPages();
        gsp.execute(url);
    }

    private void stopSubPages(){
        gsp.cancel(true);
        gsp.isCancelled = true;
    }

    @SuppressWarnings("deprecation")
    private class GetSubPages extends AsyncTask<String,Integer,String>{
        private String urlStr;
        private Intent i;
        private ProgressDialog progress=new ProgressDialog(context);
        private boolean isCancelled = false;
        protected void onPreExecute(){
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
        protected String doInBackground(String[]params){
            urlStr=MainActivity.globalURL+params[0];
            URL url;
            try{url=new URL(urlStr);}catch(MalformedURLException ex){return"bad";} //Convert the String URL into an actual URL
            try{MainActivity.docSub=Jsoup.parse(url,3000);}catch(IOException ex){return"bad";} //Try to download the URL (this only fails if the download is corrupted)
            i=new Intent(context,SubPageActivity.class);
            i.putExtra("url",urlStr);
            if(isCancelled){
                i = null;
                return "bad";
            }
            context.startActivity(i);
            return"good"; //Tell the post execution task that it worked
        }
        protected void onPostExecute(String result){
            if("good".equals(result)){
                progress.dismiss();
            }
        }
    }
}