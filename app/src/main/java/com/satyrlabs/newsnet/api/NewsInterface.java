package com.satyrlabs.newsnet.api;

import com.satyrlabs.newsnet.pojo.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mhigh on 9/27/2017.
 */

public interface NewsInterface {

    @GET("articles")
    Call<NewsResponse> getLatestNews(
            @Query("source") String source,
            @Query("apiKey") String apiKey
    );
}
