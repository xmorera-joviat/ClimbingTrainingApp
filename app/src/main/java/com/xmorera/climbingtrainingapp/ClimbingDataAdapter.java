package com.xmorera.climbingtrainingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClimbingDataAdapter extends RecyclerView.Adapter<ClimbingDataAdapter.ViewHolder> {

    private List<ClimbingData> climbingDataList;

    public ClimbingDataAdapter(List<ClimbingData> climbingDataList){
        this.climbingDataList = climbingDataList;
    }

    @NonNull
    @Override
    public ClimbingDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_climbing_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClimbingDataAdapter.ViewHolder holder, int position) {
        ClimbingData data = climbingDataList.get(position);
        holder.dateTextView.setText(data.getDate());
        holder.viaTextView.setText(data.getVia());
        holder.zonaTextView.setText(data.getZona());
        holder.intentTextView.setText(data.getIntent() == 1 ? "Intent" : "Neta");
        holder.puntuacioTextView.setText(String.valueOf(data.getPuntuacio()));
    }

    @Override
    public int getItemCount() {
        return climbingDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView viaTextView;
        TextView zonaTextView;
        TextView intentTextView;
        TextView puntuacioTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            viaTextView = itemView.findViewById(R.id.viaTextView);
            zonaTextView = itemView.findViewById(R.id.zonaTextView);
            intentTextView = itemView.findViewById(R.id.intentTextView);
            puntuacioTextView = itemView.findViewById(R.id.puntuacioTextView);
        }
    }
}