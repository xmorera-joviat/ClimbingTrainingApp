package com.xmorera.climbingtrainingapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xmorera.climbingtrainingapp.RocodromsZones.Rocodroms;
import com.xmorera.climbingtrainingapp.climbingData.ClimbingData;
import com.xmorera.climbingtrainingapp.climbingData.ClimbingDataAdapter;

import com.xmorera.climbingtrainingapp.utils.ChronoSessions;
import com.xmorera.climbingtrainingapp.utils.Puntuacio;
import com.xmorera.climbingtrainingapp.resultats.Resultats;
import com.xmorera.climbingtrainingapp.utils.BlinkHelper;
import com.xmorera.climbingtrainingapp.utils.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe principal de l'aplicació Climbing Training App.
 * l'objectiu d'aquesta aplicació es registrar l'entrenament en rocòdrom donant una puntuació
 * a les vies realitzades en funció de la seva dificultat i llargada
 *
 * @author Xavier Morera
 * @version 0.6
 *
 */
public class MainActivity extends AppCompatActivity  {

    //classe auxiliar per fer que un element del View faci pampallugues
    private BlinkHelper blinkHelper;

    //temps màxim del crono parcial de descansos en milisegons (59':59"), s'ha de configurar com una preferència
    public static final int MAX_REST_CHRONO = 3599000;

    // Elements de la interfície d'usuari
    private TextView dateTextView; //mostrar la data i mostrar la via seleccionada
    private Button diaAnterior, diaPosterior, btnAvui, btnChrono, btnResultats;
    private Spinner rocodromSpinner, descansosSpinner;
    private GridLayout zonesGrid;
    private LinearLayout entradaLayout, descansosLayout;
    private RecyclerView recyclerView;
    private CheckBox chkIntent, chkEscalfament;

    private MenuItem menu_rocodroms;

    // Variables per gestionar la data
    private final Calendar calendar = Calendar.getInstance();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private String avui;

    // variables per a gestionar la selecció de zones i dificultats
    private HashMap<String, Integer> rocodromsHashMap;
    private final List<Button> botonsZona = new ArrayList<>();//llista per guardar els botons de zona que es generen en temps d'execució

    // variables per gestionar les dades de l'escalada
    private DatabaseHelper databaseHelper;
    private ClimbingDataAdapter climbingDataAdapter;
    private List<ClimbingData> climbingDataList;
    private Puntuacio puntuacio;

    // variables per al càlcul diari
    int viesDia;
    int viesGrauDia;
    double metresDia;
    double puntuacioDia;
    double puntuacioGrauDia; // s'utilitza per a calcular la mitjana de grau de la sessió

    //variables per a entrar les dades a la base de dades
    private int idZona;
    //private int esCorda;
    private int ifIntent;
    private int ifEscalfament;
    private int descansos;
    //private int rocodromZona;
    private String dificultat;

    // elements per mostrar els resultat diaris
    private TextView viesDiaTextView, metresDiaTextView, mitjanaDiaTextView, puntuacioDiaTextView;

    // variable per controlar que quan es torna a l'inici es mostri la data actual
    boolean firstTime = true;

    //gestió del  cronometre principal (sessió)
    private boolean chrono;//per mostrar o ocultar la icona i el seu color
    Drawable chrono30;
    Drawable chrono30_carbassa;

    private LinearLayout rocodromLayout;
    private LinearLayout cronometreDiaLayout;
    private LinearLayout sessionsLayout;

    private TextView sessionChronoTextViewTitle, sessionCronoTextView, dayChronoTextView;
    private TextView sessionsNum, sessionsCronoTextView;
    private final Handler mainChronoHandler = new Handler();
    private Runnable mainChronoRunnable;

    private boolean runningMainChrono = false; //estat del cronòmetre

    private ChronoSessions chronoSessions;
    private int numSessio;
    private long mainDayChronoElapsedTime;
    private long sessionChronoElapsedTime;

    // crono parcial (descans)
    private boolean allowRestChrono; //si la data és la del dia actual activaem el crono dels descansos
    private TextView restChronoTextViewTitle, restCronoTextView;
    private final Handler restChronoHandler = new Handler();
    private Runnable restChronoRunnable;
    private boolean runningRestChrono = false; //estat del cronòmetre

    //booleana utilitzada per assegurar que no tanquem l'app per equivocació quan anem enrrere
    private boolean doubleBackToExitPressedOnce = false;

    /// /* /////////////////////////////////////////////////////////////////////////////////////////
    /// versió demo ////////////////////////////////////////////////////////////////////////////////
    /// definició de constants /////////////////////////////////////////////////////////////////////
    private int demoDurationDays = 30;
    private static final String PREFS_DEMO = "DemoPrefs";
    private static final String KEY_START_DATE ="startDate";
    long startDateMillis=0;

    /// *///////////////////////////////////////////////////////////////////////////////////////////

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

        // Comportamet del botò enrrere
        setupBackPressHandler();

        /// /* /////////////////////////////////////////////////////////////////////////////////////
        /// versió demo ////////////////////////////////////////////////////////////////////////////
        /// Comprovar si s'ha iniciat la demo //////////////////////////////////////////////////////
        SharedPreferences prefsDemo = getSharedPreferences(PREFS_DEMO, MODE_PRIVATE);
        startDateMillis = prefsDemo.getLong(KEY_START_DATE, 0);
        //recuperar els dies de demo
        /// ////////////////////////////////////////////////////////////////////////////////////////
        /// per incrementar els dies de demo modificar la BBDD en viu des de l'app Inspection
        ///  fins que no s'implementi un mètode d'autenticació d'usuaris
        /// ////////////////////////////////////////////////////////////////////////////////////////
        Cursor cursor = databaseHelper.getDemoById(1);
        if (cursor != null && cursor.moveToFirst()) {
            demoDurationDays = cursor.getInt(cursor.getColumnIndexOrThrow("DAYS_DEMO"));
        }
        cursor.close();

        if (startDateMillis == 0) {
            //iniciar la demo ara
            startDateMillis = System.currentTimeMillis();
            prefsDemo.edit().putLong(KEY_START_DATE, startDateMillis).apply();
        }
        if(isDemoExpired(startDateMillis)) {
            // notificar
            showDemoExpiredDialog();
        }

        /// */ /////////////////////////////////////////////////////////////////////////////////////

    }

    /// /* /////////////////////////////////////////////////////////////////////////////////////////
    private boolean isDemoExpired(long startDateMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        long demoEndTimeMillis = startDateMillis + (demoDurationDays * 24 * 60 * 60 * 1000L); // 30 days in milliseconds
        return currentTimeMillis > demoEndTimeMillis;
    }
    /// */ /////////////////////////////////////////////////////////////////////////////////////////


    /// *///////////////////////////////////////////////////////////////////////////////////////////
    private void showDemoExpiredDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Demo Expired")
                .setMessage("Your demo period has expired. You can no longer enter new data. Please upgrade to the full version to continue using the app.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Redirect to upgrade page or perform an action
                    // For example, you can start a new activity or open a URL
                    // Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://your-upgrade-url.com"));
                    // startActivity(intent);
                })
                //.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(false) // Prevents the dialog from being dismissed by tapping outside
                .show();
    }
    /// /* /////////////////////////////////////////////////////////////////////////////////////////



    /**
     * setupBackPressHandler
     * Aquest mètode conté tota la lògica per configurar el comportament del botó "Enrere" utilitzant OnBackPressedCallback.
     * S'encarrega de crear el callback, gestionar el Double Back Press i registrar el callback amb OnBackPressedDispatcher.
     * així s'evita que anant amb el botó enrere es tanqui l'app
     */
    private void setupBackPressHandler() {
        // Configura el OnBackPressedCallback
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Verifica si l'activitat actual és l'arrel de la tasca
                if (isTaskRoot()) {
                    // Aplica el Double Back Press només si és l'últim "Back"
                    if (doubleBackToExitPressedOnce) {
                        // Si l'usuari prem el botó "Enrere" dues vegades, tanca l'aplicació
                        finish();
                        return;
                    }

                    // Marca que el botó "Enrere" s'ha premut una vegada
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(MainActivity.this, "Prem una altra vegada per tancar l'aplicació", Toast.LENGTH_SHORT).show();

                    // Reinicia el flag després de 2 segons
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000); // 2 segons
                } else {
                    // Si no és l'arrel, permet que el botó "Enrere" funcioni normalment
                    setEnabled(false); // Desactiva temporalment el callback
                    getOnBackPressedDispatcher().onBackPressed(); // Executa el comportament per defecte del botó "Enrere"
                    setEnabled(true); // Reactiva el callback
                }
            }
        };

        // Registra el callback amb OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Configura la interfície d'usuari mapejant els elements a variables.
     */
    private void configurarUI() {
        dateTextView = findViewById(R.id.dateTextView);
        diaAnterior = findViewById(R.id.diaAnterior);
        diaPosterior = findViewById(R.id.diaPosterior);
        btnAvui = findViewById(R.id.btnAvui);
        btnChrono = findViewById(R.id.btnChrono);
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
        chkEscalfament = findViewById(R.id.chkEscalfament);
        rocodromLayout = findViewById(R.id.rocodromLayout);

        // Configuració de la visibilitat dels layouts
        rocodromLayout.setVisibility(View.VISIBLE);
        entradaLayout.setVisibility(View.GONE);
        descansosLayout.setVisibility(View.GONE);

        // mapeig dels menús
        menu_rocodroms = findViewById(R.id.menu_rocodroms);

        // instanciació del classe auxiliar per fer que un element del View faci pampallugues
        blinkHelper = new BlinkHelper();

        //inicialització del crono
        chrono=false;
        //mapeig icones per l'activació del cronometre
        chrono30 = ContextCompat.getDrawable(MainActivity.this, R.drawable.chrono30);
        chrono30_carbassa = ContextCompat.getDrawable(MainActivity.this, R.drawable.chrono30_carbassa);

        //cronometres
        sessionCronoTextView = findViewById(R.id.sessionCronoTextView);
        sessionChronoTextViewTitle = findViewById(R.id.sessionChronoTextViewTitle);
        restCronoTextView = findViewById(R.id.restCronoTextView);
        restChronoTextViewTitle = findViewById(R.id.restChronoTextViewTitle);
        dayChronoTextView = findViewById(R.id.dayCronoTextView);
        sessionsNum = findViewById(R.id.sessionsNum);
        sessionsCronoTextView = findViewById(R.id.sessionsCronoTextView);

        //Layouts dels cronos
        cronometreDiaLayout = findViewById(R.id.cronometreDiaLayout);
        sessionsLayout = findViewById(R.id.sessionsLayout);
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
        //gestió d les dates
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

        // actviar/desactivar el crono. canviar el color de la icona.
        btnChrono.setOnClickListener(view -> {

            chrono = !chrono;
            if (chrono) {
                startMainChrono();
            } else {
                stopMainChrono();
            }
        });

        //mostrar resultats i gràfics
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
        //instanciar dades del cronometre
        chronoSessions = new ChronoSessions(databaseHelper);
        puntuacio = new Puntuacio();
    }

    /**
     * amaga o mostra el panell de dades introduïdes manualment
     * els valors introduits poden ser 'View.GONE o View.VISIBLE'
     * @param visibilitat que representa l'element quevolem mostrar o amagar
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
                gestionarClickBotoZona(view);
            }
        });
        zonesGrid.addView(btnZona);
    }

    private void gestionarClickBotoZona(View view) {
        // Restaura el color de tots els botons de zona
        resetBotonsZona();
        // Resetejar els descansos
        descansos = 0;
        descansosSpinner.setSelection(0);

        // Canvi de color pel botó seleccionat
        view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.orange));

        // Recuperar la informació del botó seleccionat
        Bundle infoBoto = (Bundle) view.getTag();
        idZona = infoBoto.getInt("idZona");//int idZona = infoBoto.getInt("idZona");
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
     * setDificultatListener
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
                if (chkEscalfament.isChecked()) {
                    ifEscalfament = 1;
                } else {
                    ifEscalfament = 0;
                }
                insertData(dateTextView.getText().toString(), dificultat, idZona, ifIntent, ifEscalfament, descansos);

                stopRestChrono();
                startRestChrono();

                resetInput();
            }
        });
    }


    /**
     * insertData
     *
     * insereix les dades a la base de dades i carrega les dades del dia actual
     *
     * @param -String date, String dificultat, int zona, int intent int ifEscalfament i int descansos
     * */
    private void insertData(String date, String dificultat, int zona, int ifIntent, int ifEscalfament, int descansos){
        boolean insertSuccess = databaseHelper.insertDataCD(date, dificultat, zona, ifIntent, ifEscalfament, descansos);
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
        chkEscalfament.setChecked(false);
        ifIntent = 0;
        ifEscalfament = 0;
        descansos = 0;
        visibilitatGraus(View.GONE);
        //restaura el color de tots els botons de zona
        resetBotonsZona();
    }

    /**
     * resetBotonsZona
     * */
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
     * carregarDadesDia
     *
     * carrega les dades de la base de dades a la llista climbingDataList i notifica a l'adaptador
     */
    public void carregarDadesDia(){
        inicialitzarVariablesDadesDia();
        carregarDadesViesDadesDia();
        introduirDadesRankingDadesDia();
        actualitzarUIDadesDia();
        gestionarDataActualDadesDia();
        /// */ /////////////////////////////////////////////////////////////////////////////////////
        /// si la demo ha caducat mostrar el missatge informatiu i desactivar l'entrada de dades
        // comprovar si la demo ha expirat
        if(isDemoExpired(startDateMillis)) {
            // restringir l'entrada de dades
            entradaLayout.setVisibility(View.GONE);
            btnChrono.setVisibility(View.GONE);
            rocodromLayout.setVisibility(View.GONE);
        } else {
            rocodromLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * gestionarDataActualDadesDia
     * controlem si la data que es mostra és l'actual. En cas que no ho sigui canviem el color
     * del botó per a informar i evitar entrades errònies
     */
    private void gestionarDataActualDadesDia() {

        //recuperem les dades de les sessions del dia.
        chronoSessions.getSessions(dateTextView.getText().toString());
        Map<String, Object> dadesSessionsDia = chronoSessions.getDadesSessionsDia();
        int numSessions = (int) dadesSessionsDia.get("sessions");
    Log.d("numSessions", String.valueOf(numSessions));
        long tempsEntrenamentUltimaSessio = (long) dadesSessionsDia.get("tempsUltimaSessio");
        long tempsEntrenamentTotal = (long) dadesSessionsDia.get("tempsTotalDia");
    Log.d("numSessions", String.valueOf(numSessio));

        //numSessio = numSessions;

        if (dateTextView.getText().toString().equals(avui)) {
            try {
                calendar.setTime(dateFormat.parse(avui));
            } catch (ParseException e){
                e.printStackTrace();
                showError("Error al processar la data: " + e.getMessage());
            }
            dateTextView.setTextColor(ContextCompat.getColor(this, R.color.orange));
            btnChrono.setVisibility(View.VISIBLE);
            cronometreDiaLayout.setVisibility(View.VISIBLE);
            sessionsLayout.setVisibility(View.GONE);
            allowRestChrono = true;


            //mostrem el nombre de sessions, el temps de l'última i el temps total del dia
            updateSessionChronosTextViews(numSessions, tempsEntrenamentUltimaSessio, tempsEntrenamentTotal);

            //fem les següents assignacions per poder re-empendre les sessions del dia actual si hem sortit del programa


            mainDayChronoElapsedTime=tempsEntrenamentTotal;

        } else {
            dateTextView.setTextColor(ContextCompat.getColor(this, R.color.gray));
            btnAvui.setVisibility(View.VISIBLE);
            btnChrono.setVisibility(View.GONE);
            cronometreDiaLayout.setVisibility(View.GONE);
            sessionsLayout.setVisibility(View.VISIBLE);
            allowRestChrono = false;
            //carreguem les sessions del dia i el temps total del dia
            updateSessionsDiaTextViews(numSessions, tempsEntrenamentTotal);
        }
    }

    private void actualitzarUIDadesDia() {
        //notifiquem a l'adaptador que hi ha hagut canvis i que ha de refrescar els valors
        climbingDataAdapter.notifyDataSetChanged();
        puntuacioDiaTextView.setText(String.format("%.1f", puntuacioDia).replace(".", ","));
        viesDiaTextView.setText(String.valueOf(viesDia));
        metresDiaTextView.setText(String.valueOf(metresDia));
        mitjanaDiaTextView.setText(puntuacio.mitjanaGrau(puntuacioGrauDia/ viesGrauDia));
    }

    private void introduirDadesRankingDadesDia() {
        //introduir les dades al ranking
        //primer busquem si hi ha dades per aquest dia
        Cursor cursor2 = null;
        try{
            cursor2 = databaseHelper.getRankingByDate(dateTextView.getText().toString());
            if (cursor2 != null && cursor2.moveToFirst()) {
                //si hi ha dades actualitzem
                int idRanking = cursor2.getInt(cursor2.getColumnIndexOrThrow("ID_RANKING"));
                if(viesDia!=0) {
                    databaseHelper.updateRanking(idRanking, dateTextView.getText().toString(), puntuacioDia, puntuacioGrauDia, viesDia, (int) viesGrauDia, (int) metresDia);
                } else {
                    databaseHelper.deleteRanking(idRanking);
                }
            } else {
                //si no hi ha dades les afegim
                databaseHelper.insertRanking(dateTextView.getText().toString(), puntuacioDia, puntuacioGrauDia, viesDia, viesGrauDia, (int) metresDia);
            }
        }catch (Exception e){
            e.printStackTrace();
            showError("Error al carregar dades del ranking: " + e.getMessage());
        } finally {
            if (cursor2 != null){
                cursor2.close();
            }
        }
    }

    private void carregarDadesViesDadesDia() {
        Cursor cursor = null;

        try {
            cursor = databaseHelper.getJoinDayDataCD(dateTextView.getText().toString());
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String id_cd = cursor.getString(cursor.getColumnIndexOrThrow("ID_CD"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
                    String dificultat = cursor.getString(cursor.getColumnIndexOrThrow("DIFICULTAT"));
                    idZona = cursor.getInt(cursor.getColumnIndexOrThrow("ID_ZONA_FK"));
                    int ifIntent = cursor.getInt(cursor.getColumnIndexOrThrow("IFINTENT"));
                    int ifEscalfament = cursor.getInt(cursor.getColumnIndexOrThrow("IFESCALFAMENT"));
                    int descansos = cursor.getInt(cursor.getColumnIndexOrThrow("DESCANSOS"));
                    String nomZona = cursor.getString(cursor.getColumnIndexOrThrow("NOM_ZONA"));
                    int alturaZona = cursor.getInt(cursor.getColumnIndexOrThrow("ALTURA_ZONA"));
                    String nomCurtRocodrom = cursor.getString(cursor.getColumnIndexOrThrow("NOM_ROCO_REDUIT"));

                    nomZona = nomZona + " (" + nomCurtRocodrom + ")";
                    double puntsVia = puntuacio.getPunts(dificultat);
                    double puntsGrau = puntsVia;
                    double metresVia = alturaZona; //convertim el metres a double per si hi ha penalitzacions
                    int viaGrau = 1;

                    if (ifIntent == 1) { //en el cas d'un inent apliquem el coeficient de dificultat i contem la mitat de metres de la zona
                        puntsVia /= puntuacio.getIfIntent();// veure Puntuacio.java
                        metresVia *= puntuacio.getPenalitzacioMetres();
                        puntsGrau = 0; //si és un intent no es comptabilitzen els punts per a calcular la mitjana de grau de la sessió
                        viaGrau=0;
                    } else if (descansos > 0) {
                        puntsVia /= puntuacio.getPenalitzacioDescansos(descansos);
                        puntsGrau = 0;
                        ifIntent = 1; // si hi ha descansos ho indicarem al tag intent/descansos de l'item
                        viaGrau = 0;
                    }
                    if (ifEscalfament == 1){
                        puntsGrau = 0; //en aquest cas tampoc es comptabilitzen els punts per a calcular la mitjana de grau de la sessió
                        viaGrau = 0;
                    }
                    climbingDataList.add(new ClimbingData(id_cd, date, dificultat, nomZona, ifIntent, ifEscalfament, String.format("%.1f", puntsVia)));
                    viesDia += 1;
                    viesGrauDia += viaGrau;
                    metresDia += metresVia;
                    puntuacioDia += puntsVia;
                    puntuacioGrauDia += puntsGrau;
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
    }

    /**
     * inicialitzarVariablesDadesDia
     * */
    private void inicialitzarVariablesDadesDia() {
        viesDia = 0;
        viesGrauDia = 0;
        metresDia = 0.0;
        puntuacioDia = 0.0;
        puntuacioGrauDia = 0.0;
        climbingDataList.clear();
    }


    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Inicia el cronòmetre principal i actualitza el TextView cada segon
     * */
    private void startMainChrono() {
        btnChrono.setCompoundDrawablesWithIntrinsicBounds(null, chrono30_carbassa, null, null);
        sessionChronoTextViewTitle.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.orange));
        sessionCronoTextView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.orange));
        if(!runningMainChrono){
            numSessio += 1;
            long totalDayTimeMainChronoInicial = System.currentTimeMillis()- mainDayChronoElapsedTime;//el crono continuarà sense resetejar-se, comptabilitzant el total diari
            long sessionTimeMainChronoInicial = System.currentTimeMillis();//el crono es reseteja per comptabilitzar la sessió actual
            runningMainChrono = true;
            // defineix el runnable que actualitza el crono cada segon
            mainChronoRunnable = new Runnable() {
                @Override
                public void run() {
                    if (runningMainChrono) {
                        mainDayChronoElapsedTime = System.currentTimeMillis() - totalDayTimeMainChronoInicial;
                        sessionChronoElapsedTime = System.currentTimeMillis() - sessionTimeMainChronoInicial;
                        updateSessionChronosTextViews(numSessio, sessionChronoElapsedTime, mainDayChronoElapsedTime);//actualitza el text del crono
                        mainChronoHandler.postDelayed(this, 1000);
                    }
                }
            };
            //inicia el crono
            mainChronoHandler.post(mainChronoRunnable);
        }
    }

    /**
     * Atura el cronòmetre principal
     * */
    private void stopMainChrono(){
        btnChrono.setCompoundDrawablesWithIntrinsicBounds(null, chrono30, null, null);
        sessionChronoTextViewTitle.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.gray));
        sessionCronoTextView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.gray));

        if(runningMainChrono){
            runningMainChrono = false;
            mainChronoHandler.removeCallbacks(mainChronoRunnable);//atura el crono
            //si parem el crono principal també pararem el crono de descansos
            stopRestChrono();
            // guardem el temps de la sessió actual i el nombre de sessió
            insertSession(numSessio, sessionChronoElapsedTime);
        }
    }

    private void insertSession(int numSessio, long elapsedSessionTime) {
        databaseHelper.insertSession(dateTextView.getText().toString(), numSessio, elapsedSessionTime);
    }

    /**
     * Actualitza el TextView del cronòmetre de sessió
     * @param elapsedSessionTime - temps transcorregut en milisegons
     * */
    private void updateSessionChronosTextViews(int numSessio, long elapsedSessionTime, long elapsedTotalTime) {
        //crono de sessió
        long hoursS = elapsedSessionTime / (1000 * 60 * 60);
        long minutesS = (elapsedSessionTime % (1000 * 60 * 60)) / (1000 * 60);
        long secondsS = (elapsedSessionTime % (1000 * 60)) / 1000;

        //formata el temps en 00:00:00
        String timeSessionFormated = String.format("%02d:%02d:%02d", hoursS, minutesS, secondsS);
        // Actualitza els TextView
        sessionChronoTextViewTitle.setText("Sessio: "+ numSessio);
        sessionCronoTextView.setText(timeSessionFormated);

        //crono total diari
        long hoursT = elapsedTotalTime / (1000 * 60 * 60);
        long minutesT = (elapsedTotalTime % (1000 * 60 * 60)) / (1000 * 60);
        String timeTotalFormated = String.format("%02d:%02d", hoursT, minutesT);
        dayChronoTextView.setText(timeTotalFormated);
    }

    /**
     * Actualitza el TextView del cronòmetre de sessió
     * @param elapsedTotalTime - temps transcorregut en milisegons
     * */
    private void  updateSessionsDiaTextViews(int numSessio, long elapsedTotalTime) {
        //crono de sessió
        long hoursS = elapsedTotalTime / (1000 * 60 * 60);
        long minutesS = (elapsedTotalTime % (1000 * 60 * 60)) / (1000 * 60);

        //formata el temps en 00:00
        String timeSessionFormated = String.format("%02d:%02d", hoursS, minutesS);
        // Actualitza els TextView
        sessionsNum.setText(String.valueOf(numSessio));
        sessionsCronoTextView.setText(timeSessionFormated);
    }

    /**
     * Inicia el cronòmetre parcial i actualitza el TextView cada segon
     * */
    private void startRestChrono() {
        if(allowRestChrono){// si estem en el dia actual podrem iniciar el cronòmetre parcial
            if(!runningRestChrono){
                long startTimeRestChrono = System.currentTimeMillis();//reiniciem el crono
                runningRestChrono = true;
                //vivibilitzar els TextView
                restChronoTextViewTitle.setVisibility(View.VISIBLE);
                restCronoTextView.setVisibility(View.VISIBLE);
                restCronoTextView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.green));
                blinkHelper.stopBlinking(restCronoTextView);

                // defineix el runnable que actualitza el crono cada segon
                restChronoRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (runningRestChrono) {
                            long restChronoElapsedTime = System.currentTimeMillis() - startTimeRestChrono;
                            updateRestChronoTextView(restChronoElapsedTime);//actualitza el text del crono
                            restChronoHandler.postDelayed(this, 1000);
                            //destacar el crono al cap del temps marcat com a límit
                            if (restChronoElapsedTime >= MAX_REST_CHRONO) {
                                //stopRestChrono();
                                //updateRestChronoTextView(MAX_REST_CHRONO);
                                //el posem de color vermell per indicar que ha passat el marge de temps i fa pampellugues
                                restCronoTextView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.red));
                                blinkHelper.startBlinking(restCronoTextView);

                            }//else {
                            //  updateRestChronoTextView(restChronoElapsedTime);//actualitza el text del crono
                            //  restChronoHandler.postDelayed(this, 1000);
                            //}
                        }
                    }
                };
                //inicia el crono
                restChronoHandler.post(restChronoRunnable);
            }
        }

    }

    /**
     * Actualitza el TextView del cronòmetre parcial
     * @param elapsedTime - temps transcorregut en milisegons
     * */
    private void updateRestChronoTextView(long elapsedTime) {
        //long hours = elapsedTime / (1000 * 60 * 60)RestChrono;
        long minutes = (elapsedTime % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (elapsedTime % (1000 * 60)) / 1000;

        //formata el temps en 00:00:00
        String timeFormated = String.format("%02d:%02d", minutes, seconds);
        // Actualitza el TextView
        restCronoTextView.setText(timeFormated);
    }

    /**
     * Atura el cronòmetre parcial
     * */
    private void stopRestChrono(){
        if(runningRestChrono){
            runningRestChrono = false;
            restChronoHandler.removeCallbacks(restChronoRunnable);//atura el crono
            updateRestChronoTextView(0);

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //aturar els cronometres si encara estan funcionant
        if (runningMainChrono) {
            stopMainChrono();
        }
        if (runningRestChrono) {
            stopRestChrono();
        }
        // aturar elements que facin pampallugues
        blinkHelper.cleanup();
    }

}