package com.xmorera.climbingtrainingapp.climbingData;

import java.util.HashMap;
import java.util.Map;

public class Puntuacio {
    private Map<String, Double> puntuacio;
    //la puntuació s'ha de dividir entre x per baixar y graus:
    // 1.30  --> 1 grau
    // 1.69  --> 2 graus
    // 2.197 --> 3 graus
    // 2.856 --> 4 graus
    // 3.713 --> 5 graus
    private double penalitzacioIntent = 2.197; // 3 graus
    private double penalitzacioDescansos = 1.3; // 1 grau per descans
    private double penalitzacioMetres = 0.5;

    public Puntuacio(){
        puntuacio = new HashMap<>();

        //afegim els graus i les puntuacions
        //el càlcul es fa incrementant l'anterior un 30%
        // grau = anterior*1.3
        puntuacio.put("IV",4.0);
        puntuacio.put("IV+",5.2);
        puntuacio.put("V", 6.8);
        puntuacio.put("V+",8.8);
        puntuacio.put("6a",11.4);
        puntuacio.put("6a+",14.9);
        puntuacio.put("6b",19.3);
        puntuacio.put("6b+",25.1);
        puntuacio.put("6c", 32.6);
        puntuacio.put("6c+",42.4);
        puntuacio.put("7a",55.1);
        puntuacio.put("7a+",71.7);
        puntuacio.put("7b",93.2);
        puntuacio.put("7b+",121.2);
        puntuacio.put("7c", 157.5);
        puntuacio.put("7c+",204.7);
        puntuacio.put("8a",266.2);
        puntuacio.put("8a+",346.0);
        puntuacio.put("8b",449.8);
        puntuacio.put("8b+",584.8);
        puntuacio.put("8c", 760.2);
        puntuacio.put("8c+",988.3);

    }

    public void afegirGrau(String grau, double punts){
        puntuacio.put(grau, punts);
    }

    public double getPuntsGrau(String grau){
        return puntuacio.get(grau);
    }

    public boolean contéGrau(String grau){
        return puntuacio.containsKey(grau);
    }

    public void eliminarGrau(String grau){
        puntuacio.remove(grau);
    }

    public void mostrarPuntuacions(){
        for (Map.Entry<String, Double> entry : puntuacio.entrySet()) {
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
        double penalitzacio = penalitzacioDescansos;
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
            case 4:
                penalitzacio = 2.856;
                break;
            default:
                penalitzacio = 3.713;
                break;
        }
        return penalitzacio;
    }
}
