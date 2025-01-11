package com.xmorera.climbingtrainingapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xmorera.climbingtrainingapp.climbingData.ClimbingData;
import com.xmorera.climbingtrainingapp.climbingData.ClimbingDataAdapter;
import com.xmorera.climbingtrainingapp.climbingData.Puntuacio;
import com.xmorera.climbingtrainingapp.resultats.Resultats;
import com.xmorera.climbingtrainingapp.utils.DatabaseHelper;
import com.xmorera.climbingtrainingapp.utils.Preferencies;
import com.xmorera.climbingtrainingapp.utils.Utilitats;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
/**
 * Classe principal de l'aplicació Climbing Training App.
 * l'objectiu d'aquesta aplicació es registrar l'entrenament en rocòdrom donant una puntuació
 * a les vies realitzades en funció de la seva dificultat i llargada
 *
 * @author Xavier Morera
 * */
public class MainActivity extends AppCompatActivity  {
    LinearLayout dadesManualsLayout;
    TextView dateTextView, dificultatTextView; //mostrar la data i mostrar la via seleccionada
    Button btnEntradaManual;
    Button diaAnterior, diaPosterior;
    Button btnAvui;
    Button btnResultats;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Button btnIV, btnIVPlus,btnV, btnVPlus;
    Button btn6a, btn6aPlus,btn6b, btn6bPlus,btn6c, btn6cPlus;
    Button btn7a, btn7aPlus,btn7b, btn7bPlus,btn7c, btn7cPlus;
    Button btn8a, btn8aPlus,btn8b, btn8bPlus,btn8c, btn8cPlus;
    CheckBox chkIntent;
    Button btnAutos, btnCorda, btnShinyWall, btnBloc;//tipus de via
    //variables per a entrar les dades a la base de dades
    String dificultat;
    String zona;
    int ifIntent = 0;
    DatabaseHelper databaseHelper;//auxiliar de la base de dades

    RecyclerView recyclerView;
    ClimbingDataAdapter climbingDataAdapter;
    List<ClimbingData> climbingDataList;

    SharedPreferences preferencesGZero;
    Puntuacio puntuacio;

    TextView viesDiaTextView;
    TextView metresDiaTextView;
    TextView mitjanaDiaTextView;
    TextView puntuacioDiaTextView;
    int vies;
    double metres;
    double puntuacioDia;

    String avui;


    /**
     * onCretate
     *
     * mapeig dels elements de la pantalla a variables,
     * inicialitza els listeners dels botons de dificultat, zona i intent,
     * insereix la data actual al TextView de la data
     * implementa el listener de la data per selecionar una data qualsevol
     * carrega i mostra en un recyclerView les dades de la base de dades pel dia que indica el TextView
     * */
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dateTextView = findViewById(R.id.dateTextView);
        //data actual, la posem a l'inici de l'aplicació
        updateDateTextView();
        avui = dateTextView.getText().toString();

        // selecció d'una data mitjançant el calendari en pantalla
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        diaAnterior = findViewById(R.id.diaAnterior);
        diaAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                updateDateTextView();
                loadDayData();
            }
        });
        diaPosterior = findViewById(R.id.diaPosterior);
        diaPosterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                updateDateTextView();
                loadDayData();
            }
        });

        btnAvui = findViewById(R.id.btnAvui);
        btnAvui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTextView.setText(avui);
                loadDayData();
            }
        });

        btnResultats = findViewById(R.id.btnResultats);
        btnResultats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Resultats.class));
            }
        });

        dificultatTextView = findViewById(R.id.dificultatTextView);

        btnEntradaManual = findViewById(R.id.btnEntradaManual);
        dadesManualsLayout = findViewById(R.id.dadesManualsLayout);
        visibilitatDadesManuals(View.GONE);
        btnEntradaManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetInput();
                if (dadesManualsLayout.getVisibility() == View.GONE) {
                    visibilitatDadesManuals(View.VISIBLE);
                } else {
                    visibilitatDadesManuals(View.GONE);
                }
            }
        });
        //mapeig de botons
        {
            btnIV = findViewById(R.id.btnIV);
            //btnIVPlus = findViewById(R.id.btnIVPlus);
            btnV = findViewById(R.id.btnV);
            btnVPlus = findViewById(R.id.btnVPlus);
            btn6a = findViewById(R.id.btn6a);
            btn6aPlus = findViewById(R.id.btn6aPlus);
            btn6b = findViewById(R.id.btn6b);
            btn6bPlus = findViewById(R.id.btn6bPlus);
            btn6c = findViewById(R.id.btn6c);
            btn6cPlus = findViewById(R.id.btn6cPlus);
            btn7a = findViewById(R.id.btn7a);
            btn7aPlus = findViewById(R.id.btn7aPlus);
            btn7b = findViewById(R.id.btn7b);
            btn7bPlus = findViewById(R.id.btn7bPlus);
            btn7c = findViewById(R.id.btn7c);
            btn7cPlus = findViewById(R.id.btn7cPlus);
            btn8a = findViewById(R.id.btn8a);
            btn8aPlus = findViewById(R.id.btn8aPlus);
            btn8b = findViewById(R.id.btn8b);
            btn8bPlus = findViewById(R.id.btn8bPlus);
            btn8c = findViewById(R.id.btn8c);
            btn8cPlus = findViewById(R.id.btn8cPlus);

            //establiment dels listeners pels botons de via
            setDificultatListener(btnIV);
            //setDificultatListener(btnIVPlus);
            setDificultatListener(btnV);
            setDificultatListener(btnVPlus);
            setDificultatListener(btn6a);
            setDificultatListener(btn6aPlus);
            setDificultatListener(btn6b);
            setDificultatListener(btn6bPlus);
            setDificultatListener(btn6c);
            setDificultatListener(btn6cPlus);
            setDificultatListener(btn7a);
            setDificultatListener(btn7aPlus);
            setDificultatListener(btn7b);
            setDificultatListener(btn7bPlus);
            setDificultatListener(btn7c);
            setDificultatListener(btn7cPlus);
            setDificultatListener(btn8a);
            setDificultatListener(btn8aPlus);
            setDificultatListener(btn8b);
            setDificultatListener(btn8bPlus);
            setDificultatListener(btn8c);
            setDificultatListener(btn8cPlus);

            chkIntent = findViewById(R.id.chkIntent);

            btnAutos = findViewById(R.id.btnAutos);
            btnCorda = findViewById(R.id.btnCorda);
            btnShinyWall = findViewById(R.id.btnShinyWall);
            btnBloc = findViewById(R.id.btnBloc);
            //listeners pels botons de zona
            setZoneListener(btnAutos);
            setZoneListener(btnCorda);
            setZoneListener(btnShinyWall);
            setZoneListener(btnBloc);
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        climbingDataList = new ArrayList<>();
        climbingDataAdapter = new ClimbingDataAdapter(this, climbingDataList);
        recyclerView.setAdapter(climbingDataAdapter);

        databaseHelper = new DatabaseHelper(this);

        //inicialitzar les sharedPreferences
        preferencesGZero = getSharedPreferences("preferenciesGZero", MODE_PRIVATE);

        viesDiaTextView = findViewById(R.id.viesDiaTextView);
        metresDiaTextView = findViewById(R.id.metresDiaTextView);
        mitjanaDiaTextView = findViewById(R.id.mitjanaDiaTextView);
        puntuacioDiaTextView = findViewById(R.id.puntuacioDiaTextView);

        puntuacio = new Puntuacio();

        loadDayData(); //mostrar totes les dades de la bd
    }

    /**
     * visibilitatDadesManuals
     * amaga o mostra el panell de dades introduïdes manualment
     * @param 'View.GONE
     * @param 'View.VISIBLE
     * */
    private void visibilitatDadesManuals(int visibilitat) {
        dadesManualsLayout.setVisibility(visibilitat);
        dificultatTextView.setVisibility(visibilitat);
    }

    boolean firstTime = true;// variable per controlar que quan es torna a l'inici es mostri la data actual
    /**
     * onResume
     *
     * recarrega les dades del  dia actual en el recyclerView
     * */
    @Override
    protected void onResume() {
        super.onResume();
        String selectedDate = getIntent().getStringExtra("selectedDate");
        if (selectedDate != null && firstTime) {
            // If a date was passed, set it to the dateTextView
            dateTextView.setText(selectedDate);
            //controlar que no es puguin entrar dades per no provocar un error

        } else {
            // If no date was passed, set the current date
            updateDateTextView();

        }
        loadDayData(); // Load data for the current date
    }

    @Override
    protected void onPause() {
        super.onPause();
        firstTime = false;
    }

    /**
     * onCreateOptionsMenu
     *
     * mostra el menú de la part superiot dreta (tres puntets)
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * onOptionsItemSelected
     * gestió dels botons de menu
     * */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        // si hi ha més elements s'ha de fer amb switch
        if (id == R.id.menu_settings) {
            startActivity(new Intent(this, Preferencies.class));
            return true;
        }
        /*if (id == R.id.menu_resultats) {
            startActivity(new Intent(this, Resultats.class));
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    /**
     * updateDateTextView
     * actualització a la data actual en TextView de la data
     * */
    private void updateDateTextView() {
        String currentDate = dateFormat.format(calendar.getTime());
        dateTextView.setText(currentDate);
    }

    /**
     * showDatePicker
     * mostra el calendari per seleccionar una data i la insereix en el TextView de la data
     * */
    private void showDatePicker(){
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateTextView(); // Update TextView with new date
                        loadDayData(); // Reload data for the selected date
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    /**
     * seDificultatListener
     * listener dels botons de dificultat en la seleccio de via manual
     * quan es clica un botó de dificultat de la via insereix el seu text a la TextView de la via
     * */
    private void setDificultatListener(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dificultatTextView.setText(button.getText());
            }
        });
    }

    /**
     * setZoneListener
     * listener dels botons de zona en la selecció manual
     * quan es clica in botó de zona comprova que hi hagi una via seleccionada i
     * formateja la data per fer la inserció a la base de dades
     * amaga el panell de selecció manual
     * */
    private void setZoneListener(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dificultatTextView.getText().toString().equals("")){
                    //si no s'ha introduit una via no fer res
                    Toast.makeText(MainActivity.this, "Seleccionar dificultat", Toast.LENGTH_SHORT).show();
                }else {
                    //dades per guardar a sqlite
                    String date = dateTextView.getText().toString();
                    dificultat = dificultatTextView.getText().toString();
                    zona = button.getText().toString();
                    if (chkIntent.isChecked()) {
                        ifIntent = 1;
                    }
                    //introducció de dades a la base de dades
                    insertData(date, dificultat, zona, ifIntent);
                    resetInput();
                    visibilitatDadesManuals(View.GONE);
                }
            }
        });
    }

    /**
     * insertData
     *
     * insereix les dades a la base de dades
     *
     * @param -String date, String via, String zona, int intent
     * */
    private void insertData(String date, String dificultat, String zona, int ifIntent){
        boolean insertSuccess = databaseHelper.insertData(date, dificultat, zona, ifIntent);
        if (insertSuccess) {
            Toast.makeText(this, "Via guardada correctament", Toast.LENGTH_SHORT).show();
            loadDayData();
        } else {
            Toast.makeText(MainActivity.this, "Error en guardar la via", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * resetInput
     *
     * resteja l'elecció de la via i el checkbox de l'intent
     */
    private void resetInput() {
        //reset pantalla introducció de dades
        dificultatTextView.setText("");
        chkIntent.setChecked(false);
        ifIntent = 0;
    }

    /**
     * loadDayData
     *
     * carrega les dades de la base de dades a la llista climbingDataList i notifica a l'adaptador
     */
    public void loadDayData(){
        vies = 0;
        metres = 0.0;
        puntuacioDia = 0.0;
        climbingDataList.clear();
        Cursor cursor = databaseHelper.getDayData(dateTextView.getText().toString());
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("ID"));
                //Log.d("IDData", id);
                String date = cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
                String dificultat = cursor.getString(cursor.getColumnIndexOrThrow("DIFICULTAT"));
                String zona = cursor.getString(cursor.getColumnIndexOrThrow("ZONA"));
                int ifIntent = cursor.getInt(cursor.getColumnIndexOrThrow("IFINTENT"));
                Log.d("dificultat",dificultat);
                // activació de les preferencies,
                // ja si l'activity Preferencies no s'ha obert mai les preferencies no estan disponibles
                if (preferencesGZero.getString(zona, "error").equals("error")) {
                    startActivity(new Intent(this, Preferencies.class));
                }
                //Log.d("dificultat",preferencesGZero.getString(dificultat, "error"));
                double puntsVia = puntuacio.getPunts(dificultat);
                //double puntsVia = Double.parseDouble(preferencesGZero.getString(dificultat, "0,0").replace(",","."));
                double metresVia = Double.parseDouble(preferencesGZero.getString(zona, "0,0").replace(",","."));
                if(ifIntent == 1){ //en el cas d'un inent apliquem el coeficient de dificultat i contem la mitat de metres de la zona
                    puntsVia *= puntuacio.getIntent();
                    //puntsVia *= Double.parseDouble(preferencesGZero.getString("IntentCoeficient", "0,0").replace(",","."));
                    metresVia *= 0.5;
                }
                climbingDataList.add(new ClimbingData( id, date, dificultat, zona, ifIntent, String.format("%.1f", puntsVia)));
                vies += 1;
                metres += metresVia;
                puntuacioDia += puntsVia;
            }
            cursor.close();
        }
        climbingDataAdapter.notifyDataSetChanged();//notificar a l'adaptador que hi ha hagut canvis
        puntuacioDiaTextView.setText(String.format("%.1f", puntuacioDia).replace(".", ","));
        viesDiaTextView.setText(String.valueOf(vies));
        metresDiaTextView.setText(String.valueOf(metres));
        mitjanaDiaTextView.setText(Utilitats.mitjanaGrau(puntuacioDia/vies, preferencesGZero));
        // controlem si la data que es mostra és l'actual. En cas que no ho sigui canviem el color del botó per a informar i evitar entrades errònies
        if (dateTextView.getText().toString().equals(avui)) {
            btnAvui.setVisibility(View.GONE);
            try {
                calendar.setTime(dateFormat.parse(avui));
            } catch (ParseException e){
                e.printStackTrace();
            }
            dateTextView.setTextColor(ContextCompat.getColor(this, R.color.gray));
        } else {
            dateTextView.setTextColor(ContextCompat.getColor(this, R.color.red));
            btnAvui.setVisibility(View.VISIBLE);
        }
    }


}