package com.xmorera.climbingtrainingapp;

public class ResultatsData {
    private String date;
    private String vies;
    private String metres;
    private String puntuacio;

    public ResultatsData(String date, String vies, String metres, String puntuacio) {
        this.date = date;
        this.vies = vies;
        this.metres = metres;
        this.puntuacio = puntuacio;
    }

    public String getDate() {
        return date;
    }

    public String getVies() {
        return vies;
    }

    public String getMetres() { return metres; }

    public String getPuntuacio() {
        return puntuacio;
    }
}