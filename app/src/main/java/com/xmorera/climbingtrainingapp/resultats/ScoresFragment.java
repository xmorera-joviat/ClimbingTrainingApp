package com.xmorera.climbingtrainingapp.resultats;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.xmorera.climbingtrainingapp.R;

public class ScoresFragment extends DialogFragment {

    public ScoresFragment() {
        // Constructor buit requerit
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Crea i retorna una instància del diàleg
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setContentView(R.layout.fragment_scores); // Estableix el teu disseny personalitzat
        return dialog;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicialitza el botó de tornada


        // Aquí pots recuperar i mostrar els punts més alts
    }
}