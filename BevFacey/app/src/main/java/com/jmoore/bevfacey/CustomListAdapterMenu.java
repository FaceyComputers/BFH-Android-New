package com.jmoore.bevfacey;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

class CustomListAdapterMenu extends ArrayAdapter<String>{ //This class is the list of Information
    private final Activity context;
    private final int[]imgid;
    //      0                   1                       2                   3               4
    //R.drawable.about  R.drawable.eteachers  R.drawable.programs  R.drawable.parents  R.drawable.students
    //          5                   6                       7
    // R.drawable.athletics  R.drawable.guidance  R.drawable.sustainability


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
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView iv=(ImageView)view;
                int tag=Integer.parseInt(iv.getTag().toString());
                if(tag==MainActivity.imageids[0]){//About
                    Toast.makeText(context,"About",Toast.LENGTH_SHORT).show();
                }else if(tag==MainActivity.imageids[1]){//ETeachers
                    Toast.makeText(context,"ETeachers",Toast.LENGTH_SHORT).show();
                }else if(tag==MainActivity.imageids[2]){//Programs
                    Toast.makeText(context,"Programs",Toast.LENGTH_SHORT).show();
                }else if(tag==MainActivity.imageids[3]){//Parents
                    Toast.makeText(context,"Parents",Toast.LENGTH_SHORT).show();
                }else if(tag==MainActivity.imageids[4]){//Students
                    Toast.makeText(context,"Students",Toast.LENGTH_SHORT).show();
                }else if(tag==MainActivity.imageids[5]){//Athletics
                    Toast.makeText(context,"Athletics",Toast.LENGTH_SHORT).show();
                }else if(tag==MainActivity.imageids[6]){//Guidance
                    Toast.makeText(context,"Guidance",Toast.LENGTH_SHORT).show();
                }else if(tag==MainActivity.imageids[7]){//Sustainability
                    Toast.makeText(context,"Sustainability",Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            Picasso.with(context).load(imgid[position]).into(imageView);
        }catch(IllegalArgumentException ignored){}
        imageView.setTag(imgid[position]);
        return rowView;
    }
}