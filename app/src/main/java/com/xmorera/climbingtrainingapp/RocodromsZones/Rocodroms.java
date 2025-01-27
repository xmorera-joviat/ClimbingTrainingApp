package com.xmorera.climbingtrainingapp.RocodromsZones;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.xmorera.climbingtrainingapp.R;
import com.xmorera.climbingtrainingapp.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rocodroms extends AppCompatActivity {

    private Spinner rocodromSpinner;
    private DatabaseHelper databaseHelper;
    private HashMap<String, Integer> rocodromsHashMap;
    private List<Button> botonsZona = new ArrayList<>();
    private GridLayout zonesGrid;
    private LinearLayout zonesLayout;
    private Button btnAfegirZona, btnModificarZona, btnEliminarRoco, btnCancelar, btnAddRocodrom;
    private LinearLayout editZones;
    private int idZona, alturaZona, esCorda, rocodromZona;
    private int idRocoSeleccionat;
    private int idRocoFromMain;
    private String nomZona;
    private EditText editTextZoneName, editTextZoneHeight;
    private CheckBox checkBoxEsCord;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rocodroms);

        initializeViews();
        setupListeners();
        idRocoFromMain = getIntent().getIntExtra("idRoco", -1);
        loadSpinner(idRocoFromMain);
    }

    private void initializeViews() {
        rocodromSpinner = findViewById(R.id.spinner_rocodroms);
        databaseHelper = new DatabaseHelper(this);
        zonesGrid = findViewById(R.id.zonesGrid);
        zonesLayout = findViewById(R.id.zonesLayout);
        btnAfegirZona = findViewById(R.id.btnAfegirZona);
        btnModificarZona = findViewById(R.id.btnModificarZona);
        btnEliminarRoco = findViewById(R.id.btnEliminarRoco);
        btnAddRocodrom = findViewById(R.id.buttonAddRocodrom);
        btnCancelar = findViewById(R.id.btnCancelar);
        editZones = findViewById(R.id.editZones);
        editTextZoneName = findViewById(R.id.editTextZoneName);
        editTextZoneHeight = findViewById(R.id.editTextZoneHeight);
        checkBoxEsCord = findViewById(R.id.checkBoxEsCord);
    }

    private void setupListeners() {
        btnAfegirZona.setOnClickListener(view -> {
            if (editTextZoneName.getText().toString().isEmpty() || editTextZoneHeight.getText().toString().isEmpty()) {
                showAlert("Error", "Si us plau, ompliu el nom de la zona i l'altura.");
            } else {
                addZone();
            }
        });

        btnModificarZona.setOnClickListener(view -> {
            String nomZona = editTextZoneName.getText().toString();
            int alturaZona = Integer.parseInt(editTextZoneHeight.getText().toString());
            int esCorda = checkBoxEsCord.isChecked() ? 1 : 0;
            boolean zonaModificada = databaseHelper.updateZona(idZona, idRocoSeleccionat,nomZona, alturaZona, esCorda);
            if (zonaModificada) {
                Toast.makeText(Rocodroms.this, "Zona modificada correctament", Toast.LENGTH_SHORT).show();
                resetIntroZones();
                loadSpinner(idRocoSeleccionat);

            } else {
                Toast.makeText(Rocodroms.this, "Error al modificar la zona", Toast.LENGTH_SHORT).show();
            }

        });

        btnAddRocodrom.setOnClickListener(view -> {
            startActivity(new Intent(Rocodroms.this, NouRocodrom.class));
        });

        btnEliminarRoco.setOnClickListener(view -> {
            Cursor cursor = databaseHelper.getZonesByRocodrom(idRocoSeleccionat);
            if (cursor != null && cursor.getCount() > 0) {
                showAlert("Error", "No pots eliminar un rocodrom que conté zones.");
                return;
            }

            databaseHelper.deleteRocodrom(idRocoSeleccionat);
            Toast.makeText(Rocodroms.this, "Rocodrom eliminat correctament", Toast.LENGTH_SHORT).show();
            loadSpinner(-1);
        });

        btnCancelar.setOnClickListener(view -> finish());

        rocodromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicio, long id) {
                idRocoSeleccionat = rocodromsHashMap.get(adapterView.getItemAtPosition(posicio));
                loadZonesForSelectedRocodrom();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // No action needed
            }
        });
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(Rocodroms.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void addZone() {
        String nomZona = editTextZoneName.getText().toString();
        int alturaZona = Integer.parseInt(editTextZoneHeight.getText().toString());
        int esCorda = checkBoxEsCord.isChecked() ? 1 : 0;

        boolean zonaAfegida = databaseHelper.insertZona(idRocoSeleccionat, nomZona, alturaZona, esCorda);
        if (zonaAfegida) {
            Toast.makeText(Rocodroms.this, "Zona afegida correctament", Toast.LENGTH_SHORT).show();
            loadSpinner(idRocoSeleccionat);
            clearZoneInputFields();
        } else {
            Toast.makeText(Rocodroms.this, "Error en afegir la zona", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearZoneInputFields() {
        editTextZoneName.setText("");
        editTextZoneHeight.setText("");
        checkBoxEsCord.setChecked(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSpinner(idRocoSeleccionat);
    }

    private void loadSpinner(int idRoco) {
        rocodromsHashMap = new HashMap<>();
        Cursor cursor = databaseHelper.getAllRocodroms();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_ROCO"));
                String nom = cursor.getString(cursor.getColumnIndexOrThrow("NOM_ROCO")) + ", " + cursor.getString(cursor.getColumnIndexOrThrow("POBLACIO"));
                rocodromsHashMap.put(nom, id);
            }
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(rocodromsHashMap.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rocodromSpinner.setAdapter(adapter);

        if (idRocoFromMain != -1) {
            idRoco = idRocoFromMain;
        }
        for (int i = 0; i < adapter.getCount(); i++) {
            if (rocodromsHashMap.get(adapter.getItem(i)) == idRoco) {
                rocodromSpinner.setSelection(i);
                break;
            }
        }
    }

    private void loadZonesForSelectedRocodrom() {
        Cursor cursor2 = databaseHelper.getZonesByRocodrom(idRocoSeleccionat);
        zonesGrid.removeAllViews();
        zonesLayout.setVisibility(View.GONE);
        if (cursor2 != null) {
            if (cursor2.moveToFirst()) {
                do {
                    createZoneButton(cursor2);
                } while (cursor2.moveToNext());
            } else {
                zonesGrid.removeAllViews();
                zonesLayout.setVisibility(View.VISIBLE);
            }
            cursor2.close();
        }
    }

    private void createZoneButton(Cursor cursor) {
        idZona = cursor.getInt(cursor.getColumnIndexOrThrow("ID_ZONA"));
        nomZona = cursor.getString(cursor.getColumnIndexOrThrow("NOM_ZONA"));
        alturaZona = cursor.getInt(cursor.getColumnIndexOrThrow("ALTURA_ZONA"));
        esCorda = cursor.getInt(cursor.getColumnIndexOrThrow("ZONA_CORDA"));
        rocodromZona = cursor.getInt(cursor.getColumnIndexOrThrow("ID_ROCO_FK"));

        Button btnZona = new Button(Rocodroms.this);
        btnZona.setText(nomZona);
        btnZona.setTag(createZoneBundle());

        btnZona.setOnClickListener(view -> showZoneOptionsDialog(view));
        zonesGrid.addView(btnZona);
    }

    private Bundle createZoneBundle() {
        Bundle infoBoto = new Bundle();
        infoBoto.putInt("idZona", idZona);
        infoBoto.putString("nomZona", nomZona);
        infoBoto.putInt("alturaZona", alturaZona);
        infoBoto.putInt("esCorda", esCorda);
        infoBoto.putInt("rocodromZona", rocodromZona);
        return infoBoto;
    }

    private void showZoneOptionsDialog(View view) {
        Bundle infoBoto = (Bundle) view.getTag();
        idZona = infoBoto.getInt("idZona");
        nomZona = infoBoto.getString("nomZona");
        alturaZona = infoBoto.getInt("alturaZona");
        esCorda = infoBoto.getInt("esCorda");
        rocodromZona = infoBoto.getInt("rocodromZona");

        new AlertDialog.Builder(Rocodroms.this)
                .setTitle("Modificar/Eliminar Zona")
                .setItems(new CharSequence[]{"Modificar", "Eliminar"}, (dialog, which) -> {
                    if (which == 0) {
                        modifyZone();
                    } else if (which == 1) {
                        deleteZone();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void modifyZone() {
        editTextZoneName.setText(nomZona);
        editTextZoneHeight.setText(String.valueOf(alturaZona));
        checkBoxEsCord.setChecked(esCorda == 1);
        // Show the update button and hide the new zone button
        btnModificarZona.setVisibility(View.VISIBLE);
        btnAfegirZona.setVisibility(View.GONE);
    }

    private void deleteZone() {
        new AlertDialog.Builder(Rocodroms.this)
                .setTitle("Confirm Deletion")
                .setMessage("Segur que vols eliminar aquesta zona?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User confirmed deletion
                        try {
                            int zonaEliminada = databaseHelper.deleteZona(idZona);
                            if (zonaEliminada > 0) {
                                Toast.makeText(Rocodroms.this, "Zonaeliminada", Toast.LENGTH_SHORT).show();
                                loadSpinner(idRocoSeleccionat);
                                resetIntroZones();
                            } else {
                                Toast.makeText(Rocodroms.this, "Error en eliminar la zona", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            // Handle any exceptions that may occur during deletion
                            Toast.makeText(Rocodroms.this, "Error en eliminar la zona", Toast.LENGTH_SHORT).show();
                            Log.e("DeleteZone", "Error deleting zone", e);
                        }
                    }
                })
                .setNegativeButton("CANCELAR", null) // User cancelled
                .show();
    }

    private void resetIntroZones() {
        editTextZoneName.setText("");
        editTextZoneHeight.setText("");
        checkBoxEsCord.setChecked(false);
        // Show the new zone button and hide the update button
        btnModificarZona.setVisibility(View.GONE);
        btnAfegirZona.setVisibility(View.VISIBLE);
    }
}