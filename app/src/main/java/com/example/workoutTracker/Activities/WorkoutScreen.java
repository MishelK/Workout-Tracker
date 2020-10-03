package com.example.workoutTracker.Activities;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workoutTracker.Classes.DatabaseHelper;
import com.example.workoutTracker.R;


public class WorkoutScreen extends AppCompatActivity {

    public static final String WORKOUT_ID = "workout_id";

    TextView countdownText;
    Button btnCountdownStartPause, btnCountdownReset;

    CountDownTimer countDownTimer;
    long startTimeInMillis = 60000; // 1 Min
    long timeLeftInMillis = 60000;

    boolean timerRunning, timerReset = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workoutscreen);

        countdownText = findViewById(R.id.tv_timer);
        btnCountdownStartPause = findViewById(R.id.btn_timer_startPause);
        btnCountdownReset = findViewById(R.id.btn_timer_reset);
        timerReset = true;

        updateTimer(); // Setting the initial time left on the timer

        final String workoutID = getIntent().getStringExtra(WORKOUT_ID);  //getting the id of the workout we are looking to show in detail.
        loadDrillList(workoutID);

        btnCountdownStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });

        btnCountdownReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!timerRunning)
                    resetTimer();
            }
        });


    }

    public void startStop() {

        if(timerRunning) {
            stopTimer();
        }
        else {
            startTimer();
        }
    }

    public void startTimer(){

        if(timerReset)
            countDownTimer = new CountDownTimer(startTimeInMillis, 1000) { // countDownInterval is every second, so onTick will be called every second
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    updateTimer(); // Will update the timer TextView
                }

                @Override
                public void onFinish() {
                }

            }.start();

        else
            countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) { // countDownInterval is every second, so onTick will be called every second
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    updateTimer(); // Will update the timer TextView
                }

                @Override
                public void onFinish() {
                }
            }.start();

        btnCountdownStartPause.setText(R.string.btn_txt_pause);
        timerRunning = true;
        timerReset = false;

        btnCountdownReset.setClickable(false);
        btnCountdownReset.setTextColor(Color.GRAY);
    }

    public void stopTimer(){

        countDownTimer.cancel();
        timerRunning = false;
        btnCountdownStartPause.setText(R.string.btn_txt_start);

        btnCountdownReset.setClickable(true);
        btnCountdownReset.setTextColor(Color.BLACK);
    }

    public void resetTimer() {

        timeLeftInMillis = startTimeInMillis;
        updateTimer();
        timerReset = true;
    }

    public void updateTimer() {

        int minutes = (int) timeLeftInMillis / 60000;
        int seconds = (int) timeLeftInMillis % 60000 / 1000; // after performing % and removing time left in minutes we divide by 1000 to we have the number of seconds left

        String timeLeftText;
        timeLeftText = "" + minutes;
        timeLeftText += ":";
        if (seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;

        countdownText.setText(timeLeftText);
    }

    public void loadDrillList(final String workoutID) {

        Cursor result = DatabaseHelper.getInstance(WorkoutScreen.this).getAllDrillsData(workoutID);

        if(result.getCount() > 0) { // Checking if there are any drills in the result

            LinearLayout workoutPreviewsLayout = findViewById(R.id.ll_drill_list); // Linear Layout that will contain all sub-RelativeLayouts
            workoutPreviewsLayout.removeAllViews();
            TextView nameTv, setsTv, repsTv;

            LayoutInflater layoutInflater;
            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutInflater = LayoutInflater.from(WorkoutScreen.this);

            RelativeLayout drillView;


            while (result.moveToNext()) { // will be false when we passed the last result

                drillView = (RelativeLayout) layoutInflater.inflate(R.layout.layout_drill_checkbox, null);

                nameTv = drillView.findViewById(R.id.tv_drill_name);
                nameTv.setText(result.getString(1));
                setsTv = drillView.findViewById(R.id.tv_num_sets);
                setsTv.setText(result.getString(3));
                repsTv = drillView.findViewById(R.id.tv_num_reps);
                repsTv.setText(result.getString(2));

                final String drillID = result.getString(0);

                workoutPreviewsLayout.addView(drillView);
            }

        }

    }

}
