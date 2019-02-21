package com.socialcops.newsapp.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.socialcops.newsapp.Adapter.NewsAdapter;
import com.socialcops.newsapp.DI.DaggerNewsComponent;
import com.socialcops.newsapp.DI.NewsComponent;
import com.socialcops.newsapp.DI.NewsModule;
import com.socialcops.newsapp.Model.Articles;
import com.socialcops.newsapp.Model.News;
import com.socialcops.newsapp.Presenter.MainActivityPresenter;
import com.socialcops.newsapp.R;
import com.socialcops.newsapp.View.MainView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements MainView {

    @BindView(R.id.progress_circular)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    MainActivityPresenter mainActivityPresenter;
    @Inject
    Call<News> call;

    private NewsAdapter eAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        NewsComponent newsComponent = DaggerNewsComponent.builder()
                .newsModule(new NewsModule(this, this))
                .build();
        newsComponent.addActivity(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("News App");

        mainActivityPresenter.getArticlesList();
    }

    @Override
    public void onArticleListFetched(List<Articles> articlesList) {
        eAdapter = new NewsAdapter(articlesList);
        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(eAdapter);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(Throwable t) {
        progressBar.setVisibility(View.GONE);
    }
}
