package com.xmorera.climbingtrainingapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateConverter
 * Classe per convertir dates entre formats
 *  */
public class DateConverter {
    private static final String CUSTOM_DATE_FORMAT = "dd/MM/yyyy";
    private static final String ISO_DATE_FORMAT = "yyyy-MM-dd";


    /**
     * convertCustomToISO
     * Converteix una data de dd/MM/yyyy a yyyy-MM-dd
     * @param dateString data en format dd/MM/yyyy (custom)
     * @return data en format yyyy-MM-dd (ISO)
     * */
    public static String convertCustomToISO(String dateString){
        SimpleDateFormat customDateFormat = new SimpleDateFormat(CUSTOM_DATE_FORMAT);
        SimpleDateFormat isoDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);
        String result = null;
        try {
            Date date = customDateFormat.parse(dateString);
            result = isoDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * convertISOToCustom
     * Converteix una data de yyyy-MM-dd a dd/MM/yyyy
     * @param dateString data en format yyyy-MM-dd (ISO)
     * @return data en format dd/MM/yyyy (custom)
     *  */
    public static String convertISOToCustom(String dateString){
        SimpleDateFormat isoDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);
        SimpleDateFormat customDateFormat = new SimpleDateFormat(CUSTOM_DATE_FORMAT);
        String result = null;
        try {
            Date date = isoDateFormat.parse(dateString);
            result = customDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
