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
    public static final String TABLE_NAME_WORKOUT = "workout_table";
    public static final String TABLE_NAME_DRILL = "drills_table";
    public static final String COL_1_WORKOUT = "Workout_ID";
    public static final String COL_2_WORKOUT = "Workout_Name";
    public static final String COL_3_WORKOUT = "workout_Description";
    public static final String COL_1_DRILL = "Drill_ID";
    public static final String COL_2_DRILL = "Drill_Name";
    public static final String COL_3_DRILL = "Drill_Reps";
    public static final String COL_4_DRILL = "Drill_Sets";
    public static final String COL_5_DRILL = "Workout_ID";



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

        db.execSQL("CREATE TABLE " + TABLE_NAME_WORKOUT + "(" + COL_1_WORKOUT + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_2_WORKOUT + " TEXT," + COL_3_WORKOUT +" TEXT)" ); //Creates the Workout table
        db.execSQL("CREATE TABLE " + TABLE_NAME_DRILL + "(" + COL_1_DRILL + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_2_DRILL + " TEXT," + COL_3_DRILL + " TEXT," + COL_4_DRILL + " TEXT," + COL_5_DRILL + " TEXT)"); //Creates the Drill table

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_WORKOUT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DRILL);
        onCreate(db);

    }

    public boolean addWorkout(String name, String description) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2_WORKOUT, name);
        contentValues.put(COL_3_WORKOUT, description);

        long result = db.insert(TABLE_NAME_WORKOUT, null, contentValues); //Inserting new data into table

        return result != -1;
    }

    public boolean updateWorkout(String id, String name, String description) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1_WORKOUT, id);
        contentValues.put(COL_2_WORKOUT, name);
        contentValues.put(COL_3_WORKOUT, description);
        long result = db.update(TABLE_NAME_WORKOUT, contentValues, COL_1_WORKOUT + " = ?", new String[] { id });
        return result != 0;
    }

    public boolean deleteWorkout(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME_WORKOUT, COL_1_WORKOUT + " = ?", new String[] { id });
        return result != 0;
    }

    public Cursor getAllWorkoutData() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME_WORKOUT, null); //Gets all data from table
        return result;
    }

    public Cursor getWorkoutDataByID(String workoutID) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME_WORKOUT + " WHERE " + COL_1_WORKOUT + " = ?",new String[] { workoutID });
        return result;

    }

    public boolean addDrill(String name, String reps, String sets, String workoutID) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2_DRILL, name);
        contentValues.put(COL_3_DRILL, reps);
        contentValues.put(COL_4_DRILL, sets);
        contentValues.put(COL_5_DRILL, workoutID);

        long result = db.insert(TABLE_NAME_DRILL, null, contentValues);

        return result != -1;
    }

    public boolean updateDrill(String drillID, String name, String reps, String sets, String workoutID) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1_DRILL, drillID);
        contentValues.put(COL_2_DRILL, name);
        contentValues.put(COL_3_DRILL, reps);
        contentValues.put(COL_4_DRILL, sets);
        contentValues.put(COL_5_DRILL, workoutID);
        long result = db.update(TABLE_NAME_DRILL, contentValues, COL_1_DRILL + " = ?", new String[] { drillID });
        return result != 0;
    }

    public boolean deleteDrill(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME_DRILL, COL_1_DRILL + " = ?", new String[] { id });
        return result != 0;
    }

    public Cursor getAllDrillsData(String workoutID) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME_DRILL + " WHERE " + COL_5_DRILL + " LIKE '" + workoutID + "'", null); //Gets all data from table
        return result;
    }

}
