package com.xmorera.climbingtrainingapp.utils;

import com.xmorera.climbingtrainingapp.climbingData.Puntuacio;


public class Utilitats {

    public static String mitjanaGrau(Double mitjanaPuntsVies) {
        Puntuacio puntuacio = new Puntuacio();

        String mitjana = "---";
        if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("8c+")) {
            mitjana = "8c+";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("8c")) {
            mitjana = "8c";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("8b+")) {
            mitjana = "8b+";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("8b")) {
            mitjana = "8b";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("8a+")) {
            mitjana = "8a+";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("8a")) {
            mitjana = "8a";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("7c+")) {
            mitjana = "7c+";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("7c")) {
            mitjana = "7c";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("7b+")) {
            mitjana = "7b+";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("7b")) {
            mitjana = "7b";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("7a+")) {
            mitjana = "7a+";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("7a")) {
            mitjana = "7a";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("6c+")) {
            mitjana = "6c+";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("6c")) {
            mitjana = "6c";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("6b+")) {
            mitjana = "6b+";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("6b")) {
            mitjana = "6b";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("6a+")) {
            mitjana = "6a+";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("6a")) {
            mitjana = "6a";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("V+")) {
            mitjana = "V+";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("V")) {
            mitjana = "V";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("IV+")) {
            mitjana = "IV+";
        } else if (mitjanaPuntsVies >= puntuacio.getPuntsGrau("IV+")) {
            mitjana = "IV";
        }
        return mitjana;
    }
}