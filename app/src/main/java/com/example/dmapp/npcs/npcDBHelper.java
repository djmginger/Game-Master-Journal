//Create a duplicate version of this, that supports a different database
package com.example.dmapp.npcs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class npcDBHelper extends SQLiteOpenHelper {
    private static npcDBHelper sInstance;
    private static final String TAG = "npcDBHelper";

    private static final String TABLE_NAME = "npc_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "name";
    private static final String COL3 = "location";
    private static final String COL4 = "description";
    private static final String COL5 = "notes";
    private static final String COL6 = "voice";
    private static final String COL7 = "plothooks";
    private static final String COL8 = "race";
    private static final String COL9 = "image";

    //* get the context of each instance of npcDBHelper
    public static synchronized npcDBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new npcDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public npcDBHelper(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " TEXT, " + COL5 + " TEXT, " + COL6 + " TEXT, " + COL7 + " TEXT, " + COL8 + " TEXT, " + COL9 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    boolean addNPC(String name, String location, String description, String notes, String oldName, String voice, String plothooks, String race, String image, Boolean updateNpc){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if(!updateNpc) {  //if the npc does not exist, add a new one
            contentValues.put(COL2, name);
            contentValues.put(COL3, location);
            contentValues.put(COL4, description);
            contentValues.put(COL5, notes);
            contentValues.put(COL6, voice);
            contentValues.put(COL7, plothooks);
            contentValues.put(COL8, race);
            contentValues.put(COL9, image);

            Log.d(TAG, "addNPC: Adding " + name + ", " + race + ", " + location + ", " + description + ", " + notes + ", " + plothooks + " to " + TABLE_NAME);
            //long result =
            db.insert(TABLE_NAME, null, contentValues); //result will be -1 if data was inserted incorrectly
            return (true);
            //result != -1
        }else{  //if the npc does exist, update the current entry
            String query = "UPDATE " + TABLE_NAME + " SET " + COL2 + "=?" + "," + COL3 + "=?" + "," + COL4 + "=?" + "," + COL5 + "=?" + "," + COL6 + "=?" + "," + COL7 + "=?" + "," + COL8 + "=?" + "," + COL9 + "=?" + "WHERE " + COL2 + "=?";
            db.execSQL(query, new String [] {name, location, description, notes, voice, plothooks, race, image, oldName});

            Log.d(TAG, "addNPC: Updating " + oldName + " to " + name + ", " + race + ", " + location + ", " + description + ", " + notes + ", " + plothooks + " to " + TABLE_NAME);

            return true;
        }
    }

    Cursor getNPCS(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    public Cursor getLocationNPCS(String location){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL3 + "=?";
        return db.rawQuery(query, new String [] {location});
    }

    Cursor getSpecificNPC(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL2 + "=?";
        return db.rawQuery(query, new String [] {name});
    }

    void removeNPC(String name){
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
