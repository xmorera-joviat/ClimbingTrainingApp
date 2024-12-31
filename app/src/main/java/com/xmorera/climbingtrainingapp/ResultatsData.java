package com.xmorera.climbingtrainingapp;

public class ResultatsData {
    private String date;
    private String vies;
    private String puntuacio;

    public ResultatsData(String date, String vies, String puntuacio) {
        this.date = date;
        this.vies = vies;
        this.puntuacio = puntuacio;
    }

    public String getDate() {
        return date;
    }

    public String getVies() {
        return vies;
    }

    public String getPuntuacio() {
        return puntuacio;
    }
}