package com.socialcops.newsapp.Activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

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

    private String searchKey;

    private NewsAdapter eAdapter;
    boolean isLoading = false;
    private List<Articles> articles = new ArrayList<>();

    private int page = 1;
    boolean isSearched = false;

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

        mainActivityPresenter.getArticlesList(page);

    }

    @Override
    public void onArticleListFetched(List<Articles> articlesList, int totalResults) {
        int scrollPosition;
        if (page > 1) {
            articles.remove(articles.size() - 1);
            scrollPosition = articles.size();
            eAdapter.notifyItemRemoved(scrollPosition);
            isLoading = false;
        }
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        articles.addAll(articlesList);
        if(page>1){
            eAdapter.update(articles);
            eAdapter.notifyDataSetChanged();
        }
        if (page == 1) {
            eAdapter = new NewsAdapter(articles);
            RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(eLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(eAdapter);
            progressBar.setVisibility(View.GONE);
            eAdapter.notifyDataSetChanged();
            initScrollListener(totalResults);
            Toast.makeText(getApplicationContext(), Integer.toString(totalResults), Toast.LENGTH_SHORT).show();
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
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    if ( (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
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

        searchView.setQueryHint("Search");
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.requestFocus();
        searchView.setFocusableInTouchMode(true);
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                page = 1;
                isSearched = true;
                searchKey = query;
                articles = new ArrayList<>();
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                mainActivityPresenter.getSearchedArticlesList(searchKey, page);
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
                hideKeyboard();
                page = 1;
                isSearched = false;
                articles = new ArrayList<>();
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                mainActivityPresenter.getArticlesList(page);
                return true;
            }
        });

        return true;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getApplicationContext());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void loadMore(boolean isSearched) {
        articles.add(null);
        eAdapter.notifyItemInserted(articles.size() - 1);
        page++;
        if(!isSearched)
        mainActivityPresenter.getArticlesList(page);
        else mainActivityPresenter.getSearchedArticlesList(searchKey, page);
    }
}
