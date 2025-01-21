package com.xmorera.climbingtrainingapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "climbing_training.db";
    private static final int DATABASE_VERSION = 5; // Incremented version

    // Table names
    private static final String TABLE_CLIMBING_DATA = "climbing_data";
    private static final String TABLE_ROCODROMS = "rocodroms";
    private static final String TABLE_ZONES = "zones";
    private static final String TABLE_RANKING = "ranking";

    // Columns for climbing_data
    private static final String COL_ID_CD = "ID_CD";
    private static final String COL_DATE = "DATE";
    private static final String COL_ID_ZONA_FK = "ID_ZONA_FK";
    private static final String COL_DIFICULTAT = "DIFICULTAT";
    private static final String COL_IFINTENT = "IFINTENT";
    private static final String COL_DESCANSOS = "DESCANSOS";

    // Columns for rocodroms
    private static final String COL_ID_ROCO = "ID_ROCO";
    private static final String COL_NOM_ROCO = "NOM_ROCO";
    private static final String COL_NOM_ROCO_REDUIT = "NOM_ROCO_REDUIT";
    private static final String COL_POBLACIO = "POBLACIO";

    // Columns for zones
    private static final String COL_ID_ZONA = "ID_ZONA";
    private static final String COL_NOM_ZONA = "NOM_ZONA";
    private static final String COL_ALTURA_ZONA = "ALTURA_ZONA";
    private static final String COL_ZONA_CORDA = "ZONA_CORDA";
    private static final String COL_ID_ROCO_FK = "ID_ROCO_FK"; // Foreign key

    // Columns for ranking
    private static final String COL_ID_RANKING = "ID_RANKING";
    private static final String COL_DATE_RANKING = "DATE_RANKING";
    private static final String COL_PUNTS_RANKING = "PUNTS_RANKING";
    private static final String COL_VIES_RANKING = "VIES_RANKING";
    private static final String COL_METRES_RANKING = "METRES_RANKING";
    private static final String COL_MITJANA_RANKING = "MITJANA_RANKING";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create climbing_data table
        String createClimbingDataTable = "CREATE TABLE " + TABLE_CLIMBING_DATA + " (" +
                COL_ID_CD + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_DIFICULTAT + " TEXT, " +
                COL_ID_ZONA_FK + " INTEGER NOT NULL, " +
                COL_IFINTENT + " INTEGER," +
                COL_DESCANSOS + " INTEGER," +
                "FOREIGN KEY(" + COL_ID_ZONA_FK + ") REFERENCES " + TABLE_ZONES + "(" + COL_ID_ZONA + "))";

        // Create rocodroms table
        String createRocodromsTable = "CREATE TABLE " + TABLE_ROCODROMS + " (" +
                COL_ID_ROCO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOM_ROCO + " TEXT NOT NULL," +
                COL_NOM_ROCO_REDUIT + " TEXT," +
                COL_POBLACIO + " TEXT)";

        // Create zones table
        String createZonesTable = "CREATE TABLE " + TABLE_ZONES + " (" +
                COL_ID_ZONA + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOM_ZONA + " TEXT NOT NULL, " +
                COL_ALTURA_ZONA + " INTEGER NOT NULL, " +
                COL_ZONA_CORDA + " INTEGER DEFAULT 0, " +
                COL_ID_ROCO_FK + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COL_ID_ROCO_FK + ") REFERENCES " + TABLE_ROCODROMS + "(" + COL_ID_ROCO + "))";

        // Cretae ranking table
        String createRankingTable = "CREATE TABLE " + TABLE_RANKING + " (" +
                COL_ID_RANKING + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE_RANKING + " TEXT UNIQUE, " +
                COL_PUNTS_RANKING + " REAL, " +
                COL_VIES_RANKING + " INTEGER, " +
                COL_METRES_RANKING + " INTEGER, " +
                COL_MITJANA_RANKING + " REAL)";


        // Execute the SQL statements to create the tables
        db.execSQL(createClimbingDataTable);
        db.execSQL(createRocodromsTable);
        db.execSQL(createZonesTable);
        db.execSQL(createRankingTable);

        insertInitialDataRocodroms(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIMBING_DATA);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROCODROMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ZONES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RANKING);
            onCreate(db);

        }
        //insertInitialDataRocodroms(db);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////// CRUD CLIMBING_DATA /////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean insertDataCD(String date, String dificultat, int zona, int ifIntent, int descansos) {
        //canviem el format de la data abans d'introduir-la a SQLite
        String dateISO = DateConverter.convertCustomToISO(date);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE, dateISO);
        contentValues.put(COL_DIFICULTAT, dificultat);
        contentValues.put(COL_ID_ZONA_FK, zona);
        contentValues.put(COL_IFINTENT, ifIntent);
        contentValues.put(COL_DESCANSOS, descansos);
        long result = db.insert(TABLE_CLIMBING_DATA, null, contentValues);
        db.close();
        return result != -1;
    }

    public Cursor getAllDataCD() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CLIMBING_DATA + " ORDER BY " + COL_DATE + " DESC", new String[]{});

    }

    public Cursor getDayDataCD(String date) {
        //canviem el format de la data abans d'introduir-la a SQLite
        String dateISO = DateConverter.convertCustomToISO(date);

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CLIMBING_DATA + " WHERE " + COL_DATE + " = ? ORDER BY ID_CD DESC";
        return db.rawQuery(query, new String[]{dateISO});
    }

    public void closeDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public boolean updateDataCD(int id, String date, String dificultat, int zona, int ifIntent, int descansos){
        //canviem el format de la data abans d'introduir-la a SQLite
        String dateISO = DateConverter.convertCustomToISO(date);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE, dateISO);
        contentValues.put(COL_DIFICULTAT, dificultat);
        contentValues.put(COL_ID_ZONA_FK, zona);
        contentValues.put(COL_IFINTENT, ifIntent);
        contentValues.put(COL_DESCANSOS, descansos);
        int result = db.update(TABLE_CLIMBING_DATA, contentValues, COL_ID_CD + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0; //retorna True si la actualització s'ha realitzat correctament
    }

    public Integer deleteDataCD(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CLIMBING_DATA, COL_ID_CD + " = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getDataCDByDate(String date) {
        //canviem el format de la data abans d'introduir-la a SQLite
        String dateISO = DateConverter.convertCustomToISO(date);

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CLIMBING_DATA + " WHERE " + COL_DATE + " = ? ";
        return db.rawQuery(query, new String[]{dateISO});
    }

    public Cursor getDataCDBetweenDates(String startDate, String endDate){
        //canviem el format de la data abans d'introduir-la a SQLite
        String startDateISO = DateConverter.convertCustomToISO(startDate);
        String endDateISO = DateConverter.convertCustomToISO(endDate);

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " +
                TABLE_CLIMBING_DATA + " WHERE " + COL_DATE + " BETWEEN ? AND ? ORDER BY " + COL_DATE + " DESC";
        return db.rawQuery(query, new String[]{startDateISO, endDateISO});
    }

    /**
     * getUniqueDates
     * @param startDate
     * @param endDate
     * @return Cursor amb cada una de les dates que tenen dades entre les dates senyalades */
    public Cursor getUniqueDataCDDates(String startDate, String endDate){
        //canviem el format de la data abans d'introduir-la a SQLite
        String startDateISO = DateConverter.convertCustomToISO(startDate);
        String endDateISO = DateConverter.convertCustomToISO(endDate);


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT DISTINCT " + COL_DATE + " FROM " + TABLE_CLIMBING_DATA +
                    " WHERE " + COL_DATE + " BETWEEN ? AND ? ORDER BY " + COL_DATE + " DESC ";
            cursor = db.rawQuery(query, new String[]{startDateISO, endDateISO});
        } catch (Exception e){
            e.printStackTrace();
        }
        return cursor;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////  CRUD ROCODROM  /////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean insertRocodrom(String nomRocodrom, String nomRocodromReduit, String poblacio) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NOM_ROCO, nomRocodrom);
        contentValues.put(COL_NOM_ROCO_REDUIT, nomRocodromReduit);
        contentValues.put(COL_POBLACIO, poblacio);
        long result = db.insert(TABLE_ROCODROMS, null, contentValues);
        db.close();
        return result != -1;
    }

    public Cursor getRocodromById(int idRocodrom) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_ROCODROMS+" WHERE "+COL_ID_ROCO+" = ?", new String[]{String.valueOf(idRocodrom)});
    }

    public Cursor getAllRocodroms() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_ROCODROMS, new String[]{});
    }

    public Integer deleteRocodrom(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ROCODROMS, COL_ID_ROCO+" = ?", new String[]{String.valueOf(id)});
    }

    public boolean updateRocodrom(int id, String nomRocodrom, String nomRocodromReduit, String poblacio){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NOM_ROCO, nomRocodrom);
        contentValues.put(COL_NOM_ROCO_REDUIT, nomRocodromReduit);
        contentValues.put(COL_POBLACIO, poblacio);
        int result = db.update(TABLE_ROCODROMS, contentValues, COL_ID_ROCO+" = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////  CRUD ZONES  ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean insertZona(int idRocodrom, String nomZona, int alturaZona, int esCorda ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_ROCO_FK, idRocodrom);
        contentValues.put(COL_NOM_ZONA, nomZona);
        contentValues.put(COL_ALTURA_ZONA, alturaZona);
        contentValues.put(COL_ZONA_CORDA, esCorda);
        long result = db.insert(TABLE_ZONES, null, contentValues);
        db.close();
        return result != -1;
    }

    public Cursor getZonaById(int idZona) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+ TABLE_ZONES+" WHERE "+COL_ID_ZONA+" = ?", new String[]{String.valueOf(idZona)});
    }

    public Cursor getZonesByRocodrom(int idRocodrom) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+ TABLE_ZONES+" WHERE "+COL_ID_ROCO_FK+" = ?", new String[]{String.valueOf(idRocodrom)});
    }
    public Integer deleteZona(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ZONES, COL_ID_ZONA+" = ?", new String[]{String.valueOf(id)});
    }
    public boolean updateZona(int id, int idRocodrom, String nomZona, int alturaZona, int esCorda){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_ROCO_FK, idRocodrom);
        contentValues.put(COL_NOM_ZONA, nomZona);
        contentValues.put(COL_ALTURA_ZONA, alturaZona);
        contentValues.put(COL_ZONA_CORDA, esCorda);
        int result = db.update("zones", contentValues, "ID_ZONA = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////  CRUD RANKING  //////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean insertRanking(String dateRanking, double puntsRanking, int viesRanking, int metresRanking){
        String dateRankingISO = DateConverter.convertCustomToISO(dateRanking);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE_RANKING, dateRankingISO);
        contentValues.put(COL_PUNTS_RANKING, puntsRanking);
        contentValues.put(COL_VIES_RANKING, viesRanking);
        contentValues.put(COL_METRES_RANKING, metresRanking);
        contentValues.put(COL_MITJANA_RANKING, puntsRanking/viesRanking);
        long result = db.insert(TABLE_RANKING, null, contentValues);
        db.close();
        return result != -1;
    }

    public boolean updateRanking(int id, String dateRanking, double puntsRanking, int viesRanking, int metresRanking) {
        String dateRankingISO = DateConverter.convertCustomToISO(dateRanking);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE_RANKING, dateRankingISO);
        contentValues.put(COL_PUNTS_RANKING, puntsRanking);
        contentValues.put(COL_VIES_RANKING, viesRanking);
        contentValues.put(COL_METRES_RANKING, metresRanking);
        contentValues.put(COL_MITJANA_RANKING, puntsRanking / viesRanking);
        int result = db.update(TABLE_RANKING, contentValues, COL_ID_RANKING + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public Cursor getAllRanking() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_RANKING+" ORDER BY "+COL_ID_RANKING+" DESC", new String[]{});
    }

    public Cursor getRankingByDate(String date) {
        String dateISO = DateConverter.convertCustomToISO(date);

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_RANKING+" WHERE "+COL_DATE_RANKING+" = ?", new String[]{dateISO});
    }

    public Cursor getRankingBetweenDates(String startDate, String endDate) {
        String startDateISO = DateConverter.convertCustomToISO(startDate);
        String endDateISO = DateConverter.convertCustomToISO(endDate);

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_RANKING+" WHERE "+COL_DATE_RANKING+" BETWEEN ? AND ?", new String[]{startDateISO, endDateISO});
    }

    public Cursor getRankingByCriteri(String criteri, int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (criteri) {
            case "puntsRanking":
                criteri = COL_PUNTS_RANKING;
                break;
            case "viesRanking":
                criteri = COL_VIES_RANKING;
                break;
            case "metresRanking":
                criteri = COL_METRES_RANKING;
                break;
            case "mitjanaRanking":
                criteri = COL_MITJANA_RANKING;
                break;
            default:
                criteri = COL_PUNTS_RANKING;
        }
        return db.rawQuery("SELECT * FROM "+TABLE_RANKING+" ORDER BY "+criteri+" DESC LIMIT ?", new String[]{String.valueOf(limit)});
    }

    Cursor getRankingByCriteriBetweenDates(String startDate, String endDate, String criteri, int limit) {
        String startDateISO = DateConverter.convertCustomToISO(startDate);
        String endDateISO = DateConverter.convertCustomToISO(endDate);

        SQLiteDatabase db = this.getReadableDatabase();
        switch (criteri) {
            case "puntsRanking":
                criteri = COL_PUNTS_RANKING;
                break;
            case "viesRanking":
                criteri = COL_VIES_RANKING;
                break;
            case "metresRanking":
                criteri = COL_METRES_RANKING;
                break;
            case "mitjanaRanking":
                criteri = COL_MITJANA_RANKING;
                break;
            default:
                criteri = COL_PUNTS_RANKING;
        }
        return db.rawQuery("SELECT * FROM "+TABLE_RANKING+" WHERE "+COL_DATE_RANKING+" BETWEEN ? AND ? ORDER BY "+criteri+" DESC LIMIT ?", new String[]{startDateISO, endDateISO, String.valueOf(limit)});
    }

    public Integer deleteRanking(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_RANKING, COL_ID_RANKING+" = ?", new String[]{String.valueOf(id)});
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////  introducció de dades inicials  /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void insertInitialDataRocodroms(SQLiteDatabase db) {
        // gravetat zero
        ContentValues gravetatZero = new ContentValues();
        gravetatZero.put(COL_NOM_ROCO, "Gravetat Zero");
        gravetatZero.put(COL_NOM_ROCO_REDUIT, "GZ");
        gravetatZero.put(COL_POBLACIO, "Terrassa");
        int idGravetatZero = (int) db.insert(TABLE_ROCODROMS, null, gravetatZero);

        // inserir Zones GZ
        ContentValues gravetatZeroAutos = new ContentValues();
        gravetatZeroAutos.put(COL_NOM_ZONA, "Autos");
        gravetatZeroAutos.put(COL_ALTURA_ZONA, 10);
        gravetatZeroAutos.put(COL_ZONA_CORDA, 0);
        gravetatZeroAutos.put(COL_ID_ROCO_FK, idGravetatZero);
        db.insert(TABLE_ZONES, null, gravetatZeroAutos);

        ContentValues gravetatZeroCorda = new ContentValues();
        gravetatZeroCorda.put(COL_NOM_ZONA, "Corda");
        gravetatZeroCorda.put(COL_ALTURA_ZONA, 12);
        gravetatZeroCorda.put(COL_ZONA_CORDA, 1);
        gravetatZeroCorda.put(COL_ID_ROCO_FK, idGravetatZero);
        db.insert(TABLE_ZONES, null, gravetatZeroCorda);

        ContentValues gravetatZeroShiny = new ContentValues();
        gravetatZeroShiny.put(COL_NOM_ZONA, "Shiny");
        gravetatZeroShiny.put(COL_ALTURA_ZONA, 12);
        gravetatZeroShiny.put(COL_ZONA_CORDA, 0);
        gravetatZeroShiny.put(COL_ID_ROCO_FK, idGravetatZero);
        db.insert(TABLE_ZONES, null, gravetatZeroShiny);

        ContentValues gravetatZeroBloc = new ContentValues();
        gravetatZeroBloc.put(COL_NOM_ZONA, "Bloc");
        gravetatZeroBloc.put(COL_ALTURA_ZONA, 4);
        gravetatZeroBloc.put(COL_ZONA_CORDA, 0);
        gravetatZeroBloc.put(COL_ID_ROCO_FK, idGravetatZero);
        db.insert(TABLE_ZONES, null, gravetatZeroBloc);

        // La panxa del bou
        ContentValues laPanxaDelBou = new ContentValues();
        laPanxaDelBou.put(COL_NOM_ROCO, "La panxa del bou");
        laPanxaDelBou.put(COL_NOM_ROCO_REDUIT, "LPB");
        laPanxaDelBou.put(COL_POBLACIO, "Sabadell");
        int idLaPanxaDelBou = (int) db.insert(TABLE_ROCODROMS, null, laPanxaDelBou);

        // Climbat Barcelona
        ContentValues climbatBarcelona = new ContentValues();
        climbatBarcelona.put(COL_NOM_ROCO, "Climbat");
        climbatBarcelona.put(COL_NOM_ROCO_REDUIT, "CB");
        climbatBarcelona.put(COL_POBLACIO, "Barcelona");
        int idClimbatBarcelona = (int) db.insert(TABLE_ROCODROMS, null, climbatBarcelona);

        // Sharma Gavà
        ContentValues sharmaGava = new ContentValues();
        sharmaGava.put(COL_NOM_ROCO, "Sharma");
        sharmaGava.put(COL_NOM_ROCO_REDUIT, "SG");
        sharmaGava.put(COL_POBLACIO, "Gavà");
        int idSharmaGava = (int) db.insert(TABLE_ROCODROMS, null, sharmaGava);
    }

}
