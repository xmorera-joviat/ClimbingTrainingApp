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
            editText8a, editText8aPlus, editText8b, editText8bPlus, editText8c, editText8cPlus;
    private EditText editTextIntentCoeficient;
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
        editText8cPlus = findViewById(R.id.editText8cPlus);
        editTextIntentCoeficient = findViewById(R.id.editTextIntentCoeficient);
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
        editTextIV.setText(preferencesGZero.getString("IV", "4,0"));
        editTextV.setText(preferencesGZero.getString("V", "5,0"));
        editTextVPlus.setText(preferencesGZero.getString("V+", "5,5"));
        editText6a.setText(preferencesGZero.getString("6a", "6"));
        editText6aPlus.setText(preferencesGZero.getString("6a+", "7"));
        editText6b.setText(preferencesGZero.getString("6b", "8"));
        editText6bPlus.setText(preferencesGZero.getString("6b+", "9"));
        editText6c.setText(preferencesGZero.getString("6c", "10"));
        editText6cPlus.setText(preferencesGZero.getString("6c+", "11"));
        editText7a.setText(preferencesGZero.getString("7a", "12"));
        editText7aPlus.setText(preferencesGZero.getString("7a+", "13"));
        editText7b.setText(preferencesGZero.getString("7b", "14"));
        editText7bPlus.setText(preferencesGZero.getString("7b+", "15"));
        editText7c.setText(preferencesGZero.getString("7c", "16"));
        editText7cPlus.setText(preferencesGZero.getString("7c+", "17"));
        editText8a.setText(preferencesGZero.getString("8a", "18"));
        editText8aPlus.setText(preferencesGZero.getString("8a+", "19"));
        editText8b.setText(preferencesGZero.getString("8b", "20"));
        editText8bPlus.setText(preferencesGZero.getString("8b+", "21"));
        editText8c.setText(preferencesGZero.getString("8c", "22"));
        editText8cPlus.setText(preferencesGZero.getString("8c+", "23"));
        editTextIntentCoeficient.setText(preferencesGZero.getString("IntentCoeficient", "0,50"));
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
        editor.putString("8c+", editText8cPlus.getText().toString());
        editor.putString("IntentCoeficient", editTextIntentCoeficient.getText().toString());
        editor.putString("Autos", editTextAutos.getText().toString());
        editor.putString("Corda", editTextCorda.getText().toString());
        editor.putString("ShinyWall", editTextShiny.getText().toString());
        editor.putString("Bloc", editTextBloc.getText().toString());
        editor.apply(); // Aplica els canvis
        finish();
    }
}

