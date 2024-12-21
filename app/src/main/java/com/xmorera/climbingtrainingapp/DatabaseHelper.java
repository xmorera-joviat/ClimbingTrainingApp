package com.xmorera.climbingtrainingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "climbing_training.db";
    private static final String TABLE_NAME = "climbing_data";
    private static final String COL_ID = "ID";
    private static final String COL_DATE = "DATE";
    private static final String COL_VIA = "VIA";
    private static final String COL_PARET = "PARET";
    private static final String COL_INTENT = "INTENT";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_VIA + " TEXT, " +
                COL_PARET + " TEXT, " +
                COL_INTENT + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String date, String via, String paret, int intent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE, date);
        contentValues.put(COL_VIA, via);
        contentValues.put(COL_PARET, paret);
        contentValues.put(COL_INTENT, intent);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

}
