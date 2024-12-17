package com.example.expensemanager2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private ArrayList<NewsModel> newsList;

    public NewsAdapter(Context context, ArrayList<NewsModel> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsModel news = newsList.get(position);

        // Set the title, date, and content to their respective views
        holder.titleTextView.setText(news.getTitle());
        holder.dateTextView.setText(news.getPublishDate());

        // Render HTML content in the descriptionTextView
        if (news.getContent() != null && !news.getContent().isEmpty()) {
            holder.descriptionTextView.setText(Html.fromHtml(news.getContent(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.descriptionTextView.setText("No description available");
        }

        if (news.getImageUrl() != null && !news.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(news.getImageUrl())
                    .placeholder(R.drawable.placeholder_image) // Show while loading
                    .error(R.drawable.image_not_available)      // Show on failure
                    .into(holder.newsImageView);
        } else {
            holder.newsImageView.setImageResource(R.drawable.placeholder_image); // Default fallback
        }


        // Set the click listener to open the full article in a browser
        holder.itemView.setOnClickListener(v -> {
            if (news.getCanonicalLink() != null && !news.getCanonicalLink().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getCanonicalLink()));
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, dateTextView, descriptionTextView;
        ImageView newsImageView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            newsImageView = itemView.findViewById(R.id.newsImageView);
        }
    }
}
