package com.example.workoutTracker.Activities;

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

import com.example.workoutTracker.Classes.DatabaseHelper;
import com.example.workoutTracker.R;

import java.util.Objects;

public class WorkoutInfoActivity extends AppCompatActivity {

    public static final String WORKOUT_ID = "workout_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workoutinfo);

        final String workoutID = getIntent().getStringExtra(WORKOUT_ID);  //getting the id of the workout we are looking to show in detail.
        loadDrillList(workoutID);

        Button returnBtn = findViewById(R.id.btn_return_info);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkoutInfoActivity.this, EditWorkoutActivity.class);
                startActivity(intent);
            }
        });

        Button cancelBtn = findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkoutInfoActivity.this, EditWorkoutActivity.class);
                startActivity(intent);
            }
        });

        Button addNewDrillBtn = findViewById(R.id.btn_add_new_drill);
        addNewDrillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddDrillDialog(workoutID);
            }
        });

        Cursor result = DatabaseHelper.getInstance(WorkoutInfoActivity.this).getWorkoutDataByID(workoutID);

        final EditText workoutNameEt = findViewById(R.id.et_workout_name);
        final EditText workoutDescEt = findViewById(R.id.et_workout_desc);

        while (result.moveToNext()) {
            workoutNameEt.setText(result.getString(1)); //Will load the workout's name and desc in order to allow editing
            workoutDescEt.setText(result.getString(2));
        }

        Button saveBtn = findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean Result = DatabaseHelper.getInstance(WorkoutInfoActivity.this).updateWorkout(workoutID, workoutNameEt.getText().toString(), workoutDescEt.getText().toString()); // saving changes made to name and desc
                Intent intent = new Intent(WorkoutInfoActivity.this, EditWorkoutActivity.class);
                startActivity(intent);
            }
        });

    }

    public void loadDrillList(final String workoutID) {

        Cursor result = DatabaseHelper.getInstance(WorkoutInfoActivity.this).getDrillsByWorkoutID(workoutID);

        if(result.getCount() > 0) { // Checking if there are any drills in the result

            LinearLayout workoutPreviewsLayout = findViewById(R.id.ll_drill_list); // Linear Layout that will contain all sub-RelativeLayouts
            workoutPreviewsLayout.removeAllViews();
            TextView nameTv, setsTv, repsTv;
            Button btnDelete;

            LayoutInflater layoutInflater;
            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutInflater = LayoutInflater.from(WorkoutInfoActivity.this);

            RelativeLayout drillView;
            

            while (result.moveToNext()) { // will be false when we passed the last result

                drillView = (RelativeLayout) layoutInflater.inflate(R.layout.layout_drill_view, null);

                nameTv = drillView.findViewById(R.id.tv_drill_name);
                nameTv.setText(result.getString(1));
                setsTv = drillView.findViewById(R.id.tv_num_sets);
                setsTv.setText(result.getString(3));
                repsTv = drillView.findViewById(R.id.tv_num_reps);
                repsTv.setText(result.getString(2));

                final String drillID = result.getString(0);
                Button deleteDrillBtn = drillView.findViewById(R.id.btn_drill_delete);
                deleteDrillBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(DatabaseHelper.getInstance(WorkoutInfoActivity.this).deleteDrill(drillID)){
                            Toast.makeText(WorkoutInfoActivity.this, R.string.toast_delete_success, Toast.LENGTH_SHORT).show();
                            loadDrillList(workoutID);
                        }
                        else{
                            Toast.makeText(WorkoutInfoActivity.this, R.string.toast_delete_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                workoutPreviewsLayout.addView(drillView);
            }
            
        }

    }

    public void openAddDrillDialog(final String workoutID) {

        final Dialog dialog = new Dialog(WorkoutInfoActivity.this);
        dialog.setContentView(R.layout.dialog_adddrill);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();

        final EditText drillNameEt = dialog.findViewById(R.id.et_drill_name);
        final EditText drillSetsEt = dialog.findViewById(R.id.et_drill_sets);
        final EditText drillRepsEt = dialog.findViewById(R.id.et_drill_reps);

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

                String name,sets,reps;
                name = drillNameEt.getText().toString();
                sets = drillSetsEt.getText().toString();
                reps = drillRepsEt.getText().toString();

                if(name.isEmpty()) { // Checking if user has entered a name

                    Toast.makeText(WorkoutInfoActivity.this, R.string.toast_data_empty_field, Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isInserted = DatabaseHelper.getInstance(WorkoutInfoActivity.this).addDrill(name, reps, sets, workoutID);

                if(isInserted) {
                    Toast.makeText(WorkoutInfoActivity.this, R.string.toast_data_add_success, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadDrillList(workoutID); // After successfully adding a new drill we want to reload the drill list
                }
                else
                    Toast.makeText(WorkoutInfoActivity.this, R.string.toast_data_add_failed, Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });

    }

}
