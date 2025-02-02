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
        punts.put("IV",4.0);
        punts.put("IV+",5.2);
        punts.put("V", 6.8);
        punts.put("V+",8.8);
        punts.put("6a",11.4);
        punts.put("6a+",14.9);
        punts.put("6b",19.3);
        punts.put("6b+",25.1);
        punts.put("6c", 32.6);
        punts.put("6c+",42.4);
        punts.put("7a",55.1);
        punts.put("7a+",71.7);
        punts.put("7b",93.2);
        punts.put("7b+",121.2);
        punts.put("7c", 157.5);
        punts.put("7c+",204.7);
        punts.put("8a",266.2);
        punts.put("8a+",346.0);
        punts.put("8b",449.8);
        punts.put("8b+",584.8);
        punts.put("8c", 760.2);
        punts.put("8c+",988.3);

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
