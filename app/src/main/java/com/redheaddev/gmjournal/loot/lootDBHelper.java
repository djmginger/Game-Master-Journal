//Create a duplicate version of this, that supports a different database
package com.redheaddev.gmjournal.loot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class lootDBHelper extends SQLiteOpenHelper {
    private static lootDBHelper sInstance;
    private static final String TAG = "lootDBHelper";

    private static final String TABLE_NAME = "loot_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "name";
    private static final String COL3 = "rarity";
    private static final String COL4 = "price";
    private static final String COL5 = "description";
    private static final String COL6 = "details";
    private static final String COL7 = "image";
    private static final String COL8 = "requirements";

    //* get the context of each instance of lootDBHelper
    public static synchronized lootDBHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new lootDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public lootDBHelper(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " TEXT, " + COL5 + " TEXT," + COL6 + " TEXT, " + COL7 + " TEXT, " + COL8 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addLoot(String name, String rarity, String price, String description, String details, String image, String requirements, String oldName, boolean updateLoot){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if(!updateLoot) {  //if starting activity from clicking the Add Entry button
            contentValues.put(COL2, name);
            contentValues.put(COL3, rarity);
            contentValues.put(COL4, price);
            contentValues.put(COL5, description);
            contentValues.put(COL6, details);
            contentValues.put(COL7, image);
            contentValues.put(COL8, requirements);

            Log.d(TAG, "addLoot: Adding " + name + ", " + rarity + ", " + price + ", " + description + ", " + details + ", " + requirements + ", " + image + " to " + TABLE_NAME);

            long result = db.insert(TABLE_NAME, null, contentValues); //result will be -1 if data was inserted incorrectly
            return (result != -1);

        }else{  //if starting activity from clicking on an existing Loot name
            String query = "UPDATE " + TABLE_NAME + " SET " + COL2 + "=?" + "," + COL3 + "=?" + "," + COL4 + "=?" + "," + COL5 + "=?" + "," + COL6 + "=?" + "," + COL7 + "=?" + "," + COL8 + "=?" + "WHERE " + COL2 + "=?";
            db.execSQL(query, new String [] {name, rarity, price, description, details, image, requirements, oldName});

            Log.d(TAG, "addLoot: Updating " + oldName + " to " + name + ", " + rarity + ", " + price + ", " + description + ", " + details + ", " + image + ", "  + requirements + " to " + TABLE_NAME);

            return true;
        }
    }

    Cursor getLoot(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    Cursor getSpecificLoot(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL2 + "=?";
        return db.rawQuery(query, new String [] {name});

    }

    void removeLoot(String name){
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
