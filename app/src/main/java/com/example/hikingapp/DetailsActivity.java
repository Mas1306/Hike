package com.example.hikingapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DetailsActivity extends AppCompatActivity {
    TextView nameTv, locationTv, dateTv, lengthTv, levelTv, availableTv, descriptionTv, hike_description_details;
    MaterialButton add_button, reset_button, edit_button;
    MyDatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        myDB = new MyDatabaseHelper(DetailsActivity.this);
        nameTv = findViewById(R.id.hike_name_details);
        locationTv = findViewById(R.id.location_details);
        dateTv = findViewById(R.id.date_details);
        lengthTv = findViewById(R.id.length_details);
        levelTv = findViewById(R.id.hike_level_details);
        availableTv = findViewById(R.id.available_details);
        descriptionTv = findViewById(R.id.description_details);
        hike_description_details = findViewById(R.id.hike_description_details);

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String location = intent.getStringExtra("location");
        String date = intent.getStringExtra("date");
        String length = intent.getStringExtra("length");
        String level = intent.getStringExtra("level");
        switch (level.toLowerCase()) {

            case "easy":
                levelTv.setBackground(createLevelBackground(Color.parseColor("#C8E6C9")));
                levelTv.setTextColor(Color.parseColor("#1B5E20"));
                break;
            case "moderate":
                levelTv.setBackground(createLevelBackground(Color.parseColor("#FFF3CD")));
                levelTv.setTextColor(Color.parseColor("#856404"));
                break;
            case "very hard":
                levelTv.setBackground(createLevelBackground(Color.parseColor("#F8D7DA")));
                levelTv.setTextColor(Color.parseColor("#721C24"));
                break;
            default:
                levelTv.setBackground(createLevelBackground(Color.parseColor("#E0E0E0")));
                levelTv.setTextColor(Color.BLACK);
                break;
        }
        String available = intent.getStringExtra("available");
        String description = intent.getStringExtra("description");
        if (description == null || description.trim().isEmpty() || description.equalsIgnoreCase("null")) {
            hike_description_details.setVisibility(View.GONE);
            descriptionTv.setVisibility(View.GONE);
        } else {
            hike_description_details.setVisibility(View.VISIBLE);
            descriptionTv.setVisibility(View.VISIBLE);
            descriptionTv.setText(description.trim());
        }

        nameTv.setText(name);
        locationTv.setText(location);
        dateTv.setText(date);
        lengthTv.setText(length + " km");
        levelTv.setText(level);
        availableTv.setText(available);

        if (description == null || description.trim().isEmpty()) {
            descriptionTv.setVisibility(View.GONE);
        } else {
            descriptionTv.setVisibility(View.VISIBLE);
            descriptionTv.setText(description);
        }
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        add_button = findViewById(R.id.btnAddHike);
        add_button.setOnClickListener(v -> {
            Intent intent1 = new Intent(DetailsActivity.this, AddActivity.class);
            startActivity(intent1);
        });

        reset_button = findViewById(R.id.btnReset);
        reset_button.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_reset, null);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DetailsActivity.this)
                    .setView(dialogView);
            AlertDialog dialog = builder.create();
            dialog.show();
            dialogView.findViewById(R.id.btnCancel).setOnClickListener(view -> dialog.dismiss());
            dialogView.findViewById(R.id.btnDelete).setOnClickListener(view -> {
                myDB.resetDatabase();
                Toast.makeText(this, "Database reset successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                Intent homeIntent = new Intent(DetailsActivity.this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
                finish();
            });
        });

        edit_button = findViewById(R.id.btnEdit);
        edit_button.setOnClickListener(v -> {
            Intent editIntent = new Intent(DetailsActivity.this, AddActivity.class);
            String hikeId = getIntent().getStringExtra("hike_id");
            if (hikeId == null) {
                Toast.makeText(this, "Error: Hike ID missing", Toast.LENGTH_SHORT).show();
                return;
            }
            editIntent.putExtra("hike_id", hikeId);
            editIntent.putExtra("name", nameTv.getText().toString());
            editIntent.putExtra("location", locationTv.getText().toString());
            editIntent.putExtra("date", dateTv.getText().toString());
            editIntent.putExtra("length", lengthTv.getText().toString().replace(" km",""));
            editIntent.putExtra("level", levelTv.getText().toString());
            editIntent.putExtra("available", availableTv.getText().toString());
            String descText = (descriptionTv.getVisibility() == View.VISIBLE)
                    ? descriptionTv.getText().toString()
                    : null;
            editIntent.putExtra("description", descText);
            startActivityForResult(editIntent, 101);
        });

    }
    private GradientDrawable createLevelBackground(int backgroundColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(backgroundColor);
        shape.setCornerRadius(20f);
        return shape;
    }
    private void updateLevelUI(String level) {
        levelTv.setText(level);
        switch (level.toLowerCase()) {
            case "easy":
                levelTv.setBackground(createLevelBackground(Color.parseColor("#C8E6C9")));
                levelTv.setTextColor(Color.parseColor("#1B5E20"));
                break;
            case "moderate":
                levelTv.setBackground(createLevelBackground(Color.parseColor("#FFF3CD")));
                levelTv.setTextColor(Color.parseColor("#856404"));
                break;
            case "very hard":
                levelTv.setBackground(createLevelBackground(Color.parseColor("#F8D7DA")));
                levelTv.setTextColor(Color.parseColor("#721C24"));
                break;
            default:
                levelTv.setBackground(createLevelBackground(Color.parseColor("#E0E0E0")));
                levelTv.setTextColor(Color.BLACK);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            String hikeId = getIntent().getStringExtra("hike_id");
            Cursor cursor = myDB.getHikeById(hikeId);
            if(cursor != null && cursor.moveToFirst()) {
                nameTv.setText(cursor.getString(cursor.getColumnIndexOrThrow("hike_name")));
                locationTv.setText(cursor.getString(cursor.getColumnIndexOrThrow("hike_location")));
                dateTv.setText(cursor.getString(cursor.getColumnIndexOrThrow("hike_date")));
                lengthTv.setText(cursor.getString(cursor.getColumnIndexOrThrow("hike_length")) + " km");
                String updatedLevel = cursor.getString(cursor.getColumnIndexOrThrow("hike_level"));
                updateLevelUI(updatedLevel);
                String available = cursor.getString(cursor.getColumnIndexOrThrow("hike_available"));
                if ("1".equals(available)) {
                    availableTv.setText("Parking Available");
                } else {
                    availableTv.setText("No Parking");
                }
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("hike_description"));
                descriptionTv.setText("");

                if(desc == null || desc.trim().isEmpty() || desc.equalsIgnoreCase("null")) {
                    descriptionTv.setVisibility(View.GONE);
                    hike_description_details.setVisibility(View.GONE);
                } else {
                    descriptionTv.setVisibility(View.VISIBLE);
                    hike_description_details.setVisibility(View.VISIBLE);
                    descriptionTv.setText(desc.trim());
                }
            }
        }
    }

}

