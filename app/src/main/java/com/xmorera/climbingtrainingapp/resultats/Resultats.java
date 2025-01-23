package com.xmorera.climbingtrainingapp.resultats;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import com.github.mikephil.charting.utils.MPPointF;
import com.xmorera.climbingtrainingapp.R;
import com.xmorera.climbingtrainingapp.climbingData.Puntuacio;
import com.xmorera.climbingtrainingapp.utils.DatabaseHelper;
import com.xmorera.climbingtrainingapp.utils.DateConverter;
import com.xmorera.climbingtrainingapp.utils.Utilitats;


public class Resultats extends AppCompatActivity implements View.OnClickListener  {

    private Button btnSetmanal;
    private Button btnMensual;
    private Button btnTrimestral;
    private Button btnSemestral;
    private Button btnAnual;
    private Button btnTotal;

    private LinearLayout layoutAltres;

    private EditText startDateEditText;
    private EditText endDateEditText;

    private Button btnOrdData;
    private Button btnOrdVies;
    private Button btnOrdMetres;
    private Button btnOrdPunts;
    private Button btnOrdMitjana;

    //variables per mantenir l'estat d'ordenació
    private boolean isDataAscending = true;
    private boolean isViesAscending = true;
    private boolean isMetresAscending = true;
    private boolean isPuntsAscending = true;
    private boolean isMitjanaAscending = true;

    private RecyclerView resultatsRecyclerView;
    private ResultatsDataAdapter resultatsDataAdapter;
    private List<ResultatsData> resultatsDataList;

    private DatabaseHelper databaseHelper;

    private Calendar calendar = Calendar.getInstance(); // Únic Calendar per a amb dues dates

    Puntuacio puntuacio;

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
        btnTrimestral = findViewById(R.id.btnTrimestral);
        btnSemestral = findViewById(R.id.btnSemestral);
        btnAnual = findViewById(R.id.btnAnual);
        btnTotal = findViewById(R.id.btnTotal);

        btnSetmanal.setOnClickListener(this);
        btnMensual.setOnClickListener(this);
        btnTrimestral.setOnClickListener(this);
        btnSemestral.setOnClickListener(this);
        btnAnual.setOnClickListener(this);
        btnTotal.setOnClickListener(this);

        startDateEditText = findViewById(R.id.start_date);
        endDateEditText = findViewById(R.id.end_date);

        startDateEditText.setFocusable(false);
        startDateEditText.setFocusableInTouchMode(false);
        endDateEditText.setFocusable(false);
        endDateEditText.setFocusableInTouchMode(false);

        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePicker(endDateEditText));

        btnOrdData = findViewById(R.id.btnOrdData);
        btnOrdData.setOnClickListener(v -> sortResults("data"));
        btnOrdVies = findViewById(R.id.btnOrdVies);
        btnOrdVies.setOnClickListener(v -> sortResults("vies"));
        btnOrdMetres = findViewById(R.id.btnOrdMetres);
        btnOrdMetres.setOnClickListener(v -> sortResults("metres"));
        btnOrdPunts = findViewById(R.id.btnOrdPunts);
        btnOrdPunts.setOnClickListener(v -> sortResults("punts"));
        btnOrdMitjana = findViewById(R.id.btnOrdMitjana);
        btnOrdMitjana.setOnClickListener(v -> sortResults("mitjana"));

        resultatsRecyclerView = findViewById(R.id.resultats_view);
        resultatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        resultatsDataList = new ArrayList<>();
        resultatsDataAdapter = new ResultatsDataAdapter(this, resultatsDataList);
        resultatsRecyclerView.setAdapter(resultatsDataAdapter);

        databaseHelper = new DatabaseHelper(this);
        puntuacio = new Puntuacio();

        chartView = findViewById(R.id.chart_view);

        // Estableix la data inicial a 7 dies abans de la data actual
        calendar.setTime(new Date()); // Estableix la data actual
        updateDate(startDateEditText, -7); // Data inicial: 7 dies abans
        updateDate(endDateEditText, 7); // Data final: data actual

        // Realitza la consulta inicial
        performQuery();

        //creació de la custom marker view per veure la data en fer click en un node de la gràfica
        CustomMarkerView markerView = new CustomMarkerView(this, R.layout.custom_marker_view);
        chartView.setMarker(markerView);

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
        if (view.getId() == R.id.btnTrimestral) {
            daysToSubstract = -90;
        }
        if (view.getId() == R.id.btnSemestral) {
            daysToSubstract = -180;
        }
        if (view.getId() == R.id.btnAnual) {
            daysToSubstract = -365;
        }
        if (view.getId() == R.id.btnTotal) {
            daysToSubstract = -10000;
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

        DateConverter dates = new DateConverter();
        int dies = dates.getDaysBetweenDates(startDate, endDate);
        updateTextColorBtnPeriodes(-dies);

        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            // Clear previous results
            resultatsDataList.clear();

            // Query the database
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

                            resultatsDataList.add(new ResultatsData(dateCustom, String.valueOf(viesDia), String.valueOf(metresDia).replace(".", ","), String.format("%,.1f", puntuacioData).replace(".", ","), puntuacioData / viesDia));
                        }
                    } finally {
                        cursor.close(); // Ensure the cursor is closed after processing
                    }
                } else {
                    Toast.makeText(this, "Selecciona les dates, inicial i final", Toast.LENGTH_SHORT).show();
                }

                // Notify the adapter and generate the chart
                resultatsDataAdapter.notifyDataSetChanged();
                resultatsRecyclerView.setVisibility(View.VISIBLE);
                if (resultatsDataList.isEmpty()) {
                    Toast.makeText(this, "No hi ha dades per mostrar", Toast.LENGTH_SHORT).show();
                }

                // Generate the chart
                generateChart();
            }
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
    /**
     * CustomMarkerView
     * classe auxiliar per a visualitzar la data d'un node en fer-ne click
     */
    public class CustomMarkerView extends MarkerView {

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
            // Check if the index is valid
            if (index < 0 || index >= resultatsDataList.size()) {
                return ""; // Return an empty string if the index is invalid
            }
            // Adjust this line based on your chart's data order
            //return resultatsDataList.get(resultatsDataList.size() - 1 - index).getDate(); // For reverse order
            return resultatsDataList.get(index).getDate(); // For original order
        }

        @Override
        public MPPointF getOffset() {
            int markerWidth = getWidth();
            int markerHeight = getHeight();

            // Center the marker
            float offsetX = -markerWidth / 1.45f; // Shift marker to the left
            float offsetY = markerHeight / 4; // Shift marker down

            return MPPointF.getInstance(offsetX, offsetY); // Return an MPPointF instance
        }
    }

    private void sortResults(String criteria){
        switch (criteria){
            case "data":
                if (isDataAscending) {
                    resultatsDataList.sort((r1, r2) -> r1.getDate().compareTo(r2.getDate()));
                } else{
                    resultatsDataList.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
                }
                isDataAscending = !isDataAscending;
                break;
            case "vies":
                if (isViesAscending) {
                    resultatsDataList.sort((r1, r2) -> r1.getVies().compareTo(r2.getVies()));
                } else{
                    resultatsDataList.sort((r1, r2) -> r2.getVies().compareTo(r1.getVies()));
                }
                isViesAscending = !isViesAscending;
                break;
            case "metres":
                if (isMetresAscending) {
                    resultatsDataList.sort((r1, r2) -> r1.getMetres().compareTo(r2.getMetres()));
                } else{
                    resultatsDataList.sort((r1, r2) -> r2.getMetres().compareTo(r1.getMetres()));
                }
                isMetresAscending = !isMetresAscending;
                break;
            case "punts":
                if (isPuntsAscending) {
                    resultatsDataList.sort((r1, r2) -> r1.getPuntuacio().compareTo(r2.getPuntuacio()));
                }else {
                    resultatsDataList.sort((r1, r2) -> r2.getPuntuacio().compareTo(r1.getPuntuacio()));
                }
                isPuntsAscending= !isPuntsAscending;
                break;
            case "mitjana":
                if (isMitjanaAscending) {
                    resultatsDataList.sort((r1, r2) -> r1.getMitjana().compareTo(r2.getMitjana()));
                }else {
                    resultatsDataList.sort((r1, r2) -> r2.getMitjana().compareTo(r1.getMitjana()));
                }
                isMitjanaAscending= !isMitjanaAscending;
                break;
        }
        resultatsDataAdapter.notifyDataSetChanged();
    }

    private boolean isDarkTheme(){
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    private void resetTextColorBtnPeriodes(){
        int textColor;

        if(isDarkTheme()){
            textColor= ContextCompat.getColor(this, R.color.white);
        }
        else{
            textColor= ContextCompat.getColor(this, R.color.black);
        }
        btnSetmanal.setTextColor(textColor);
        btnMensual.setTextColor(textColor);
        btnTrimestral.setTextColor(textColor);
        btnSemestral.setTextColor(textColor);
        btnAnual.setTextColor(textColor);
        btnTotal.setTextColor(textColor);
    }

    private void updateTextColorBtnPeriodes(int initialDateOffset){
        resetTextColorBtnPeriodes();
        switch (initialDateOffset){
            case -7:
                btnSetmanal.setTextColor(ContextCompat.getColor(this, R.color.orange));
                break;
            case -30:
                btnMensual.setTextColor(ContextCompat.getColor(this, R.color.orange));
                break;
            case -90:
                btnTrimestral.setTextColor(ContextCompat.getColor(this, R.color.orange));
                break;
            case -180:
                btnSemestral.setTextColor(ContextCompat.getColor(this, R.color.orange));
                break;
            case -365:
                btnAnual.setTextColor(ContextCompat.getColor(this, R.color.orange));
                break;
            case -10000:
                btnTotal.setTextColor(ContextCompat.getColor(this, R.color.orange));
                break;
            default:
                resetTextColorBtnPeriodes();
                break;
        }
    }


}
