package com.example.workouttracker.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

public class WorkoutInfoActivity extends AppCompatActivity {

    public static final String WORKOUT_ID = "workout_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workoutinfo);

        Button returnBtn = findViewById(R.id.btn_return_info);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkoutInfoActivity.this, EditWorkoutActivity.class);
                startActivity(intent);
            }
        });

        String workoutID = getIntent().getStringExtra(WORKOUT_ID);  //getting the id of the workout we are looking to show in detail.
        Cursor result = DatabaseHelper.getInstance(WorkoutInfoActivity.this).getWorkoutDataByID(workoutID);

        EditText workoutNameEt = findViewById(R.id.et_workout_name);
        EditText workoutDescEt = findViewById(R.id.et_workout_desc);

        while (result.moveToNext()) {
            workoutNameEt.setText(result.getString(1)); //Will load the workout's name and desc in order to allow editing
            workoutDescEt.setText(result.getString(2));
        }

    }

    public void loadDrillList(String workoutID) {

        Cursor result = DatabaseHelper.getInstance(WorkoutInfoActivity.this).getAllDrillsData(workoutID);

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

}
