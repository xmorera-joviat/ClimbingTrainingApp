package com.xmorera.climbingtrainingapp.resultats;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.xmorera.climbingtrainingapp.R;
import com.xmorera.climbingtrainingapp.utils.DatabaseHelper;
import com.xmorera.climbingtrainingapp.utils.DateConverter;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

public class GraficaZoom extends AppCompatActivity {
    private List<ResultatsData> resultatsDataList;
    private DatabaseHelper databaseHelper;
    private LineChart chartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grafica_zoom);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        resultatsDataList = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
        chartView = findViewById(R.id.chart_view);
        performQuey();

    }

    private void performQuey() {
        Intent intent = getIntent();
        String startDate = intent.getStringExtra("dataInicial");
        String endDate = intent.getStringExtra("dataFinal");
        //Toast.makeText(this, "Data inicial: " + startDate + ", Data final: " + endDate, Toast.LENGTH_LONG).show();
        //fer la consulta amb les dates seleccionades i mostrar la gràfica zoom
        resultatsDataList.clear();

        Cursor cursor = databaseHelper.getRankingBetweenDates(startDate, endDate);
        if (cursor != null) {
            if (cursor != null) {
                try {
                    while (cursor.moveToNext()) {
                        // Process the cursor data
                        String date = cursor.getString(cursor.getColumnIndexOrThrow("DATE_RANKING"));
                        String dateCustom = DateConverter.convertISOToCustom(date);
                        double puntuacioData = cursor.getDouble(cursor.getColumnIndexOrThrow("PUNTS_RANKING"));
                        int viesDia = cursor.getInt(cursor.getColumnIndexOrThrow("VIES_RANKING"));
                        double metresDia = cursor.getDouble(cursor.getColumnIndexOrThrow("METRES_RANKING"));
                        double mitjanaDia = cursor.getDouble(cursor.getColumnIndexOrThrow("MITJANA_RANKING"));

                        resultatsDataList.add(new ResultatsData(dateCustom, String.valueOf(viesDia), String.valueOf(metresDia).replace(".", ","), String.format("%,.1f", puntuacioData).replace(".", ","), mitjanaDia));

                    }
                } finally {
                    cursor.close(); // Ensure the cursor is closed after processing
                }
            } else {
                Toast.makeText(this, "Error en les dates, inicial i final", Toast.LENGTH_SHORT).show();
            }
        }
        if (resultatsDataList.isEmpty()) {
            Toast.makeText(this, "No hi ha dades per mostrar", Toast.LENGTH_SHORT).show();
        }
        else {
            // Generate the chart
            generateChart();
        }
    }

    private void generateChart() {
        // Prepare data for the chart
        ArrayList<Entry> routesEntries = new ArrayList<>();
        ArrayList<Entry> metersEntries = new ArrayList<>();
        ArrayList<Entry> scoreEntries = new ArrayList<>();

        // Loop through the resultatsDataList to create entries for the chart in original order
        for (int i = 0; i < resultatsDataList.size(); i++) {
            // Assuming the score, number of routes, and meters are what you want to plot
            double score = Double.parseDouble(resultatsDataList.get(i).getPuntuacio().replace(",", "."));
            int routes = Integer.parseInt(resultatsDataList.get(i).getVies());
            double meters = Double.parseDouble(resultatsDataList.get(i).getMetres().replace(",", "."));

            // Use 'i' for the x-axis to maintain original order
            routesEntries.add(new Entry(i, (float) routes)); // X-axis is the index in original order
            metersEntries.add(new Entry(i, (float) meters)); // X-axis is the index in original order
            scoreEntries.add(new Entry(i, (float) score)); // X-axis is the index in original order
        }
        // Create LineDataSets with the entries
        LineDataSet routesDataSet = new LineDataSet(routesEntries, "Vies");
        routesDataSet.setColor(ContextCompat.getColor(this, R.color.blau_turquesa)); // Set line color for routes
        routesDataSet.setValueTextSize(10f);
        routesDataSet.setValueTextColor(Color.GRAY);
        routesDataSet.setDrawCircles(true);
        routesDataSet.setCircleColor(ContextCompat.getColor(this, R.color.blau_turquesa));
        routesDataSet.setLineWidth(2f);
        routesDataSet.setCircleRadius(5f);

        LineDataSet metersDataSet = new LineDataSet(metersEntries, "Metres");
        metersDataSet.setColor(ContextCompat.getColor(this, R.color.blue)); // Set line color for meters
        metersDataSet.setValueTextSize(10f);
        metersDataSet.setValueTextColor(Color.GRAY);
        metersDataSet.setDrawCircles(true);
        metersDataSet.setCircleColor(ContextCompat.getColor(this, R.color.blue));
        metersDataSet.setLineWidth(2f);
        metersDataSet.setCircleRadius(5f);

        LineDataSet scoreDataSet = new LineDataSet(scoreEntries, "Puntuació");
        scoreDataSet.setColor(ContextCompat.getColor(this, R.color.green)); // Set line color for score
        scoreDataSet.setValueTextSize(10f);
        scoreDataSet.setValueTextColor(Color.GRAY);
        scoreDataSet.setDrawCircles(true);
        scoreDataSet.setCircleColor(ContextCompat.getColor(this, R.color.green));
        scoreDataSet.setLineWidth(2f);
        scoreDataSet.setCircleRadius(5f);

        // Create LineData object with all datasets
        LineData lineData = new LineData(routesDataSet, metersDataSet, scoreDataSet);

        // Set data to the chart
        chartView.setData(lineData);
        chartView.invalidate(); // Refresh the chart

    }

}