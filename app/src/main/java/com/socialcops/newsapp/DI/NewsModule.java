package com.socialcops.newsapp.DI;

import android.content.Context;

import com.socialcops.newsapp.Presenter.MainActivityPresenter;
import com.socialcops.newsapp.View.MainView;

import dagger.Module;
import dagger.Provides;

@Module
public class NewsModule {

    MainView mainView;
    Context context;

    public NewsModule(MainView mainView, Context context) {
        this.mainView = mainView;
        this.context = context;
    }

    @Provides
    MainActivityPresenter getMainActivityPresenter() {
        return new MainActivityPresenter(context, mainView);
    }
}
