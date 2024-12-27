package com.xmorera.climbingtrainingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import java.util.ArrayList;

public class ReconeixementDeVeu {

    private final Activity activity;
    private final ActivityResultLauncher<Intent> voiceInputLauncher;

    public ReconeixementDeVeu(Activity activity, ActivityResultLauncher<Intent> voiceInputLauncher) {
        this.activity = activity;
        this.voiceInputLauncher = voiceInputLauncher;
    }

    /**
     * Starts the voice input process.
     */
    public void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ca-ES"); // Set language to Catalan
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Digues el grau, la zona (i si és intent...)");

        try {
            voiceInputLauncher.launch(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "Ho sento, el reconeixement de veu no és compatible en aquest dispositiu", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the result from the voice input.
     *
     * @param resultCode The result code from the activity.
     * @param data The intent data returned from the voice recognition activity.
     */
    public void handleVoiceInputResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String comandaDeVeu = results.get(0);
                // Process the recognized command
                adCommandToDatabase(comandaDeVeu); // Call your method to handle the command
            } else {
                Toast.makeText(activity, "No s'han reconegut resultats", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Error en el reconeixement de veu", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Adds the recognized command to the database.
     *
     * @param comandaDeVeu The recognized voice command.
     */
    private void adCommandToDatabase(String comandaDeVeu) {
        // Implement your logic to add the command to the database
        // For example, you might want to parse the command and insert it into your database
        // databaseHelper.insertData(...);
        Toast.makeText(activity, comandaDeVeu, Toast.LENGTH_SHORT).show();

        String[] cercarGrau1 = {"4", "quatre", "quart", "5", "cinquè"," quinto","6","sis", "cis", "sisè","7","set","setè", "8", "vuit", "buit", "vuitè"};

        StringBuilder resultats = new StringBuilder();
        resultats.append("paraules trobades\n");
        //mostrar els resultats de l'entrenament en quadre de dialog
        for (String grau : cercarGrau1){
            if(comandaDeVeu.contains(grau)){
                resultats.append(grau).append(", ");
            }
        }
        if(resultats.length() > 0){
            showResultsDialog(resultats.toString());
        }
    }

    private void showResultsDialog(String resultats) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Resultats");
        builder.setMessage(resultats);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}