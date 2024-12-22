package com.xmorera.climbingtrainingapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Layout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    LinearLayout dadesManualsLayout;
    TextView dateTextView, viaTextView; //mostrar la data i mostrar la via seleccionada
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Button btn6a, btn6b, btn6c, btn7a, btn7b, btn7c;
    Button btn6aPlus, btn6bPlus, btn6cPlus, btn7aPlus, btn7bPlus, btn7cPlus;
    CheckBox chkIntent;
    Button btnAutos, btnCorda, btnShinyWall, btnBloc;//tipus de via
    //variables per a entrar les dades a la base de dades
    String via;
    String paret;
    int intent = 0;
    TextView mostrarDadesTextView; //camp  temporal per mostrar dades de la base de dades s'ha de fer amb un recyclerView
    DatabaseHelper databaseHelper;//auxiliar de la base de dades
    Button btnSpeak;//botó per entrar les comandes de veu
    Button btnEntradaManual;


    //Definició de l'ActivityResultLauncher per tractar el reconeixement de veu
    private final ActivityResultLauncher<Intent> voiceInputLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (results != null && !results.isEmpty()) {
                        String command = results.get(0);
                        Toast.makeText(this, "Command: " + command, Toast.LENGTH_SHORT).show();
                        //afegir les dades reconegudes a la bd
                    }
                }
            });

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
        //selecció d'una data
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        viaTextView = findViewById(R.id.viaTextView);

        btnSpeak = findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(view -> startVoiceInput());

        btnEntradaManual = findViewById(R.id.btnEntradaManual);
        dadesManualsLayout = findViewById(R.id.dadesManualsLayout);
        dadesManualsLayout.setVisibility(View.GONE);
        viaTextView.setVisibility(View.GONE);
        btnEntradaManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetInput();
                if (dadesManualsLayout.getVisibility() == View.GONE) {
                    dadesManualsLayout.setVisibility(View.VISIBLE);
                    viaTextView.setVisibility(View.VISIBLE);
                } else {
                    dadesManualsLayout.setVisibility(View.GONE);
                    viaTextView.setVisibility(View.GONE);
                }
            }
        });



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
        //establiment dels listeners
        setGrauListener(btn6a);
        setGrauListener(btn6aPlus);
        setGrauListener(btn6b);
        setGrauListener(btn6bPlus);
        setGrauListener(btn6c);
        setGrauListener(btn6cPlus);
        setGrauListener(btn7a);
        setGrauListener(btn7aPlus);
        setGrauListener(btn7b);
        setGrauListener(btn7bPlus);
        setGrauListener(btn7c);
        setGrauListener(btn7cPlus);

        chkIntent = findViewById(R.id.chkIntent);

        btnAutos = findViewById(R.id.btnAutos);
        btnCorda = findViewById(R.id.btnCorda);
        btnShinyWall = findViewById(R.id.btnShinyWall);
        btnBloc = findViewById(R.id.btnBloc);
        //listeners pels botons de paret
        setWallListener(btnAutos);
        setWallListener(btnCorda);
        setWallListener(btnShinyWall);
        setWallListener(btnBloc);

        databaseHelper = new DatabaseHelper(this);
        displayData(); //mostrar totes les dades de la bd
        mostrarDadesTextView = findViewById(R.id.mostrarDadesTextView);//camp temporal a substituir per un RecyclerView

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Gestió dels botons de menú
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            startActivity(new Intent(this, Preferencies.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateDateTextView() {
        String currentDate = dateFormat.format(calendar.getTime());
        dateTextView.setText(currentDate);
    }

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
                    }
                }, year, month, day);
        datePickerDialog.show();


    }

    private void setGrauListener(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viaTextView.setText(button.getText());
            }
        });
    }


    private void setWallListener(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viaTextView.getText().toString().equals("")){
                    //si no s'ha introduit una via no fer res
                }else {

                    //dades per guardar a sqlite
                    String dia = dateTextView.getText().toString().split("/")[0];
                    String mes = dateTextView.getText().toString().split("/")[1];
                    String any = dateTextView.getText().toString().split("/")[2];
                    via = viaTextView.getText().toString();
                    paret = button.getText().toString();
                    if (chkIntent.isChecked()) {
                        intent = 1;
                    }
                    //introducció de dades a la base de dades
                    boolean insertSuccess = databaseHelper.insertData(dia + "/" + mes + "/" + any, via, paret, intent);
                    if (insertSuccess) {
                        Toast.makeText(MainActivity.this, "Via guardada correctament", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Error en guardar la via", Toast.LENGTH_SHORT).show();
                    }
                    resetInput();
                    displayData(); //mostrar totes les dades emmagatzemades a la bd
                }
            }
        });
    }

    private void resetInput() {
        //reset pantalla introducció de dades
        viaTextView.setText("");
        chkIntent.setChecked(false);
        intent = 0;
    }

    private void displayData(){
        Cursor cursor = databaseHelper.getAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No s'han trobat dades", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (cursor.moveToNext()) {
            stringBuilder.append("Data: ").append(cursor.getString(1)).append(" ");
            stringBuilder.append("Via: ").append(cursor.getString(2)).append(" ");
            stringBuilder.append("Paret: ").append(cursor.getString(3)).append(" ");
            stringBuilder.append("Intent: ").append(cursor.getInt(4)).append("\n");
        }
        cursor.close();
        mostrarDadesTextView.setText(stringBuilder.toString());

    }

    private void startVoiceInput() {
        resetInput();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ca-ES");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Digues el grau, la paret (i si és intent...)");
        try {
            voiceInputLauncher.launch(intent);
        } catch (ActivityNotFoundException a){
            Toast.makeText(this, "Ho sento, el reconeixement de veu no és compatible en aquest dispositiu", Toast.LENGTH_SHORT).show();
        }
    }
}