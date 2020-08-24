//Create a duplicate version of this, that supports a different database
package com.redheaddev.gmjournal.cities.locations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class locationsDBHelper extends SQLiteOpenHelper {
    private static locationsDBHelper sInstance;
    private static final String TAG = "locationsDBHelper";

    private static final String TABLE_NAME = "location_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "cityname";
    private static final String COL3 = "locname";
    private static final String COL4 = "description";
    private static final String COL5 = "plothooks";
    private static final String COL6 = "notes";
    //* get the context of each instance of locationsDBHelper
    public static synchronized locationsDBHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new locationsDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public locationsDBHelper(Context context){
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

    boolean addLocation(String cityName, String locName, String description, String plothooks, String notes, String oldName, Boolean updateInfo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if(!updateInfo) {  //if the location does not exist, add a new one
            contentValues.put(COL2, cityName);
            contentValues.put(COL3, locName);
            contentValues.put(COL4, description);
            contentValues.put(COL5, plothooks);
            contentValues.put(COL6, notes);

            Log.d(TAG, "addLocation: Adding " + cityName + ", " + locName + ", " + description + ", " + plothooks + ", " + notes + " to " + TABLE_NAME);

            long result = db.insert(TABLE_NAME, null, contentValues); //result will be -1 if data was inserted incorrectly
            return (result != -1);

        }else{  //if the location does exist, update the current entry
            String query = "UPDATE " + TABLE_NAME + " SET " + COL3 + "=?" + "," + COL4 + "=?" + "," + COL5 + "=?" + "," + COL6 + "=?" + "WHERE " + COL3 + "=?";
            db.execSQL(query, new String [] {locName, description, plothooks, notes, oldName});

            Log.d(TAG, "addLocation: Updating " + oldName + " to " + locName + ", " + description + ", " + plothooks + ", " + notes + " to " + TABLE_NAME);

            return true;
        }
    }

    public boolean updateCityName(String cityName, String oldName) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 + "=?" + "WHERE " + COL2 + "=?";
        db.execSQL(query, new String [] {cityName, oldName});

        Log.d(TAG, "addLocation: Updating " + oldName + " to " + cityName + " to " + TABLE_NAME);

        return true;
    }

    public Cursor getLocations(String cityName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL2 + "=?";
        return db.rawQuery(query, new String [] {cityName});
    }

    Cursor getSpecificLocation(String locName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL3 + "=?";
        return db.rawQuery(query, new String [] {locName});

    }

    void removeLocation(String locName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL3 + "=?";
        db.execSQL(query, new String [] {locName});
    }

    public boolean checkNonExistence(String entryName){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + COL3 + "=?";
        Cursor cursor = db.rawQuery(Query, new String [] {entryName});
        if(cursor.getCount() <= 0){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }


}
