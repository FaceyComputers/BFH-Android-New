package com.jmoore.bevfacey;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] itemname;
    private final String[] imgid;
    private final String[] itemdesc;

    public CustomListAdapter(Activity context, String[] itemname, String[]itemdesc,String[] imgid) {
        super(context, R.layout.mylist, itemname);

        this.context=context;
        this.itemname=itemname;
        this.itemdesc=itemdesc;
        this.imgid=imgid;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.itemTitle);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.itemDesc);

        txtTitle.setText(itemname[position]);
        //imageView.setImageResource(imgid[position]);
        extratxt.setText("Description "+itemname[position]);
        System.out.println("WORKEDFROMCUSTOMLISTADAPTER");
        return rowView;

    }
}