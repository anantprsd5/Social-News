package com.socialcops.newsapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.socialcops.newsapp.Adapter.NewsAdapter;
import com.socialcops.newsapp.DI.DaggerNewsComponent;
import com.socialcops.newsapp.DI.NewsComponent;
import com.socialcops.newsapp.DI.NewsModule;
import com.socialcops.newsapp.Model.Articles;
import com.socialcops.newsapp.Presenter.MainActivityPresenter;
import com.socialcops.newsapp.R;
import com.socialcops.newsapp.View.MainView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainView {

    @BindView(R.id.progress_circular)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    MainActivityPresenter mainActivityPresenter;

    private NewsAdapter eAdapter;
    boolean isLoading = false;
    private List<Articles> articles = new ArrayList<>();

    private int page = 1;

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

        mainActivityPresenter.getArticlesList(page);

    }

    @Override
    public void onArticleListFetched(List<Articles> articlesList, int totalResults) {
        if (page > 1) {
            articles.remove(articles.size() - 1);
            int scrollPosition = articles.size();
            eAdapter.notifyItemRemoved(scrollPosition);
            isLoading = false;
        }
        articles.addAll(articlesList);
        eAdapter = new NewsAdapter(articles);
        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(eAdapter);
        progressBar.setVisibility(View.GONE);
        eAdapter.notifyDataSetChanged();
        if (page == 1) {
            initScrollListener(totalResults);
        }
    }

    @Override
    public void onFailure(Throwable t) {
        progressBar.setVisibility(View.GONE);
    }

    public void initScrollListener(int totalResults) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == articles.size() - 1) {
                        //bottom of list!
                        int item = totalResults / page;
                        if (item > 20) {
                            loadMore();
                            isLoading = true;
                        }
                    }
                }
            }
        });
    }

    private void loadMore() {
        articles.add(null);
        eAdapter.notifyItemInserted(articles.size() - 1);
        page++;
        mainActivityPresenter.getArticlesList(page);
    }
}
