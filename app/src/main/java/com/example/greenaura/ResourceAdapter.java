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
    private OnItemClickListener listener;

    public ResourceAdapter(Context context, List<Resource> resourceList) {
        this.context = context;
        this.resourceList = resourceList;
    }

    public interface OnItemClickListener {
        void onItemClick(Resource resource);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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
        Glide.with(context).load(resource.getResourcePhoto()).into(holder.image);
        // Set the click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(resource);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resourceList.size();
    }

    static class ResourceViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.resource_image);
        }
    }
}
