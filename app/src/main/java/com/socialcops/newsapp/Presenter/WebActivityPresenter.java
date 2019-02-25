package com.socialcops.newsapp.Presenter;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.socialcops.newsapp.View.WebViewInterface;

import javax.inject.Inject;

public class WebActivityPresenter {

    private String url;
    private WebViewInterface webViewInterface;

    @Inject
    public WebActivityPresenter(String url, WebViewInterface webViewInterface){
        this.url = url;
        this.webViewInterface = webViewInterface;
    }

    public void loadUrl(WebView webView){
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                webViewInterface.onLoadFinished();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webViewInterface.onErrorReceived(description);
            }
        });

        webView.loadUrl(url);
    }
}
