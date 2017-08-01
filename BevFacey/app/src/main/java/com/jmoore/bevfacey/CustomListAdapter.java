package com.jmoore.bevfacey;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class CustomListAdapter extends ArrayAdapter<String>{
    private final Activity context;
    private final String[]itemname;
    private final String[]imgid;
    private final String[]itemdesc;

    CustomListAdapter(Activity context,String[]itemname,String[]itemdesc,String[]imgid){
        super(context,R.layout.mylist,itemname);
        this.context=context;
        this.itemname=itemname;
        this.itemdesc=itemdesc;
        this.imgid=imgid;
    }

    public View getView(int position,View view,ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist,null,true);

        String s = ",,,";
        String fixDesc=itemdesc[position].replaceAll(s,"\n\n");
        fixDesc=fixDesc.replaceFirst("\n\n","");
        if(fixDesc.contains(itemname[position])){
            fixDesc=fixDesc.replaceAll(itemname[position],"");
        }

        ImageView imageView = rowView.findViewById(R.id.icon);
        TextView txtTitle=rowView.findViewById(R.id.itemTitle);
        TextView extratxt=rowView.findViewById(R.id.itemDesc);

        if(!itemname[position].isEmpty()){
            txtTitle.setText(itemname[position]);
        }else{
            txtTitle.setTextSize(0);
            txtTitle.setText("");
        }
        new ImageLoadTask(imgid[position], imageView).execute();
        extratxt.setText(fixDesc);
        return rowView;
    }

    private class ImageLoadTask extends AsyncTask<Void,Void,Bitmap>{

        private String url;
        private ImageView imageView;

        ImageLoadTask(String url, ImageView imageView){
            this.url=url;
            this.imageView=imageView;
        }
        @Override
        protected Bitmap doInBackground(Void...params){
            try{
                URL urlConnection=new URL(url);
                HttpURLConnection connection=(HttpURLConnection)urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input=connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            }catch(Exception e){}
            return null;
        }
        @Override
        protected void onPostExecute(Bitmap result){
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }
    }
}