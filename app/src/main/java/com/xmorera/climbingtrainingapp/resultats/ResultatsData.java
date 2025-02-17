package com.xmorera.climbingtrainingapp.resultats;

public class ResultatsData {
    private String date;
    private String vies;
    private String metres;
    private String puntuacio;
    private Double mitjana;

    public ResultatsData(String date, String vies, String metres, String puntuacio, Double mitjana) {
        this.date = date;
        this.vies = vies;
        this.metres = metres;
        this.puntuacio = puntuacio;
        this.mitjana=mitjana;

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

    public Double getMitjana() { return mitjana; }

}