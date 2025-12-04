package com.example.hikingapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity {
    MaterialButton esc_button, create_button;
    TextInputEditText name_input, location_input, date_input, length_input, description_input;
    AutoCompleteTextView level_input;
    SwitchMaterial available_input;
    TextView title;
    String hikeId = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_hike);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        name_input = findViewById(R.id.etNameHike);
        location_input = findViewById(R.id.etLocationHike);
        date_input = findViewById(R.id.etDateHike);
        length_input = findViewById(R.id.etLengthHike);
        level_input = findViewById(R.id.autoCompleteDifficulty);
        available_input = findViewById(R.id.switchAvailable);
        description_input = findViewById(R.id.etDescriptionHike);
        esc_button = findViewById(R.id.btnCloseAdd);
        create_button = findViewById(R.id.btnCreateHike);
        title = findViewById(R.id.titleHike);
        String[] levels = {"Easy", "Moderate", "Very Hard"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, levels);
        level_input.setAdapter(adapter);
        date_input.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        date_input.setText(selectedDate);
                    },
                    year, month, day
            );
            Calendar min = Calendar.getInstance();
            min.set(2000, Calendar.JANUARY, 1);
            datePickerDialog.getDatePicker().setMinDate(min.getTimeInMillis());
            datePickerDialog.show();
        });
        esc_button.setOnClickListener(v -> finish());
        create_button.setOnClickListener(v -> {
            String name = name_input.getText().toString().trim();
            String location = location_input.getText().toString().trim();
            String date = date_input.getText().toString().trim();
            String level = level_input.getText().toString().trim();
            String descriptionText = description_input.getText() == null ? null : description_input.getText().toString().trim();
            if (descriptionText != null && descriptionText.isEmpty()) {
                descriptionText = null;
            }
            if (name.isEmpty() || location.isEmpty() || date.isEmpty() || level.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }
            String lengthStr = length_input.getText().toString().trim();
            int length;
            try {
                length = Integer.parseInt(lengthStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Length must be a number.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (length < 0 || length > 100) {
                Toast.makeText(this, "Length must be between 0 and 100.", Toast.LENGTH_SHORT).show();
                return;
            }
            String available = available_input.isChecked() ? "1" : "0";
            MyDatabaseHelper myDB = new MyDatabaseHelper(AddActivity.this);
            boolean success;
            if (hikeId != null && !hikeId.trim().isEmpty()) {
                success = myDB.updateHike(hikeId, name, location, date, String.valueOf(length), level, available, descriptionText);
                if (success) {
                    Toast.makeText(this, "Hike updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                myDB.addHike(name, location, date, String.valueOf(length), level, available, descriptionText);
                Toast.makeText(this, "Hike added successfully!", Toast.LENGTH_SHORT).show();
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updated", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
        Intent intent = getIntent();
        hikeId = intent.getStringExtra("hike_id");
        boolean isUpdate = hikeId != null && !hikeId.trim().isEmpty();
        if (isUpdate) {
            name_input.setText(intent.getStringExtra("name"));
            location_input.setText(intent.getStringExtra("location"));
            date_input.setText(intent.getStringExtra("date"));
            length_input.setText(intent.getStringExtra("length"));
            String levelValue = intent.getStringExtra("level");
            if (levelValue != null) {
                level_input.setText(levelValue, false);
            }
            String availableVal = intent.getStringExtra("available");
            available_input.setChecked("1".equals(availableVal) || "true".equalsIgnoreCase(availableVal));
            String desc = intent.getStringExtra("description");
            if (desc != null && !desc.trim().isEmpty() && !"null".equalsIgnoreCase(desc)) {
                description_input.setText(desc);
            } else {
                description_input.setText("");
            }
            create_button.setText("Update Hike");
            title.setText("Update This Hike");
        } else {
            create_button.setText("Add Hike");
            title.setText("Create New Hike");
        }
    }
}
