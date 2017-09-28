package com.satyrlabs.newsnet.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.satyrlabs.newsnet.R;
import com.satyrlabs.newsnet.pojo.News;

import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<News> news;
    private int rowLayout;
    private Context context;

    public static class NewsViewHolder extends RecyclerView.ViewHolder{
        LinearLayout newsLayout;
        TextView articleTitle;
        TextView articleDescription;

        public NewsViewHolder(View v){
            super(v);
            newsLayout = (LinearLayout) v.findViewById(R.id.news_layout);
            articleTitle = (TextView) v.findViewById(R.id.title);
            articleDescription = (TextView) v.findViewById(R.id.description);
        }
    }

    public NewsAdapter(List<News> news, int rowLayout, Context context) {
        this.news = news;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public NewsAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout,parent,false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, final int position){
        holder.articleTitle.setText(news.get(position).getTitle());
        holder.articleDescription.setText(news.get(position).getDescription());
    }

    @Override
    public int getItemCount(){
        return news.size();
    }
}
