package com.example.workouttracker;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class EditWorkoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editworkout);

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
                openSettingsDialog();
            }
        });

    }


    private void openSettingsDialog() {

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

                if(name.isEmpty()) { //Checking if user has entered a name
                    Toast.makeText(EditWorkoutActivity.this, R.string.toast_data_empty_field, Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isInserted = DatabaseHelper.getInstance(EditWorkoutActivity.this).addWorkout(name,description); //Sending the new workout data to insert data method

                if(isInserted)
                    Toast.makeText(EditWorkoutActivity.this, R.string.toast_data_add_success, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(EditWorkoutActivity.this, R.string.toast_data_add_failed, Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });



    }

}
