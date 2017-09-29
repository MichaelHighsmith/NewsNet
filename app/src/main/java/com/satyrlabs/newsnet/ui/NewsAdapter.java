package com.satyrlabs.newsnet.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.satyrlabs.newsnet.R;
import com.satyrlabs.newsnet.pojo.News;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<News> news;
    private int rowLayout;
    private Context context;

    ArticleCallback callback;

    public interface ArticleCallback{
        void onArticleClicked(String url);
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder{
        LinearLayout newsLayout;
        LinearLayout titleLayout;
        LinearLayout imageLayout;
        TextView articleTitle;
        TextView articleDescription;
        ImageView articleImage;

        public NewsViewHolder(View v){
            super(v);
            newsLayout = (LinearLayout) v.findViewById(R.id.news_layout);
            titleLayout = (LinearLayout) v.findViewById(R.id.title_layout);
            imageLayout = (LinearLayout) v.findViewById(R.id.image_layout);
            articleTitle = (TextView) v.findViewById(R.id.title);
            articleDescription = (TextView) v.findViewById(R.id.description);
            articleImage = (ImageView) v.findViewById(R.id.image);
        }
    }

    public NewsAdapter(List<News> news, int rowLayout, Context context, ArticleCallback callback) {
        this.news = news;
        this.rowLayout = rowLayout;
        this.context = context;
        this.callback = callback;
    }

    @Override
    public NewsAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout,parent,false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, final int position){
        //set textviews with data from response
        holder.articleTitle.setText(news.get(position).getTitle());
        String description = news.get(position).getDescription();
        //limit description length to 100 char while avoiding ArrayOutOfBoundsException)
        if(description!= null){
            description = description.substring(0, Math.min(description.length(), 100)) + "...";
        }
        holder.articleDescription.setText(description);
        //Use Picasso and the image url to load image on the news feed
        Picasso.with(context).load(news.get(position).getUrlToImage()).into(holder.articleImage);

        //implement the articlecallback interface for clicks (sends to url in MainActivity)
        final String url = news.get(position).getUrl();
        holder.newsLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(callback!=null){
                    callback.onArticleClicked(url);
                }else{
                    Log.v("error in calling url", url);
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return news.size();
    }
}
