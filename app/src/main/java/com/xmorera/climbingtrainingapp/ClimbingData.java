package com.xmorera.climbingtrainingapp;

public class ClimbingData {
    private String date;
    private String via;
    private String zona;
    private int intent;

    public ClimbingData(String date, String via, String zona, int intent) {
        this.date = date;
        this.via = via;
        this.zona = zona;
        this.intent = intent;
        }

    public String getDate() {
        return date;
    }

    public String getVia() {
        return via;
    }

    public String getZona() {
        return zona;
    }

    public int getIntent() {
        return intent;
    }
}
