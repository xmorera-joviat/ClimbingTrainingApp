package com.xmorera.climbingtrainingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Resultats extends AppCompatActivity {
    private EditText startDateEditText;
    private EditText endDateEditText;
    private TextView resultsDisplayTextView;
    private Button btnConsultar;
    private Button btnExportar;
    private Button btnImportar;
    private RecyclerView chartRecyclerView;

    private Calendar calendar = Calendar.getInstance(); // Únic Calendar per a ambdues dates
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

        // Desactivar l'entrada manual a les dates


        startDateEditText = findViewById(R.id.start_date);
        endDateEditText = findViewById(R.id.end_date);

        startDateEditText.setFocusable(false);
        startDateEditText.setFocusableInTouchMode(false);
        endDateEditText.setFocusable(false);
        endDateEditText.setFocusableInTouchMode(false);

        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePicker(endDateEditText));

        resultsDisplayTextView = findViewById(R.id.results_display);
        btnConsultar = findViewById(R.id.btn_consultar);
        btnExportar = findViewById(R.id.btn_exportar);
        btnImportar = findViewById(R.id.btn_importar);
        chartRecyclerView = findViewById(R.id.chart_view);

        btnConsultar.setOnClickListener(v -> performQuery());
        btnExportar.setOnClickListener(v -> exportToCSV());
        btnImportar.setOnClickListener(v -> importFromCSV());

        // Inicialitzar la data final amb la data actual
        updateDateTextView(endDateEditText);
    }

    private void updateDateTextView(EditText dateEditText) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = dateFormat.format(calendar.getTime());
        dateEditText.setText(formattedDate); // Actualitza el TextView corresponent
    }

    private void showDatePicker(EditText dateEditText) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                    updateDateTextView(dateEditText); // Actualitza el TextView amb la nova data
                }, year, month, day);
        datePickerDialog.show();
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
            resultsDisplayTextView.setText("Selecciona les dates, inicial i final");
        }
    }

    private void generateChart() {
        // Implementa la lògica per generar el gràfic aquí
        // Potser utilitzant una biblioteca com MPAndroidChart o similar
    }

    private void exportToCSV() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        } else {
            // Implementa la lògica per exportar dades a un fitxer CSV
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "resultats.csv");
                FileWriter writer = new FileWriter(file);
                writer.append("Data,Resultat\n");
                // Afegir dades aquí
                writer.flush();
                writer.close();
                Toast.makeText(this, "Exportació completada", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error en l'exportació", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void importFromCSV() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
        } else {
            // Implementa la lògica per importar dades des d'un fitxer CSV
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/csv");
            startActivityForResult(intent, PICK_CSV_FILE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CSV_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Processa cada línia del fitxer CSV
                    }
                    reader.close();
                    Toast.makeText(this, "Importació completada", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error en la importació", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}