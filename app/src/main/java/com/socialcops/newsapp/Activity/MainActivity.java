package com.socialcops.newsapp.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.socialcops.newsapp.Adapter.NewsAdapter;
import com.socialcops.newsapp.DI.DaggerNewsComponent;
import com.socialcops.newsapp.DI.NewsComponent;
import com.socialcops.newsapp.DI.NewsModule;
import com.socialcops.newsapp.Helper.JobSchedulerHelper;
import com.socialcops.newsapp.Model.Articles;
import com.socialcops.newsapp.Presenter.MainActivityPresenter;
import com.socialcops.newsapp.R;
import com.socialcops.newsapp.View.MainView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainView {

    @BindView(R.id.progress_circular)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    MainActivityPresenter mainActivityPresenter;

    private String searchKey;

    private NewsAdapter eAdapter;
    boolean isLoading = false;
    private List<Articles> articles = new ArrayList<>();

    private int page = 1;
    boolean isSearched = false;

    private MenuItem sortItem;

    private String sources;

    private String countryCodeValue;
    private TelephonyManager telephonyManager;

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
        getSupportActionBar().setTitle("Social News");

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        getHeadlinesList();

        JobSchedulerHelper jobSchedulerHelper = new JobSchedulerHelper(this);
        jobSchedulerHelper.createJobDispatcher();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            page = 1;
            if (!isSearched) {
                swipeRefreshLayout.setRefreshing(true);
                mainActivityPresenter.getArticlesList(page, sources, countryCodeValue);
                articles = new ArrayList<>();
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onArticleListFetched(List<Articles> articlesList, int totalResults) {
        swipeRefreshLayout.setRefreshing(false);
        int scrollPosition;
        if (page > 1) {
            articles.remove(articles.size() - 1);
            scrollPosition = articles.size();
            eAdapter.notifyItemRemoved(scrollPosition);
            isLoading = false;
            articles.addAll(articlesList);
            eAdapter.update(articles);
            eAdapter.notifyDataSetChanged();
        } else {
            articles.addAll(articlesList);
            eAdapter = new NewsAdapter(articles, this);
            RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(eLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(eAdapter);
            eAdapter.notifyDataSetChanged();
            initScrollListener(totalResults);
        }

        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(Throwable t) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(String url) {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("URL", url);
        startActivity(intent);
    }

    @Override
    public void fetchedSourcesList(String sources) {
        if(sources.length()==0)
            return;
        this.sources = sources;
        toggleProgressVisibility(true);
        recyclerView.setVisibility(View.GONE);
        page = 1;
        articles = new ArrayList<>();
        countryCodeValue = "";
        if(!isSearched)
            mainActivityPresenter.getArticlesList(page, sources, countryCodeValue);
        else mainActivityPresenter.getSearchedArticlesList(searchKey, page, sources);
        setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void toggleProgressVisibility(boolean visible) {
        if(visible)
            progressBar.setVisibility(View.VISIBLE);
        else
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
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        //bottom of list!
                        int item = totalResults / page;
                        if (item > 20) {
                            loadMore(isSearched);
                            isLoading = true;
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        mainActivityPresenter.setSearchViewFeatures(searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                page = 1;
                isSearched = true;
                searchKey = query;
                articles = new ArrayList<>();
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                if(sortItem!=null)
                    sortItem.setChecked(false);
                mainActivityPresenter.getSearchedArticlesList(searchKey, page, sources);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItem searchItem = menu.findItem(R.id.search);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                mainActivityPresenter.hideKeyboard(MainActivity.this);
                page = 1;
                isSearched = false;
                articles = new ArrayList<>();
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                countryCodeValue = telephonyManager.getNetworkCountryIso();
                sources = "";
                mainActivityPresenter.getArticlesList(page, sources, countryCodeValue);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item1:
                sortItem = item;
                if(item.isChecked())
                item.setChecked(false);
                else {
                    item.setChecked(true);
                    if (articles != null) {
                        Collections.sort(articles);
                        eAdapter.update(articles);
                        eAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(0);
                    }
                }
                return true;
            case R.id.item2:
                toggleProgressVisibility(true);
                mainActivityPresenter.getSourcesList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadMore(boolean isSearched) {
        if(sortItem!=null)
            sortItem.setChecked(false);
        articles.add(null);
        eAdapter.notifyItemInserted(articles.size() - 1);
        page++;
        if (!isSearched)
            mainActivityPresenter.getArticlesList(page, sources, countryCodeValue);
        else mainActivityPresenter.getSearchedArticlesList(searchKey, page, sources);
    }

    public void setDisplayHomeAsUpEnabled(boolean value){
        if(!isSearched) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(value);
            getSupportActionBar().setDisplayShowHomeEnabled(value);
            String title = value == true ? "Sources" : "Social News";
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getHeadlinesList();
        return super.onSupportNavigateUp();
    }

    public void getHeadlinesList(){
        toggleProgressVisibility(true);
        recyclerView.setVisibility(View.GONE);
        page = 1;
        articles = new ArrayList<>();
        countryCodeValue = telephonyManager.getNetworkCountryIso();
        sources = "";
        mainActivityPresenter.getArticlesList(page, sources, countryCodeValue);
        setDisplayHomeAsUpEnabled(false);
    }
}
