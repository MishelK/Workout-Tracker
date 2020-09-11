package com.example.workouttracker.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance = null;

    public static final String DATABASE_NAME = "Workout.db";
    public static final String WORKOUT_TABLE_NAME = "workout_table";
    public static final String COL_1 = "Workout_ID";
    public static final String COL_2 = "Workout_Name";
    public static final String COL_3 = "workout_Description";


    public static DatabaseHelper getInstance(Context context){

        if(instance == null)
            instance = new DatabaseHelper(context.getApplicationContext());
        return instance;
    }


    private DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + WORKOUT_TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_2 + " TEXT," + COL_3 +" TEXT)" ); //Creates the Workout table

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + WORKOUT_TABLE_NAME);
        onCreate(db);

    }

    public boolean addWorkout(String name, String description) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, description);

        long result = db.insert(WORKOUT_TABLE_NAME, null, contentValues); //Inserting new data into table

        return result != -1;
    }

    public boolean updateWorkout(String id, String name, String description) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, description);
        long result = db.update(WORKOUT_TABLE_NAME, contentValues, COL_1 + " = ?", new String[] { id });
        return result != 0;
    }

    public boolean deleteWorkout(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(WORKOUT_TABLE_NAME, COL_1 + " = ?", new String[] { id });
        return result != 0;
    }

    public Cursor getAllWorkoutData() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + WORKOUT_TABLE_NAME, null); //Gets all data from table
        return result;
    }



}
