package com.socialcops.newsapp.Adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.socialcops.newsapp.Model.Articles;
import com.socialcops.newsapp.R;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.CustomViewHolder> {
    private List<Articles> articles;
    private Context context;

    public NewsAdapter(List<Articles> articles) {
        this.articles = articles;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.articles_list, parent, false);

        context = parent.getContext();
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Articles article = articles.get(position);
        holder.articleNameView.setText(article.getTitle());
        holder.articleSourceView.setText(article.getSource().getName());
        holder.articleDateView.setText(article.getPublishedAt());

        Glide
                .with(context)
                .load(article.getUrlToImage())
                .centerCrop()
                .into(holder.articleImageView);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView articleNameView;
        public TextView articleSourceView;
        public TextView articleDateView;
        public ImageView articleImageView;

        public CustomViewHolder(View view) {
            super(view);
            articleNameView = view.findViewById(R.id.news_title);
            articleDateView = view.findViewById(R.id.news_date);
            articleSourceView = view.findViewById(R.id.news_source);
            articleImageView = view.findViewById(R.id.news_image);
        }
    }
}
