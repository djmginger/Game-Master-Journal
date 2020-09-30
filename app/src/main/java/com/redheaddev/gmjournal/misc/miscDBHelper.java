//Create a duplicate version of this, that supports a different database
package com.redheaddev.gmjournal.misc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class miscDBHelper extends SQLiteOpenHelper {
    private static miscDBHelper sInstance;
    private static final String TAG = "miscDBHelper";

    private static final String TABLE_NAME = "misc_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "name";
    private static final String COL3 = "info1";
    private static final String COL4 = "info2";
    private static final String COL5 = "info3";
    private static final String COL6 = "info4";
    private static final String COL7 = "image";
    private static final String COL8 = "linkednpc";
    private static final String COL9 = "linkedcity";
    private static final String COL10 = "linkedloot";
    private static final String COL11 = "linkeditemgroup";
    private static final String COL12 = "miscgroup";

    //* get the context of each instance of lootDBHelper
    public static synchronized miscDBHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new miscDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public miscDBHelper(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " TEXT, " + COL5 + " TEXT," + COL6 + " TEXT, " + COL7 + " TEXT, " + COL8 + " TEXT, " + COL9 + " TEXT, " + COL10 + " TEXT," + COL11 + " TEXT, " + COL12 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean addMisc(String name, String info1, String info2, String info3, String info4, String image, String linkedNpc, String linkedCity, String linkedLoot, String linkedItemGroup, String miscGroup, String oldName, boolean updateMisc){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if(!updateMisc) {  //if starting activity from clicking the Add Entry button
            contentValues.put(COL2, name);
            contentValues.put(COL3, info1);
            contentValues.put(COL4, info2);
            contentValues.put(COL5, info3);
            contentValues.put(COL6, info4);
            contentValues.put(COL7, image);
            contentValues.put(COL8, linkedNpc);
            contentValues.put(COL9, linkedCity);
            contentValues.put(COL10, linkedLoot);
            contentValues.put(COL11, linkedItemGroup);
            contentValues.put(COL12, miscGroup);

            //Log.d(TAG, "addLoot: Adding " + name + ", " + rarity + ", " + price + ", " + description + ", " + details + ", " + requirements + ", " + image + ", " + lootGroup + " to " + TABLE_NAME);

            long result = db.insert(TABLE_NAME, null, contentValues); //result will be -1 if data was inserted incorrectly
            return (result != -1);

        }else{  //if starting activity from clicking on an existing Misc name
            String query = "UPDATE " + TABLE_NAME + " SET " + COL2 + "=?" + "," + COL3 + "=?" + "," + COL4 + "=?" + "," + COL5 + "=?" + "," + COL6 + "=?" + "," + COL7 + "=?" + "," + COL8 + "=?" + "," + COL9 + "=?" + "," + COL10 + "=?" + "," + COL11 + "=?" + "," + COL12 + "=?" + "WHERE " + COL2 + "=?";
            db.execSQL(query, new String [] {name, info1, info2, info3, info4, image, linkedNpc, linkedCity, linkedLoot, linkedItemGroup, miscGroup, oldName});

            //Log.d(TAG, "addLoot: Updating " + name + ", " + rarity + ", " + price + ", " + description + ", " + details + ", " + requirements + ", " + image + ", " + lootGroup + " to " + TABLE_NAME);
            return true;
        }
    }

    public Cursor getMisc(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    Cursor getSpecificMisc(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL2 + "=?";
        return db.rawQuery(query, new String [] {name});

    }

    public Cursor getMiscGroup(String group){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL12 + "=?";
        return db.rawQuery(query, new String [] {group});

    }

    void removeMisc(String name){
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

    public boolean checkGroupNonExistence(String groupName){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + COL12 + "=?";
        Cursor cursor = db.rawQuery(Query, new String [] {groupName});
        if(cursor.getCount() <= 0){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }


}
