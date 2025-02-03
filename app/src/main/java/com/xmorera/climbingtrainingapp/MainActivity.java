package com.xmorera.climbingtrainingapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xmorera.climbingtrainingapp.RocodromsZones.Rocodroms;
import com.xmorera.climbingtrainingapp.climbingData.ClimbingData;
import com.xmorera.climbingtrainingapp.climbingData.ClimbingDataAdapter;

import com.xmorera.climbingtrainingapp.climbingData.Puntuacio;
import com.xmorera.climbingtrainingapp.resultats.Resultats;
import com.xmorera.climbingtrainingapp.utils.DatabaseHelper;
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

    // Elements de la interfície d'usuari
    private TextView dateTextView; //mostrar la data i mostrar la via seleccionada
    private Button diaAnterior, diaPosterior, btnAvui, btnResultats;
    private Spinner rocodromSpinner, descansosSpinner;
    private GridLayout zonesGrid;
    private LinearLayout entradaLayout, descansosLayout;
    private RecyclerView recyclerView;
    private CheckBox chkIntent;

    private MenuItem menu_rocodroms;

    // Variables per gestionar la data
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private String avui;

    // variables per a gestionar la selecció de zones i dificultats
    private HashMap<String, Integer> rocodromsHashMap;
    private List<Button> botonsZona = new ArrayList<>();//llista per guardar els botons de zona que es generen en temps d'execució

    // variables per gestionar les dades de l'escalaa
    private DatabaseHelper databaseHelper;
    private ClimbingDataAdapter climbingDataAdapter;
    private List<ClimbingData> climbingDataList;
    private Puntuacio puntuacio;

    // variables per al càlcul diari
    int viesDia;
    double metresDia;
    double puntuacioDia;

    //variables per a entrar les dades a la base de dades
    private int idZona, alturaZona, esCorda, ifIntent, descansos, rocodromZona;
    private String nomZona, dificultat;

    // elements per mostrar els resultat diaris
    private TextView viesDiaTextView, metresDiaTextView, mitjanaDiaTextView, puntuacioDiaTextView;

    // variable per controlar que quan es torna a l'inici es mostri la data actual
    boolean firstTime = true;

    /**
     * onCreate
     *
     * Mapeig dels elements de la pantalla a variables,
     * inicialitza els listeners dels botons de dificultat, zona i intent,
     * insereix la data actual al TextView de la data,
     * implementa el listener de la data per seleccionar una data qualsevol,
     * carrega i mostra en un recyclerView les dades de la base de dades pel dia que indica el TextView.
     */
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Configuració de la interfície d'usuari
        configurarUI();

        // Inicialització de la data actual
        inicialitzarDataActual();

        // Configuració dels listeners
        configurarListeners();

        // Inicialització del RecyclerView
        inicialitzarRecyclerView();

        // Carregar les dades del dia actual
        carregarDadesDia();
    }

    /**
     * Configura la interfície d'usuari mapejant els elements a variables.
     */
    private void configurarUI() {
        dateTextView = findViewById(R.id.dateTextView);
        diaAnterior = findViewById(R.id.diaAnterior);
        diaPosterior = findViewById(R.id.diaPosterior);
        btnAvui = findViewById(R.id.btnAvui);
        btnResultats = findViewById(R.id.btnResultats);
        zonesGrid = findViewById(R.id.zonesGrid);
        rocodromSpinner = findViewById(R.id.rocodromSpinner);
        entradaLayout = findViewById(R.id.entradaLayout);
        descansosLayout = findViewById(R.id.descansosLayout);
        descansosSpinner = findViewById(R.id.descansosSpinner);
        recyclerView = findViewById(R.id.recyclerView);
        viesDiaTextView = findViewById(R.id.viesDiaTextView);
        metresDiaTextView = findViewById(R.id.metresDiaTextView);
        mitjanaDiaTextView = findViewById(R.id.mitjanaDiaTextView);
        puntuacioDiaTextView = findViewById(R.id.puntuacioDiaTextView);
        chkIntent = findViewById(R.id.chkIntent);

        // Configuració de la visibilitat dels layouts
        entradaLayout.setVisibility(View.GONE);
        descansosLayout.setVisibility(View.GONE);

        // mapeig dels menús
        menu_rocodroms = findViewById(R.id.menu_rocodroms);

    }

    /**
     * Inicialitza la data actual i actualitza el TextView corresponent.
     */
    private void inicialitzarDataActual() {
        updateDateTextView();
        avui = dateTextView.getText().toString();
    }

    /**
     * Configura els listeners per als botons i altres elements interactius.
     */
    private void configurarListeners() {
        dateTextView.setOnClickListener(v -> showDatePicker());

        diaAnterior.setOnClickListener(view -> {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            actualitzarData();
        });

        diaPosterior.setOnClickListener(view -> {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            actualitzarData();
        });

        btnAvui.setOnClickListener(view -> {
            dateTextView.setText(avui);
            carregarDadesDia();
        });

        btnResultats.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, Resultats.class))
        );

        configurarSpinnerDescansos();
        configurarBotonsDificultat();
    }

    /**
     * Actualitza el TextView de la data i carrega les dades del dia.
     */
    private void actualitzarData() {
        updateDateTextView();
        carregarDadesDia();
    }

    /**
     * Configura el Spinner per seleccionar el nombre de descansos.
     */
    private void configurarSpinnerDescansos() {
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
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    /**
     * Configura els botons de dificultat.
     */
    private void configurarBotonsDificultat() {
        // Array de IDs dels botons de dificultat
        int[] buttonIds = {
                R.id.btnIV, R.id.btnIVPlus,
                R.id.btnV, R.id.btnVPlus,
                R.id.btn6a, R.id.btn6aPlus,
                R.id.btn6b, R.id.btn6bPlus,
                R.id.btn6c, R.id.btn6cPlus,
                R.id.btn7a, R.id.btn7aPlus,
                R.id.btn7b, R.id.btn7bPlus,
                R.id.btn7c, R.id.btn7cPlus,
                R.id.btn8a, R.id.btn8aPlus,
                R.id.btn8b, R.id.btn8bPlus,
                R.id.btn8c, R.id.btn8cPlus
        };

        // Iterar sobre els IDs per inicialitzar els botons i establir els listeners
        for (int id : buttonIds) {
            Button button = findViewById(id);
            setDificultatListener(button);
        }
    }

    /**
     * Inicialitza el RecyclerView per mostrar les dades d'escalada.
     */
    private void inicialitzarRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        climbingDataList = new ArrayList<>();
        climbingDataAdapter = new ClimbingDataAdapter(this, climbingDataList);
        recyclerView.setAdapter(climbingDataAdapter);
        databaseHelper = new DatabaseHelper(this);
        puntuacio = new Puntuacio();
    }

    /**
     * amaga o mostra el panell de dades introduïdes manualment
     * @param 'View.GONE
     * @param 'View.VISIBLE
     * */
    private void visibilitatGraus(int visibilitat) {
        entradaLayout.setVisibility(visibilitat);
    }

    /**
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
        carregarDadesDia(); // Load data for the current date

    }

    /**
     * loadSpinnerRocodroms
     * Mostra els diferents rocodroms que tenim a la base de dades
     * */
    private void loadSpinnerRocodroms() {
        // Inicialitzar el HashMap per emmagatzemar els rocòdroms
        rocodromsHashMap = new HashMap<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Carregar els rocòdroms des de la base de dades
        carregarRocodroms(databaseHelper);

        // Configurar l'adaptador per al spinner
        configurarSpinner();

        // Recuperar l'últim rocòdrom seleccionat
        recuperarUltimRocodrom();

        // Configurar el listener per gestionar les seleccions del spinner
        configurarListenerSpinner(databaseHelper);
    }

    private void carregarRocodroms(DatabaseHelper databaseHelper) {
        Cursor cursor = databaseHelper.getAllRocodroms();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_ROCO"));
                String nom = cursor.getString(cursor.getColumnIndexOrThrow("NOM_ROCO")) + ", " +
                        cursor.getString(cursor.getColumnIndexOrThrow("POBLACIO"));
                rocodromsHashMap.put(nom, id);
            }
            cursor.close();
        }
    }

    private void configurarSpinner() {
        // Crear l'adaptador per al spinner amb els noms dels rocòdroms
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                rocodromsHashMap.keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rocodromSpinner.setAdapter(adapter);
    }

    private void recuperarUltimRocodrom() {
        // Recuperar l'últim rocòdrom seleccionat de les preferències
        SharedPreferences prefs = getSharedPreferences("Rocodrom", MODE_PRIVATE);
        int ultimRocodrom = prefs.getInt("ultimRocodrom", 0);
        rocodromSpinner.setSelection(ultimRocodrom);
    }

    private void configurarListenerSpinner(DatabaseHelper databaseHelper) {
        // Configurar el listener per gestionar les opcions seleccionades
        rocodromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicio, long id) {
                // Guardar l'últim rocòdrom seleccionat
                guardarUltimRocodrom(posicio);

                // Generar les zones del rocòdrom seleccionat
                generarZones(databaseHelper, adapterView, posicio);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Opcional: gestionar el cas quan no es selecciona cap element
            }
        });
    }

    private void guardarUltimRocodrom(int posicio) {
        // Guardar la posició de l'últim rocòdrom seleccionat a les preferències
        SharedPreferences prefs = getSharedPreferences("Rocodrom", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("ultimRocodrom", posicio);
        editor.apply();
    }

    private void generarZones(DatabaseHelper databaseHelper, AdapterView<?> adapterView, int posicio) {
        // Recuperar les zones del rocòdrom seleccionat
        Cursor cursor2 = databaseHelper.getZonesByRocodrom(rocodromsHashMap.get(adapterView.getItemAtPosition(posicio)));
        zonesGrid.removeAllViews(); // Buidar el GridLayout zonesGrid

        if (cursor2 != null) {
            if (cursor2.moveToFirst()) {
                do {
                    crearBotonsZona(cursor2);
                } while (cursor2.moveToNext());
            } else {
                mostrarMissatgeSenseZones(adapterView, posicio);
            }
            cursor2.close();
        }
    }

    private void crearBotonsZona(Cursor cursor2) {
        // Recuperar les dades de la zona
        int idZona = cursor2.getInt(cursor2.getColumnIndexOrThrow("ID_ZONA"));
        String nomZona = cursor2.getString(cursor2.getColumnIndexOrThrow("NOM_ZONA"));
        int alturaZona = cursor2.getInt(cursor2.getColumnIndexOrThrow("ALTURA_ZONA"));
        int esCorda = cursor2.getInt(cursor2.getColumnIndexOrThrow("ZONA_CORDA"));
        int rocodromZona = cursor2.getInt(cursor2.getColumnIndexOrThrow("ID_ROCO_FK"));

        // Crear un Bundle amb la informació del botó
        Bundle infoBoto = new Bundle();
        infoBoto.putInt("idZona", idZona);
        infoBoto.putString("nomZona", nomZona);
        infoBoto.putInt("alturaZona", alturaZona);
        infoBoto.putInt("esCorda", esCorda);
        infoBoto.putInt("rocodromZona", rocodromZona);

        // Crear el botó de la zona
        Button btnZona = new Button(MainActivity.this);
        btnZona.setText(nomZona);
        btnZona.setTag(infoBoto);
        botonsZona.add(btnZona);

        // Configurar el listener del botó de zona
        btnZona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gestionarClickBotonZona(view);
            }
        });
        zonesGrid.addView(btnZona);
    }

    private void gestionarClickBotonZona(View view) {
        // Restaura el color de tots els botons de zona
        resetBotonsZona();
        // Resetejar els descansos
        descansos = 0;
        descansosSpinner.setSelection(0);

        // Canvi de color pel botó seleccionat
        view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.orange));

        // Recuperar la informació del botó seleccionat
        Bundle infoBoto = (Bundle) view.getTag();
        int idZona = infoBoto.getInt("idZona");
        String nomZona = infoBoto.getString("nomZona");
        int alturaZona = infoBoto.getInt("alturaZona");
        int esCorda = infoBoto.getInt("esCorda");
        int rocodromZona = infoBoto.getInt("rocodromZona");
        entradaLayout.setVisibility(View.VISIBLE);

        // Mostrar o amagar el layout de descansos segons el tipus de zona
        if (esCorda == 1) {
            descansosLayout.setVisibility(View.VISIBLE);
        } else {
            descansosLayout.setVisibility(View.GONE);
        }
    }

    private void mostrarMissatgeSenseZones(AdapterView<?> adapterView, int posicio) {
        // Generar el Layout per inserir zones si no hi ha zones definides
        zonesGrid.removeAllViews();
        TextView textView = new TextView(MainActivity.this);
        GridLayout.LayoutParams textParams = new GridLayout.LayoutParams();
        textParams.setGravity(Gravity.CENTER); // Centrar el TextView
        textParams.setMargins(20, 0, 0, 0); // Marge superior per separar del TextView
        textView.setLayoutParams(textParams);
        textView.setTextSize(18);
        textView.setText("No hi ha zones definides per aquest rocòdrom");
        textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.orange));

        // Afegir un listener per crear zones per aquest rocòdrom
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Rocodroms.class);
                // Passar l'id del rocòdrom
                intent.putExtra("idRoco", rocodromsHashMap.get(adapterView.getItemAtPosition(posicio)));
                startActivity(intent);
            }
        });

        zonesGrid.addView(textView);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rocodroms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_rocodroms) {
            startActivity(new Intent(MainActivity.this, Rocodroms.class));
            return true;
        }
        // Si cap ítem coincideix, crida al mètode de la superclasse
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
                        carregarDadesDia(); // Reload data for the selected date
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

                insertData(dateTextView.getText().toString(), dificultat, idZona, ifIntent, descansos);
                resetInput();
            }
        });
    }

    /**
     * insertData
     *
     * insereix les dades a la base de dades i carrega les dades del dia actual
     *
     * @param -String date, String dificultat, int zona, int intent i int descansos
     * */
    private void insertData(String date, String dificultat, int zona, int ifIntent, int descansos){
        boolean insertSuccess = databaseHelper.insertDataCD(date, dificultat, zona, ifIntent, descansos);
        if (insertSuccess) {
            //Toast.makeText(this, "Via guardada correctament", Toast.LENGTH_SHORT).show();
            carregarDadesDia();
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
    public void carregarDadesDia(){

        viesDia = 0;
        metresDia = 0.0;
        puntuacioDia = 0.0;
        climbingDataList.clear();

        Cursor cursor = null;
        Cursor cursor2 = null;

        try {
            cursor = databaseHelper.getJoinDayDataCD(dateTextView.getText().toString());
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String id_cd = cursor.getString(cursor.getColumnIndexOrThrow("ID_CD"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
                    String dificultat = cursor.getString(cursor.getColumnIndexOrThrow("DIFICULTAT"));
                    idZona = cursor.getInt(cursor.getColumnIndexOrThrow("ID_ZONA_FK"));
                    int ifIntent = cursor.getInt(cursor.getColumnIndexOrThrow("IFINTENT"));
                    int descansos = cursor.getInt(cursor.getColumnIndexOrThrow("DESCANSOS"));
                    nomZona = cursor.getString(cursor.getColumnIndexOrThrow("NOM_ZONA"));
                    alturaZona = cursor.getInt(cursor.getColumnIndexOrThrow("ALTURA_ZONA"));
                    String nomCurtRocodrom = cursor.getString(cursor.getColumnIndexOrThrow("NOM_ROCO_REDUIT"));

                    nomZona = nomZona + " (" + nomCurtRocodrom + ")";
                    double puntsVia = puntuacio.getPunts(dificultat);
                    double metresVia = alturaZona; //convertim el metres a double per si hi ha penalitzacions
                    if (ifIntent == 1) { //en el cas d'un inent apliquem el coeficient de dificultat i contem la mitat de metres de la zona
                        puntsVia /= puntuacio.getIfIntent();// veure Puntuacio.java
                        metresVia *= puntuacio.getPenalitzacioMetres();
                    } else if (descansos > 0) {
                        puntsVia /= puntuacio.getPenalitzacioDescansos(descansos);
                        ifIntent = 1; // si hi ha descansos ho indicarem al tag intent/descansos de l'item
                    }
                    climbingDataList.add(new ClimbingData(id_cd, date, dificultat, nomZona, ifIntent, String.format("%.1f", puntsVia)));
                    viesDia += 1;
                    metresDia += metresVia;
                    puntuacioDia += puntsVia;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            showError("Error al carregar dades del dia: " + e.getMessage());
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }

        //introduir les dades al ranking
        //primer busquem si hi ha dades per aquest dia
        try{
            cursor2 = databaseHelper.getRankingByDate(dateTextView.getText().toString());
            if (cursor2 != null && cursor2.moveToFirst()) {
                //si hi ha dades actualitzem
                int idRanking = cursor2.getInt(cursor2.getColumnIndexOrThrow("ID_RANKING"));
                if(viesDia!=0) {
                    databaseHelper.updateRanking(idRanking, dateTextView.getText().toString(), puntuacioDia, viesDia, (int) metresDia);
                } else {
                    databaseHelper.deleteRanking(idRanking);
                }
            } else {
                //si no hi ha dades les afegim
                databaseHelper.insertRanking(dateTextView.getText().toString(), puntuacioDia, viesDia, (int) metresDia);
            }
        }catch (Exception e){
            e.printStackTrace();
            showError("Error al carregar dades del ranking: " + e.getMessage());
        } finally {
            if (cursor2 != null){
                cursor2.close();
            }
        }


        //notifiquem a l'adaptador que hi ha hagut canvis i que ha de refrescar els valors
        climbingDataAdapter.notifyDataSetChanged();
        puntuacioDiaTextView.setText(String.format("%.1f", puntuacioDia).replace(".", ","));
        viesDiaTextView.setText(String.valueOf(viesDia));
        metresDiaTextView.setText(String.valueOf(metresDia));
        mitjanaDiaTextView.setText(Utilitats.mitjanaGrau(puntuacioDia/ viesDia));


        // controlem si la data que es mostra és l'actual. En cas que no ho sigui canviem el color del botó per a informar i evitar entrades errònies
        if (dateTextView.getText().toString().equals(avui)) {
            try {
                calendar.setTime(dateFormat.parse(avui));
            } catch (ParseException e){
                e.printStackTrace();
                showError("Error al processar la data: " + e.getMessage());
            }
            dateTextView.setTextColor(ContextCompat.getColor(this, R.color.orange));
        } else {
            dateTextView.setTextColor(ContextCompat.getColor(this, R.color.gray));
            btnAvui.setVisibility(View.VISIBLE);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}