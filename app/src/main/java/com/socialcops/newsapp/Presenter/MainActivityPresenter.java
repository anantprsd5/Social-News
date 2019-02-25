package com.socialcops.newsapp.Presenter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.socialcops.newsapp.Helper.Constants;
import com.socialcops.newsapp.Model.Articles;
import com.socialcops.newsapp.Model.News;
import com.socialcops.newsapp.Model.SourceModel;
import com.socialcops.newsapp.Model.Sources;
import com.socialcops.newsapp.Retrofit.ApiService;
import com.socialcops.newsapp.Retrofit.RetroClient;
import com.socialcops.newsapp.View.MainView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityPresenter {

    public Context context;
    public MainView mainView;

    private String sourcesString="";

    @Inject
    public MainActivityPresenter(Context context, MainView mainView) {
        this.context = context;
        this.mainView = mainView;
    }

    public void getArticlesList(int page, String sourcesString, String countryCodeValue) {
        ApiService apiService = RetroClient.getApiService(context);
        //Call to retrofit to get JSON response converted to POJO
        Call<News> call = apiService.getMyJSON(countryCodeValue.toLowerCase(), Constants.API_KEY,
                Integer.toString(page), sourcesString);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {

                if (response.isSuccessful()) {
                    List<Articles> articlesList = response.body().getArticles();
                    mainView.onArticleListFetched(articlesList, response.body().getTotalResults());
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                mainView.onFailure(t);
            }
        });
    }

    public void getSearchedArticlesList(String query, int page, String sources) {
        ApiService apiService = RetroClient.getApiService(context);
        //Call to retrofit to get JSON response converted to POJO
        Call<News> call = apiService.getSearchJSON(query, Constants.API_KEY, Integer.toString(page),
                sources);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {

                if (response.isSuccessful()) {
                    List<Articles> articlesList = response.body().getArticles();
                    mainView.onArticleListFetched(articlesList, response.body().getTotalResults());
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                mainView.onFailure(t);
            }
        });
    }

    public void setSearchViewFeatures(SearchView searchView) {
        searchView.setQueryHint("Search");
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.requestFocus();
        searchView.setFocusableInTouchMode(true);
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void getSourcesList() {
        ApiService apiService = RetroClient.getApiService(context);
        //Call to retrofit to get JSON response converted to POJO
        Call<SourceModel> call = apiService.getSourceJSON(Constants.API_KEY);
        call.enqueue(new Callback<SourceModel>() {
            @Override
            public void onResponse(Call<SourceModel> call, Response<SourceModel> response) {
                if (response.isSuccessful()) {
                    List<Sources> articlesList = response.body().getSources();
                    String[] sources = new String[articlesList.size()];
                    String[] sourcesId = new String[articlesList.size()];
                    for (int i = 0; i < articlesList.size(); i++) {
                        sources[i] = articlesList.get(i).getName();
                        sourcesId[i] = articlesList.get(i).getId();
                    }
                    withMultiChoiceItems(sources, sourcesId);
                    mainView.toggleProgressVisibility(false);
                }
            }

            @Override
            public void onFailure(Call<SourceModel> call, Throwable t) {
                mainView.onFailure(t);
            }
        });
    }

    public void withMultiChoiceItems(String[] items, String[] itemsId) {
        ArrayList<String> selectedList = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        sourcesString = "";
        builder.setTitle("Select all news sources of your choice");
        builder.setMultiChoiceItems(items, null,
                (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedList.add(itemsId[which]);
                    } else if (selectedList.contains(itemsId[which])) {
                        selectedList.remove(itemsId[which]);
                    }
                });

        builder.setPositiveButton("DONE", (dialogInterface, i) -> {

            for (int j = 0; j < selectedList.size(); j++) {
                sourcesString = sourcesString+ selectedList.get(j) + ",";
            }

            sourcesString = sourcesString.substring(0, sourcesString.length()-1);
            mainView.fetchedSourcesList(sourcesString);
        });

        builder.show();
    }

}
