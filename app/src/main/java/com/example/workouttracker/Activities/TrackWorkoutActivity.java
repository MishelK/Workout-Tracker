package com.example.workouttracker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workouttracker.R;

public class TrackWorkoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trackworkouts);

        //Setting Return Button
        Button btnReturn = findViewById(R.id.btn_return_track);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackWorkoutActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
