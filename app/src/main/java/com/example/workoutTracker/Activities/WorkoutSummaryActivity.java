package com.example.workoutTracker.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workoutTracker.Classes.DatabaseHelper;
import com.example.workoutTracker.R;


public class WorkoutSummaryActivity extends AppCompatActivity {

    public static final String WORKOUT_ID = "workout_id";

    TextView workoutNameTv, dateTv, durationTv;
    RatingBar ratingBar;
    Button closeBtn;

    String workoutID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workoutsummary);

        workoutNameTv = findViewById(R.id.tv_workout_name);
        dateTv = findViewById(R.id.tv_date);
        durationTv = findViewById(R.id.tv_duration);
        ratingBar = findViewById(R.id.ratingbar_workout);

        workoutID = getIntent().getStringExtra(WORKOUT_ID);

        getWorkoutName();
        getSummaryInfo();

        closeBtn = findViewById(R.id.btn_close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkoutSummaryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    public void getWorkoutName() {

        Cursor result = DatabaseHelper.getInstance(WorkoutSummaryActivity.this).getWorkoutDataByID(workoutID);

        if(result.getCount() > 0){
            while (result.moveToNext()){
                workoutNameTv.setText(result.getString(1));
            }
        }
    }

    public void getSummaryInfo() {

        Cursor result = DatabaseHelper.getInstance(WorkoutSummaryActivity.this).getSummaryByWorkoutID(workoutID);

        if(result.getCount() > 0){
            while (result.moveToNext()){
                dateTv.setText(result.getString(1));
                durationTv.setText(result.getString(2));
                ratingBar.setRating(Float.parseFloat(result.getString(3)));
            }
        }


    }

}
