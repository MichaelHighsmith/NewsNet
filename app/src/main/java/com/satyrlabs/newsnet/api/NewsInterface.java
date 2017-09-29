package com.satyrlabs.newsnet.api;

import com.satyrlabs.newsnet.pojo.NewsResponse;
import com.satyrlabs.newsnet.pojo.SourcesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsInterface {

    @GET("articles")
    Call<NewsResponse> getLatestNews(
            @Query("source") String source,
            @Query("sortBy") String sortBy,
            @Query("apiKey") String apiKey
    );

    //called when no sort available
    @GET("articles")
    Call<NewsResponse> getLatestNewsNoSort(
        @Query("source") String source,
        @Query("apiKey") String apiKey
    );


    @GET("sources")
    Call<SourcesResponse> getNewsSources(
            @Query("language") String language,
            @Query("apiKey") String apiKey
    );
}
