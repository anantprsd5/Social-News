package com.socialcops.newsapp;

import com.socialcops.newsapp.Helper.Constants;
import com.socialcops.newsapp.Model.Articles;
import com.socialcops.newsapp.Model.News;
import com.socialcops.newsapp.Model.SourceModel;
import com.socialcops.newsapp.Retrofit.ApiService;
import com.socialcops.newsapp.View.MainView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MainActivityTest {

    @Mock
    MainView mainView;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getSourcesListTest(){
        ApiService apiService = Mockito.mock(ApiService.class);
        Call<SourceModel> mockedCall = Mockito.mock(Call.class);
        Mockito.when(apiService.getSourceJSON(Constants.API_KEY)).thenReturn(mockedCall);
        Mockito.doAnswer(invocation -> {
            Callback<SourceModel> callback = invocation.getArgument(0);
            callback.onResponse(mockedCall, Response.success(new SourceModel()));
            return null;
        }).when(mockedCall).enqueue(any(Callback.class));
        mainView.toggleProgressVisibility(false);

    }

    @Test
    public void getArticlesListTest(){
        ApiService apiService = Mockito.mock(ApiService.class);
        Call<News> mockedCall = Mockito.mock(Call.class);
        Mockito.when(apiService.getMyJSON("in", Constants.API_KEY, Integer.toString(1), "")).thenReturn(mockedCall);
        Mockito.doAnswer(invocation -> {
            Callback<News> callback = invocation.getArgument(0);
            callback.onResponse(mockedCall, Response.success(new News()));
            return null;
        }).when(mockedCall).enqueue(any(Callback.class));
        List<Articles> articles = Mockito.mock(List.class);
        mainView.onArticleListFetched(articles, 10);
    }

    @Test
    public void getSearchedArticlesList(){
        ApiService apiService = Mockito.mock(ApiService.class);
        Call<News> mockedCall = Mockito.mock(Call.class);
        Mockito.when(apiService.getSearchJSON("test", Constants.API_KEY, Integer.toString(1), "")).thenReturn(mockedCall);
        Mockito.doAnswer(invocation -> {
            Callback<News> callback = invocation.getArgument(0);
            callback.onResponse(mockedCall, Response.success(new News()));
            return null;
        }).when(mockedCall).enqueue(any(Callback.class));
        List<Articles> articles = Mockito.mock(List.class);
        mainView.onArticleListFetched(articles, 10);
    }
}
