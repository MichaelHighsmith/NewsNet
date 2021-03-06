package com.satyrlabs.newsnet.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.activeandroid.query.Select;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.satyrlabs.newsnet.R;
import com.satyrlabs.newsnet.api.ApiClient;
import com.satyrlabs.newsnet.api.NewsInterface;
import com.satyrlabs.newsnet.pojo.News;
import com.satyrlabs.newsnet.pojo.NewsResponse;
import com.satyrlabs.newsnet.pojo.Sources;
import com.satyrlabs.newsnet.pojo.SourcesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SourceAdapter.AdapterCallback, SourceAdapter.AdapterLongCallback, NewsAdapter.ArticleCallback, CustomAdapter.CustomCallback{

    //powered by "NewsAPI.org"

    private static final String TAG = MainActivity.class.getSimpleName();
    private final static String API_KEY = "0b86b2841499436cb769406b08f0ad62";

    RecyclerView recyclerView;
    RecyclerView sourceRecyclerView;
    NewsInterface apiService;
    SourceAdapter sourceAdapter;

    RecyclerView customSourceRecyclerView;
    CustomAdapter customSourceAdapter;
    ArrayList<String> customList;

    TextView emptyRecycler;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //instantiate the recyclerviews
        recyclerView = (RecyclerView) findViewById(R.id.news_recycler_view);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sourceRecyclerView = (RecyclerView) findViewById(R.id.source_recycler_view);
        sourceRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        customSourceRecyclerView = (RecyclerView) findViewById(R.id.custom_source_recycler_view);
        customSourceRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        emptyRecycler = (TextView) findViewById(R.id.empty_recycler_text);

        //create an instance of the interface that points to retrofit object instance
        apiService = ApiClient.getClient().create(NewsInterface.class);

        //call the ApiClient to get initial responses for all recyclerviews
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
                    sourceAdapter = new SourceAdapter(sources, R.layout.list_item_sources, getApplicationContext(), MainActivity.this, MainActivity.this);
                    sourceRecyclerView.setAdapter(sourceAdapter);
                }
            }

            @Override
            public void onFailure(Call<SourcesResponse> call, Throwable t) {
            }
        });

        customList = new ArrayList<>();

        List<CustomSource> customSources = getAll();
        for(int i = 0; i<customSources.size(); i++){
            CustomSource customSource = customSources.get(i);
            customList.add(customSource.name);
        }

        customSourceAdapter = new CustomAdapter(this, customList, MainActivity.this);
        if(customList.isEmpty()){
            emptyRecycler.setVisibility(View.VISIBLE);
            customSourceRecyclerView.setVisibility(View.GONE);
        } else{
            emptyRecycler.setVisibility(View.GONE);
            customSourceRecyclerView.setVisibility(View.VISIBLE);
        }
        customSourceRecyclerView.setAdapter(customSourceAdapter);



        //Check for what the most recent source was and then open it first
        SharedPreferences sharedPreferences = getSharedPreferences("myPref", 0);
        String currentSource = sharedPreferences.getString("source", "techcrunch");
        String currentSortBy = sharedPreferences.getString("sortBy", "top");

        callNewsFeed(currentSource, currentSortBy);
    }

    //Called when a new source is clicked on
    public void callNewsFeed(final String source, String sortBy){
        //Call the getLatestNews method from interface and save the returned value as a call
        Call<NewsResponse> call = apiService.getLatestNews(source, sortBy, API_KEY);
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
                    //If the sort type isn't available for this call, call again with the default sort(top);
                    callDefaultSort(source);
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
            }
        });
    }

    //This is called if the previous response code was not 200 due to a sortBy category not being available for that source.  the default sort is called instead
    public void callDefaultSort(String source){
        //Call the getLatestNews method from interface and save the returned value as a call
        Call<NewsResponse> call = apiService.getLatestNewsNoSort(source, API_KEY);
        //enqueue the call (asynchronous)
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                //Check that the response is not null
                if(response.body() != null){
                    //get the list of articles (each one matches the POJO 'News')
                    List<News> news = response.body().getArticles();
                    recyclerView.setAdapter(new NewsAdapter(news, R.layout.list_item_news, getApplicationContext(), MainActivity.this));
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
            }
        });
    }


    @Override
    public void onItemClicked(String id) {
        //update the sharedpref
        SharedPreferences sharedPreferences = getSharedPreferences("myPref", 0);
        String sortBy = sharedPreferences.getString("sortBy", "top");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("source", id);
        editor.apply();

        //load the new sources articles
        callNewsFeed(id, sortBy);
    }

    //Open the article on its homepage
    @Override
    public void onArticleClicked(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.sort_by:
                changeSortOrder(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeSortOrder(MenuItem item){
        SharedPreferences sharedPreferences = getSharedPreferences("myPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String currentSortBy = sharedPreferences.getString("sortBy", "popular");
        String currentSource = sharedPreferences.getString("source", "techcrunch");
        if(currentSortBy.equals("popular")){
            currentSortBy = "latest";
        } else if(currentSortBy.equals("latest")){
            currentSortBy = "popular";
        }
        //change the title and store the sharedPref for sortBy
        item.setTitle(currentSortBy);
        editor.putString("sortBy", currentSortBy);
        editor.apply();
        //new call
        onItemClicked(currentSource);
    }

    public List<CustomSource> getAll(){
        return new Select()
                .from(CustomSource.class)
                .orderBy("Name ASC")
                .execute();
    }

    @Override
    public void onCustomSourceClicked(String id) {
        //update the sharedpref
        SharedPreferences sharedPreferences = getSharedPreferences("myPref", 0);
        String sortBy = sharedPreferences.getString("sortBy", "top");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("source", id);
        editor.apply();

        //load the new sources articles
        callNewsFeed(id, sortBy);
    }

    @Override
    public void onItemLongClicked(String id) {
        //Save custom sources
        CustomSource customSource = new CustomSource();
        customSource.name = id;
        //Check that the source isn't already in the custom list, if not then add it and save to database
        if (customList.contains(id)){
            Toast.makeText(this, "Already added", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "Source Added To Favorites", Toast.LENGTH_SHORT).show();
            customSource.save();
            customList.add(id);
            emptyRecycler.setVisibility(View.GONE);
            customSourceRecyclerView.setVisibility(View.VISIBLE);
            customSourceAdapter.notifyDataSetChanged();
        }

    }
}
