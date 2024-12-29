package com.example.greenaura;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.greenaura.R;

import java.util.List;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ResourceViewHolder> {

    private Context context;
    private List<Resource> resourceList;

    public ResourceAdapter(Context context, List<Resource> resourceList) {
        this.context = context;
        this.resourceList = resourceList;
    }

    @NonNull
    @Override
    public ResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tipsandresourcescardsforhomepage, parent, false);
        return new ResourceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResourceViewHolder holder, int position) {
        Resource resource = resourceList.get(position);
        holder.title.setText(resource.getTitle());
        holder.description.setText(resource.getDescription());
        Glide.with(context).load(resource.getPhotoUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return resourceList.size();
    }

    static class ResourceViewHolder extends RecyclerView.ViewHolder {

        TextView title, description;
        ImageView image;

        public ResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.resource_title);
            description = itemView.findViewById(R.id.resource_description);
            image = itemView.findViewById(R.id.resource_image);
        }
    }
}
