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

    private EditText editTextIV, editTextV, editTextVPlus,
            editText6a, editText6aPlus, editText6b, editText6bPlus, editText6c, editText6cPlus,
            editText7a, editText7aPlus, editText7b, editText7bPlus, editText7c, editText7cPlus,
            editText8a, editText8aPlus, editText8b, editText8bPlus, editText8c;
    private EditText editTextIntentCoeficient, editTextShinyCoeficient, editTextCordaCoeficient, editTextBlocCoeficient;
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

        editTextIV = findViewById(R.id.editTextIV);
        editTextV = findViewById(R.id.editTextV);
        editTextVPlus = findViewById(R.id.editTextVPlus);
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
        editText8a = findViewById(R.id.editText8a);
        editText8aPlus = findViewById(R.id.editText8aPlus);
        editText8b = findViewById(R.id.editText8b);
        editText8bPlus = findViewById(R.id.editText8bPlus);
        editText8c = findViewById(R.id.editText8c);
        editTextIntentCoeficient = findViewById(R.id.editTextIntentCoeficient);
        editTextShinyCoeficient = findViewById(R.id.editTextShinyCoeficient);
        editTextCordaCoeficient = findViewById(R.id.editTextCordaCoeficient);
        editTextBlocCoeficient = findViewById(R.id.editTextBlocCoeficient);
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
        editTextIV.setText(preferencesGZero.getString("IV", "1,0"));
        editTextV.setText(preferencesGZero.getString("V", "1,4"));
        editTextVPlus.setText(preferencesGZero.getString("V+", "2,0"));
        editText6a.setText(preferencesGZero.getString("6a", "2,8"));
        editText6aPlus.setText(preferencesGZero.getString("6a+", "3,9"));
        editText6b.setText(preferencesGZero.getString("6b", "5,4"));
        editText6bPlus.setText(preferencesGZero.getString("6b+", "7,5"));
        editText6c.setText(preferencesGZero.getString("6c", "10,6"));
        editText6cPlus.setText(preferencesGZero.getString("6c+", "14,8"));
        editText7a.setText(preferencesGZero.getString("7a", "20,7"));
        editText7aPlus.setText(preferencesGZero.getString("7a+", "28,9"));
        editText7b.setText(preferencesGZero.getString("7b", "40,5"));
        editText7bPlus.setText(preferencesGZero.getString("7b+", "56,7"));
        editText7c.setText(preferencesGZero.getString("7c", "79,4"));
        editText7cPlus.setText(preferencesGZero.getString("7c+", "111,1"));
        editText8a.setText(preferencesGZero.getString("8a", "155,6"));
        editText8aPlus.setText(preferencesGZero.getString("8a+", "217,8"));
        editText8b.setText(preferencesGZero.getString("8b", "304,9"));
        editText8bPlus.setText(preferencesGZero.getString("8b+", "426,9"));
        editText8c.setText(preferencesGZero.getString("8c", "597,6"));
        editTextIntentCoeficient.setText(preferencesGZero.getString("IntentCoeficient", "0,10"));
        editTextShinyCoeficient.setText(preferencesGZero.getString("ShinyCoeficient", "1,10"));
        editTextCordaCoeficient.setText(preferencesGZero.getString("CordaCoeficient", "1,20"));
        editTextBlocCoeficient.setText(preferencesGZero.getString("BlocCoeficient", "1,30"));
        editTextAutos.setText(preferencesGZero.getString("Autos", "10,0"));
        editTextCorda.setText(preferencesGZero.getString("Corda", "12,0"));
        editTextShiny.setText(preferencesGZero.getString("ShinyWall", "12,0"));
        editTextBloc.setText(preferencesGZero.getString("Bloc", "4,0"));
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = preferencesGZero.edit();
        //editor.putString("IV", "1.0");
        editor.putString("IV", editTextIV.getText().toString());
        editor.putString("V", editTextV.getText().toString());
        editor.putString("V+", editTextVPlus.getText().toString());
        editor.putString("6a", editText6a.getText().toString());
        editor.putString("6a+", editText6aPlus.getText().toString());
        editor.putString("6b", editText6b.getText().toString());
        editor.putString("6b+", editText6bPlus.getText().toString());
        editor.putString("6c", editText6c.getText().toString());
        editor.putString("6c+", editText6cPlus.getText().toString());
        editor.putString("7a", editText7a.getText().toString());
        editor.putString("7a+", editText7aPlus.getText().toString());
        editor.putString("7b", editText7b.getText().toString());
        editor.putString("7b+", editText7bPlus.getText().toString());
        editor.putString("7c", editText7c.getText().toString());
        editor.putString("7c+", editText7cPlus.getText().toString());
        editor.putString("8a", editText8a.getText().toString());
        editor.putString("8a+", editText8aPlus.getText().toString());
        editor.putString("8b", editText8b.getText().toString());
        editor.putString("8b+", editText8bPlus.getText().toString());
        editor.putString("8c", editText8c.getText().toString());
        editor.putString("IntentCoeficient", editTextIntentCoeficient.getText().toString());
        editor.putString("CordaCoeficient", editTextCordaCoeficient.getText().toString());
        editor.putString("ShinyCoeficient", editTextShinyCoeficient.getText().toString());
        editor.putString("BlocCoeficient", editTextBlocCoeficient.getText().toString());
        editor.putString("Autos", editTextAutos.getText().toString());
        editor.putString("Corda", editTextCorda.getText().toString());
        editor.putString("ShinyWall", editTextShiny.getText().toString());
        editor.putString("Bloc", editTextBloc.getText().toString());
        editor.apply(); // Aplica els canvis
        finish();
    }
}

