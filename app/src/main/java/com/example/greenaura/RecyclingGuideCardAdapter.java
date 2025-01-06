package com.example.greenaura;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclingGuideCardAdapter extends RecyclerView.Adapter<RecyclingGuideCardAdapter.ViewHolder> {

    private Context context;
    private List<RecyclingGuide> recyclingGuideList;
    private OnItemClickListener onItemClickListener;

    public RecyclingGuideCardAdapter(Context context, List<RecyclingGuide> recyclingGuideList) {
        this.context = context;
        this.recyclingGuideList = recyclingGuideList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclingguidescardsforhomepage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecyclingGuide guide = recyclingGuideList.get(position);
        holder.imageView.setImageResource(guide.getImageResId());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(guide);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recyclingGuideList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclingGuide guide);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.resource_image);
        }
    }
}
