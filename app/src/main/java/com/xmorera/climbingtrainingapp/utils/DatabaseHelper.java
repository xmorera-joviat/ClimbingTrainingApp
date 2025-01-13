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

        String createRocodromsTable = "CREATE TABLE IF NOT EXISTS rocodroms (" +
                "ID_ROCO INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NOM_ROCO TEXT NOT NULL)";

        String createZonesTable = "CREATE TABLE IF NOT EXISTS zones (" +
                "ID_ZONA INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NOM_ZONA TEXT NOT NULL, " +
                "ALTURA_ZONA INTEGER NOT NULL, " +
                "ID_ROCO INTEGER NOT NULL, " +
                "FOREIGN KEY(ID_ROCO) REFERENCES rocodroms(ID_ROCO) ON DELETE CASCADE)";

        db.execSQL(createTable);
        db.execSQL(createRocodromsTable);
        db.execSQL(createZonesTable);

        //inserir dades inicials sobre rocòdroms
        insertInitialDataRocodroms(db);

    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////// CRUD CLIMBING_DATA /////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String date, String dificultat, String zona, int ifIntent) {
        //canviem el format de la data abans d'introduir-la a SQLite
        String dateISO = DateConverter.convertCustomToISO(date);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE, dateISO);
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
        //canviem el format de la data abans d'introduir-la a SQLite
        String dateISO = DateConverter.convertCustomToISO(date);

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_DATE + " = ? ORDER BY ID DESC";
        return db.rawQuery(query, new String[]{dateISO});
    }


    public void closeDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public boolean updateData(int id, String date, String dificultat, String zona, int ifIntent){
        //canviem el format de la data abans d'introduir-la a SQLite
        String dateISO = DateConverter.convertCustomToISO(date);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE, dateISO);
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
        //canviem el format de la data abans d'introduir-la a SQLite
        String dateISO = DateConverter.convertCustomToISO(date);

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_DATE + " = ? ";
        return db.rawQuery(query, new String[]{dateISO});
    }

    public Cursor getDataBetweenDates(String startDate, String endDate){
        //canviem el format de la data abans d'introduir-la a SQLite
        String startDateISO = DateConverter.convertCustomToISO(startDate);
        String endDateISO = DateConverter.convertCustomToISO(endDate);

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " +
                TABLE_NAME + " WHERE " + COL_DATE + " BETWEEN ? AND ? ORDER BY " + COL_DATE + " DESC";
        return db.rawQuery(query, new String[]{startDateISO, endDateISO});
    }

    /**
     * getUniqueDates
     * @param startDate
     * @param endDate
     * @return Cursor amb cada una de les dates que tenen dades entre les dates senyalades */
    public Cursor getUniqueDates(String startDate, String endDate){
        //canviem el format de la data abans d'introduir-la a SQLite
        String startDateISO = DateConverter.convertCustomToISO(startDate);
        String endDateISO = DateConverter.convertCustomToISO(endDate);

        //Log.d("Resultats", "Data inicial: " + startDate + " Data final: " + endDate);
        //Log.d("Resultats", "Data inicial: " + startDateISO + " Data final: " + endDateISO);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT DISTINCT " + COL_DATE + " FROM " + TABLE_NAME +
                    " WHERE " + COL_DATE + " BETWEEN ? AND ? ORDER BY " + COL_DATE + " DESC ";
            cursor = db.rawQuery(query, new String[]{startDateISO, endDateISO});
        } catch (Exception e){
            e.printStackTrace();
        }
        return cursor;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  CRUD ROCODROM  ////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    public boolean insertRocodrom(String nomRocodrom) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NOM_ROCO", nomRocodrom);
        long result = db.insert("rocodroms", null, contentValues);
        db.close();
        return result != -1;
    }

    public Cursor getAllRocodroms() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM rocodroms", new String[]{});
    }

    public Integer deleteRocodrom(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("rocodroms", "ID_ROCO = ?", new String[]{String.valueOf(id)});
    }

    public boolean updateRocodrom(int id, String nomRocodrom){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NOM_ROCO", nomRocodrom);
        int result = db.update("rocodroms", contentValues, "ID_ROCO = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////  CRUD ZONES  ////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    public boolean insertZona(int idRocodrom, String nomZona, int alturaZona ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_ROCO", idRocodrom);
        contentValues.put("NOM_ZONA", nomZona);
        contentValues.put("ALTURA_ZONA", alturaZona);
        long result = db.insert("zones", null, contentValues);
        db.close();
        return result != -1;
    }
    public Cursor getZonesByRocodrom(int idRocodrom) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM zones WHERE ID_ROCO = ?", new String[]{String.valueOf(idRocodrom)});
    }
    public Integer deleteZona(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("zones", "ID_ZONA = ?", new String[]{String.valueOf(id)});
    }
    public boolean updateZona(int id, int idRocodrom, String nomZona, int alturaZona){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_ROCO", idRocodrom);
        contentValues.put("NOM_ZONA", nomZona);
        contentValues.put("ALTURA_ZONA", alturaZona);
        int result = db.update("zones", contentValues, "ID_ZONA = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////  introducció de dades inicials  ////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    private void insertInitialDataRocodroms(SQLiteDatabase db) {
        // gravetat zero
        ContentValues gravetatZero = new ContentValues();
        gravetatZero.put("NOM_ROCO", "Gravetat Zero, Terrassa");
        int idGravetatZero = (int) db.insert("rocodroms", null, gravetatZero);

        // inserir Zones G0
        ContentValues gravetatZeroAutos = new ContentValues();
        gravetatZeroAutos.put("NOM_ZONA", "Autos");
        gravetatZeroAutos.put("ALTURA_ZONA", 10);
        gravetatZeroAutos.put("ID_ROCO", idGravetatZero);
        db.insert("zones", null, gravetatZeroAutos);

        ContentValues gravetatZeroCorda = new ContentValues();
        gravetatZeroCorda.put("NOM_ZONA", "Corda");
        gravetatZeroCorda.put("ALTURA_ZONA", 12);
        gravetatZeroCorda.put("ID_ROCO", idGravetatZero);
        db.insert("zones", null, gravetatZeroCorda);

        ContentValues gravetatZeroShiny = new ContentValues();
        gravetatZeroShiny.put("NOM_ZONA", "Shiny");
        gravetatZeroShiny.put("ALTURA_ZONA", 12);
        gravetatZeroShiny.put("ID_ROCO", idGravetatZero);
        db.insert("zones", null, gravetatZeroShiny);

        ContentValues gravetatZeroBloc = new ContentValues();
        gravetatZeroBloc.put("NOM_ZONA", "Bloc");
        gravetatZeroBloc.put("ALTURA_ZONA", 4);
        gravetatZeroBloc.put("ID_ROCO", idGravetatZero);
        db.insert("zones", null, gravetatZeroBloc);

        // La panxa del bou
        ContentValues laPanxaDelBou = new ContentValues();
        laPanxaDelBou.put("NOM_ROCO", "La panxa del bou, Sabadell");
        int idLaPanxaDelBou = (int) db.insert("rocodroms", null, laPanxaDelBou);

        // Climbat Barcelona
        ContentValues climbatBarcelona = new ContentValues();
        climbatBarcelona.put("NOM_ROCO", "Climbat, Barcelona");
        int idClimbatBarcelona = (int) db.insert("rocodroms", null, climbatBarcelona);

        // Sharma Gavà
        ContentValues sharmaGava = new ContentValues();
        sharmaGava.put("NOM_ROCO", "Sharma, Gavà");
        int idSharmaGava = (int) db.insert("rocodroms", null, sharmaGava);
    }

}
