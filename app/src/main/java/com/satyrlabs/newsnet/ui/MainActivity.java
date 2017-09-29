package com.satyrlabs.newsnet.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.satyrlabs.newsnet.R;
import com.satyrlabs.newsnet.api.ApiClient;
import com.satyrlabs.newsnet.api.NewsInterface;
import com.satyrlabs.newsnet.pojo.News;
import com.satyrlabs.newsnet.pojo.NewsResponse;
import com.satyrlabs.newsnet.pojo.Sources;
import com.satyrlabs.newsnet.pojo.SourcesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SourceAdapter.AdapterCallback, NewsAdapter.ArticleCallback{

    //powered by "NewsAPI.org"

    private static final String TAG = MainActivity.class.getSimpleName();
    private final static String API_KEY = "0b86b2841499436cb769406b08f0ad62";

    RecyclerView recyclerView;
    RecyclerView sourceRecyclerView;
    NewsInterface apiService;
    SourceAdapter sourceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiate the recyclerviews
        recyclerView = (RecyclerView) findViewById(R.id.news_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sourceRecyclerView = (RecyclerView) findViewById(R.id.source_recycler_view);
        sourceRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //create an instance of the interface that points to retrofit object instance
        apiService = ApiClient.getClient().create(NewsInterface.class);

        //call the ApiClient to get initial responses for both recyclerviews
        initialCall();

    }

    //Set the source recyclerview(and call the main newsfeed method)
    public void initialCall(){
        Call<SourcesResponse> sourceCall = apiService.getNewsSources("en", API_KEY);
        sourceCall.enqueue(new Callback<SourcesResponse>() {
            @Override
            public void onResponse(Call<SourcesResponse> call, Response<SourcesResponse> response) {
                int statusCode = response.code();
                if(response.body() != null){
                    List<Sources> sources = response.body().getSources();
                    sourceAdapter = new SourceAdapter(sources, R.layout.list_item_sources, getApplicationContext(), MainActivity.this);
                    sourceRecyclerView.setAdapter(sourceAdapter);
                } else {
                    Log.v(response.toString(), "is null");
                }
                Log.v(Integer.toString(statusCode), "is the status code");
            }

            @Override
            public void onFailure(Call<SourcesResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });

        //Check for what the most recent source was and then open it first
        SharedPreferences sharedPreferences = getSharedPreferences("myPref", 0);
        String currentSource = sharedPreferences.getString("source", "techcrunch");
        callNewsFeed(currentSource);
    }

    //Called when a new source is clicked on
    public void callNewsFeed(String source){
        //Call the getLatestNews method from interface and save the returned value as a call
        Call<NewsResponse> call = apiService.getLatestNews(source, API_KEY);
        //enqueue the call (asynchronous)
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                int statusCode = response.code();
                //Check that the response is not null
                if(response.body() != null){
                    //get the list of articles (each one matches the POJO 'News')
                    List<News> news = response.body().getArticles();
                    recyclerView.setAdapter(new NewsAdapter(news, R.layout.list_item_news, getApplicationContext(), MainActivity.this));
                } else {
                    Log.v(response.toString(), "is null");
                }
                Log.v(Integer.toString(statusCode), "is the status code");
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }


    @Override
    public void onItemClicked(String id) {
        //load the new sources articles
        callNewsFeed(id);
        //update the sharedpref
        SharedPreferences sharedPreferences = getSharedPreferences("myPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("source", id);
        editor.apply();
    }

    @Override
    public void onArticleClicked(String url) {
        //Launch an intent to the articles url on its homepage
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);


    }
}
