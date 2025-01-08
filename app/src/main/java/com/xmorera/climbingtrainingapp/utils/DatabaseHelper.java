package com.xmorera.climbingtrainingapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "climbing_training.db";
    private static final String TABLE_NAME = "climbing_data";
    private static final String COL_ID = "ID";
    private static final String COL_DATE = "DATE";
    private static final String COL_DIFICULTAT = "DIFICULTAT";
    private static final String COL_ZONA = "ZONA";
    private static final String COL_IFINTENT = "IFINTENT";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_DIFICULTAT + " TEXT, " +
                COL_ZONA + " TEXT, " +
                COL_IFINTENT + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String date, String dificultat, String zona, int ifIntent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE, date);
        contentValues.put(COL_DIFICULTAT, dificultat);
        contentValues.put(COL_ZONA, zona);
        contentValues.put(COL_IFINTENT, ifIntent);
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_DATE + " DESC", new String[]{});

    }

    public Cursor getDayData(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_DATE + " = ? ORDER BY ID DESC";
        return db.rawQuery(query, new String[]{date});
    }


    public void closeDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public boolean updateData(int id, String date, String dificultat, String zona, int ifIntent){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE, date);
        contentValues.put(COL_DIFICULTAT, dificultat);
        contentValues.put(COL_ZONA, zona);
        contentValues.put(COL_IFINTENT, ifIntent);
        int result = db.update(TABLE_NAME, contentValues, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0; //retorna True si la actualització s'ha realitzat correctament
    }

    public Integer deleteData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getDataByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_DATE + " = ? ";
        return db.rawQuery(query, new String[]{date});
    }

    public Cursor getDataBetweenDates(String startDate, String endDate){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " +
                TABLE_NAME + " WHERE " + COL_DATE + " BETWEEN ? AND ? ORDER BY " + COL_DATE + " DESC";
        return db.rawQuery(query, new String[]{startDate, endDate});
    }

    /**
     * getUniqueDates
     * @param startDate
     * @param endDate
     * @return Cursor amb cada una de les dates que tenen dades entre les dates senyalades */
    public Cursor getUniqueDates(String startDate, String endDate){
        Log.d("Resultats", "Data inicial: " + startDate + " Data final: " + endDate);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT DISTINCT " + COL_DATE + " FROM " + TABLE_NAME +
                    " WHERE " + COL_DATE + " BETWEEN ? AND ? ORDER BY " + COL_DATE + " DESC ";
            cursor = db.rawQuery(query, new String[]{startDate, endDate});
        } catch (Exception e){
            e.printStackTrace();
        }
        return cursor;
    }
}
