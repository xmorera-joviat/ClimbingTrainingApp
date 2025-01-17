package com.xmorera.climbingtrainingapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
<<<<<<< HEAD
import android.util.Log;
import android.widget.Toast;

import com.xmorera.climbingtrainingapp.MainActivity;
=======
>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "climbing_training.db";
    private static final int DATABASE_VERSION = 2; // Incremented version

    // Table names
<<<<<<< HEAD
    private static final String CLIMBING_DATA = "CLIMBING_DATA";
    private static final String TABLE_ROCODROMS = "ROCODROMS";
    private static final String TABLE_ZONES = "ZONES";

    // Columns for climbing_data
    private static final String COL_ID_CD = "ID_CD";
    private static final String COL_ID_ZONA_FK = "ID_ZONA_FK";
    private static final String COL_DATE = "DATE";
    private static final String COL_IFINTENT = "IFINTENT";
    private static final String COL_DESCANSOS = "DESCANSOS";
    private static final String COL_DIFICULTAT = "DIFICULTAT";
=======
    private static final String TABLE_CLIMBING_DATA = "climbing_data";
    private static final String TABLE_ROCODROMS = "rocodroms";
    private static final String TABLE_ZONES = "zones";

    // Columns for climbing_data
    private static final String COL_ID_CD = "ID_CD";
    private static final String COL_DATE = "DATE";
    private static final String COL_DIFICULTAT = "DIFICULTAT";
    private static final String COL_ID_ZONA_FK = "ID_ZONA_CD";
    private static final String COL_IFINTENT = "IFINTENT";
    private static final String COL_DESCANSOS = "DESCANSOS";

>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))

    // Columns for rocodroms
    private static final String COL_ID_ROCO = "ID_ROCO";
    private static final String COL_NOM_ROCO = "NOM_ROCO";
<<<<<<< HEAD
    private static final String COL_LOCALITAT = "LOCALITAT_ROCO";
    private static final String COL_NOM_ROCO_REDUIT = "NOM_ROCO_REDUIT";
=======
    private static final String COL_NOM_ROCO_REDUIT = "NOM_ROCO_REDUIT";
    private static final String COL_ROCO_POBLACIO = "ROCO_POBLACIO";
>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))

    // Columns for zones
    private static final String COL_ID_ZONA = "ID_ZONA";
    private static final String COL_NOM_ZONA = "NOM_ZONA";
    private static final String COL_ALTURA_ZONA = "ALTURA_ZONA";
    private static final String COL_ZONA_CORDA = "ZONA_CORDA";
    private static final String COL_ID_ROCO_FK = "ID_ROCO_FK"; // Foreign key

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create climbing_data table
<<<<<<< HEAD
        String createClimbingDataTable =
                "CREATE TABLE " + CLIMBING_DATA + "(" +
                        COL_ID_CD + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COL_ID_ZONA_FK + " INTEGER," +
                        COL_DATE + " TEXT," +
                        COL_IFINTENT + " INTEGER," +
                        COL_DESCANSOS + " INTEGER," +
                        COL_DIFICULTAT + " TEXT," +
                        "FOREIGN KEY(" + COL_ID_ZONA_FK + ") REFERENCES " + TABLE_ZONES + "(" + COL_ID_ZONA + ")" +
                        ")";
=======
        String createClimbingDataTable = "CREATE TABLE " + TABLE_CLIMBING_DATA + " (" +
                COL_ID_CD + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT NOT NULL, " +
                COL_DIFICULTAT + " TEXT NOT NULL, " +
                COL_ID_ZONA_FK + " INTEGER NOT NULL, " +
                COL_IFINTENT + " INTEGER DEFAULT 0,"+
                COL_DESCANSOS + " INTEGER DEFAULT 0,"+
                "FOREIGN KEY(" + COL_ID_ZONA_FK + ") REFERENCES " + TABLE_ZONES + "(" + COL_ID_ZONA + "))";
>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))

        // Create rocodroms table
        String createRocodromsTable = "CREATE TABLE " + TABLE_ROCODROMS + " (" +
                COL_ID_ROCO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
<<<<<<< HEAD
                COL_NOM_ROCO + " TEXT NOT NULL, " +
                COL_NOM_ROCO_REDUIT + " TEXT CHECK(LENGTH(NOM_ROCO_REDUIT) <= 5), " +
                COL_LOCALITAT + " TEXT)";
=======
                COL_NOM_ROCO + " TEXT NOT NULL," +
                COL_NOM_ROCO_REDUIT + " TEXT,"+
                COL_ROCO_POBLACIO + " TEXT)";
>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))

        // Create zones table
        String createZonesTable = "CREATE TABLE " + TABLE_ZONES + " (" +
                COL_ID_ZONA + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOM_ZONA + " TEXT NOT NULL, " +
                COL_ALTURA_ZONA + " INTEGER NOT NULL, " +
                COL_ZONA_CORDA + " INTEGER DEFAULT 0, " +
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
<<<<<<< HEAD
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS " + CLIMBING_DATA);
=======
       if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIMBING_DATA);
>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROCODROMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ZONES);
            onCreate(db);

        }
        insertInitialDataRocodroms(db);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////// CRUD CLIMBING_DATA /////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


<<<<<<< HEAD
    public boolean insertData(int idZona, String date, int ifIntent, int descansos, String dificultat) {
=======
    public boolean insertClimbingData(String date, String dificultat, int zona, int ifIntent, int descansos) {
>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))
        //canviem el format de la data abans d'introduir-la a SQLite
        String dateISO = DateConverter.convertCustomToISO(date);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_ZONA_FK, idZona);
        contentValues.put(COL_DATE, dateISO);
<<<<<<< HEAD
        contentValues.put(COL_IFINTENT, ifIntent);
        contentValues.put(COL_DESCANSOS, descansos);
        contentValues.put(COL_DIFICULTAT, dificultat);
        long result = db.insert(CLIMBING_DATA, null, contentValues);
=======
        contentValues.put(COL_DIFICULTAT, dificultat);
        contentValues.put(COL_ID_ZONA_FK, zona);
        contentValues.put(COL_IFINTENT, ifIntent);
        contentValues.put(COL_DESCANSOS, descansos);
        long result = db.insert(TABLE_CLIMBING_DATA, null, contentValues);
>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))
        db.close();
        return result != -1;
    }

    public Cursor getAllClimbingData() {
        SQLiteDatabase db = this.getReadableDatabase();
<<<<<<< HEAD
        return db.rawQuery("SELECT * FROM " + CLIMBING_DATA + " ORDER BY " + COL_DATE + " DESC", new String[]{});
=======
        return db.rawQuery("SELECT * FROM " + TABLE_CLIMBING_DATA + " ORDER BY " + COL_DATE + " DESC", new String[]{});
>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))

    }

    public Cursor getDayClimbingData(String date) {
        //canviem el format de la data abans d'introduir-la a SQLite
        String dateISO = DateConverter.convertCustomToISO(date);

        SQLiteDatabase db = this.getReadableDatabase();
<<<<<<< HEAD
        String query = "SELECT * FROM " + CLIMBING_DATA + " WHERE " + COL_DATE + " = ? ORDER BY ID_CD DESC";
=======
        String query = "SELECT * FROM " + TABLE_CLIMBING_DATA + " WHERE " + COL_DATE + " = ? ORDER BY ID DESC";
>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))
        return db.rawQuery(query, new String[]{dateISO});
    }


    public void closeDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

<<<<<<< HEAD
    public boolean updateData(int id, int idZona, String date, int ifIntent, int descansos, String dificultat){
=======
    public boolean updateClimbingData(int id, String date, String dificultat, int zona, int ifIntent, int descansos){
>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))
        //canviem el format de la data abans d'introduir-la a SQLite
        String dateISO = DateConverter.convertCustomToISO(date);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_ZONA_FK, idZona);
        contentValues.put(COL_DATE, dateISO);
<<<<<<< HEAD
        contentValues.put(COL_IFINTENT, ifIntent);
        contentValues.put(COL_DESCANSOS, descansos);
        contentValues.put(COL_DIFICULTAT, dificultat);
        int result = db.update(CLIMBING_DATA, contentValues, COL_ID_CD + " = ?", new String[]{String.valueOf(id)});
=======
        contentValues.put(COL_DIFICULTAT, dificultat);
        contentValues.put(COL_ID_ZONA_FK, zona);
        contentValues.put(COL_IFINTENT, ifIntent);
        contentValues.put(COL_DESCANSOS, descansos);
        int result = db.update(TABLE_CLIMBING_DATA, contentValues, COL_ID_CD + " = ?", new String[]{String.valueOf(id)});
>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))
        db.close();
        return result > 0; //retorna True si la actualització s'ha realitzat correctament
    }

    public Integer deleteClimbingData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
<<<<<<< HEAD
        return db.delete(CLIMBING_DATA, COL_ID_CD + " = ?", new String[]{String.valueOf(id)});
    }

    /*public Cursor getDataByDate(String date) {
=======
        return db.delete(TABLE_CLIMBING_DATA, COL_ID_CD + " = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getClimbingDataByDate(String date) {
>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))
        //canviem el format de la data abans d'introduir-la a SQLite
        String dateISO = DateConverter.convertCustomToISO(date);

        SQLiteDatabase db = this.getReadableDatabase();
<<<<<<< HEAD
        String query = "SELECT * FROM " + CLIMBING_DATA + " WHERE " + COL_DATE + " = ? ";
=======
        String query = "SELECT * FROM " + TABLE_CLIMBING_DATA + " WHERE " + COL_DATE + " = ? ";
>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))
        return db.rawQuery(query, new String[]{dateISO});
    }*/

    public Cursor getClimbingDataBetweenDates(String startDate, String endDate){
        //canviem el format de la data abans d'introduir-la a SQLite
        String startDateISO = DateConverter.convertCustomToISO(startDate);
        String endDateISO = DateConverter.convertCustomToISO(endDate);

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " +
<<<<<<< HEAD
                CLIMBING_DATA + " WHERE " + COL_DATE + " BETWEEN ? AND ? ORDER BY " + COL_DATE + " DESC";
=======
                TABLE_CLIMBING_DATA + " WHERE " + COL_DATE + " BETWEEN ? AND ? ORDER BY " + COL_DATE + " DESC";
>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))
        return db.rawQuery(query, new String[]{startDateISO, endDateISO});
    }

    /**
     * getUniqueDates
     * @param startDate
     * @param endDate
     * @return Cursor amb cada una de les dates que tenen dades entre les dates senyalades */
    public Cursor getClimbingUniqueDates(String startDate, String endDate){
        //canviem el format de la data abans d'introduir-la a SQLite
        String startDateISO = DateConverter.convertCustomToISO(startDate);
        String endDateISO = DateConverter.convertCustomToISO(endDate);


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
<<<<<<< HEAD
            String query = "SELECT DISTINCT " + COL_DATE + " FROM " + CLIMBING_DATA +
=======
            String query = "SELECT DISTINCT " + COL_DATE + " FROM " + TABLE_CLIMBING_DATA +
>>>>>>> 31d7d03 (versió 0.5.0.5.1 (terminal OK))
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
    public boolean insertRocodrom(String nomRocodrom, String nomRocoReduit, String localitatRocodrom) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NOM_ROCO, nomRocodrom);
        contentValues.put(COL_NOM_ROCO_REDUIT, nomRocoReduit);
        contentValues.put(COL_LOCALITAT, localitatRocodrom);
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

    public boolean updateRocodrom(int id, String nomRocodrom, String nomRocoReduit,String localitatRocodrom){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NOM_ROCO, nomRocodrom);
        contentValues.put(COL_NOM_ROCO_REDUIT, nomRocoReduit);
        contentValues.put(COL_LOCALITAT, localitatRocodrom);
        int result = db.update(TABLE_ROCODROMS, contentValues, COL_ID_ROCO+" = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////  CRUD ZONES  ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean insertZona(String nomZona, int alturaZona, int esCorda, int idRocodrom ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NOM_ZONA, nomZona);
        contentValues.put(COL_ALTURA_ZONA, alturaZona);
        contentValues.put(COL_ZONA_CORDA, esCorda);
        contentValues.put(COL_ID_ROCO_FK, idRocodrom);
        long result = db.insert(TABLE_ZONES, null, contentValues);
        db.close();
        return result != -1;
    }
    public Cursor getZone(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+ TABLE_ZONES +" WHERE "+COL_ID_ZONA+" = ?", new String[]{String.valueOf(id)});

    }
    public Cursor getZonesByRocodrom(int idRocodrom) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT Z.*, R.NOM_ROCO_REDUIT " +
                "FROM ZONES Z " +
                "JOIN ROCODROMS R ON Z.ID_ROCO_FK = R.ID_ROCO " +
                "WHERE Z.ID_ROCO_FK = ?";
        return db.rawQuery(query, new String[]{String.valueOf(idRocodrom)});
        //return db.rawQuery("SELECT * FROM "+ TABLE_ZONES+" WHERE "+COL_ID_ROCO_FK+" = ?", new String[]{String.valueOf(idRocodrom)});
    }
    public Integer deleteZona(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ZONES, COL_ID_ZONA+" = ?", new String[]{String.valueOf(id)});
    }
    public boolean updateZona(int id, int idRocodrom, String nomZona, int alturaZona, int esCorda){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NOM_ZONA, nomZona);
        contentValues.put(COL_ALTURA_ZONA, alturaZona);
        contentValues.put(COL_ZONA_CORDA, esCorda);
        contentValues.put(COL_ID_ROCO_FK, idRocodrom);
        int result = db.update("zones", contentValues, "ID_ZONA = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////  JOIN GET ZONES FROM CLIMBING_DATA  ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Cursor getClimbingDataZone(String date){
        String dateISO = DateConverter.convertCustomToISO(date);
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT CD.*, Z.ALTURA_ZONA, Z.NOM_ZONA, Z.ZONA_CORDA " +
                "FROM CLIMBING_DATA CD " +
                "JOIN ZONES Z ON CD.ID_ZONA_FK = Z.ID_ZONA " +
                "WHERE CD.DATE = ? ORDER BY CD.ID_CD DESC";
        return db.rawQuery(query, new String[]{dateISO});
    }

    public Cursor getIdZonaFromRocodrom(int idRocodrom, String zona){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ID_ZONA FROM ZONES WHERE ID_ROCO = ? AND NOM_ZONA = ?";
        return db.rawQuery(query, new String[]{String.valueOf(idRocodrom), zona});
    }





    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////  introducció de dades inicials  /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void insertInitialDataRocodroms(SQLiteDatabase db) {
        // gravetat zero
        ContentValues gravetatZero = new ContentValues();
        gravetatZero.put(COL_NOM_ROCO, "Gravetat Zero");
        gravetatZero.put(COL_NOM_ROCO_REDUIT, "GZ");
        gravetatZero.put(COL_LOCALITAT, "Terrassa");
        int idGravetatZero = (int) db.insert(TABLE_ROCODROMS, null, gravetatZero);

        // inserir Zones G0
        ContentValues gravetatZeroAutos = new ContentValues();
        gravetatZeroAutos.put(COL_NOM_ZONA, "Autos");
        gravetatZeroAutos.put(COL_ALTURA_ZONA, 10);
        gravetatZeroAutos.put(COL_ZONA_CORDA, 0);
        gravetatZeroAutos.put(COL_ID_ROCO_FK, idGravetatZero);
        db.insert(TABLE_ZONES, null, gravetatZeroAutos);

        ContentValues gravetatZeroCorda = new ContentValues();
        gravetatZeroCorda.put(COL_NOM_ZONA, "Corda");
        gravetatZeroCorda.put(COL_ALTURA_ZONA, 12);
        gravetatZeroCorda.put(COL_ID_ROCO_FK, idGravetatZero);
        gravetatZeroCorda.put(COL_ZONA_CORDA, 1);
        db.insert(TABLE_ZONES, null, gravetatZeroCorda);

        ContentValues gravetatZeroShiny = new ContentValues();
        gravetatZeroShiny.put(COL_NOM_ZONA, "Shiny");
        gravetatZeroShiny.put(COL_ALTURA_ZONA, 12);
        gravetatZeroShiny.put(COL_ID_ROCO_FK, idGravetatZero);
        gravetatZeroShiny.put(COL_ZONA_CORDA, 0);
        db.insert(TABLE_ZONES, null, gravetatZeroShiny);

        ContentValues gravetatZeroBloc = new ContentValues();
        gravetatZeroBloc.put(COL_NOM_ZONA, "Bloc");
        gravetatZeroBloc.put(COL_ALTURA_ZONA, 4);
        gravetatZeroBloc.put(COL_ID_ROCO_FK, idGravetatZero);
        gravetatZeroBloc.put(COL_ZONA_CORDA, 0);
        db.insert(TABLE_ZONES, null, gravetatZeroBloc);

        // La panxa del bou
        ContentValues laPanxaDelBou = new ContentValues();
        laPanxaDelBou.put(COL_NOM_ROCO, "La panxa del bou");
        laPanxaDelBou.put(COL_NOM_ROCO_REDUIT, "LPB");
        laPanxaDelBou.put(COL_LOCALITAT, "Sabadell");
        int idLaPanxaDelBou = (int) db.insert(TABLE_ROCODROMS, null, laPanxaDelBou);

        // Climbat Barcelona
        ContentValues climbatBarcelona = new ContentValues();
        climbatBarcelona.put(COL_NOM_ROCO, "Climbat");
        climbatBarcelona.put(COL_NOM_ROCO_REDUIT, "CLBT");
        climbatBarcelona.put(COL_LOCALITAT, "Barcelona");
        int idClimbatBarcelona = (int) db.insert(TABLE_ROCODROMS, null, climbatBarcelona);

        // Sharma Gavà
        ContentValues sharmaGava = new ContentValues();
        sharmaGava.put(COL_NOM_ROCO, "Sharma");
        sharmaGava.put(COL_NOM_ROCO_REDUIT, "SH-G");
        sharmaGava.put(COL_LOCALITAT,"Gavà");
        int idSharmaGava = (int) db.insert(TABLE_ROCODROMS, null, sharmaGava);
    }

}
