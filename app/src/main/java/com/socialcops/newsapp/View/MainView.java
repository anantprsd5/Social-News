package com.socialcops.newsapp.View;

import com.socialcops.newsapp.Model.Articles;

import java.util.List;

public interface MainView {
    void onArticleListFetched(List<Articles> articlesList, int totalResults);
    void onFailure(Throwable t);
    void onItemClick(String url);
}
