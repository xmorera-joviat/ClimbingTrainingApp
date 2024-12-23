package com.xmorera.climbingtrainingapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Preferencies extends AppCompatActivity {

    private EditText editText6a, editText6aPlus, editText6b, editText6bPlus, editText6c, editText6cPlus;
    private EditText editText7a, editText7aPlus, editText7b, editText7bPlus, editText7c, editText7cPlus;
    private EditText editTextIntent;
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

        editText6a = findViewById(R.id.editText6a);
        editText6aPlus = findViewById(R.id.editText6aPlus);
        editText6b = findViewById(R.id.editText6b);
        editText6bPlus = findViewById(R.id.editText6bPlus);
        editText6c = findViewById(R.id.editText6c);
        editText6cPlus = findViewById(R.id.editText6cPlus);
        editText7a = findViewById(R.id.editText7a);
        editText7aPlus = findViewById(R.id.editText7aPlus);
        editText7b = findViewById(R.id.editText7b);
        editText7bPlus = findViewById(R.id.editText7bPlus);
        editText7c = findViewById(R.id.editText7c);
        editText7cPlus = findViewById(R.id.editText7cPlus);
        editTextIntent = findViewById(R.id.editTextIntent);
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
        editText6a.setText(preferencesGZero.getString("6a", "1,0"));
        editText6aPlus.setText(preferencesGZero.getString("6aPlus", "1,5"));
        editText6b.setText(preferencesGZero.getString("6b", "2,3"));
        editText6bPlus.setText(preferencesGZero.getString("6bPlus", "3,4"));
        editText6c.setText(preferencesGZero.getString("6c", "5,1"));
        editText6cPlus.setText(preferencesGZero.getString("6cPlus", "7,6"));
        editText7a.setText(preferencesGZero.getString("7a", "11,4"));
        editText7aPlus.setText(preferencesGZero.getString("7aPlus", "17,1"));
        editText7b.setText(preferencesGZero.getString("7b", "25,6"));
        editText7bPlus.setText(preferencesGZero.getString("7bPlus", "38,4"));
        editText7c.setText(preferencesGZero.getString("7c", "57,7"));
        editText7cPlus.setText(preferencesGZero.getString("7cPlus", "86,6"));
        editTextIntent.setText(preferencesGZero.getString("Intent", "0,25"));
        editTextAutos.setText(preferencesGZero.getString("Autos", "10,0"));
        editTextCorda.setText(preferencesGZero.getString("Corda", "12,0"));
        editTextShiny.setText(preferencesGZero.getString("Shinywall", "12,0"));
        editTextBloc.setText(preferencesGZero.getString("Bloc", "4,0"));
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = preferencesGZero.edit();
        editor.putString("6a", editText6a.getText().toString());
        editor.putString("6aPlus", editText6aPlus.getText().toString());
        editor.putString("6b", editText6b.getText().toString());
        editor.putString("6bPlus", editText6bPlus.getText().toString());
        editor.putString("6c", editText6c.getText().toString());
        editor.putString("6cPlus", editText6cPlus.getText().toString());
        editor.putString("7a", editText7a.getText().toString());
        editor.putString("7aPlus", editText7aPlus.getText().toString());
        editor.putString("7b", editText7b.getText().toString());
        editor.putString("7bPlus", editText7bPlus.getText().toString());
        editor.putString("7c", editText7c.getText().toString());
        editor.putString("7cPlus", editText7cPlus.getText().toString());
        editor.putString("Intent", editTextIntent.getText().toString());
        editor.putString("Autos", editTextAutos.getText().toString());
        editor.putString("Corda", editTextCorda.getText().toString());
        editor.putString("Shinywall", editTextShiny.getText().toString());
        editor.putString("Bloc", editTextBloc.getText().toString());
        editor.apply(); // Aplica els canvis
        finish();
    }
}

