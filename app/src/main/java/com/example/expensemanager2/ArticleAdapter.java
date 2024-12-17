package com.example.expensemanager2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private Context context;
    private List<ArticleModel> articleList;

    public ArticleAdapter(Context context, List<ArticleModel> articleList) {
        this.context = context;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.article_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArticleModel article = articleList.get(position);

        // Set title, date, insights, and link
        holder.title.setText(article.getTitle());
        holder.publishOn.setText(article.getPublishOn());
        holder.insights.setText(article.getStructuredInsights());
        holder.articleLink.setText("Read Full Article");

        // Load the image using Glide
        Glide.with(context).load(article.getImageUrl()).into(holder.imageView);

        String fullArticleLink = "https://seekingalpha.com" + article.getArticleLink();
        // Handle "Read More" link click
        holder.articleLink.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullArticleLink));
            context.startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, publishOn, insights, articleLink;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.articleTitle);
            publishOn = itemView.findViewById(R.id.articleDate);
            insights = itemView.findViewById(R.id.articleInsights);
            articleLink = itemView.findViewById(R.id.articleLink);
            imageView = itemView.findViewById(R.id.articleImage);
        }
    }
}
