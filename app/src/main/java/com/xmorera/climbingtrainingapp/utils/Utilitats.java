package com.xmorera.climbingtrainingapp.utils;

import android.content.SharedPreferences;

import com.xmorera.climbingtrainingapp.climbingData.Puntuacio;


public class Utilitats {

    // càlcul de la mitjana diaria de grau.
    public static String mitjanaGrau(Double mitjanaPuntsVies) {
        Puntuacio puntuacio = new Puntuacio();

        String mitjana = "---";
        if (mitjanaPuntsVies >= puntuacio.getPunts("IV") && mitjanaPuntsVies < ((puntuacio.getPunts("IV") + puntuacio.getPunts("IV+")) / 2)) {
            mitjana = "IV";
        } else if (mitjanaPuntsVies >= ((puntuacio.getPunts("IV") + puntuacio.getPunts("IV+")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("IV+") + puntuacio.getPunts("V")) / 2)){
            mitjana = "IV+";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("IV+") + puntuacio.getPunts("V")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("V") + puntuacio.getPunts("V+")) / 2)){
            mitjana = "V";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("V") + puntuacio.getPunts("V+")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("V+") + puntuacio.getPunts("6a")) / 2)){
            mitjana = "V+";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("V+") + puntuacio.getPunts("6a")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("6a") + puntuacio.getPunts("6a+")) / 2)){
            mitjana = "6a";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("6a") + puntuacio.getPunts("6a+")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("6a+") + puntuacio.getPunts("6b")) / 2)){
            mitjana = "6a+";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("6a+") + puntuacio.getPunts("6b")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("6b") + puntuacio.getPunts("6b+")) / 2)){
            mitjana = "6b";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("6b") + puntuacio.getPunts("6b+")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("6b+") + puntuacio.getPunts("6c")) / 2)){
            mitjana = "6b+";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("6b+") + puntuacio.getPunts("6c")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("6c") + puntuacio.getPunts("6c+")) / 2)){
            mitjana = "6c";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("6c") + puntuacio.getPunts("6c+")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("6c+") + puntuacio.getPunts("7a")) / 2)){
            mitjana = "6c+";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("6c+") + puntuacio.getPunts("7a")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("7a") + puntuacio.getPunts("7a+")) / 2)){
            mitjana = "7a";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("7a") + puntuacio.getPunts("7a+")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("7a+") + puntuacio.getPunts("7b")) / 2)){
            mitjana = "7a+";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("7a+") + puntuacio.getPunts("7b")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("7b") + puntuacio.getPunts("7b+")) / 2)){
            mitjana = "7b";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("7b") + puntuacio.getPunts("7b+")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("7b+") + puntuacio.getPunts("7c")) / 2)){
            mitjana = "7b+";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("7b+") + puntuacio.getPunts("7c")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("7c") + puntuacio.getPunts("7c+")) / 2)){
            mitjana = "7c";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("7c") + puntuacio.getPunts("7c+")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("7c+") + puntuacio.getPunts("8a")) / 2)){
            mitjana = "7c+";
        }  else if (mitjanaPuntsVies >=((puntuacio.getPunts("7c+") + puntuacio.getPunts("8a")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("8a") + puntuacio.getPunts("8a+")) / 2)){
            mitjana = "8a";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("8a") + puntuacio.getPunts("8a+")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("8a+") + puntuacio.getPunts("8b")) / 2)){
            mitjana = "8a+";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("8a+") + puntuacio.getPunts("8b")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("8b") + puntuacio.getPunts("8b+")) / 2)){
            mitjana = "8b";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("8b") + puntuacio.getPunts("8b+")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("8b+") + puntuacio.getPunts("8c")) / 2)){
            mitjana = "8b+";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("8b+") + puntuacio.getPunts("8c")) / 2) && mitjanaPuntsVies < ((puntuacio.getPunts("8c") + puntuacio.getPunts("8c+")) / 2)){
            mitjana = "8c";
        } else if (mitjanaPuntsVies >=((puntuacio.getPunts("8c") + puntuacio.getPunts("8c+")) / 2)){
            mitjana = "8c+";
        }

        return mitjana;
    }

}