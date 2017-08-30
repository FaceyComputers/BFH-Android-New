package com.jmoore.bevfacey;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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

    CustomListAdapter(Activity context,String[]itemname,String[]itemdesc,String[]imgid){ //Constructor
        super(context,R.layout.mylist,itemname);
        this.context=context;
        this.itemname=itemname;
        this.itemdesc=itemdesc;
        this.imgid=imgid;
    }

    @NonNull
    public View getView(int position,View view,@NonNull ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist,null,true);

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
        try {
            Picasso.with(context).load(imgid[position]).into(imageView);
        }catch(IllegalArgumentException ignored){}
        String nl=fixDesc+"\n";
        if(!itemname[position].isEmpty()){
            nl=nl.replaceFirst(" ","");
        }
        extratxt.setText(nl);
        return rowView;
    }
}