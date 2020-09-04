package com.example.workouttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Getting the homepage's buttons
        Button btnBeginWorkout = findViewById(R.id.btn_begin);
        Button btnEditWorkout = findViewById(R.id.btn_edit);
        Button btnTrackWorkout = findViewById(R.id.btn_track);
        Button btnSettings = findViewById(R.id.btn_settings);

        //Setting the Home Page's Buttons listeners
        btnBeginWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BeginWorkoutActivity.class);
                startActivity(intent);
            }
        });

        btnEditWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditWorkoutActivity.class);
                startActivity(intent);
            }
        });

        btnTrackWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TrackWorkoutActivity.class);
                startActivity(intent);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });




    }
}