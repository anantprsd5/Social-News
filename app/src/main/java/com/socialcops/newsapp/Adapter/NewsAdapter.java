package com.socialcops.newsapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.socialcops.newsapp.Model.Articles;
import com.socialcops.newsapp.R;
import com.socialcops.newsapp.View.MainView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Articles> articles;
    private Context context;

    private MainView mainView;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public NewsAdapter(List<Articles> articles, MainView mainView) {
        this.articles = articles;
        this.mainView = mainView;
    }

    public void update(List<Articles> articles){
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
            String publishedAt = article.getPublishedAt();
            String formatted = publishedAt.substring(0, publishedAt.indexOf('T'));
            String formattedDate = getFormattedDate(formatted);
            viewHolder.articleDateView.setText(formattedDate);

            Glide
                    .with(context)
                    .load(article.getUrlToImage())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.articleImageView);
        } else if (holder instanceof LoadingViewHolder) {
            //Display Progress
        }
    }

    private String getFormattedDate(String publishedAt) {
        SimpleDateFormat month_date = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
            date = sdf.parse(publishedAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String month_name = month_date.format(date);
        return month_name;
    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 : articles.size();
    }

    @Override
    public int getItemViewType(int position) {
        return articles.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView articleNameView;
        public TextView articleSourceView;
        public TextView articleDateView;
        public ImageView articleImageView;
        public CardView cardView;

        public CustomViewHolder(View view) {
            super(view);
            articleNameView = view.findViewById(R.id.news_title);
            articleDateView = view.findViewById(R.id.news_date);
            articleSourceView = view.findViewById(R.id.news_source);
            articleImageView = view.findViewById(R.id.news_image);
            cardView = view.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String url = articles.get(position).getUrl();
            mainView.onItemClick(url);
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
