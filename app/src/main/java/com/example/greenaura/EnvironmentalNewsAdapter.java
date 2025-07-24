package com.example.greenaura;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EnvironmentalNewsAdapter extends RecyclerView.Adapter<EnvironmentalNewsAdapter.ViewHolder> {

    private Context context;
    private List<EnvironmentalNews> newsList;

    public EnvironmentalNewsAdapter(Context context, List<EnvironmentalNews> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the card layout for each item
        View view = LayoutInflater.from(context).inflate(R.layout.environmentalnewscardsforhomepage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the current news item
        EnvironmentalNews currentNews = newsList.get(position);

        // Load the image using Glide
        Glide.with(context)
                .load(currentNews.getImageUrl())
                .into(holder.newsImage);

        // Set click listener for the card to navigate to respective fragment
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, currentNews.getNewsActivityClass());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImage;

        public ViewHolder(View itemView) {
            super(itemView);
            newsImage = itemView.findViewById(R.id.newsImage);
        }
    }
}
