package com.satyrlabs.newsnet.ui;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.satyrlabs.newsnet.R;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>{

    private LayoutInflater inflater;
    private Context context;
    ArrayList<String> sourceList;

    CustomCallback callback;

    public interface CustomCallback{
        void onCustomSourceClicked(String id);
    }

    public CustomAdapter(Context context, ArrayList sourceList, CustomCallback callback){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.sourceList = sourceList;
        this.callback = callback;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = inflater.inflate(R.layout.list_item_custom_sources, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        final String id = sourceList.get(position);
        holder.name_textview.setText(id);
        holder.customSourcesLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(callback!=null){
                    callback.onCustomSourceClicked(id);
                }
            }
        });

        holder.customSourcesLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(callback!=null){
                    callback.onCustomSourceClicked(id);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount(){
        return sourceList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout customSourcesLayout;
        TextView name_textview;

        public MyViewHolder(View itemView){
            super(itemView);
            customSourcesLayout = (LinearLayout) itemView.findViewById(R.id.custom_sources_layout);
            name_textview = (TextView) itemView.findViewById(R.id.custom_name);
        }
    }

}
