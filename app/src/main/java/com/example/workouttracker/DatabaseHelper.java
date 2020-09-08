package com.example.workouttracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Workout.db";
    public static final String WORKOUT_TABLE_NAME = "workout_table";
    public static final String COL_1 = "Workout_ID";
    public static final String COL_2 = "Workout_Name";
    public static final String COL_3 = "workout_Description";



    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
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

}
