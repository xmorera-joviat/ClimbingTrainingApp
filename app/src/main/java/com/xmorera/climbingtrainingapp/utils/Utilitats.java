package com.xmorera.climbingtrainingapp.utils;

import android.content.SharedPreferences;


public class Utilitats {

    public static String mitjanaGrau(Double puntsVies, SharedPreferences preferences) {

        String mitjana = "---";
        if (puntsVies >= Double.parseDouble(preferences.getString("8c+", "error").replace(",", "."))) {
            mitjana = "8c+";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("8c", "error").replace(",", "."))) {
            mitjana = "8c";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("8b+", "error").replace(",", "."))) {
            mitjana = "8b+";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("8b", "error").replace(",", "."))) {
            mitjana = "8b";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("8a+", "error").replace(",", "."))) {
            mitjana = "8a+";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("8a", "error").replace(",", "."))) {
            mitjana = "8a";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("7c+", "error").replace(",", "."))) {
            mitjana = "7c+";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("7c", "error").replace(",", "."))) {
            mitjana = "7c";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("7b+", "error").replace(",", "."))) {
            mitjana = "7b+";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("7b", "error").replace(",", "."))) {
            mitjana = "7b";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("7a+", "error").replace(",", "."))) {
            mitjana = "7a+";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("7a", "error").replace(",", "."))) {
            mitjana = "7a";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("6c+", "error").replace(",", "."))) {
            mitjana = "6c+";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("6c", "error").replace(",", "."))) {
            mitjana = "6c";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("6b+", "error").replace(",", "."))) {
            mitjana = "6b+";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("6b", "error").replace(",", "."))) {
            mitjana = "6b";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("6a+", "error").replace(",", "."))) {
            mitjana = "6a+";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("6a", "error").replace(",", "."))) {
            mitjana = "6a";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("V+", "error").replace(",", "."))) {
            mitjana = "V+";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("V", "error").replace(",", "."))) {
            mitjana = "V";
        } else if (puntsVies >= Double.parseDouble(preferences.getString("IV", "error").replace(",", "."))) {
            mitjana = "IV";
        }
        return mitjana;
    }
}