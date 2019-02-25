package com.socialcops.newsapp.DI;

import com.socialcops.newsapp.Presenter.WebActivityPresenter;
import com.socialcops.newsapp.View.WebViewInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class WebModule {

    private String url;
    private WebViewInterface webViewInterface;

    public WebModule(String url, WebViewInterface webViewInterface){
        this.url = url;
        this.webViewInterface = webViewInterface;
    }

    @Provides
    WebActivityPresenter getWebActivityPresenter(){
        return new WebActivityPresenter(url, webViewInterface);
    }
}
