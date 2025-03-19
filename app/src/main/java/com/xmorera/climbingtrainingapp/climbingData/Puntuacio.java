package com.xmorera.climbingtrainingapp.climbingData;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Puntuacio {
    private Map<String, Double> punts;
    //la puntuació s'ha de dividir entre x per baixar y graus:
    // 1.30  --> 1 grau
    // 1.69  --> 2 graus
    // 2.197 --> 3 graus
    // 2.856 --> 4 graus
    // 3.713 --> 5 graus
    private double penalitzacioIntent = 2.197;
    private double penalitzacioMetres = 0.5;

    public Puntuacio(){
        punts = new HashMap<>();

        //afegim els graus i les puntuacions
        //el càlcul es fa incrementant l'anterior un 30%
        // grau = anterior*1.3
        punts.put("IV",   1.0);
        punts.put("IV+",  1.3);
        punts.put("V",    1.69);
        punts.put("V+",   2.197);
        punts.put("6a",   2.851);
        punts.put("6a+",  3.71293);
        punts.put("6b",   4.826809);
        punts.put("6b+",  6.2748517);
        punts.put("6c",   8.15730721);
        punts.put("6c+", 10.60449937);
        punts.put("7a",  13.78584918);
        punts.put("7a+", 17.92160394);
        punts.put("7b",  23.29808512);
        punts.put("7b+", 30.28751066);
        punts.put("7c",  39.37376386);
        punts.put("7c+", 51.18589301);
        punts.put("8a",  66.54166092);
        punts.put("8a+", 86.50415919);
        punts.put("8b", 112.455407);
        punts.put("8b+",146.192029);
        punts.put("8c", 190.0496377);
        punts.put("8c+",247.0645291);

    }

    public void afegir(String grau, double punts){
        this.punts.put(grau, punts);
    }

    public double getPunts(String dificultat){
        //retornar el valor de la clau corresponent de la hashmap
        //Log.d("dificultat", "getPunts: "+dificultat+" valor: "+ String.valueOf(punts.get(dificultat)));
        return punts.get(dificultat);
    }

    public boolean conté(String grau){
        return punts.containsKey(grau);
    }

    public void eliminar(String grau){
        punts.remove(grau);
    }

    public void mostrarPuntuacions(){
        for (Map.Entry<String, Double> entry : punts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public double getIfIntent(){
        return penalitzacioIntent;
    }

    public double getPenalitzacioMetres(){
        return penalitzacioMetres;
    }

    public double getPenalitzacioDescansos (int descansos){
        double penalitzacio = 1.3;
        switch (descansos){
            case 1:
                penalitzacio = 1.3;
                break;
            case 2:
                penalitzacio = 1.69;
                break;
            case 3:
                penalitzacio = 2.197;
                break;
            default:
                penalitzacio = 2.197;
                break;
        }
        return penalitzacio;
    }
}
