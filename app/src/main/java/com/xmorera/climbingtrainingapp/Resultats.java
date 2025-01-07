package com.xmorera.climbingtrainingapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Resultats extends AppCompatActivity implements View.OnClickListener {
    private Button btnSetmanal;
    private Button btnMensual;
    private Button btnAnual;


    private LinearLayout layoutAltres;

    private EditText startDateEditText;
    private EditText endDateEditText;


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
        layoutAltres = findViewById(R.id.layoutAltres);

        btnSetmanal = findViewById(R.id.btnSetmanal);
        btnMensual = findViewById(R.id.btnMensual);
        btnAnual = findViewById(R.id.btnAnual);
        btnSetmanal.setOnClickListener(this);
        btnMensual.setOnClickListener(this);
        btnAnual.setOnClickListener(this);

        startDateEditText = findViewById(R.id.start_date);
        endDateEditText = findViewById(R.id.end_date);

        startDateEditText.setFocusable(false);
        startDateEditText.setFocusableInTouchMode(false);
        endDateEditText.setFocusable(false);
        endDateEditText.setFocusableInTouchMode(false);

        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePicker(endDateEditText));

        resultatsRecyclerView = findViewById(R.id.resultats_view);
        resultatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        resultatsDataList = new ArrayList<>();
        resultatsDataAdapter = new ResultatsDataAdapter(this, resultatsDataList);
        resultatsRecyclerView.setAdapter(resultatsDataAdapter);

        chartRecyclerView = findViewById(R.id.chart_view);

        databaseHelper = new DatabaseHelper(this);
        preferencesGZero = getSharedPreferences("preferenciesGZero", MODE_PRIVATE);

        // Inicialitzar la data final amb la data actual
        updateDateTextView(endDateEditText);

    }

    @Override
    public void onClick(View view) {

        int daysToSubstract = 0;
        if (view.getId() == R.id.btnSetmanal) {
            daysToSubstract = -7;
        }

        if (view.getId() == R.id.btnMensual) {
            daysToSubstract = -30;
        }
        if (view.getId() == R.id.btnAnual) {
            daysToSubstract = -365;
        }

        calendar.add(Calendar.DAY_OF_MONTH, daysToSubstract);
        updateDateTextView(startDateEditText);
        //tornem a posar la data actual al dia final
        calendar.setTime(new Date());
        updateDateTextView(endDateEditText);
        performQuery();
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
                    updateDateTextView(dateEditText);
                    performQuery();// Actualitza el TextView amb la nova data
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
                        Double metresDia = 0.0;
                        while (cursor2.moveToNext()) {

                            String dificultat = cursor2.getString(cursor2.getColumnIndexOrThrow("DIFICULTAT"));
                            String zona = cursor2.getString(cursor2.getColumnIndexOrThrow("ZONA"));
                            int ifIntent = cursor2.getInt(cursor2.getColumnIndexOrThrow("IFINTENT"));

                            // activació de les preferencies,
                            // ja si l'activity Preferencies no s'ha obert mai les preferencies no estan disponibles
                            if (preferencesGZero.getString(dificultat, "error").equals("error")) {
                                startActivity(new Intent(this, Preferencies.class));
                            }

                            puntuacioDia += Double.parseDouble(preferencesGZero.getString(dificultat, "0,0").replace(",", "."));
                            viesDia += 1;
                            metresDia += Double.parseDouble(preferencesGZero.getString(zona, "0,0").replace(",", "."));

                        }
                        cursor2.close();
                        resultatsDataList.add(new ResultatsData(date,String.valueOf(viesDia),String.valueOf(metresDia).replace(".",","),String.valueOf(puntuacioDia).replace(".",",")));

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



    private void generateChart () {
        // Implementa la lògica per generar el gràfic aquí
        // Potser utilitzant una biblioteca com MPAndroidChart o similar
    }



}
