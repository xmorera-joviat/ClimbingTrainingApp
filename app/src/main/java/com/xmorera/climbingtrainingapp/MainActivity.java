package com.xmorera.climbingtrainingapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView dateTextView, infoTextView;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Button btnV, btnVplus, btn6a, btn6b, btn6c, btn7a, btn7b, btn7c;
    Button btnVPlus, btn6aPlus, btn6bPlus, btn6cPlus, btn7aPlus, btn7bPlus, btn7cPlus;
    CheckBox chkIntent;
    Button btnAutos, btnCorda, btnShinyWall, btnBloc;
    String via;
    String paret;
    int intent = 0;
    Date data;
    TextView dadesTextView;
    DatabaseHelper databaseHelper;

    @SuppressLint("MissingInflatedId")
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
        //data actual
        updateDateTextView();
        //selecció d'una data
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        infoTextView = findViewById(R.id.infoTextView);

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

        //establiment dels listeners
        setGrauListener(btnV);
        setGrauListener(btnVPlus);
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

        btnAutos = findViewById(R.id.btnAutos);
        btnCorda = findViewById(R.id.btnCorda);
        btnShinyWall = findViewById(R.id.btnShinyWall);
        btnBloc = findViewById(R.id.btnBloc);

        //listeners pels botons de climbing
        setWallListener(btnAutos);
        setWallListener(btnCorda);
        setWallListener(btnShinyWall);
        setWallListener(btnBloc);

        chkIntent = findViewById(R.id.chkIntent);

        dadesTextView = findViewById(R.id.dadesTextView);

        databaseHelper = new DatabaseHelper(this);
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
                infoTextView.setText(button.getText());
            }
        });
    }


    private void setWallListener(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(infoTextView.getText().toString().equals("")){
                    //si no s'ha introduit una via no fer res
                }else {

                    //dades per guardar a sqlite
                    String dia = dateTextView.getText().toString().split("/")[0];
                    String mes = dateTextView.getText().toString().split("/")[1];
                    String any = dateTextView.getText().toString().split("/")[2];
                    via = infoTextView.getText().toString();
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
                    //reset
                    infoTextView.setText("");
                    chkIntent.setChecked(false);
                    intent = 0;
                }
            }
        });
    }

    private void displayData(){
        Cursor cursor = databaseHelper.getAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No s'han trobat dades", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (cursor.moveToNext()) {
            stringBuilder.append("Date: ").append(cursor.getString(1)).append("\n");
            stringBuilder.append("Via: ").append(cursor.getString(2)).append("\n");
            stringBuilder.append("Paret: ").append(cursor.getString(3)).append("\n");
            stringBuilder.append("Intent: ").append(cursor.getInt(4)).append("\n\n");
        }
        cursor.close();
        dadesTextView.setText(stringBuilder.toString());

    }
}