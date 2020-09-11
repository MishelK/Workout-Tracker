package com.example.workouttracker.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workouttracker.Classes.DatabaseHelper;
import com.example.workouttracker.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Setting Return Button
        Button btnReturn = findViewById(R.id.btn_return_settings);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final EditText etId = findViewById(R.id.et_update_id);
        final EditText etName = findViewById(R.id.et_update_name);
        final EditText etDesc = findViewById(R.id.et_update_desc);

        //View all data section
        Button btnViewAll = findViewById(R.id.btn_view_data);
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor result = DatabaseHelper.getInstance(SettingsActivity.this).getAllWorkoutData();

                if(result.getCount() == 0) {
                    showMessage("Error", "Nothing found");
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while (result.moveToNext()) { // will be false when we passed the last result

                    buffer.append("Id : "+ result.getString(0)+ "\n");
                    buffer.append("Name : "+ result.getString(1)+ "\n");
                    buffer.append("Description : "+ result.getString(2)+ "\n\n");

                }

                showMessage("Data", buffer.toString());
            }
        });
        //End of view all data section

        //Update data section

        Button btnUpdateData = findViewById(R.id.btn_update_data);
        btnUpdateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isUpdated = DatabaseHelper.getInstance(SettingsActivity.this).updateWorkout(etId.getText().toString(), etName.getText().toString(), etDesc.getText().toString());

                if(isUpdated)
                    Toast.makeText(SettingsActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(SettingsActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();

            }
        });

        //End of update data section

        //Delete data section

        Button btnDeleteData = findViewById(R.id.btn_delete_data);
        btnDeleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDeleted = DatabaseHelper.getInstance(SettingsActivity.this).deleteWorkout(etId.getText().toString());

                if(isDeleted)
                    Toast.makeText(SettingsActivity.this, "Delete Successful", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(SettingsActivity.this, "Delete Failed", Toast.LENGTH_SHORT).show();
            }
        });

        //End of delete data section

    }

    public void showMessage(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


}
