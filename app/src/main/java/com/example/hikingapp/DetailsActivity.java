package com.example.hikingapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    TextView nameTv, locationTv, dateTv, lengthTv, levelTv, availableTv, descriptionTv, hike_description_details;
    MaterialButton add_button, reset_button, edit_button, add_observation_button;
    MyDatabaseHelper myDB;
    RecyclerView recyclerViewObs;
    View emptyLayout;
    ArrayList<String> obs_id, obs_name, obs_time, obs_comment;
    ObservationAdapter obsAdapter;
    int hikeId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String hikeIdStr = getIntent().getStringExtra("hike_id");
        if (hikeIdStr != null) {
            try {
                hikeId = Integer.parseInt(hikeIdStr);
            } catch (NumberFormatException e) {
                hikeId = -1;
            }
        }
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
        recyclerViewObs = findViewById(R.id.recyclerView2);
        emptyLayout = findViewById(R.id.emptyLayout);
        add_button = findViewById(R.id.btnAddHike);
        reset_button = findViewById(R.id.btnReset);
        edit_button = findViewById(R.id.btnEdit);
        loadHikeInfoFromIntent();
        obs_id = new ArrayList<>();
        obs_name = new ArrayList<>();
        obs_time = new ArrayList<>();
        obs_comment = new ArrayList<>();
        obsAdapter = new ObservationAdapter(this, obs_id, obs_name, obs_time, obs_comment);
        recyclerViewObs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewObs.setAdapter(obsAdapter);
        obsAdapter.setOnDeleteClickListener(obsId -> {
            boolean ok = myDB.deleteObservation(Integer.parseInt(obsId));
            if (ok) {
                Toast.makeText(DetailsActivity.this, "Observation deleted", Toast.LENGTH_SHORT).show();
                loadObservations();
            } else {
                Toast.makeText(DetailsActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });
        obsAdapter.setOnEditClickListener((obsId, obs_name, obs_date, obs_comment) -> {
            showEditObservationDialog(obsId, obs_name, obs_comment);
        });
        loadObservations();
        add_observation_button = findViewById(R.id.btnAddObservations);
        add_observation_button.setOnClickListener(v -> showAddObservationDialog());
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
    private void loadHikeInfoFromIntent() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String location = intent.getStringExtra("location");
        String date = intent.getStringExtra("date");
        String length = intent.getStringExtra("length");
        String level = intent.getStringExtra("level");
        String available = intent.getStringExtra("available");
        String description = intent.getStringExtra("description");

        nameTv.setText(name != null ? name : "");
        locationTv.setText(location != null ? location : "");
        dateTv.setText(date != null ? date : "");
        if (length != null && !length.isEmpty()) lengthTv.setText(length + " km");
        else lengthTv.setText("");

        if (level != null) {
            levelTv.setText(level);
            updateLevelUI(level);
        } else levelTv.setText("");

        availableTv.setText(available != null ? available : "");

        if (description == null || description.trim().isEmpty() || "null".equalsIgnoreCase(description)) {
            hike_description_details.setVisibility(View.GONE);
            descriptionTv.setVisibility(View.GONE);
        } else {
            hike_description_details.setVisibility(View.VISIBLE);
            descriptionTv.setVisibility(View.VISIBLE);
            descriptionTv.setText(description.trim());
        }
    }
    private void loadObservations() {
        obs_id.clear();
        obs_name.clear();
        obs_time.clear();
        obs_comment.clear();

        if (hikeId == -1) {
            recyclerViewObs.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            return;
        }
        Cursor cursor = myDB.getObservationsByHike(hikeId);
        if (cursor == null || cursor.getCount() == 0) {
            if (cursor != null) cursor.close();
            recyclerViewObs.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            return;
        }
        while (cursor.moveToNext()) {
            obs_id.add(cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_OBS_ID)));
            obs_name.add(cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_OBS_NAME)));
            obs_time.add(cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_OBS_DATE)));
            obs_comment.add(cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_OBS_COMMENT)));
        }
        cursor.close();
        emptyLayout.setVisibility(View.GONE);
        recyclerViewObs.setVisibility(View.VISIBLE);
        obsAdapter.notifyDataSetChanged();
    }
    private void showAddObservationDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_observation, null);
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialog.show();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextInputEditText etName = dialogView.findViewById(R.id.etObservationName);
        TextInputEditText etComment = dialogView.findViewById(R.id.etObservationComment);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSaveObservation);
        MaterialButton btnClose = dialogView.findViewById(R.id.btnCloseObservationDialog);
        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String comment = etComment.getText().toString().trim();
            if (name.isEmpty()) {
                etName.setError("Please enter observation name");
                return;
            }
            String dateTime = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
            boolean inserted = myDB.addObservation(hikeId, name, dateTime, comment);
            if (inserted) {
                Toast.makeText(DetailsActivity.this, "Observation added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadObservations();
            } else {
                Toast.makeText(DetailsActivity.this, "Failed to add observation", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showEditObservationDialog(String obsId, String currentName, String currentComment) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_observation, null);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        dialog.show();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextInputEditText etName = dialogView.findViewById(R.id.etObservationName);
        TextInputEditText etComment = dialogView.findViewById(R.id.etObservationComment);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSaveObservation);
        MaterialButton btnClose = dialogView.findViewById(R.id.btnCloseObservationDialog);
        etName.setText(currentName);
        etComment.setText(currentComment != null ? currentComment : "");
        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String comment = etComment.getText().toString().trim();
            if (name.isEmpty()) {
                etName.setError("Please enter observation name");
                return;
            }
            String dateTime = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
            boolean ok = myDB.updateObservation(Integer.parseInt(obsId), name, dateTime, comment);
            if (ok) {
                Toast.makeText(DetailsActivity.this, "Observation updated", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadObservations();
            } else {
                Toast.makeText(DetailsActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

