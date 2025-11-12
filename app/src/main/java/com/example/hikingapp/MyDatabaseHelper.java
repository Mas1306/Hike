package com.example.hikingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "HikeApp.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "hike_list";
    private static final String COLUMN_ID = "hike_id";
    private static final String COLUMN_NAME = "hike_name";
    private static final String COLUMN_LOCATION = "hike_location";
    private static final String COLUMN_DATE = "hike_date";
    private static final String COLUMN_LENGTH = "hike_length";
    private static final String COLUMN_LEVEL = "hike_level";
    private static final String COLUMN_AVAILABLE = "hike_available";
    private static final String COLUMN_DESCRIPTION = "hike_description";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME+ " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_LENGTH + " INTEGER, " +
                COLUMN_LEVEL + " TEXT, " +
                COLUMN_AVAILABLE + " INTEGER, " +
                COLUMN_DESCRIPTION + " TEXT);" ;
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    void addHike(String name, String location, String date, String length, String level, String available, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_LOCATION, location);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_LENGTH, length);
        cv.put(COLUMN_LEVEL, level);
        cv.put(COLUMN_AVAILABLE, available);
        cv.put(COLUMN_DESCRIPTION, description);
        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1 ){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
        }
    }
    public void deleteOneHike(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }
    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }
    Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, null);
    }
}
