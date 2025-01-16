package com.xmorera.climbingtrainingapp.climbingData;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xmorera.climbingtrainingapp.MainActivity;
import com.xmorera.climbingtrainingapp.R;
import com.xmorera.climbingtrainingapp.utils.DatabaseHelper;

import java.util.List;

public class ClimbingDataAdapter extends RecyclerView.Adapter<ClimbingDataAdapter.ViewHolder> {

    private List<ClimbingData> climbingDataList;
    private Context context;

    public ClimbingDataAdapter(Context context, List<ClimbingData> climbingDataList){
        this.climbingDataList = climbingDataList;
        this.context = context;
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
        //holder.idTextView.setText(data.getId());
        holder.dateTextView.setText(data.getDate());
        holder.dificultatTextView.setText(data.getDificultat());
        holder.zonaTextView.setText(data.getZona());
        String nomRocoReduit;
        nomRocoReduit = (data.getNomRocoReduit() == null) ? "" : " (" + data.getNomRocoReduit() + ")";
        holder.nomRocoReduitTextView.setText(nomRocoReduit);
        boolean neta = data.getIfIntent() == 1 || data.getDescansos() > 0;
        holder.ifIntentTextView.setText(neta ? "Intent/descansos" : "Neta");
        holder.ifIntentTextView.setTextColor(neta ?
                ContextCompat.getColor(context, R.color.red) :
                ContextCompat.getColor(context, R.color.gray));
        holder.puntuacioTextView.setText(String.valueOf(data.getPuntuacio()));
        holder.btnEsborrarVia.setOnClickListener(v -> {
            showDeleteConfirmationDialog(Integer.parseInt(data.getId()), position);
        });
    }

    @Override
    public int getItemCount() {
        return climbingDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //TextView idTextView;
        TextView dateTextView;
        TextView dificultatTextView;
        TextView zonaTextView;
        TextView nomRocoReduitTextView;
        TextView ifIntentTextView;
        TextView puntuacioTextView;
        Button btnEsborrarVia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //idTextView = itemView.findViewById(R.id.idTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            dificultatTextView = itemView.findViewById(R.id.dificultatTextView);
            zonaTextView = itemView.findViewById(R.id.zonaTextView);
            nomRocoReduitTextView = itemView.findViewById(R.id.nomRocoReduit);
            ifIntentTextView = itemView.findViewById(R.id.intentTextView);
            puntuacioTextView = itemView.findViewById(R.id.puntuacioTextView);
            btnEsborrarVia = itemView.findViewById(R.id.btnEsborrarVia);
        }
    }

    public void showDeleteConfirmationDialog(int id, int position){
        // Create a new AlertDialog.Builder instance
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        DatabaseHelper databaseHelper = new DatabaseHelper(context);
                        databaseHelper.deleteData(id);
                        climbingDataList.remove(position);
                        notifyItemRemoved(position);
                        ((MainActivity) context).loadDayData();//refrescar la vista de MainActivity
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Esborrar item?")
                .setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

}