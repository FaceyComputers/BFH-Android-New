package com.jmoore.bevfacey;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

            }
        });
        return rowView;
    }
}