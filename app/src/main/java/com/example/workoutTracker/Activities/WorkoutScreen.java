package com.example.workoutTracker.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workoutTracker.Classes.DatabaseHelper;
import com.example.workoutTracker.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class WorkoutScreen extends AppCompatActivity {

    public static final String WORKOUT_ID = "workout_id";
    public static final String DRILL_HASH_MAP = "drillMap";

    TextView countdownTv, setsRemainingTv;
    Button btnCountdownStartPause, btnCountdownReset, btnTimerPlus, btnTimerMinus, btnCurrentDrillNext, btnCancelWorkout, btnFinishWorkout;

    CountDownTimer countDownTimer;
    long timerStartTimeInMillis = 60000, timerTimeLeftInMillis = 60000; // 1 Min, will be used with the countdown timer
    long workoutStartTimeInMillis, workoutEndTimeInMillis = 0; // will be used to calculate workout duration

    float workoutRating = 0;

    boolean timerRunning, timerReset = false;
    boolean initialCurrentDrillSelection = true;

    HashMap<String, Boolean> drillMap; // Used to track completed drills
    String currentDrillID, workoutID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workoutscreen);

        countdownTv = findViewById(R.id.tv_timer);
        btnCountdownStartPause = findViewById(R.id.btn_timer_startPause);
        btnCountdownReset = findViewById(R.id.btn_timer_reset);
        timerReset = true;
        updateTimer(); // Setting the initial time left on the timer view
        workoutStartTimeInMillis = System.currentTimeMillis(); // Used to know workout duration

        workoutID = getIntent().getStringExtra(WORKOUT_ID);  // Getting the id of the workout we are looking to show in detail.
        initDrillMap(workoutID); // Initializing the HashMap which is used to track checked drills
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

        btnTimerPlus = findViewById(R.id.btn_timer_plus);
        btnTimerMinus = findViewById(R.id.btn_timer_minus);
        btnTimerPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!timerRunning){
                    timerTimeLeftInMillis += 10000;
                    timerStartTimeInMillis += 10000;
                    updateTimer();
                }

            }
        });
        btnTimerMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!timerRunning && timerStartTimeInMillis > 0){
                    timerTimeLeftInMillis -= 10000;
                    timerStartTimeInMillis -= 10000;
                    updateTimer();
                }
            }
        });

        setsRemainingTv = findViewById(R.id.tv_current_drill_sets);
        btnCurrentDrillNext = findViewById(R.id.btn_current_drill_next); // The "Next" button is used upon completion of a set, we decrease the num of sets remaining by 1 and if all sets have been completed, we check the drill and select a new one
        btnCurrentDrillNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int setsRemaining = Integer.parseInt(setsRemainingTv.getText().toString()) - 1;
                if(setsRemaining == 0){ // if we reached 0 then we need to tick the current drill and set the next one
                    tickDrill(currentDrillID);
                    selectNewCurrentDrill(workoutID);
                    loadDrillList(workoutID);
                }
                else{
                    setsRemainingTv.setText(Integer.toString(setsRemaining));
                }

            }
        });

        btnCancelWorkout = findViewById(R.id.btn_cancel); // This button is used to cancel a workout and exit the workout screen
        btnCancelWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(WorkoutScreen.this); // This dialog is to confirm the user would like to cancel the workout
                dialog.setContentView(R.layout.dialog_cancelworkout);
                dialog.setCancelable(true);
                dialog.show();

                Button btnNo = dialog.findViewById(R.id.btn_no);
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Button btnYes = dialog.findViewById(R.id.btn_yes);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WorkoutScreen.this, BeginWorkoutActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        btnFinishWorkout = findViewById(R.id.btn_finish);
        btnFinishWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(WorkoutScreen.this); // This dialog is to confirm the user would like to finish the workout
                dialog.setContentView(R.layout.dialog_finishworkout);
                dialog.setCancelable(true);
                dialog.show();

                Button btnNo = dialog.findViewById(R.id.btn_no);
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Button btnYes = dialog.findViewById(R.id.btn_yes);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        openRateWorkoutDialog();
                    }
                });

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
            countDownTimer = new CountDownTimer(timerStartTimeInMillis, 1000) { // countDownInterval is every second, so onTick will be called every second
                @Override
                public void onTick(long millisUntilFinished) {
                    timerTimeLeftInMillis = millisUntilFinished;
                    updateTimer(); // Will update the timer TextView
                }

                @Override
                public void onFinish() {
                }

            }.start();

        else
            countDownTimer = new CountDownTimer(timerTimeLeftInMillis, 1000) { // countDownInterval is every second, so onTick will be called every second
                @Override
                public void onTick(long millisUntilFinished) {
                    timerTimeLeftInMillis = millisUntilFinished;
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

        timerTimeLeftInMillis = timerStartTimeInMillis;
        updateTimer();
        timerReset = true;
    }


    public void updateTimer() {

        int minutes = (int) timerTimeLeftInMillis / 60000;
        int seconds = (int) timerTimeLeftInMillis % 60000 / 1000; // after performing % and removing time left in minutes we divide by 1000 to we have the number of seconds left

        String timeLeftText = "";
        if (minutes < 10) timeLeftText += "0";
        timeLeftText += "" + minutes;
        timeLeftText += ":";
        if (seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;

        countdownTv.setText(timeLeftText);
    }


    public void loadDrillList(final String workoutID) {

        Cursor result = DatabaseHelper.getInstance(WorkoutScreen.this).getDrillsByWorkoutID(workoutID);

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

        Cursor result = DatabaseHelper.getInstance(WorkoutScreen.this).getDrillsByWorkoutID(workoutID);
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
            final Dialog dialog = new Dialog(WorkoutScreen.this); // This dialog is to confirm the user would like to finalize the workout
            dialog.setContentView(R.layout.dialog_drillscomplete);
            dialog.setCancelable(true);
            dialog.show();

            Button btnNo = dialog.findViewById(R.id.btn_no);
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            Button btnYes = dialog.findViewById(R.id.btn_yes);
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    openRateWorkoutDialog();
                }
            });

        }
    }


    public void initDrillMap(final String workoutID) {

        drillMap = new HashMap<>();
        Cursor result = DatabaseHelper.getInstance(WorkoutScreen.this).getDrillsByWorkoutID(workoutID);

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


    public void openRateWorkoutDialog() {

        final Dialog dialog = new Dialog(WorkoutScreen.this); // This dialog is to get a workout rating form the user
        dialog.setContentView(R.layout.dialog_getworkoutrating);
        dialog.setCancelable(true);
        dialog.show();

        RatingBar ratingBar = dialog.findViewById(R.id.ratingbar_workout);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                workoutRating = rating;
            }
        });

        Button btnSubmit = dialog.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWorkout();
            }
        });


    }


    public void finishWorkout() {

        Intent intent = new Intent(WorkoutScreen.this, WorkoutSummaryActivity.class);

        workoutEndTimeInMillis = System.currentTimeMillis();
        long workoutDurationInMillis = workoutEndTimeInMillis - workoutStartTimeInMillis; // Calculating Workout Duration

        int hours = (int) workoutDurationInMillis / 3600000;
        int minutes = (int) workoutDurationInMillis % 3600000 / 60000; // removing time elapsed in hours in order to be left with the remainder
        int seconds = (int) workoutDurationInMillis % 60000 / 1000; // after performing % and removing time left in minutes we divide by 1000 to we have the number of seconds left

        String workoutDurationFormatted = "";
        if (hours < 10) workoutDurationFormatted += "0";
        workoutDurationFormatted += "" + hours;
        workoutDurationFormatted += ":";
        if (minutes < 10) workoutDurationFormatted += "0";
        workoutDurationFormatted += "" + minutes;
        workoutDurationFormatted += ":";
        if (seconds < 10) workoutDurationFormatted += "0";
        workoutDurationFormatted += seconds; // now the workout duration is formatted into a string

        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()); // Getting the current date and setting it in selected format
        String formattedDate = df.format(currentDate);

        String workoutRatingString = Float.toString(workoutRating);

        if(!DatabaseHelper.getInstance(this).addSummary(formattedDate, workoutDurationFormatted, workoutRatingString, workoutID))
            Toast.makeText(this, R.string.toast_data_add_failed, Toast.LENGTH_SHORT).show();

        intent.putExtra(WORKOUT_ID, workoutID);

        startActivity(intent);

    }


}
