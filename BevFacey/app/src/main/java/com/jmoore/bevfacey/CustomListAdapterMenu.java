package com.jmoore.bevfacey;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class CustomListAdapterMenu extends ArrayAdapter<String>{ //This class is the list of Information
    private final Activity context;
    private final String[]menuText;

    CustomListAdapterMenu(Activity context,String[]menuText,String[]menuText2){
        super(context,R.layout.mylistmenu,menuText2);
        this.context=context;
        this.menuText=menuText;
    }

    @NonNull
    public View getView(int position,View view,@NonNull ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylistmenu,null,true);

        TextView tv = rowView.findViewById(R.id.menuText);
        tv.setTypeface(MainActivity.typefaceMenuItems);
        tv.setText(menuText[position]);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv2=(TextView)view;

            }
        });
        return rowView;
    }
}