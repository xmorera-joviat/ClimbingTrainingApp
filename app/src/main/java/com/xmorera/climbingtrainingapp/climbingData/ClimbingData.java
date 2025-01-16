package com.xmorera.climbingtrainingapp.climbingData;

public class ClimbingData {
    private final String id;
    private final String date;
    private final String dificultat;
    private final String zona;
    private final String nomRocoReduit;
    private final int ifIntent;
    private final int descansos;
    private final String puntuacio;

    public ClimbingData(String id, String date, String via, String zona, String nomRocoReduit, int intent, int descansos, String puntuacio) {
        this.id = id;
        this.date = date;
        this.dificultat = via;
        this.zona = zona;
        this.nomRocoReduit = nomRocoReduit;
        this.ifIntent = intent;
        this.descansos = descansos;
        this.puntuacio = puntuacio;
    }

    public String getId() { return id;}

    public String getDate() {
    return date;
}

public String getDificultat() {
    return dificultat;
}

public String getZona() {
    return zona;
}

public String getNomRocoReduit() { return nomRocoReduit;}

public int getIfIntent() {
    return ifIntent;
}

public int getDescansos() { return descansos;}

public String getPuntuacio() { return puntuacio;}

}
