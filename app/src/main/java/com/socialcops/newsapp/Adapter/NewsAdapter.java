package com.socialcops.newsapp.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.socialcops.newsapp.Model.Articles;
import com.socialcops.newsapp.R;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Articles> articles;
    private Context context;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public NewsAdapter(List<Articles> articles) {
        this.articles = articles;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == VIEW_TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.articles_list, parent, false);
            return new CustomViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CustomViewHolder) {
            Articles article = articles.get(position);
            CustomViewHolder viewHolder = (CustomViewHolder) holder;
            viewHolder.articleNameView.setText(article.getTitle());
            viewHolder.articleSourceView.setText(article.getSource().getName());
            viewHolder.articleDateView.setText(article.getPublishedAt());

            Glide
                    .with(context)
                    .load(article.getUrlToImage())
                    .centerCrop()
                    .into(viewHolder.articleImageView);
        } else if (holder instanceof LoadingViewHolder) {
            //Display Progress
        }
    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 : articles.size();
    }

    @Override
    public int getItemViewType(int position) {
        return articles.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

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

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
