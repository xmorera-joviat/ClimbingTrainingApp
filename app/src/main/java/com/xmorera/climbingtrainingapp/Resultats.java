package com.xmorera.climbingtrainingapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.xmorera.climbingtrainingapp.utils.DatabaseHelper;
import com.xmorera.climbingtrainingapp.utils.DateConverter;


public class Resultats extends AppCompatActivity implements View.OnClickListener  {
    private Button btnSetmanal;
    private Button btnMensual;
    private Button btnAnual;

    private LinearLayout layoutAltres;

    private EditText startDateEditText;
    private EditText endDateEditText;

    private RecyclerView resultatsRecyclerView;
    private ResultatsDataAdapter resultatsDataAdapter;
    private List<ResultatsData> resultatsDataList;

    private DatabaseHelper databaseHelper;

    private Calendar calendar = Calendar.getInstance(); // Únic Calendar per a amb dues dates

    private SharedPreferences preferencesGZero;

    private LineChart chartView;

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

        databaseHelper = new DatabaseHelper(this);
        preferencesGZero = getSharedPreferences("preferenciesGZero", MODE_PRIVATE);

        chartView = findViewById(R.id.chart_view);

        // Inicialitzar la data final amb la data actual
        updateDateTextView(endDateEditText);

        //creació de la custom marker view per veue la data en fer click en un node de la gràfica
        CustomMarkerView markerView = new CustomMarkerView(this, R.layout.custom_marker_view);
        chartView.setMarkerView(markerView);

        //set the value selected listener
        chartView.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //mostrar el marcador quan un valor es sellecionat
                markerView.refreshContent(e, h);
                markerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected() {
                markerView.setVisibility(View.GONE);
            }
        });
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

        updateDate(startDateEditText, daysToSubstract);
        calendar.setTime(new Date()); //reset a data actual
        updateDate(endDateEditText, 0); // Actualitzar la data final amb la data actual
        performQuery();
    }

    private void updateDate(EditText dateEditText, int daysToSubstract) {
        calendar.add(Calendar.DAY_OF_MONTH, daysToSubstract);
        updateDateTextView(dateEditText);
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

            // esborrar dates anteriors
            resultatsDataList.clear();

            Cursor cursor = databaseHelper.getUniqueDates(startDate, endDate);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    //obtenció de les dates que tenen dades
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
                    String dateCustom = DateConverter.convertISOToCustom(date);

                    //per a cada data calcular el nombre de vies i la puntuació del dia
                    Cursor cursor2 = databaseHelper.getDayData(dateCustom);
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
                        resultatsDataList.add(new ResultatsData(dateCustom,String.valueOf(viesDia),String.valueOf(metresDia).replace(".",","),String.valueOf(puntuacioDia).replace(".",",")));
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

    private void generateChart() {
        // Prepare data for the chart
        ArrayList<Entry> routesEntries = new ArrayList<>();
        ArrayList<Entry> metersEntries = new ArrayList<>();
        ArrayList<Entry> scoreEntries = new ArrayList<>();

        // Loop through the resultatsDataList to create entries for the chart
        for (int i = resultatsDataList.size() - 1; i >= 0; i--) {
            // Assuming the score, number of routes, and meters are what you want to plot
            double score = Double.parseDouble(resultatsDataList.get(i).getPuntuacio().replace(",", "."));
            int routes = Integer.parseInt(resultatsDataList.get(i).getVies());
            double meters = Double.parseDouble(resultatsDataList.get(i).getMetres().replace(",", "."));

            routesEntries.add(new Entry(resultatsDataList.size() - 1 - i, (float) routes)); // X-axis is the index in reverse order
            metersEntries.add(new Entry(resultatsDataList.size() - 1 - i, (float) meters)); // X-axis is the index in reverse order
            scoreEntries.add(new Entry(resultatsDataList.size() - 1 - i, (float) score)); // X-axis is the index in reverse order

        }

        // Create LineDataSets with the entries
        LineDataSet routesDataSet = new LineDataSet(routesEntries, "Vies");
        routesDataSet.setColor(ContextCompat.getColor(this, R.color.blue)); // Set line color for routes
        routesDataSet.setValueTextSize(10f);
        routesDataSet.setValueTextColor(Color.GRAY);
        routesDataSet.setDrawCircles(true);
        routesDataSet.setCircleColor(ContextCompat.getColor(this, R.color.blue));
        routesDataSet.setLineWidth(2f);
        routesDataSet.setCircleRadius(5f);

        LineDataSet metersDataSet = new LineDataSet(metersEntries, "Metres");
        metersDataSet.setColor(ContextCompat.getColor(this, R.color.green)); // Set line color for meters
        metersDataSet.setValueTextSize(10f);
        metersDataSet.setValueTextColor(Color.GRAY);
        metersDataSet.setDrawCircles(true);
        metersDataSet.setCircleColor(ContextCompat.getColor(this, R.color.green));
        metersDataSet.setLineWidth(2f);
        metersDataSet.setCircleRadius(5f);

        LineDataSet scoreDataSet = new LineDataSet(scoreEntries, "Puntuació");
        scoreDataSet.setColor(ContextCompat.getColor(this, R.color.red)); // Set line color for score
        scoreDataSet.setValueTextSize(10f);
        scoreDataSet.setValueTextColor(Color.GRAY);
        scoreDataSet.setDrawCircles(true);
        scoreDataSet.setCircleColor(ContextCompat.getColor(this, R.color.red));
        scoreDataSet.setLineWidth(2f);
        scoreDataSet.setCircleRadius(5f);

        // Create LineData object with all datasets
        LineData lineData = new LineData( routesDataSet, metersDataSet, scoreDataSet);

        // Set data to the chart
        chartView.setData(lineData);
        chartView.invalidate(); // Refresh the chart
    }



    //classe auxiliar per a visualitzar la data d'un node en fer-ne click
    public class CustomMarkerView extends MarkerView{

        private TextView tvDate;

        public CustomMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);
            tvDate = findViewById(R.id.tvDate);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            String date = getDateFromEntry(e);
            tvDate.setText(date);
            super.refreshContent(e, highlight);
        }

        private String getDateFromEntry(Entry e) {
            int index = (int) e.getX();
            return resultatsDataList.get(resultatsDataList.size() -1 - index).getDate();
        }

        public int getXOffset(float xpos) {
            return -getWidth() / 2;
        }

        public int getYOffset(float ypos) {
            return -getHeight();
        }
    }


}
