package com.xmorera.climbingtrainingapp.RocodromsZones;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.xmorera.climbingtrainingapp.R;
import com.xmorera.climbingtrainingapp.utils.DatabaseHelper;

public class NouRocodrom extends AppCompatActivity {
    private Button btnGuardar;
    private Button btnGuardarCrearZones;
    private Button btnCancelar;

    TextView editTextNom;
    TextView editTextComCurt;
    TextView editTextPoblacio;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nou_rocodrom);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> {
            //comprovacions , que estiguin tots els camps plens i que no existeixi
            if(insertRocoDatabase()){
                finish();
            }


        });
        btnGuardarCrearZones = findViewById(R.id.btnGuardarCrearZones);
        btnGuardarCrearZones.setOnClickListener(v -> {
            //comprovacions , que estiguin tots els camps plens i que no existeixi
            if(insertRocoDatabase()) {
                //obtenir l'id del rocodrom creat i adjuntar-lo a l'intent
                Intent intent = new Intent(this, Rocodroms.class);
                intent.putExtra("idRoco", db.getLastRocodromId());
                startActivity(intent);
            }

        });
        btnCancelar = findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(v -> {
            finish();
        });

        editTextNom = findViewById(R.id.editTextNom);
        editTextComCurt = findViewById(R.id.editTextComCurt);
        editTextPoblacio = findViewById(R.id.editTextPoblacio);

        db = new DatabaseHelper(this);


    }

    public boolean insertRocoDatabase(){

        if (editTextNom.getText().toString().isEmpty() || editTextComCurt.getText().toString().isEmpty() || editTextPoblacio.getText().toString().isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Si us plau, ompliu tots els camps.")
                    .setPositiveButton("OK", null)
                    .show();
            return false;
        } else {
            if (db.getRocodromsByNameAndCity(editTextNom.getText().toString(), editTextPoblacio.getText().toString())) {
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("El rocodrom ja existeix.")
                        .setPositiveButton("OK", null)
                        .show();
                return false;
            } else {
                String nom = editTextNom.getText().toString();
                String nomCurt = editTextComCurt.getText().toString();
                String poblacio = editTextPoblacio.getText().toString();
                db.insertRocodrom(nom, nomCurt, poblacio);
                return true;
            }
        }

    }

}