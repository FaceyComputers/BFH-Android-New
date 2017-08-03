package com.jmoore.bevfacey;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

class CustomListAdapterMenu extends ArrayAdapter<String>{ //This class is the list of Information
    private final Activity context;
    private final int[]imgid;

    CustomListAdapterMenu(Activity context,int[]imgid,String[]imgtext){
        super(context,R.layout.mylistmenu,imgtext);
        this.context=context;
        this.imgid=imgid;
    }

    @NonNull
    public View getView(int position,View view,@NonNull ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylistmenu,null,true);

        ImageView imageView = rowView.findViewById(R.id.iconMenus);

        System.out.println(imgid[position]);
        try {
            Picasso.with(context).load(imgid[position]).into(imageView);
        }catch(IllegalArgumentException ignored){}
        return rowView;
    }
}