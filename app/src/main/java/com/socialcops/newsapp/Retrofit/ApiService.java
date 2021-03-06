package com.socialcops.newsapp.Retrofit;

import com.socialcops.newsapp.Model.News;
import com.socialcops.newsapp.Model.SourceModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    /*
    Retrofit get annotation with our URL
    And our method that will return us the List of EmployeeList
    */
    @GET("top-headlines")
    Call<News> getMyJSON(@Query("country") String country, @Query("apiKey") String apiKey,
                          @Query("page") String page, @Query("sources") String sources);

    @GET("everything")
    Call<News> getSearchJSON(@Query("q") String query, @Query("apiKey") String apiKey
    , @Query("page") String page, @Query("sources") String sources);

    @GET("sources")
    Call<SourceModel> getSourceJSON(@Query("apiKey") String apiKey);

}
