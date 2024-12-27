package com.xmorera.climbingtrainingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Resultats extends AppCompatActivity {
    private EditText startDateEditText;
    private EditText endDateEditText;
    private TextView resultsDisplayTextView;
    private Button btnConsultar;
    private Button btnExportar;
    private Button btnImportar;
    private RecyclerView chartRecyclerView;

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 2;
    private static final int PICK_CSV_FILE = 3;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resultats);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startDateEditText = findViewById(R.id.start_date);
        endDateEditText = findViewById(R.id.end_date);
        resultsDisplayTextView = findViewById(R.id.results_display);
        btnConsultar = findViewById(R.id.btn_consultar);
        btnExportar = findViewById(R.id.btn_exportar);
        btnImportar = findViewById(R.id.btn_importar);
        chartRecyclerView = findViewById(R.id.chart_view);

        btnConsultar.setOnClickListener(v -> performQuery());
        btnExportar.setOnClickListener(v -> exportToCSV());
        btnImportar.setOnClickListener(v -> importFromCSV());
    }

    private void performQuery() {
        String startDate = startDateEditText.getText().toString();
        String endDate = endDateEditText.getText().toString();

        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            // Realitza la consulta a la base de dades i mostra els resultats al resultsDisplayTextView
            resultsDisplayTextView.setText("Resultats de la consulta");

            // Generació del gràfic
            generateChart();
        } else {
            resultsDisplayTextView.setText("Selecciona les dates");
        }
    }

    private void generateChart() {
        // Implementació del gràfic
    }

    private void exportToCSV() {
        // Comprovar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        } else {
            writeCSV();
        }
    }

    private void writeCSV() {
        String results = resultsDisplayTextView.getText().toString();
        String csvData = "Data Inicial,Data Final,Resultats\n" + startDateEditText.getText().toString() + "," + endDateEditText.getText().toString() + "," + results;

        File file = new File(Environment.getExternalStorageDirectory(), "resultats.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.append(csvData);
            Toast.makeText(this, "Fitxer CSV creat: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error en crear el fitxer CSV", Toast.LENGTH_SHORT).show();
        }
    }

    private void importFromCSV() {
        // Comprovar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
        } else {
            openCSVFile();
        }
    }

    private void openCSVFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/csv");
        startActivityForResult(intent, PICK_CSV_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CSV_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            readCSV(uri);
        }
    }

    private void readCSV(Uri uri) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)))) {
            String line;
            StringBuilder csvData = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                csvData.append(line).append("\n");
            }
            resultsDisplayTextView.setText(csvData.toString());
            Toast.makeText(this, "Fitxer CSV importat", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error en llegir el fitxer CSV", Toast.LENGTH_SHORT).show();
        }
    }
}
