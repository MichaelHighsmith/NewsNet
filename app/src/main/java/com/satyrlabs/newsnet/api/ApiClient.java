package com.satyrlabs.newsnet.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mhigh on 9/27/2017.
 */

public class ApiClient {

    public static final String BASE_URL = "http://newsapi.org/v1/";
    private static Retrofit retrofit = null;

    public static OkHttpClient GetClient(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        return httpClient.addInterceptor(logging).build();
    }

    public static Retrofit getClient(){
        if(retrofit == null){
            //create a retrofit instance using the builder class
            retrofit = new Retrofit.Builder()
                    .client(GetClient())
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}