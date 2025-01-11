package com.xmorera.climbingtrainingapp.climbingData;

import java.util.HashMap;
import java.util.Map;

public class Puntuacio {
    private Map<String, Double> puntuacio;
    //penalització per intent
    private double intent= 0.1;

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

    public void afegir(String grau, double punts){
        puntuacio.put(grau, punts);
    }

    public double getPunts(String grau){
        return puntuacio.get(grau);
    }

    public boolean conté(String grau){
        return puntuacio.containsKey(grau);
    }

    public void eliminar(String grau){
        puntuacio.remove(grau);
    }

    public void mostrarPuntuacions(){
        for (Map.Entry<String, Double> entry : puntuacio.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public void setIntent(double intent){
        this.intent = intent;
    }

    public double getIntent(){
        return intent;
    }
}
