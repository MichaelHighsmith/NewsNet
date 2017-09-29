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
import com.satyrlabs.newsnet.pojo.Sources;

import java.util.List;

public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.SourceViewHolder> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private List<Sources> sources;
    private int rowLayout;
    private Context context;

    AdapterCallback callback;

    //interface that is implemented by MainActivity to reference recyclerview clicks
    public interface AdapterCallback{
        void onItemClicked(String id);
    }



    public static class SourceViewHolder extends RecyclerView.ViewHolder {
        LinearLayout sourcesLayout;
        TextView sourceName;

        public SourceViewHolder(View v){
            super(v);
            sourcesLayout = (LinearLayout) v.findViewById(R.id.sources_layout);
            sourceName = (TextView) v.findViewById(R.id.name);
        }
    }

    public SourceAdapter(List<Sources> sources, int rowLayout, Context context, AdapterCallback callback){
        this.sources = sources;
        this.rowLayout = rowLayout;
        this.context = context;
        this.callback = callback;
    }

    @Override
    public SourceViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new SourceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SourceViewHolder holder, int position){
        final String name = sources.get(position).getName();
        final String id = sources.get(position).getId();
        holder.sourceName.setText(name);
        //Set the onclickListener for each news source
        holder.sourcesLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(callback!=null){
                    callback.onItemClicked(id);
                }else{
                    Log.v("callback is null", "callback is null");
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return sources.size();
    }


}
