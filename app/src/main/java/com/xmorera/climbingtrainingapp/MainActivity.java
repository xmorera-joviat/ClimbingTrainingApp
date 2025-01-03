package com.xmorera.climbingtrainingapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Button btnIV, btnV, btnVPlus;
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
    Button btnSpeak;//botó per entrar les comandes de veu
    Button btnEntradaManual;

    RecyclerView recyclerView;
    ClimbingDataAdapter climbingDataAdapter;
    List<ClimbingData> climbingDataList;

    SharedPreferences preferencesGZero;

    double puntuacioDia;
    TextView puntuacioDiaTextView;
    int vies;
    TextView viesDiaTextView;

    private ReconeixementDeVeu reconeixementDeVeu;



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
        //selecció d'una data mitjançant el calendari en pantalla
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        dificultatTextView = findViewById(R.id.dificultatTextView);
        btnSpeak = findViewById(R.id.btnSpeak);
        reconeixementDeVeu = new ReconeixementDeVeu(this, voiceInputLauncher);
        btnSpeak.setOnClickListener(view -> reconeixementDeVeu.startVoiceInput());

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
        puntuacioDiaTextView = findViewById(R.id.puntuacioDiaTextView);
        viesDiaTextView = findViewById(R.id.viesDiaTextView);
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
    /**
     * onResume
     *
     * recarrega les dades del  dia actual en el recyclerView
     * */
    @Override
    protected void onResume() {
        super.onResume();
        loadDayData();
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
        if (id == R.id.menu_resultats) {
            startActivity(new Intent(this, Resultats.class));
            return true;
        }
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
        puntuacioDia = 0.0;
        vies = 0;
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

                String puntuacio = puntuacioVia(dificultat, zona, ifIntent);
                climbingDataList.add(new ClimbingData( id, date, dificultat, zona, ifIntent, puntuacio));
                puntuacioDia += Double.parseDouble(puntuacio.replace(",","."));
                vies += 1;
            }
            cursor.close();
        }
        climbingDataAdapter.notifyDataSetChanged();//notificar a l'adaptador que hi ha hagut canvis
        puntuacioDiaTextView.setText(String.format("%.1f", puntuacioDia).replace(".", ","));
        viesDiaTextView.setText(String.valueOf(vies));
    }

    /**
     * puntuacioVia
     *
     * s'encarrega de fer els càlculs depenent de la dificultat de la via, de la llargada i de l'intent
     * @param -String via, String zona, int intent
     * @return String corresponent a la puntuació de la via
     * */
    private String puntuacioVia(String dificultat, String zona, int ifIntent) {
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



    /*
     * Implementació de l'ActivityResultLauncher llençat a ReconeixementDeVeu per tractar el reconeixement de veu
     */
    public final ActivityResultLauncher<Intent> voiceInputLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->
            reconeixementDeVeu.handleVoiceInputResult(result.getResultCode(), result.getData())
    );


}