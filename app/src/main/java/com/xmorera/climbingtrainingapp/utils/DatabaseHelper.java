package com.xmorera.climbingtrainingapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.material.shape.CutCornerTreatment;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "climbing_training.db";
    private static final int DATABASE_VERSION = 2; // Incremented version

    // Table names
    private static final String TABLE_NAME = "climbing_data";
    private static final String TABLE_ROCODROMS = "rocodroms";
    private static final String TABLE_ZONES = "zones";

    // Columns for climbing_data
    private static final String COL_ID = "ID";
    private static final String COL_DATE = "DATE";
    private static final String COL_DIFICULTAT = "DIFICULTAT";
    private static final String COL_ZONA = "ZONA";
    private static final String COL_IFINTENT = "IFINTENT";

    // Columns for rocodroms
    private static final String COL_ID_ROCO = "ID_ROCO";
    private static final String COL_NOM_ROCO = "NOM_ROCO";

    // Columns for zones
    private static final String COL_ID_ZONA = "ID_ZONA";
    private static final String COL_NOM_ZONA = "NOM_ZONA";
    private static final String COL_ALTURA_ZONA = "ALTURA_ZONA";
    private static final String COL_ID_ROCO_FK = "ID_ROCO"; // Foreign key

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create climbing_data table
        String createClimbingDataTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_DIFICULTAT + " TEXT, " +
                COL_ZONA + " TEXT, " +
                COL_IFINTENT + " INTEGER)";

        // Create rocodroms table
        String createRocodromsTable = "CREATE TABLE " + TABLE_ROCODROMS + " (" +
                COL_ID_ROCO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOM_ROCO + " TEXT NOT NULL)";

        // Create zones table
        String createZonesTable = "CREATE TABLE " + TABLE_ZONES + " (" +
                COL_ID_ZONA + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOM_ZONA + " TEXT NOT NULL, " +
                COL_ALTURA_ZONA + " INTEGER NOT NULL, " +
                COL_ID_ROCO_FK + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COL_ID_ROCO_FK + ") REFERENCES " + TABLE_ROCODROMS + "(" + COL_ID_ROCO + ") ON DELETE CASCADE)";

        // Execute the SQL statements to create the tables
        db.execSQL(createClimbingDataTable);
        db.execSQL(createRocodromsTable);
        db.execSQL(createZonesTable);

        insertInitialDataRocodroms(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROCODROMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ZONES);
            onCreate(db);

        }
        insertInitialDataRocodroms(db);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////// CRUD CLIMBING_DATA /////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////


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
        contentValues.put(COL_NOM_ROCO, nomRocodrom);
        long result = db.insert(TABLE_ROCODROMS, null, contentValues);
        db.close();
        return result != -1;
    }

    public Cursor getAllRocodroms() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_ROCODROMS, new String[]{});
    }

    public Integer deleteRocodrom(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ROCODROMS, COL_ID_ROCO+" = ?", new String[]{String.valueOf(id)});
    }

    public boolean updateRocodrom(int id, String nomRocodrom){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NOM_ROCO, nomRocodrom);
        int result = db.update(TABLE_ROCODROMS, contentValues, COL_ID_ROCO+" = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////  CRUD ZONES  ////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    public boolean insertZona(int idRocodrom, String nomZona, int alturaZona ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_ROCO_FK, idRocodrom);
        contentValues.put(COL_NOM_ZONA, nomZona);
        contentValues.put(COL_ALTURA_ZONA, alturaZona);
        long result = db.insert(TABLE_ZONES, null, contentValues);
        db.close();
        return result != -1;
    }
    public Cursor getZonesByRocodrom(int idRocodrom) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+ TABLE_ZONES+" WHERE "+COL_ID_ROCO_FK+" = ?", new String[]{String.valueOf(idRocodrom)});
    }
    public Integer deleteZona(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ZONES, COL_ID_ZONA+" = ?", new String[]{String.valueOf(id)});
    }
    public boolean updateZona(int id, int idRocodrom, String nomZona, int alturaZona){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_ROCO_FK, idRocodrom);
        contentValues.put(COL_NOM_ZONA, nomZona);
        contentValues.put(COL_ALTURA_ZONA, alturaZona);
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
        gravetatZero.put(COL_NOM_ROCO, "Gravetat Zero, Terrassa");
        int idGravetatZero = (int) db.insert(TABLE_ROCODROMS, null, gravetatZero);

        // inserir Zones G0
        ContentValues gravetatZeroAutos = new ContentValues();
        gravetatZeroAutos.put(COL_NOM_ZONA, "Autos");
        gravetatZeroAutos.put(COL_ALTURA_ZONA, 10);
        gravetatZeroAutos.put(COL_ID_ROCO_FK, idGravetatZero);
        db.insert(TABLE_ZONES, null, gravetatZeroAutos);

        ContentValues gravetatZeroCorda = new ContentValues();
        gravetatZeroCorda.put(COL_NOM_ZONA, "Corda");
        gravetatZeroCorda.put(COL_ALTURA_ZONA, 12);
        gravetatZeroCorda.put(COL_ID_ROCO_FK, idGravetatZero);
        db.insert(TABLE_ZONES, null, gravetatZeroCorda);

        ContentValues gravetatZeroShiny = new ContentValues();
        gravetatZeroShiny.put(COL_NOM_ZONA, "Shiny");
        gravetatZeroShiny.put(COL_ALTURA_ZONA, 12);
        gravetatZeroShiny.put(COL_ID_ROCO_FK, idGravetatZero);
        db.insert(TABLE_ZONES, null, gravetatZeroShiny);

        ContentValues gravetatZeroBloc = new ContentValues();
        gravetatZeroBloc.put(COL_NOM_ZONA, "Bloc");
        gravetatZeroBloc.put(COL_ALTURA_ZONA, 4);
        gravetatZeroBloc.put(COL_ID_ROCO_FK, idGravetatZero);
        db.insert(TABLE_ZONES, null, gravetatZeroBloc);

        // La panxa del bou
        ContentValues laPanxaDelBou = new ContentValues();
        laPanxaDelBou.put(COL_NOM_ROCO, "La panxa del bou, Sabadell");
        int idLaPanxaDelBou = (int) db.insert(TABLE_ROCODROMS, null, laPanxaDelBou);

        // Climbat Barcelona
        ContentValues climbatBarcelona = new ContentValues();
        climbatBarcelona.put(COL_NOM_ROCO, "Climbat, Barcelona");
        int idClimbatBarcelona = (int) db.insert(TABLE_ROCODROMS, null, climbatBarcelona);

        // Sharma Gavà
        ContentValues sharmaGava = new ContentValues();
        sharmaGava.put(COL_NOM_ROCO, "Sharma, Gavà");
        int idSharmaGava = (int) db.insert(TABLE_ROCODROMS, null, sharmaGava);
    }

}
