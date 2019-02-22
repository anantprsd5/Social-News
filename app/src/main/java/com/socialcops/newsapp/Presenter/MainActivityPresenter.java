package com.socialcops.newsapp.Presenter;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.socialcops.newsapp.Constants;
import com.socialcops.newsapp.Model.Articles;
import com.socialcops.newsapp.Model.News;
import com.socialcops.newsapp.Retrofit.ApiService;
import com.socialcops.newsapp.Retrofit.RetroClient;
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
    public MainActivityPresenter(Context context, MainView mainView) {
        this.context = context;
        this.mainView = mainView;
    }

    public void getArticlesList(int page) {
        ApiService apiService = RetroClient.getApiService();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = telephonyManager.getNetworkCountryIso();
        //Call to retrofit to get JSON response converted to POJO
        Call<News> call = apiService.getMyJSON(countryCodeValue.toLowerCase(), Constants.API_KEY,
                Integer.toString(page));
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {

                if (response.isSuccessful()) {
                    List<Articles> articlesList = response.body().getArticles();
                    mainView.onArticleListFetched(articlesList, response.body().getTotalResults());
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                mainView.onFailure(t);
            }
        });
    }
}
