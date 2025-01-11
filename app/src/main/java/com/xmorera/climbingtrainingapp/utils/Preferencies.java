package com.xmorera.climbingtrainingapp.utils;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.xmorera.climbingtrainingapp.R;

public class Preferencies extends AppCompatActivity {

    private EditText editTextAutos, editTextCorda, editTextShiny, editTextBloc;
    private Button btnSave;

    private SharedPreferences preferencesGZero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preferencies);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.preferencies), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferencesGZero = getSharedPreferences("preferenciesGZero", MODE_PRIVATE);

        editTextAutos = findViewById(R.id.editTextAutos);
        editTextCorda = findViewById(R.id.editTextCorda);
        editTextShiny = findViewById(R.id.editTextShiny);
        editTextBloc = findViewById(R.id.editTextBloc);
        btnSave = findViewById(R.id.btnSave);

        //càrrega dels valors guardats
        loadPreferences();

        //configuració del botó guardar
        btnSave.setOnClickListener(v -> savePreferences());
    }

    private void loadPreferences() {
        // Carrega els valors de SharedPreferences
        editTextAutos.setText(preferencesGZero.getString("Autos", "10,0"));
        editTextCorda.setText(preferencesGZero.getString("Corda", "12,0"));
        editTextShiny.setText(preferencesGZero.getString("ShinyWall", "12,0"));
        editTextBloc.setText(preferencesGZero.getString("Bloc", "4,0"));
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = preferencesGZero.edit();
        //editor.putString("IV", "1.0");
        editor.putString("Autos", editTextAutos.getText().toString());
        editor.putString("Corda", editTextCorda.getText().toString());
        editor.putString("ShinyWall", editTextShiny.getText().toString());
        editor.putString("Bloc", editTextBloc.getText().toString());
        editor.apply(); // Aplica els canvis
        finish();
    }
}

