//Create a duplicate version of this, that supports a different database
package com.redheaddev.gmjournal.presets;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class presetsDBHelper extends SQLiteOpenHelper {
    private static presetsDBHelper sInstance;
    private static final String TAG = "presetsDBHelper";

    private static final String TABLE_NAME = "presets_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "variable";
    private static final String COL3 = "value";
    //* get the context of each instance of citiesDBHelper
    public static synchronized presetsDBHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new presetsDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    presetsDBHelper(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT, " + COL3 + " TEXT)";
        db.execSQL(createTable);

        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(1,'Environment', 'Desert')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(2,'Environment', 'Forest')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(3,'Environment', 'Geothermic')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(4,'Environment', 'Grassland')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(5,'Environment', 'Hills')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(6,'Environment', 'Jungle')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(7,'Environment', 'Marine')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(8,'Environment', 'Mountains')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(9,'Environment', 'Polar')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(10,'Environment', 'Savanna')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(11,'Environment', 'Swamp')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(12,'Environment', 'Tundra')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(13,'Environment', 'Wasteland')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(14,'Population', 'Tiny')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(15,'Population', 'Small')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(16,'Population', 'Medium')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(17,'Population', 'Large')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(18,'Population', 'Huge')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(19,'Economy', 'Bartering')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(20,'Economy', 'Farming')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(21,'Economy', 'Fishing')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(22,'Economy', 'Hunting')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(23,'Economy', 'Magic')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(24,'Economy', 'Mining')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(25,'Economy', 'Theiving')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(26,'Race', 'Dwarf')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(27,'Race', 'Elf')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(28,'Race', 'Faerie')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(29,'Race', 'Goblin')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(30,'Race', 'Gnome')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(31,'Race', 'Halfling')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(32,'Race', 'Human')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(33,'Race', 'Orc')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(34,'Race', 'Ogre')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addPreset(String variable, String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if(checkNonExistence(value)) {  //if the preset does not exist, add a new one
            contentValues.put(COL2, variable);
            contentValues.put(COL3, value);

            Log.d(TAG, "addCity: Adding " + variable + ", " + value + " to " + TABLE_NAME);
            db.insert(TABLE_NAME, null, contentValues);

        }else{  //if the npc does exist, update the current entry
            String query = "UPDATE " + TABLE_NAME + " SET " + COL2 + "=?" + "," + COL3 + "=?" + "WHERE " + COL2 + "=?";
            db.execSQL(query, new String [] {variable, value});

        }
    }

    Cursor getPresets(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, new String [] {});
    }

    Cursor getSpecificPresets(String variable){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL2 + "=?";
        return db.rawQuery(query, new String [] {variable});
    }

    void removeSpecificPreset(String value){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL3 + "=?";
        db.execSQL(query, new String [] {value});
    }

    public boolean checkNonExistence(String entryName){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + COL3 + "=?";
        Cursor cursor = db.rawQuery(Query, new String [] {entryName});
        if(cursor.getCount() <= 0){
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }
}
