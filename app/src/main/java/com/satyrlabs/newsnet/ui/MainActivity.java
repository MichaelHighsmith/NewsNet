package com.satyrlabs.newsnet.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.satyrlabs.newsnet.R;
import com.satyrlabs.newsnet.api.ApiClient;
import com.satyrlabs.newsnet.api.NewsInterface;
import com.satyrlabs.newsnet.pojo.News;
import com.satyrlabs.newsnet.pojo.NewsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //API key 0b86b2841499436cb769406b08f0ad62
    //powered by "NewsAPI.org"

    private static final String TAG = MainActivity.class.getSimpleName();
    private final static String API_KEY = "0b86b2841499436cb769406b08f0ad62";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(API_KEY.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please obtain your API KEY first from themoviedb.org", Toast.LENGTH_SHORT).show();
            return;
        }

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.news_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //create an instance of the interface that points to retrofit object instance
        NewsInterface apiService = ApiClient.getClient().create(NewsInterface.class);
        //Note: apiService is not null here


        //Call the getLatestNews method from interface and save the returned value as a call
        Call<NewsResponse> call = apiService.getLatestNews("techcrunch", API_KEY);
        //enqueue the call (asynchronous)
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                int statusCode = response.code();
                //Check that the response is not null
                if(response.body() != null){
                    //get the list of articles (each one matches the POJO 'News')
                    List<News> news = response.body().getArticles();
                    recyclerView.setAdapter(new NewsAdapter(news, R.layout.list_item_news, getApplicationContext()));
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
}
