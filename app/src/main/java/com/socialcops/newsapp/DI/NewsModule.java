package com.socialcops.newsapp.DI;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.socialcops.newsapp.Constants;
import com.socialcops.newsapp.Model.News;
import com.socialcops.newsapp.Presenter.MainActivityPresenter;
import com.socialcops.newsapp.Retrofit.ApiService;
import com.socialcops.newsapp.Retrofit.RetroClient;
import com.socialcops.newsapp.View.MainView;

import dagger.Module;
import dagger.Provides;
import retrofit2.Call;

@Module
public class NewsModule {

    MainView mainView;
    Context context;

    public NewsModule(MainView mainView, Context context) {
        this.mainView = mainView;
        this.context = context;
    }

    @Provides
    MainActivityPresenter getMainActivityPresenter(Call<News> call) {
        return new MainActivityPresenter(context, mainView, call);
    }

    @Provides
    Call<News> getNewsCall() {
        ApiService apiService = RetroClient.getApiService();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = telephonyManager.getNetworkCountryIso();
        //Call to retrofit to get JSON response converted to POJO
        Call<News> call = apiService.getMyJSON(countryCodeValue.toLowerCase(), Constants.API_KEY);
        return call;
    }
}
