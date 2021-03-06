package com.bevfacey.bfhnew;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

class CustomListAdapter extends RecyclerView.Adapter<ArticleHolder> { //This class is the list of Information
    private Activity context;
    private String[] content;

    CustomListAdapter(Activity context, String[] content) {
        setHasStableIds(true);
        this.context = context;
        this.content = content;
    }

    @Override
    public ArticleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item_layout, parent, false);
        view.setClickable(true);
        view.setFocusable(true);
        return new ArticleHolder(this.context, view);
    }

    @Override
    public void onBindViewHolder(ArticleHolder holder, int position){
        holder.bindArticle(content[position]);
    }

    @Override
    public int getItemCount(){
        return this.content.length;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}