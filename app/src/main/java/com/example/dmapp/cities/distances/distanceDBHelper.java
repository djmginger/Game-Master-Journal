//Create a duplicate version of this, that supports a different database
package com.example.dmapp.cities.distances;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class distanceDBHelper extends SQLiteOpenHelper {
    private static distanceDBHelper sInstance;
    private static final String TAG = "distanceDBHelper";

    private static final String TABLE_NAME = "distance_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "cityfrom";
    private static final String COL3 = "cityto";
    private static final String COL4 = "distance";
    //* get the context of each instance of citiesDBHelper
    public static synchronized distanceDBHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new distanceDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public distanceDBHelper(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addDistance(String cityFrom, String cityTo, String distance){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if(checkNonExistence(cityFrom, cityTo)) {  //if the distance does not exist, add a new one
            contentValues.put(COL2, cityFrom);
            contentValues.put(COL3, cityTo);
            contentValues.put(COL4, distance);

            Log.d(TAG, "addCity: Adding " + cityFrom + ", " + cityTo + ", " + distance + " to " + TABLE_NAME);
            db.insert(TABLE_NAME, null, contentValues);
        }
    }

    public void updateCitiesName(String cityName, String oldName) {
        SQLiteDatabase db = this.getWritableDatabase();

        Log.d(TAG, "updateCityName: so it at least gets to here");

        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 + "=?" + "WHERE " + COL2 + "=?";
        db.execSQL(query, new String [] {cityName, oldName});

        Log.d(TAG, "updateCityName: Now we're inbetween the queries");

        String query2 = "UPDATE " + TABLE_NAME + " SET " + COL3 + "=?" + "WHERE " + COL3 + "=?";
        db.execSQL(query2, new String [] {cityName, oldName});
    }

    public Cursor getDistances(String cityFrom){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL2 + "=?";
        if(!(query.equals(null))){
            Log.d(TAG, "getDistances: Creating list of distances");
        }
        return db.rawQuery(query, new String [] {cityFrom});
    }

    Cursor getAllDistances(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME ;
        return db.rawQuery(query, new String [] {});
    }

    void removeSpecificDistance(String cityFrom, String cityTo){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL2 + "=?" + " AND " + COL3 + "=?";
        db.execSQL(query, new String [] {cityFrom, cityTo});
    }

    public boolean checkNonExistence(String cityFrom, String cityTo){
        Log.d(TAG, "checkNonExistence: Checking if distance from " + cityFrom + " to " + cityTo + " exists");
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_NAME + " WHERE " + COL2 + "=?" + " AND " + COL3 + "=?";
        Cursor cursor = db.rawQuery(Query, new String [] {cityFrom, cityTo});
        Log.d(TAG, "checkNonExistence: cursor count is " + cursor.getCount());
        if(cursor.getCount() <= 0){
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }
}
