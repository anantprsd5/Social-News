package com.socialcops.newsapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.socialcops.newsapp.Model.Articles;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.CustomViewHolder> {
    private List<Articles> articles;

    public NewsAdapter(List<Articles> articles) {
        this.articles = articles;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.employee_list, parent, false);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Articles article = articles.get(position);
        holder.articleName.setText(article.getTitle());
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView articleName;

        public CustomViewHolder(View view) {
            super(view);
            articleName = (TextView) view.findViewById(R.id.articleName);
        }
    }
}
