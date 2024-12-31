package com.xmorera.climbingtrainingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Resultats extends AppCompatActivity {
    private EditText startDateEditText;
    private EditText endDateEditText;
    private Button btnConsultar;
    private Button btnExportar;
    private Button btnImportar;

    private RecyclerView resultatsRecyclerView;
    private ResultatsDataAdapter resultatsDataAdapter;
    private List<ResultatsData> resultatsDataList;

    private RecyclerView chartRecyclerView;

    private DatabaseHelper databaseHelper;

    private Calendar calendar = Calendar.getInstance(); // Únic Calendar per a amb dues dates
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 2;
    private static final int PICK_CSV_FILE = 3;

    private SharedPreferences preferencesGZero;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resultats);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.resultats), (v, insets) -> {
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

        btnConsultar = findViewById(R.id.btn_consultar);
        btnExportar = findViewById(R.id.btn_exportar);
        btnImportar = findViewById(R.id.btn_importar);

        resultatsRecyclerView = findViewById(R.id.resultats_view);
        resultatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        resultatsDataList = new ArrayList<>();
        resultatsDataAdapter = new ResultatsDataAdapter(this, resultatsDataList);
        resultatsRecyclerView.setAdapter(resultatsDataAdapter);

        chartRecyclerView = findViewById(R.id.chart_view);

        btnConsultar.setOnClickListener(v -> performQuery());
        //btnExportar.setOnClickListener(v -> exportToCSV());
        //btnImportar.setOnClickListener(v -> importFromCSV());

        databaseHelper = new DatabaseHelper(this);
        preferencesGZero = getSharedPreferences("preferenciesGZero", MODE_PRIVATE);

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

            // Realitza la consulta a la base de dades i mostra els resultats
            // esborrar dades anteriors
            resultatsDataList.clear();

            Cursor cursor = databaseHelper.getUniqueDates(startDate, endDate);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    //obtenció de les dates que tenen dades
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
                    //Log.d("Resultats", "Data: " + date);
                    //per a cada data calcular el nombre de vies i la puntuació del dia

                    Cursor cursor2 = databaseHelper.getDayData(date);
                    if (cursor2 != null) {
                        Double puntuacioDia = 0.0;
                        int viesDia = 0;
                        while (cursor2.moveToNext()) {
                            String dificultat = cursor2.getString(cursor2.getColumnIndexOrThrow("DIFICULTAT"));
                            String zona = cursor2.getString(cursor2.getColumnIndexOrThrow("ZONA"));
                            int ifIntent = cursor2.getInt(cursor2.getColumnIndexOrThrow("IFINTENT"));
                            String puntuacio = puntuacioData(dificultat, zona, ifIntent);
                            puntuacioDia += Double.parseDouble(puntuacio.replace(",", "."));
                            viesDia += 1;

                        }
                        cursor2.close();
                        resultatsDataList.add(new ResultatsData(date,String.valueOf(viesDia),String.valueOf(puntuacioDia).replace(".",",")));

                        //Log.d("resultats", "dia:  " + date + " vies: " + viesDia + " puntuacioDia: " + puntuacioDia);
                    }
                }
                cursor.close();
                //Log.d("Resultats", "ResultatsDataList size: " + resultatsDataList.size());
                resultatsDataAdapter.notifyDataSetChanged();
                resultatsRecyclerView.setVisibility(View.VISIBLE);
                if (resultatsDataList.isEmpty()) {
                    Toast.makeText(this, "No hi ha dades per mostrar", Toast.LENGTH_SHORT).show();
                }

                // Generació del gràfic
                generateChart();

            }

        } else {
            Toast.makeText(this, "Selecciona les dates, inicial i final", Toast.LENGTH_SHORT).show();

        }
    }

    private String puntuacioData(String dificultat, String zona, int ifIntent) {
        // activació de les preferencies,
        // ja si l'activity Preferencies no s'ha obert mai les preferencies no estan disponibles
        if (preferencesGZero.getString(dificultat, "error").equals("error")) {
            startActivity(new Intent(this, Preferencies.class));
        }
        String viaValor = preferencesGZero.getString(dificultat, "0,0").replace(",",".");
        String zonaMetres= preferencesGZero.getString(zona, "0,0").replace(",",".");
        String coeficientZona = preferencesGZero.getString(zona+"Coeficient", "1,0").replace(",",".");
        double puntsVia = Double.parseDouble(viaValor)*Double.parseDouble(zonaMetres)*Double.parseDouble(coeficientZona);
        if(ifIntent == 1){
            puntsVia *= Double.parseDouble(preferencesGZero.getString("IntentCoeficient", "0,0").replace(",","."));

        }


        return String.format("%.1f", puntsVia).replace(".", ",");
    }

    private void generateChart () {
        // Implementa la lògica per generar el gràfic aquí
        // Potser utilitzant una biblioteca com MPAndroidChart o similar
    }


  /*  private void exportToCSV () {
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

    private void importFromCSV () {
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
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
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
*/
}
