package com.socialcops.newsapp.Services;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.socialcops.newsapp.Constants;
import com.socialcops.newsapp.Model.Articles;
import com.socialcops.newsapp.Model.News;
import com.socialcops.newsapp.NotificationHandler;
import com.socialcops.newsapp.Retrofit.ApiService;
import com.socialcops.newsapp.Retrofit.RetroClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        // Do some work here
        getLatestNews();
        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }

    public void getLatestNews(){
        ApiService apiService = RetroClient.getApiService(this);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = telephonyManager.getNetworkCountryIso();
        //Call to retrofit to get JSON response converted to POJO
        Call<News> call = apiService.getMyJSON(countryCodeValue.toLowerCase(), Constants.API_KEY,
                Integer.toString(1), "");
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {

                if (response.isSuccessful()) {
                    List<Articles> articlesList = response.body().getArticles();
                    Articles articles = articlesList.get(0);
                    String title = articles.getSource().getName();
                    String text = articles.getTitle();
                    String url = articles.getUrl();
                    displayNotifications(title, text, url);
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Log.d("Notification", "onFailure: Cannot display notification");
            }
        });
    }

    private void displayNotifications(String title, String text, String url) {
        NotificationHandler notificationHandler = new NotificationHandler(this);
        notificationHandler.buildNotification(url, title, text);
        notificationHandler.createNotificationChannel();
        notificationHandler.displayNotification();
    }
}
