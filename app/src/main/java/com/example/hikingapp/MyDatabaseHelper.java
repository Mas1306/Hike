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
    private static final int DATABASE_VERSION = 3;
    public static final String TABLE_HIKE = "hike_list";
    public static final String COLUMN_HIKE_ID = "hike_id";
    public static final String COLUMN_HIKE_NAME = "hike_name";
    public static final String COLUMN_HIKE_LOCATION = "hike_location";
    public static final String COLUMN_HIKE_DATE = "hike_date";
    public static final String COLUMN_HIKE_LENGTH = "hike_length";
    public static final String COLUMN_HIKE_LEVEL = "hike_level";
    public static final String COLUMN_HIKE_AVAILABLE = "hike_available";
    public static final String COLUMN_HIKE_DESCRIPTION = "hike_description";
    public static final String TABLE_OBSERVATION = "observation_list";
    public static final String COLUMN_OBS_ID = "observation_id";
    public static final String COLUMN_OBS_HIKE_ID = "obs_hike_id";
    public static final String COLUMN_OBS_NAME = "observation_name";
    public static final String COLUMN_OBS_DATE = "observation_date";
    public static final String COLUMN_OBS_COMMENT = "observation_comment";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createHikeTable =
                "CREATE TABLE " + TABLE_HIKE + " (" +
                        COLUMN_HIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_HIKE_NAME + " TEXT, " +
                        COLUMN_HIKE_LOCATION + " TEXT, " +
                        COLUMN_HIKE_DATE + " TEXT, " +
                        COLUMN_HIKE_LENGTH + " INTEGER, " +
                        COLUMN_HIKE_LEVEL + " TEXT, " +
                        COLUMN_HIKE_AVAILABLE + " INTEGER, " +
                        COLUMN_HIKE_DESCRIPTION + " TEXT);";
        db.execSQL(createHikeTable);

        String createObservationTable =
                "CREATE TABLE " + TABLE_OBSERVATION + " (" +
                        COLUMN_OBS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_OBS_HIKE_ID + " INTEGER, " +
                        COLUMN_OBS_NAME + " TEXT, " +
                        COLUMN_OBS_DATE + " TEXT, " +
                        COLUMN_OBS_COMMENT + " TEXT, " +
                        "FOREIGN KEY(" + COLUMN_OBS_HIKE_ID + ") REFERENCES " + TABLE_HIKE + "(" + COLUMN_HIKE_ID + "));";
        db.execSQL(createObservationTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKE);
        onCreate(db);
    }
    void addHike(String name, String location, String date, String length, String level, String available, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_HIKE_NAME, name);
        cv.put(COLUMN_HIKE_LOCATION, location);
        cv.put(COLUMN_HIKE_DATE, date);
        cv.put(COLUMN_HIKE_LENGTH, length);
        cv.put(COLUMN_HIKE_LEVEL, level);
        cv.put(COLUMN_HIKE_AVAILABLE, available);
        cv.put(COLUMN_HIKE_DESCRIPTION, description);
        long result = db.insert(TABLE_HIKE, null, cv);
        Toast.makeText(context, result == -1 ? "Failed" : "Added Successfully", Toast.LENGTH_SHORT).show();
    }

    public boolean updateHike(String id, String name, String location, String date, String length, String level, String available, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_HIKE_NAME, name);
        cv.put(COLUMN_HIKE_LOCATION, location);
        cv.put(COLUMN_HIKE_DATE, date);
        cv.put(COLUMN_HIKE_LENGTH, length);
        cv.put(COLUMN_HIKE_LEVEL, level);
        cv.put(COLUMN_HIKE_AVAILABLE, available);
        if (description == null || description.trim().isEmpty()) {
            cv.putNull(COLUMN_HIKE_DESCRIPTION);
        } else {
            cv.put(COLUMN_HIKE_DESCRIPTION, description);
        }
        int result = db.update(TABLE_HIKE, cv, COLUMN_HIKE_ID + "=?", new String[]{id});
        return result > 0;
    }

    public Cursor getHikeById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_HIKE + " WHERE " + COLUMN_HIKE_ID + "=?", new String[]{id});
    }

    public void deleteOneHike(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HIKE, COLUMN_HIKE_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_HIKE);
        db.execSQL("DELETE FROM " + TABLE_OBSERVATION);
        db.close();
    }

    Cursor readAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_HIKE, null);
    }

    public boolean addObservation(int hikeId, String name, String date, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_OBS_HIKE_ID, hikeId);
        cv.put(COLUMN_OBS_NAME, name);
        cv.put(COLUMN_OBS_DATE, date);
        cv.put(COLUMN_OBS_COMMENT, comment);

        long result = db.insert(TABLE_OBSERVATION, null, cv);
        return result != -1;
    }

    public Cursor getObservationsByHike(int hikeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_OBSERVATION + " WHERE " + COLUMN_OBS_HIKE_ID + " = ? ORDER BY " + COLUMN_OBS_DATE + " DESC",
                new String[]{String.valueOf(hikeId)}
        );
    }

    public boolean updateObservation(int obsId, String name, String date, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_OBS_NAME, name);
        cv.put(COLUMN_OBS_DATE, date);
        cv.put(COLUMN_OBS_COMMENT, comment);

        int result = db.update(TABLE_OBSERVATION, cv,
                COLUMN_OBS_ID + " = ?",
                new String[]{String.valueOf(obsId)}
        );

        return result > 0;
    }

    public boolean deleteObservation(int obsId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_OBSERVATION,
                COLUMN_OBS_ID + " = ?",
                new String[]{String.valueOf(obsId)}
        );
        return result > 0;
    }
}
