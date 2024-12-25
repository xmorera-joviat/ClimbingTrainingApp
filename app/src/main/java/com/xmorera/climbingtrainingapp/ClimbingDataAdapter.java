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
        holder.idTextView.setText(data.getId());
        holder.dateTextView.setText(data.getDate());
        holder.dificultatTextView.setText(data.getDificultat());
        holder.zonaTextView.setText(data.getZona());
        holder.ifIntentTextView.setText(data.getIfIntent() == 1 ? "Intent" : "Neta");
        holder.puntuacioTextView.setText(String.valueOf(data.getPuntuacio()));
    }

    @Override
    public int getItemCount() {
        return climbingDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView idTextView;
        TextView dateTextView;
        TextView dificultatTextView;
        TextView zonaTextView;
        TextView ifIntentTextView;
        TextView puntuacioTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.idTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            dificultatTextView = itemView.findViewById(R.id.dificultatTextView);
            zonaTextView = itemView.findViewById(R.id.zonaTextView);
            ifIntentTextView = itemView.findViewById(R.id.intentTextView);
            puntuacioTextView = itemView.findViewById(R.id.puntuacioTextView);
        }
    }
}