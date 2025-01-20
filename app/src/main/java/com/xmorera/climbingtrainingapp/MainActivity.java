package com.xmorera.climbingtrainingapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.HashMap;
import java.util.List;
/**
 * Classe principal de l'aplicació Climbing Training App.
 * l'objectiu d'aquesta aplicació es registrar l'entrenament en rocòdrom donant una puntuació
 * a les vies realitzades en funció de la seva dificultat i llargada
 *
 * @author Xavier Morera
 * */
public class MainActivity extends AppCompatActivity  {

    TextView dateTextView; //mostrar la data i mostrar la via seleccionada
    Button diaAnterior, diaPosterior;
    Button btnAvui;
    Button btnResultats;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    LinearLayout entradaLayout;
    LinearLayout descansosLayout;
    Spinner rocodromSpinner;
    Button btnAfegirRocoZona;
    HashMap<String, Integer> rocodromsHashMap;
    GridLayout zonesGrid;
    Spinner descansosSpinner;
    Button btnIV, btnIVPlus,btnV, btnVPlus;
    Button btn6a, btn6aPlus,btn6b, btn6bPlus,btn6c, btn6cPlus;
    Button btn7a, btn7aPlus,btn7b, btn7bPlus,btn7c, btn7cPlus;
    Button btn8a, btn8aPlus,btn8b, btn8bPlus,btn8c, btn8cPlus;
    CheckBox chkIntent;

    DatabaseHelper databaseHelper;//auxiliar de la base de dades

    RecyclerView recyclerView;
    ClimbingDataAdapter climbingDataAdapter;
    List<ClimbingData> climbingDataList;

    Puntuacio puntuacio;

    TextView viesDiaTextView;
    TextView metresDiaTextView;
    TextView mitjanaDiaTextView;
    TextView puntuacioDiaTextView;

    // variables per al càlcul diari
    int vies;
    double metres;
    double puntuacioDia;

    //variables per a entrar les dades a la base de dades
    int idZona;
    String nomZona;
    String dificultat;
    int alturaZona;
    int esCorda;
    int ifIntent;
    int descansos;
    int rocodromZona;

    String avui;

    //llista per guardar els botons de zona que es generen en temps d'execució
    List<Button> botonsZona = new ArrayList<>();



    /**
     * onCreate
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
        zonesGrid = findViewById(R.id.zonesGrid);

        rocodromSpinner = findViewById(R.id.rocodromSpinner);

        btnAfegirRocoZona = findViewById(R.id.btnAfegirRocoZona);
        btnAfegirRocoZona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startActivity(new Intent(MainActivity.this, AfegirRocoZona.class));
                Toast.makeText(MainActivity.this, "Afegir/Editar Roco/Zones", Toast.LENGTH_SHORT).show();

            }
        });

        entradaLayout = findViewById(R.id.entradaLayout);
        entradaLayout.setVisibility(View.GONE);
        descansosLayout = findViewById(R.id.descansosLayout);
        descansosLayout.setVisibility(View.GONE);

        descansosSpinner = findViewById(R.id.descansosSpinner);
        String[] descansos = {"0", "1", "2", "3", "4", "5"};
        ArrayAdapter<String> adapterDescansos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, descansos);
        adapterDescansos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        descansosSpinner.setAdapter(adapterDescansos);
        descansosSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.this.descansos = Integer.parseInt(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //mapeig de botons
        {
            btnIV = findViewById(R.id.btnIV);
            btnIVPlus = findViewById(R.id.btnIVPlus);
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
            setDificultatListener(btnIVPlus);
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


            //listeners pels botons de dificultat per entrar les dades

        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        climbingDataList = new ArrayList<>();
        climbingDataAdapter = new ClimbingDataAdapter(this, climbingDataList);
        recyclerView.setAdapter(climbingDataAdapter);

        databaseHelper = new DatabaseHelper(this);

        viesDiaTextView = findViewById(R.id.viesDiaTextView);
        metresDiaTextView = findViewById(R.id.metresDiaTextView);
        mitjanaDiaTextView = findViewById(R.id.mitjanaDiaTextView);
        puntuacioDiaTextView = findViewById(R.id.puntuacioDiaTextView);

        puntuacio = new Puntuacio();

        loadDayData(); //mostrar totes les dades de la bd
    }

    /**
     * visibilitatGraus
     * amaga o mostra el panell de dades introduïdes manualment
     * @param 'View.GONE
     * @param 'View.VISIBLE
     * */
    private void visibilitatGraus(int visibilitat) {
        entradaLayout.setVisibility(visibilitat);
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
        } else {
            // If no date was passed, set the current date
            updateDateTextView();
        }
        //carrega Spinner
        loadSpinnerRocodroms();
        loadDayData(); // Load data for the current date

    }

    /**
     * loadSpinnerRocodroms
     * Mostra els diferents rocodroms que tenim a la base de dades
     * */
    private void loadSpinnerRocodroms() {
        rocodromsHashMap = new HashMap<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Cursor cursor = databaseHelper.getAllRocodroms();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_ROCO"));
                String nom = cursor.getString(cursor.getColumnIndexOrThrow("NOM_ROCO"))+", "+cursor.getString(cursor.getColumnIndexOrThrow("POBLACIO"));

                rocodromsHashMap.put(nom, id);
            }
            cursor.close();
        }

// Adaptador per l'spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rocodromsHashMap.keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rocodromSpinner.setAdapter(adapter);

// Últim rocòdrom seleccionat
        SharedPreferences prefs = getSharedPreferences("Rocodrom", MODE_PRIVATE);
        int ultimRocodrom = prefs.getInt("ultimRocodrom", 0);
        rocodromSpinner.setSelection(ultimRocodrom);

// Listener per gestionar les opcions
        rocodromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicio, long id) {
                //guardar l'últim rocodrom seleccionat
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("ultimRocodrom", posicio);
                editor.apply();

                //generar les zones del rocòdrom seleccionat
                DatabaseHelper databaseHelper2 = new DatabaseHelper(MainActivity.this);
                Cursor cursor2 = databaseHelper2.getZonesByRocodrom(rocodromsHashMap.get(adapterView.getItemAtPosition(posicio))); //recuperem les zones del rocodrom seleccionat per id guardada a posicio
                zonesGrid.removeAllViews(); //buidar GridLayout zonesGrid
                if (cursor2 != null) {
                    if (cursor2.moveToFirst()) {
                        do{
                            //recuperem les dades de la zona
                            idZona = cursor2.getInt(cursor2.getColumnIndexOrThrow("ID_ZONA"));
                            nomZona = cursor2.getString(cursor2.getColumnIndexOrThrow("NOM_ZONA"));
                            alturaZona = cursor2.getInt(cursor2.getColumnIndexOrThrow("ALTURA_ZONA"));
                            esCorda = cursor2.getInt(cursor2.getColumnIndexOrThrow("ZONA_CORDA"));
                            rocodromZona = cursor2.getInt(cursor2.getColumnIndexOrThrow("ID_ROCO_FK"));

                            Bundle infoBoto = new Bundle();
                            infoBoto.putInt("idZona", idZona);
                            infoBoto.putString("nomZona", nomZona);
                            infoBoto.putInt("alturaZona", alturaZona);
                            infoBoto.putInt("esCorda", esCorda);
                            infoBoto.putInt("rocodromZona", rocodromZona);

                            //generar botons de zona
                            Button btnZona = new Button(MainActivity.this);
                            btnZona.setText(nomZona);
                            btnZona.setTag(infoBoto);
                            botonsZona.add(btnZona);

                            //listener del botó de zona
                            btnZona.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //restaura el color de tots els botons de zona
                                    resetBotonsZona();
                                    //resetejar els descansos
                                    descansos =0;
                                    descansosSpinner.setSelection(0);

                                    //canvi de color pel boto seleccionat
                                    view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.orange));

                                    Bundle infoBoto = (Bundle) view.getTag();
                                    idZona = infoBoto.getInt("idZona");
                                    nomZona = infoBoto.getString("nomZona");
                                    alturaZona = infoBoto.getInt("alturaZona");
                                    esCorda = infoBoto.getInt("esCorda");
                                    rocodromZona = infoBoto.getInt("rocodromZona");
                                    entradaLayout.setVisibility(View.VISIBLE);

                                    if (esCorda == 1) {
                                        descansosLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        descansosLayout.setVisibility(View.GONE);
                                    }
                                }
                            });
                            zonesGrid.addView(btnZona);

                        }
                        while (cursor2.moveToNext()) ;
                    }else {
                        //generar el Layout per inserir zones
                        zonesGrid.removeAllViews();
                        // Crear un TextView dinàmicament
                        TextView textView = new TextView(MainActivity.this);
                        GridLayout.LayoutParams textParams = new GridLayout.LayoutParams();
                        textParams.setGravity(Gravity.CENTER); // Centrar el TextView
                        textParams.setMargins(20, 0, 0, 0); // Margen superior para separar del TextView
                        textView.setLayoutParams(textParams);
                        textView.setTextSize(18);
                        textView.setText("No hi ha zones definides \nper aquest rocòdrom");
                        textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.orange)); // Asegúrate de que el color esté definido

                        // Crear un Button dinámicamente
                        Button btnAfegirZona = new Button(MainActivity.this);
                        GridLayout.LayoutParams buttonParams = new GridLayout.LayoutParams();
                        buttonParams.setGravity(Gravity.CENTER); // Centrar el Button
                        buttonParams.setMargins(30, 8, 0, 0); // Margen superior para separar del TextView
                        btnAfegirZona.setLayoutParams(buttonParams);
                        btnAfegirZona.setText("Afegir");

                        // Establir l'esdeveniment de clic per al botó
                        btnAfegirZona.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Gestionar el clic del botó
                                Toast.makeText(MainActivity.this, "Afegir Zona", Toast.LENGTH_SHORT).show();
                            }
                        });
                        zonesGrid.addView(textView);
                        zonesGrid.addView(btnAfegirZona);
                    }
                }
                cursor2.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Optional: Handle the case when no item is selected
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        firstTime = false;
        entradaLayout.setVisibility(View.GONE);
    }

    /**
     * onCreateOptionsMenu
     *
     * mostra el menú de la part superiot dreta (tres puntets)
     * */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    /**
//     * onOptionsItemSelected
//     * gestió dels botons de menu
//     * */
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        // si hi ha més elements s'ha de fer amb switch
//        if (id == R.id.menu_settings) {
//            startActivity(new Intent(this, Preferencies.class));
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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
                dificultat = button.getText().toString();
                if (chkIntent.isChecked()) {
                    ifIntent = 1;
                } else {
                    ifIntent = 0;
                }
/*
                String message =
                        "Rocodrom: " + rocodromSpinner.getSelectedItem().toString() +
                        "\nData: "+dateTextView.getText().toString()+
                        "\nidZona: "+idZona+
                        "\nZona: "+nomZona+
                        "\nAltura:"+alturaZona+
                        "\nDescansos: "+ descansos +
                        "\nÉs intent: "+ifIntent+
                        "\nDificultat: " + dificultat;

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Dades a introduir a la BD")
                        .setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                AlertDialog dialog = builder.create();
                dialog.show();
*/

                insertData(dateTextView.getText().toString(), dificultat, idZona, ifIntent, descansos);
                resetInput();
            }
        });
    }




    /**
     * insertData
     *
     * insereix les dades a la base de dades
     *
     * @param -String date, String via, int zona, int intent
     * */
    private void insertData(String date, String dificultat, int zona, int ifIntent, int descansos){
        boolean insertSuccess = databaseHelper.insertDataCD(date, dificultat, zona, ifIntent, descansos);
        if (insertSuccess) {
            //Toast.makeText(this, "Via guardada correctament", Toast.LENGTH_SHORT).show();
            loadDayData();
        } else {
            Toast.makeText(MainActivity.this, "Error en guardar la via", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * resetInput
     * resteja l'entrada de dades
     */
    private void resetInput() {
        //reset pantalla introducció de dades
        chkIntent.setChecked(false);
        ifIntent = 0;
        descansos = 0;
        visibilitatGraus(View.GONE);
        //restaura el color de tots els botons de zona
        resetBotonsZona();
    }

    private void resetBotonsZona() {
        for (Button button : botonsZona) {
            //selecció del color de fons del botó del tema
            TypedArray colorFonsBotons = obtainStyledAttributes(R.style.Base_Theme_ClimbingTrainingApp, new int[]{android.R.attr.colorButtonNormal});
            int colorFons = colorFonsBotons.getColor(0, 0);
            colorFonsBotons.recycle();//alliberem recursos
            button.setBackgroundColor(colorFons);
        }
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
        String nomCurtRocodrom="";

        Cursor cursor = databaseHelper.getDayDataCD(dateTextView.getText().toString());
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id_cd = cursor.getString(cursor.getColumnIndexOrThrow("ID_CD"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
                String dificultat = cursor.getString(cursor.getColumnIndexOrThrow("DIFICULTAT"));
                idZona = cursor.getInt(cursor.getColumnIndexOrThrow("ID_ZONA_FK"));
                int ifIntent = cursor.getInt(cursor.getColumnIndexOrThrow("IFINTENT"));
                int descansos = cursor.getInt(cursor.getColumnIndexOrThrow("DESCANSOS"));

                //recuperem les dades de la zona
                Cursor cursor2 = databaseHelper.getZonaById(idZona);
                if (cursor2 != null) {
                    if (cursor2.moveToFirst()) {
                        nomZona = cursor2.getString(cursor2.getColumnIndexOrThrow("NOM_ZONA"));
                        alturaZona = cursor2.getInt(cursor2.getColumnIndexOrThrow("ALTURA_ZONA"));
                        rocodromZona = cursor2.getInt(cursor2.getColumnIndexOrThrow("ID_ROCO_FK"));
                    }
                    cursor2.close();
                }
                //recuperem el nom reduit del rocodrom de la zona
                Cursor cursor3 = databaseHelper.getRocodromById(rocodromZona);
                if (cursor3 != null) {
                    if (cursor3.moveToFirst()) {
                        nomCurtRocodrom = cursor3.getString(cursor3.getColumnIndexOrThrow("NOM_ROCO_REDUIT"));
                    }
                    cursor3.close();
                } else { nomCurtRocodrom = ""; }

                nomZona = nomZona + " (" + nomCurtRocodrom + ")";
                double puntsVia = puntuacio.getPunts(dificultat);
                double metresVia = alturaZona; //convertim el metres a Double per si hi ha penalitzacions
                if(ifIntent == 1){ //en el cas d'un inent apliquem el coeficient de dificultat i contem la mitat de metres de la zona
                    puntsVia /= puntuacio.getIfIntent();// veure Puntuacio.java
                    metresVia *= puntuacio.getPenalitzacioMetres();
                } else if (descansos > 0){
                    puntsVia /= puntuacio.getPenalitzacioDescansos(descansos);
                    ifIntent=1; // si hi ha descansos ho indicarem al tag intent/descansos de l'item
                }
                climbingDataList.add(new ClimbingData( id_cd, date, dificultat, nomZona, ifIntent, String.format("%.1f", puntsVia)));
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
        mitjanaDiaTextView.setText(Utilitats.mitjanaGrau(puntuacioDia/vies));
        // controlem si la data que es mostra és l'actual. En cas que no ho sigui canviem el color del botó per a informar i evitar entrades errònies
        if (dateTextView.getText().toString().equals(avui)) {
            try {
                calendar.setTime(dateFormat.parse(avui));
            } catch (ParseException e){
                e.printStackTrace();
            }
            dateTextView.setTextColor(ContextCompat.getColor(this, R.color.gray));
        } else {
            dateTextView.setTextColor(ContextCompat.getColor(this, R.color.orange));
            btnAvui.setVisibility(View.VISIBLE);
        }

    }


}