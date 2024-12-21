package com.xmorera.climbingtrainingapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TextView dateTextView, infoTextView;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Button btnV, btnVplus, btn6a, btn6b, btn6c, btn7a, btn7b, btn7c;
    Button btnVPlus, btn6aPlus, btn6bPlus, btn6cPlus, btn7aPlus, btn7bPlus, btn7cPlus;
    CheckBox chkBIntent;
    Button btnAutos, btnCorda, btnShinyWall, btnBloc;


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
        updateDateTextView();

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
        setButtonListener(btnV);
        setButtonListener(btnVPlus);
        setButtonListener(btn6a);
        setButtonListener(btn6aPlus);
        setButtonListener(btn6b);
        setButtonListener(btn6bPlus);
        setButtonListener(btn6c);
        setButtonListener(btn6cPlus);
        setButtonListener(btn7a);
        setButtonListener(btn7aPlus);
        setButtonListener(btn7b);
        setButtonListener(btn7bPlus);
        setButtonListener(btn7c);
        setButtonListener(btn7cPlus);


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

    private void setButtonListener(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoTextView.setText(button.getText());
            }
        });
    }

}