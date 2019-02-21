package com.socialcops.newsapp.Presenter;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.socialcops.newsapp.Model.Articles;
import com.socialcops.newsapp.Model.News;
import com.socialcops.newsapp.View.MainView;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityPresenter {

    public Context context;
    public MainView mainView;

    @Inject
    TelephonyManager telephonyManager;
    @Inject
    Call<News> call;

    @Inject
    public MainActivityPresenter(Context context, MainView mainView){
        this.context = context;
        this.mainView = mainView;
    }

    public void getArticlesList(){
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {

                if (response.isSuccessful()) {
                    List<Articles> articlesList = response.body().getArticles();
                    mainView.onArticleListFetched(articlesList);
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                mainView.onFailure(t);
            }
        });
    }
}
