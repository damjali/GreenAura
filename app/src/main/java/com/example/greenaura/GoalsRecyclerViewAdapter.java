package com.example.greenaura;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GoalsRecyclerViewAdapter extends RecyclerView.Adapter<GoalsRecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<Goals> goalsArrayList;

    public GoalsRecyclerViewAdapter(Context context, ArrayList<Goals> goalsArrayList){
        this.context = context;
        this.goalsArrayList = goalsArrayList;
    }
    public GoalsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.goal_item,parent,false);
        return new GoalsRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalsRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.goalTitle.setText(goalsArrayList.get(position).getGoalTitle());
//        holder.goalImage.setImageResource(goalsArrayList.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return goalsArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView goalImage;
        TextView goalTitle;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            goalImage = itemView.findViewById(R.id.goalsImageView);
            goalTitle = itemView.findViewById(R.id.goalsTitleTextView);
        }
    }
}
