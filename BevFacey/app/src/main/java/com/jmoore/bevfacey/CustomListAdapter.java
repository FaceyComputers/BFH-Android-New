package com.jmoore.bevfacey;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

class CustomListAdapter extends RecyclerView.Adapter<ArticleHolder>{ //This class is the list of Information

    private Activity context;
    private String[]itemname;
    private String[]imgid;
    private String[]itemdesc;

    CustomListAdapter(Activity context,String[]itemname,String[]itemdesc,String[]imgid){
        this.context=context;
        this.itemname=itemname;
        this.itemdesc=itemdesc;
        this.imgid=imgid;
    }

    @Override
    public ArticleHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mylist,parent,false);
        return new ArticleHolder(this.context, view);
    }

    @Override
    public void onBindViewHolder(ArticleHolder holder, int position){
        holder.bindArticle(itemname[position],itemdesc[position],imgid[position]);
    }

    @Override
    public int getItemCount(){
        return this.itemdesc.length;
    }
}