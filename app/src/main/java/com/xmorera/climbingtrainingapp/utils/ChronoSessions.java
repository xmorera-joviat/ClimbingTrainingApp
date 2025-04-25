package com.xmorera.climbingtrainingapp.utils;

import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe `ChronoSessions`:
 *
 * Aquesta classe s'encarrega de gestionar i recuperar estadístiques de sessions d'entrenament
 * per a una aplicació d'entrenament d'escalada. Utilitza un `DatabaseHelper` per accedir a les dades
 * de la base de dades i emmagatzema els resultats en una memòria cau per evitar consultes redundants.
 *
 * Funcionalitats principals:
 * - Recupera el nombre de sessions, el temps total de sessió i el temps total del dia per a una data específica.
 * - Utilitza una memòria cau (`Map`) per emmagatzemar els resultats i millorar l'eficiència.
 * - Gestiona errors i tanca recursos adequadament per evitar fuites de memòria.
 *
 * * Exemple d'utlitizació
 * * DatabaseHelper dbHelper = new DatabaseHelper(context);
 * ChronoSession chronoSession = new ChronoSession(dbHelper);
 *
 * String date = "01-10-2025";
 * int sessions = chronoSession.getSessio(date);
 * long lastSessionTime = chronoSession.getTempsUltimaSessio(date);
 * long totalDayTime = chronoSession.getTempsTotalDia(date);
 *
 * System.out.println("Sessions: " + sessions);
 * System.out.println("Last Session Time: " + lastSessionTime);
 * System.out.println("Total Day Time: " + totalDayTime);
 *
 */
public class ChronoSessions {
    private DatabaseHelper databaseHelper;
    private Map<String, Object> dadesSessionsDia; // Cache for session stats

    /**
     * Constructor:
     * Inicialitza la classe amb una instància de `DatabaseHelper` i crea la memòria cau.
     *
     * @param databaseHelper Instància de `DatabaseHelper` per accedir a la base de dades.
     */
    public ChronoSessions(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        this.dadesSessionsDia = new HashMap<>();
    }

    /**
     * Recupera les dades de les sessions per a una data específica des de la base de dades
     * i les emmagatzema en la memòria cau.
     *
     * @param data La data per a la qual es volen recuperar les dades de les sessions.
     */
    private void recuperaDadesSessions(String data) {
        int sessions = 0    ;
        long tempsUltimaSessio = 0;
        long tempsTotalDia = 0;
        Cursor cursor = null;

        try {
            cursor = databaseHelper.getSessionByDate(data);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    sessions++;
                    tempsUltimaSessio = cursor.getLong(cursor.getColumnIndexOrThrow("TEMPS_SESSION"));
                    tempsTotalDia += tempsUltimaSessio;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Consider logging this instead
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Emmagatzema els resultats en la memòria cau
        dadesSessionsDia.put("sessions", sessions);
        dadesSessionsDia.put("tempsUltimaSessio", tempsUltimaSessio);
        dadesSessionsDia.put("tempsTotalDia", tempsTotalDia);
    }

    /**
     * Mètode `getDadesSessionsDia`:
     * Retorna una còpia de les dades de sessions emmagatzemades en la memòria cau.
     *
     * @return Un `Map` amb les dades de sessions.
     */
    public Map<String, Object> getDadesSessionsDia() {
        return new HashMap<>(dadesSessionsDia);
    }

    /**
     * Mètode `getSessions`:
     * Retorna el nombre de sessions per a una data específica.
     *
     * @param data La data per a la qual es vol obtenir el nombre de sessions.
     * @return El nombre de sessions.
     */
    public int getSessions(String data) {
        recuperaDadesSessions(data);
        return (int) dadesSessionsDia.get("sessions");
    }

    /**
     * Mètode `getTempsUltimaSessio`:
     * Retorna el temps de la última sessió per a una data específica.
     *
     * @param data La data per a la qual es vol obtenir el temps de la última sessió.
     * @return El temps de la última sessió en mil·lisegons.
     */
    public long getTempsUltimaSessio(String data) {
        recuperaDadesSessions(data);
        return (long) dadesSessionsDia.get("tempsUltimaSessio");
    }

    /**
     * Mètode `getTempsTotalDia`:
     * Retorna el temps total de totes les sessions per a una data específica.
     *
     * @param data La data per a la qual es vol obtenir el temps total del dia.
     * * @return El temps total del dia en mil·lisegons.
     */
    public long getTempsTotalDia(String data) {
        recuperaDadesSessions(data);
        return (long) dadesSessionsDia.get("tempsTotalDia");
    }
}
