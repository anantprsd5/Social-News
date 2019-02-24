package com.socialcops.newsapp.Activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.socialcops.newsapp.DI.DaggerWebComponent;
import com.socialcops.newsapp.DI.WebComponent;
import com.socialcops.newsapp.DI.WebModule;
import com.socialcops.newsapp.Presenter.WebActivityPresenter;
import com.socialcops.newsapp.R;
import com.socialcops.newsapp.View.WebViewInterface;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WebActivity extends AppCompatActivity implements WebViewInterface {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Inject
    WebActivityPresenter webActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Social News");

        String url = getIntent().getExtras().getString("URL");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        progressBar.setVisibility(View.VISIBLE);

        WebComponent webComponent = DaggerWebComponent.builder()
                .webModule(new WebModule(url, this))
                .build();

        webComponent.addActivity(this);

        webActivityPresenter.loadUrl(webView);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onLoadFinished() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onErrorReceived(String description) {
        Toast.makeText(this, description, Toast.LENGTH_SHORT).show();
    }
}
