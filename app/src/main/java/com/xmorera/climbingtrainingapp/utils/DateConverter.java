package com.xmorera.climbingtrainingapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
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

    public static int getDaysBetweenDates(String date1, String date2) {
        // Convert the custom date format to ISO format
        date1 = convertCustomToISO(date1);
        date2 = convertCustomToISO(date2);

        SimpleDateFormat isoDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);
        try {
            // Parse the ISO formatted strings into Date objects
            Date startDate = isoDateFormat.parse(date1);
            Date endDate = isoDateFormat.parse(date2);

            // Use Calendar to calculate the difference in days
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            endCalendar.setTime(endDate);

            // Calculate the difference in milliseconds
            long diffInMillis = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();

            // Convert milliseconds to days
            return (int) (diffInMillis / (1000 * 60 * 60 * 24));
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // or throw an exception, or handle it as needed
        }
    }
}
