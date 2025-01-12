package com.xmorera.climbingtrainingapp.utils;

import android.content.SharedPreferences;

import com.xmorera.climbingtrainingapp.climbingData.Puntuacio;


public class Utilitats {

    public static String mitjanaGrau(Double mitjanaPuntsVies) {
        Puntuacio puntuacio = new Puntuacio();

        String mitjana = "---";
        if (mitjanaPuntsVies >= puntuacio.getPunts("8c+")) {
            mitjana = "8c+";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("8c")) {
            mitjana = "8c";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("8b+")) {
            mitjana = "8b+";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("8b")) {
            mitjana = "8b";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("8a+")) {
            mitjana = "8a+";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("8a")) {
            mitjana = "8a";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("7c+")) {
            mitjana = "7c+";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("7c")) {
            mitjana = "7c";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("7b+")) {
            mitjana = "7b+";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("7b")) {
            mitjana = "7b";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("7a+")) {
            mitjana = "7a+";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("7a")) {
            mitjana = "7a";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("6c+")) {
            mitjana = "6c+";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("6c")) {
            mitjana = "6c";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("6b+")) {
            mitjana = "6b+";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("6b")) {
            mitjana = "6b";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("6a+")) {
            mitjana = "6a+";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("6a")) {
            mitjana = "6a";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("V+")) {
            mitjana = "V+";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("V")) {
            mitjana = "V";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("IV+")) {
            mitjana = "IV+";
        } else if (mitjanaPuntsVies >= puntuacio.getPunts("IV+")) {
            mitjana = "IV";
        }
        return mitjana;
    }
}