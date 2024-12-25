package com.xmorera.climbingtrainingapp;

public class ClimbingData {
    private String id;
    private String date;
    private String dificultat;
    private String zona;
    private int ifIntent;
    private String puntuacio;

    public ClimbingData(String id, String date, String via, String zona, int intent, String puntuacio) {
        this.id = id;
        this.date = date;
        this.dificultat = via;
        this.zona = zona;
        this.ifIntent = intent;
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

public int getIfIntent() {
    return ifIntent;
}

public String getPuntuacio() { return puntuacio;}

}
