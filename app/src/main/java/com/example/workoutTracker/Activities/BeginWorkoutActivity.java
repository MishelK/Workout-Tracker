package com.example.workoutTracker.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workoutTracker.Classes.DatabaseHelper;
import com.example.workoutTracker.R;

public class BeginWorkoutActivity extends AppCompatActivity {

    public static final String WORKOUT_ID = "workout_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginworkout);

        loadWorkoutList();

        //Setting Return Button
        Button btnReturn = findViewById(R.id.btn_return_begin);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BeginWorkoutActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    public void loadWorkoutList() {

        final Cursor result = DatabaseHelper.getInstance(BeginWorkoutActivity.this).getAllWorkoutData();

        if(result.getCount() > 0) { //Checking if there are any workouts returned from the database

            LinearLayout workoutListLayout = findViewById(R.id.ll_workout_list); // Linear Layout that will contain all sub-RelativeLayouts
            workoutListLayout.removeAllViews();
            TextView nameTv, descTv;
            Button btnSelect;

            LayoutInflater layoutInflater;
            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutInflater = LayoutInflater.from(BeginWorkoutActivity.this);

            RelativeLayout workoutPreview;

            while (result.moveToNext()) { // will be false when we ran out of unchecked results

                workoutPreview = (RelativeLayout) layoutInflater.inflate(R.layout.layout_workout_preview, null);

                nameTv = workoutPreview.findViewById(R.id.tv_workout_preview_name);
                nameTv.setText(result.getString(1));
                descTv = workoutPreview.findViewById(R.id.tv_workout_preview_desc);
                descTv.setText(result.getString(2));
                btnSelect = workoutPreview.findViewById(R.id.btn_workout_more);
                final String workoutID = result.getString(0);
                btnSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { // Upon selecting a workout
                        Intent intent = new Intent(BeginWorkoutActivity.this, WorkoutScreen.class);
                        intent.putExtra(WORKOUT_ID, workoutID);
                        startActivity(intent);
                    }
                });
                workoutListLayout.addView(workoutPreview);
            }

        }

    }


}
