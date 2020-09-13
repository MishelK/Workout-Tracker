package com.example.workouttracker.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workouttracker.Classes.DatabaseHelper;
import com.example.workouttracker.R;

import java.util.Objects;

public class EditWorkoutActivity extends AppCompatActivity {

    public static final String WORKOUT_ID = "workout_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editworkout);

        loadWorkoutPreview(); // Loading workout preview list

        //Setting Return Button listener
        Button btnReturn = findViewById(R.id.btn_return_edit);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditWorkoutActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        //Setting Add Workout Button listener
        Button btnAddWorkout = findViewById(R.id.btn_add_new_workout);
        btnAddWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddWorkoutDialog();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadWorkoutPreview();
    }

    private void openAddWorkoutDialog() {

        final Dialog dialog = new Dialog(EditWorkoutActivity.this);
        dialog.setContentView(R.layout.dialog_addworkout);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();

        final EditText workoutNameEt = dialog.findViewById(R.id.et_workout_name);
        final EditText workoutDescEt = dialog.findViewById(R.id.et_workout_description);

        //Setting Button Listeners
        Button closeBtn = dialog.findViewById(R.id.btn_close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button addBtn = dialog.findViewById(R.id.btn_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name, description;
                name = workoutNameEt.getText().toString();
                description = workoutDescEt.getText().toString();

                if(name.isEmpty()) { // Checking if user has entered a name

                    Toast.makeText(EditWorkoutActivity.this, R.string.toast_data_empty_field, Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isInserted = DatabaseHelper.getInstance(EditWorkoutActivity.this).addWorkout(name,description); // Sending the new workout data to insert data method

                if(isInserted) {
                    Toast.makeText(EditWorkoutActivity.this, R.string.toast_data_add_success, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadWorkoutPreview(); // After successfully adding a new workout we want to reload the workout preview list
                }
                else
                    Toast.makeText(EditWorkoutActivity.this, R.string.toast_data_add_failed, Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });


    }

    public void loadWorkoutPreview() {

        final Cursor result = DatabaseHelper.getInstance(EditWorkoutActivity.this).getAllWorkoutData();

        if(result.getCount() > 0) { // Checking if there are any workouts in the database

            LinearLayout workoutPreviewsLayout = findViewById(R.id.ll_workout_preview); // Linear Layout that will contain all sub-RelativeLayouts
            workoutPreviewsLayout.removeAllViews();
            TextView nameTv, descTv;
            Button btnInfo;

            LayoutInflater layoutInflater;
            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutInflater = LayoutInflater.from(EditWorkoutActivity.this);

            RelativeLayout workoutPreview;

            while (result.moveToNext()) { // will be false when we passed the last result

                workoutPreview = (RelativeLayout) layoutInflater.inflate(R.layout.layout_workout_preview, null);

                nameTv = workoutPreview.findViewById(R.id.tv_workout_preview_name);
                nameTv.setText(result.getString(1));
                descTv = workoutPreview.findViewById(R.id.tv_workout_preview_desc);
                descTv.setText(result.getString(2));
                btnInfo = workoutPreview.findViewById(R.id.btn_workout_more);
                final String workoutID = result.getString(0);
                btnInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EditWorkoutActivity.this, WorkoutInfoActivity.class); // Upon clicking the arrow button on a workout preview, user will be shown a detailed view
                        intent.putExtra(WORKOUT_ID, workoutID);
                        startActivity(intent);
                    }
                });

                workoutPreviewsLayout.addView(workoutPreview);

            }
        }

    }

}
