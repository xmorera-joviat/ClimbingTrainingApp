package com.xmorera.climbingtrainingapp.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * La classe {@code Puntuacio} gestiona els punts associats a diferents graus d'escalada.
 *
 * <p>
 * Els punts representen el nivell de dificultat i s'utilitzen per calcular l'avaluació general
 * (mitjana) dels intents en una aplicació d'entrenament d'escalada. Els valors de puntuació
 * s'incrementen en un 30% respecte al valor anterior.
 * </p>
 *
 * <p>
 * També s'afegeixen penalitzacions per intents, metres o descansos efectuats.
 * </p>
 *
 * @author Xavier Morera
 */
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

    /**
     * Constructor per defecte.
     * <p>
     * Inicialitza el mapa de punts amb els graus d'escalada i els seus corresponents valors,
     * que s'incrementen successivament un 30% respecte al valor anterior.
     * </p>
     */
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

    /**
     * Afegeix o actualitza un grau d'escalada al mapa de punts.
     *
     * @param grau  El nom del grau d'escalada (per exemple, "6a", "7b+").
     * @param punts La puntuació assignada al grau.
     */
    public void afegir(String grau, double punts){
        this.punts.put(grau, punts);
    }

    /**
     * Obté la puntuació associada a un grau determinat.
     *
     * @param dificultat El grau d'escalada del qual es vol obtenir la puntuació.
     * @return La puntuació corresponent al grau indicat.
     */
    public double getPunts(String dificultat){
        //retornar el valor de la clau corresponent de la hashmap
        //Log.d("dificultat", "getPunts: "+dificultat+" valor: "+ String.valueOf(punts.get(dificultat)));
        return punts.get(dificultat);
    }

    /**
     * Comprova si el mapa conté el grau indicat.
     *
     * @param grau El grau a comprovar.
     * @return {@code true} si el grau existeix, {@code false} en cas contrari.
     */
    public boolean conté(String grau){
        return punts.containsKey(grau);
    }

    /**
     * Elimina un grau d'escalada del mapa.
     *
     * @param grau El grau que es vol eliminar.
     */
    public void eliminar(String grau){
        punts.remove(grau);
    }

    /**
     * Mostra per consola tots els graus d'escalada amb la seva puntuació corresponent.
     */
    public void mostrarPuntuacions(){
        for (Map.Entry<String, Double> entry : punts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    /**
     * Obté la penalització per intents.
     *
     * @return La penalització per intents.
     */
    public double getIfIntent(){
        return penalitzacioIntent;
    }

    /**
     * Obté la penalització per metres.
     *
     * @return La penalització per cada metre.
     */
    public double getPenalitzacioMetres(){
        return penalitzacioMetres;
    }

    /**
     * Obté la penalització basada en el nombre de descansos.
     *
     * @param descansos El nombre de descansos.
     * @return La penalització associada als descansos.
     */
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

    /**
     * Calcula la mitjana de grau basada en una puntuació mitjana de vides.
     *
     * <p>
     * Aquest mètode compara la puntuació mitjana amb els intervals determinats pels punts de cada grau,
     * utilitzant la mitjana entre dos graus consecutius per definir el límit de cada interval.
     * </p>
     *
     * @param mitjanaPuntsVies La puntuació mitjana de vides.
     * @return El grau d'escalada corresponent a la puntuació mitjana.
     */
    public String mitjanaGrau(Double mitjanaPuntsVies) {
        
        String mitjana = "---";
        if (mitjanaPuntsVies >= getPunts("IV") && mitjanaPuntsVies < ((getPunts("IV") + getPunts("IV+")) / 2)) {
            mitjana = "IV";
        } else if (mitjanaPuntsVies >= ((getPunts("IV") + getPunts("IV+")) / 2) && mitjanaPuntsVies < ((getPunts("IV+") + getPunts("V")) / 2)){
            mitjana = "IV+";
        } else if (mitjanaPuntsVies >=((getPunts("IV+") + getPunts("V")) / 2) && mitjanaPuntsVies < ((getPunts("V") + getPunts("V+")) / 2)){
            mitjana = "V";
        } else if (mitjanaPuntsVies >=((getPunts("V") + getPunts("V+")) / 2) && mitjanaPuntsVies < ((getPunts("V+") + getPunts("6a")) / 2)){
            mitjana = "V+";
        } else if (mitjanaPuntsVies >=((getPunts("V+") + getPunts("6a")) / 2) && mitjanaPuntsVies < ((getPunts("6a") + getPunts("6a+")) / 2)){
            mitjana = "6a";
        } else if (mitjanaPuntsVies >=((getPunts("6a") + getPunts("6a+")) / 2) && mitjanaPuntsVies < ((getPunts("6a+") + getPunts("6b")) / 2)){
            mitjana = "6a+";
        } else if (mitjanaPuntsVies >=((getPunts("6a+") + getPunts("6b")) / 2) && mitjanaPuntsVies < ((getPunts("6b") + getPunts("6b+")) / 2)){
            mitjana = "6b";
        } else if (mitjanaPuntsVies >=((getPunts("6b") + getPunts("6b+")) / 2) && mitjanaPuntsVies < ((getPunts("6b+") + getPunts("6c")) / 2)){
            mitjana = "6b+";
        } else if (mitjanaPuntsVies >=((getPunts("6b+") + getPunts("6c")) / 2) && mitjanaPuntsVies < ((getPunts("6c") + getPunts("6c+")) / 2)){
            mitjana = "6c";
        } else if (mitjanaPuntsVies >=((getPunts("6c") + getPunts("6c+")) / 2) && mitjanaPuntsVies < ((getPunts("6c+") + getPunts("7a")) / 2)){
            mitjana = "6c+";
        } else if (mitjanaPuntsVies >=((getPunts("6c+") + getPunts("7a")) / 2) && mitjanaPuntsVies < ((getPunts("7a") + getPunts("7a+")) / 2)){
            mitjana = "7a";
        } else if (mitjanaPuntsVies >=((getPunts("7a") + getPunts("7a+")) / 2) && mitjanaPuntsVies < ((getPunts("7a+") + getPunts("7b")) / 2)){
            mitjana = "7a+";
        } else if (mitjanaPuntsVies >=((getPunts("7a+") + getPunts("7b")) / 2) && mitjanaPuntsVies < ((getPunts("7b") + getPunts("7b+")) / 2)){
            mitjana = "7b";
        } else if (mitjanaPuntsVies >=((getPunts("7b") + getPunts("7b+")) / 2) && mitjanaPuntsVies < ((getPunts("7b+") + getPunts("7c")) / 2)){
            mitjana = "7b+";
        } else if (mitjanaPuntsVies >=((getPunts("7b+") + getPunts("7c")) / 2) && mitjanaPuntsVies < ((getPunts("7c") + getPunts("7c+")) / 2)){
            mitjana = "7c";
        } else if (mitjanaPuntsVies >=((getPunts("7c") + getPunts("7c+")) / 2) && mitjanaPuntsVies < ((getPunts("7c+") + getPunts("8a")) / 2)){
            mitjana = "7c+";
        }  else if (mitjanaPuntsVies >=((getPunts("7c+") + getPunts("8a")) / 2) && mitjanaPuntsVies < ((getPunts("8a") + getPunts("8a+")) / 2)){
            mitjana = "8a";
        } else if (mitjanaPuntsVies >=((getPunts("8a") + getPunts("8a+")) / 2) && mitjanaPuntsVies < ((getPunts("8a+") + getPunts("8b")) / 2)){
            mitjana = "8a+";
        } else if (mitjanaPuntsVies >=((getPunts("8a+") + getPunts("8b")) / 2) && mitjanaPuntsVies < ((getPunts("8b") + getPunts("8b+")) / 2)){
            mitjana = "8b";
        } else if (mitjanaPuntsVies >=((getPunts("8b") + getPunts("8b+")) / 2) && mitjanaPuntsVies < ((getPunts("8b+") + getPunts("8c")) / 2)){
            mitjana = "8b+";
        } else if (mitjanaPuntsVies >=((getPunts("8b+") + getPunts("8c")) / 2) && mitjanaPuntsVies < ((getPunts("8c") + getPunts("8c+")) / 2)){
            mitjana = "8c";
        } else if (mitjanaPuntsVies >=((getPunts("8c") + getPunts("8c+")) / 2)){
            mitjana = "8c+";
        }

        return mitjana;
    }

}
