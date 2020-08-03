//Create a duplicate version of this, that supports a different database
package com.example.dmapp.cities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class citiesDBHelper extends SQLiteOpenHelper {
    private static citiesDBHelper sInstance;
    private static final String TAG = "citiesDBHelper";
    private static final String TABLE_NAME = "city_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "name";
    private static final String COL3 = "environment";
    private static final String COL4 = "population";
    private static final String COL5 = "economy";
    private static final String COL6 = "notes";
    //* get the context of each instance of citiesDBHelper
    public static synchronized citiesDBHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new citiesDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public citiesDBHelper(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " TEXT, " + COL5 + " TEXT, " + COL6 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addCity(String name, String environment, String population, String economy, String notes, String oldName, boolean updateCity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if(!updateCity) {  //if the npc does not exist, add a new one
            contentValues.put(COL2, name);
            contentValues.put(COL3, environment);
            contentValues.put(COL4, population);
            contentValues.put(COL5, economy);
            contentValues.put(COL6, notes);

            Log.d(TAG, "addCity: Adding " + name + ", " + environment + ", " + population + ", " + economy + " to " + TABLE_NAME);

            long result = db.insert(TABLE_NAME, null, contentValues); //result will be -1 if data was inserted incorrectly
            return (result != -1);

        }else{  //if the npc does exist, update the current entry
            String query = "UPDATE " + TABLE_NAME + " SET " + COL2 + "=?" + "," + COL3 + "=?" + "," + COL4 + "=?" + "," + COL5 + "=?" + "," + COL6 + "=?" + "WHERE " + COL2 + "=?";
            db.execSQL(query, new String [] {name, environment, population, economy, notes, oldName});

            Log.d(TAG, "addCity: Updating " + oldName + " to " + name + ", " + environment + ", " + population + ", " + economy + ", " + notes + " to " + TABLE_NAME);

            return true;
        }
    }

    public Cursor getCities(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    Cursor getSpecificCity(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL2 + "=?";
        return db.rawQuery(query, new String [] {name});

    }

    void removeCity(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL2 + "=?";
        db.execSQL(query, new String [] {name});
    }

    public boolean checkNonExistence(String entryName){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + COL2 + "=?";
        Cursor cursor = db.rawQuery(Query, new String [] {entryName});
        if(cursor.getCount() <= 0){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }


}
