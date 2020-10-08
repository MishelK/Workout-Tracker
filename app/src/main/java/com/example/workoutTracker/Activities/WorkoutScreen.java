package com.example.workoutTracker.Activities;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workoutTracker.Classes.DatabaseHelper;
import com.example.workoutTracker.R;

import java.util.HashMap;


public class WorkoutScreen extends AppCompatActivity {

    public static final String WORKOUT_ID = "workout_id";

    TextView countdownTv, setsRemainingTv;
    Button btnCountdownStartPause, btnCountdownReset, btnCurrentDrillNext;

    CountDownTimer countDownTimer;
    long startTimeInMillis = 60000; // 1 Min
    long timeLeftInMillis = 60000;

    boolean timerRunning, timerReset = false;
    boolean initialCurrentDrillSelection = true;

    HashMap<String, Boolean> drillMap; // Used to track completed drills
    String currentDrillID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workoutscreen);

        countdownTv = findViewById(R.id.tv_timer);
        btnCountdownStartPause = findViewById(R.id.btn_timer_startPause);
        btnCountdownReset = findViewById(R.id.btn_timer_reset);
        timerReset = true;

        updateTimer(); // Setting the initial time left on the timer/

        final String workoutID = getIntent().getStringExtra(WORKOUT_ID);  // Getting the id of the workout we are looking to show in detail.
        initDrillMap(workoutID); // Initializing the HashMap which is used to track ticked drills
        loadDrillList(workoutID); // Loading the drill list with checkboxes

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

        setsRemainingTv = findViewById(R.id.tv_current_drill_sets);
        btnCurrentDrillNext = findViewById(R.id.btn_current_drill_next);
        btnCurrentDrillNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int setsRemaining = Integer.parseInt(setsRemainingTv.getText().toString()) - 1;
                if(setsRemaining == 0){ //if we reached 0 then we need to tick the current drill and set the next one
                    tickDrill(currentDrillID);
                    selectNewCurrentDrill(workoutID);
                    loadDrillList(workoutID);
                }
                else{
                    setsRemainingTv.setText(Integer.toString(setsRemaining));
                }

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

        countdownTv.setText(timeLeftText);
    }

    public void loadDrillList(final String workoutID) {

        Cursor result = DatabaseHelper.getInstance(WorkoutScreen.this).getAllDrillsData(workoutID);

        if(result.getCount() > 0) { // Checking if there are any drills in the result

            LinearLayout workoutPreviewsLayout = findViewById(R.id.ll_drill_list); // Linear Layout that will contain all sub-RelativeLayouts
            workoutPreviewsLayout.removeAllViews();
            TextView nameTv, setsTv, repsTv;
            CheckBox checkBox;

            LayoutInflater layoutInflater;
            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutInflater = LayoutInflater.from(WorkoutScreen.this);

            RelativeLayout drillView;


            while (result.moveToNext()) { // will be false when we passed the last result

                drillView = (RelativeLayout) layoutInflater.inflate(R.layout.layout_drill_checkbox, null);

                // Following section is to select the first unchecked drill in the list to be the current drill by default
                if(initialCurrentDrillSelection && !drillMap.get(result.getString(0))) { // Checking if initial selection & if drill is unchecked
                    setCurrentDrillById(result.getString(0));
                    currentDrillID = result.getString(0);
                    initialCurrentDrillSelection = false;
                }
                // Loading the drill list into the scroll view below Current Drill
                nameTv = drillView.findViewById(R.id.tv_drill_name);
                nameTv.setText(result.getString(1));
                repsTv = drillView.findViewById(R.id.tv_num_reps);
                repsTv.setText(result.getString(2));
                setsTv = drillView.findViewById(R.id.tv_num_sets);
                setsTv.setText(result.getString(3));

                final String drillID = result.getString(0);
                checkBox = drillView.findViewById(R.id.cb_drill);
                if(drillMap.get(drillID)) // if the current drill has been marked as checked on the map then we want to draw it as checked
                    checkBox.setChecked(true);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        tickDrill(drillID);
                        if(isChecked){
                            if(drillID.equals(currentDrillID)){ //in this case we want to select a new current drill
                                selectNewCurrentDrill(workoutID);
                            }
                        }
                    }
                });

                drillView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!drillMap.get(drillID)) // This if statement will prevent from selecting a completed drill as the current drill
                            setCurrentDrillById(drillID);
                    }
                });

                workoutPreviewsLayout.addView(drillView);
            }

        }

    }

    public void setCurrentDrillById(String drillID) {

        Cursor result = DatabaseHelper.getInstance(WorkoutScreen.this).getDrillById(drillID);

        if(result.getCount() > 0){
            result.moveToNext();

            TextView currentDrillName, currentDrillReps, currentDrillSets;
            currentDrillName = findViewById(R.id.tv_current_drill_name);
            currentDrillReps = findViewById(R.id.tv_current_drill_reps);
            currentDrillSets = findViewById(R.id.tv_current_drill_sets);
            currentDrillName.setText(result.getString(1));
            currentDrillReps.setText(result.getString(2));
            currentDrillSets.setText(result.getString(3));
            currentDrillID = result.getString(0);
        }
    }

    public void selectNewCurrentDrill(String workoutID) {
        boolean found = false;

        Cursor result = DatabaseHelper.getInstance(WorkoutScreen.this).getAllDrillsData(workoutID);
        if(result.getCount() > 0){
            while(result.moveToNext() && !found){
                if(!drillMap.get(result.getString(0))){ // checking if the drill is ticked or not
                    TextView currentDrillName, currentDrillReps, currentDrillSets;
                    currentDrillName = findViewById(R.id.tv_current_drill_name);
                    currentDrillReps = findViewById(R.id.tv_current_drill_reps);
                    currentDrillSets = findViewById(R.id.tv_current_drill_sets);
                    currentDrillName.setText(result.getString(1));
                    currentDrillReps.setText(result.getString(2));
                    currentDrillSets.setText(result.getString(3));
                    currentDrillID = result.getString(0);
                    found = true;
                }
            }
        }
        if(!found){ // Will be true when all drills have been completed

        }
    }

    public void initDrillMap(final String workoutID) {

        drillMap = new HashMap<>();
        Cursor result = DatabaseHelper.getInstance(WorkoutScreen.this).getAllDrillsData(workoutID);

        if(result.getCount() > 0){
            while(result.moveToNext()){
                drillMap.put(result.getString(0), false); // Inserting all drills into HashMap with false value.
            }
        }
    }

    public void tickDrill(String drillID) { // Will change value of flag inside HashMap to opposite value.

        boolean temp;
        if(drillMap.containsKey(drillID)) {

                temp = drillMap.get(drillID);
                drillMap.put(drillID, !temp);
        }
    }


}
