package com.xmorera.climbingtrainingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResultatsDataAdapter extends RecyclerView.Adapter<ResultatsDataAdapter.ViewHolder> {

    private List<ResultatsData> resultatsDataList;
    private Context context;

    public ResultatsDataAdapter(Context context, List<ResultatsData> resultatsDataList) {
        this.resultatsDataList = resultatsDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ResultatsDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resultats_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultatsDataAdapter.ViewHolder holder, int position) {
        ResultatsData data = resultatsDataList.get(position);
        holder.dateTextView.setText(data.getDate());
        holder.viesTextView.setText(data.getVies());
        holder.puntuacioTextView.setText(data.getPuntuacio());

    }

    @Override
    public int getItemCount() {
        return resultatsDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView viesTextView;
        TextView puntuacioTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            viesTextView = itemView.findViewById(R.id.viesTextView);
            puntuacioTextView = itemView.findViewById(R.id.puntuacioTextView);
        }
    }
}
