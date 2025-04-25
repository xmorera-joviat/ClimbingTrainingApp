package com.xmorera.climbingtrainingapp.resultats;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xmorera.climbingtrainingapp.MainActivity;
import com.xmorera.climbingtrainingapp.R;
import com.xmorera.climbingtrainingapp.utils.Puntuacio;

import java.util.List;

/**
 * Adapter per a mostrar una llista de resultats en un RecyclerView.
 * Aquesta classe s'encarrega de crear i vincular les vistes per a cada element de la llista de resultats.
 */
public class ResultatsDataAdapter extends RecyclerView.Adapter<ResultatsDataAdapter.ViewHolder> {

    private List<ResultatsData> resultatsDataList;
    private Context context;

    /**
     * Constructor de la classe ResultatsDataAdapter.
     *
     * @param context Context de l'activitat que utilitza aquest adapter.
     * @param resultatsDataList Llista de dades de resultats a mostrar.
     */
    public ResultatsDataAdapter(Context context, List<ResultatsData> resultatsDataList) {
        this.resultatsDataList = resultatsDataList;
        this.context = context;
    }

    /**
     * Crea una nova vista per a un element de la llista.
     *
     * @param parent El grup de vista al qual s'afegeix la nova vista.
     * @param viewType Tipus de vista de l'element.
     * @return Un nou ViewHolder que conté la vista inflada.
     */
    @NonNull
    @Override
    public ResultatsDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resultats_data, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Vincula les dades a la vista per a un element específic de la llista.
     *
     * @param holder El ViewHolder que conté les vistes a vincular.
     * @param position La posició de l'element a la llista.
     */
    @Override
    public void onBindViewHolder(@NonNull ResultatsDataAdapter.ViewHolder holder, int position) {
        Puntuacio puntuacio = new Puntuacio();
        ResultatsData data = resultatsDataList.get(position);
        holder.dateTextView.setText(data.getDate());
        holder.viesTextView.setText(data.getVies());
        holder.puntuacioTextView.setText(data.getPuntuacio());
        holder.metresTextView.setText(data.getMetres());
        holder.mitjanaTextView.setText(puntuacio.mitjanaGrau(data.getMitjana()));

        // Set an OnClickListener for the itemView
        holder.itemView.setOnClickListener(v -> {
            // Create an Intent to start MainActivity
            Intent intent = new Intent(context, MainActivity.class);

            // Pass the selected date as an extra
            intent.putExtra("selectedDate", data.getDate());
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Start MainActivity
            context.startActivity(intent);
        });
    }

    /**
     * Retorna el nombre d'elements a la llista.
     *
     * @return Nombre d'elements a la llista de resultats.
     */
    @Override
    public int getItemCount() {
        return resultatsDataList.size();
    }

    /**
     * Classe interna ViewHolder que manté les referències a les vistes per a cada element.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView viesTextView;
        TextView puntuacioTextView;
        TextView metresTextView;
        TextView mitjanaTextView;

        /**
         * Constructor de la classe ViewHolder.
         *
         * @param itemView La vista de l'element que conté les referències a les vistes.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            viesTextView = itemView.findViewById(R.id.viesTextView);
            puntuacioTextView = itemView.findViewById(R.id.puntuacioTextView);
            metresTextView = itemView.findViewById(R.id.metresTextView);
            mitjanaTextView = itemView.findViewById(R.id.mitjanaTextView);
        }
    }
}
