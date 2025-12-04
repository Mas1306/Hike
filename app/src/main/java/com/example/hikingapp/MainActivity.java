package com.example.hikingapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MaterialButton add_button, reset_button, arrange_button, btnCloseFilter, btnFilters, btnApplyFilters, btnClearFilters;
    MyDatabaseHelper myDB;
    ArrayList<String> hike_id, hike_name, hike_location, hike_date, hike_length, hike_level, hike_available, hike_description;
    ArrayList<String> master_id, master_name, master_location, master_date, master_length, master_level, master_available, master_description;
    CustomAdapter customAdapter;
    LinearLayout emptyLayout;
    TextInputEditText searchInput;
    TextView sortText;
    ConstraintLayout filterPanel;
    View overlay;
    boolean isFilterOpen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        emptyLayout = findViewById(R.id.emptyLayout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        myDB = new MyDatabaseHelper(MainActivity.this);
        hike_id = new ArrayList<>();
        hike_name = new ArrayList<>();
        hike_location = new ArrayList<>();
        hike_date = new ArrayList<>();
        hike_length = new ArrayList<>();
        hike_level = new ArrayList<>();
        hike_available = new ArrayList<>();
        hike_description = new ArrayList<>();
        master_id = new ArrayList<>();
        master_name = new ArrayList<>();
        master_location = new ArrayList<>();
        master_date = new ArrayList<>();
        master_length = new ArrayList<>();
        master_level = new ArrayList<>();
        master_available = new ArrayList<>();
        master_description = new ArrayList<>();
        customAdapter = new CustomAdapter(
                this, hike_id, hike_name, hike_location, hike_date, hike_length, hike_level, hike_available, hike_description
        );
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        add_button = findViewById(R.id.btnAddHike);
        add_button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivity(intent);
        });
        reset_button = findViewById(R.id.btnReset);
        reset_button.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_reset, null);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this)
                    .setView(dialogView);
            AlertDialog dialog = builder.create();
            dialog.show();
            dialogView.findViewById(R.id.btnCancel).setOnClickListener(view -> dialog.dismiss());
            dialogView.findViewById(R.id.btnDelete).setOnClickListener(view -> {
                myDB.resetDatabase();
                Toast.makeText(this, "Database reset successfully", Toast.LENGTH_SHORT).show();
                loadHikeList();
                dialog.dismiss();
            });
        });
        customAdapter.setOnDeleteClickListener(hikeId -> {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle("Delete Hike?")
                    .setMessage("Are you sure you want to delete this hike?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        myDB.deleteOneHike(Integer.parseInt(hikeId));
                        loadHikeList();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        searchInput = findViewById(R.id.etSearch);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim().toLowerCase();
                if (query.isEmpty()) {
                    restoreFromMaster();
                    return;
                }
                ArrayList<String> filtered_id = new ArrayList<>();
                ArrayList<String> filtered_name = new ArrayList<>();
                ArrayList<String> filtered_location = new ArrayList<>();
                ArrayList<String> filtered_date = new ArrayList<>();
                ArrayList<String> filtered_length = new ArrayList<>();
                ArrayList<String> filtered_level = new ArrayList<>();
                ArrayList<String> filtered_available = new ArrayList<>();
                ArrayList<String> filtered_description = new ArrayList<>();
                for (int i = 0; i < master_name.size(); i++) {
                    if (master_name.get(i).toLowerCase().contains(query)) {
                        filtered_id.add(master_id.get(i));
                        filtered_name.add(master_name.get(i));
                        filtered_location.add(master_location.get(i));
                        filtered_date.add(master_date.get(i));
                        filtered_length.add(master_length.get(i));
                        filtered_level.add(master_level.get(i));
                        filtered_available.add(master_available.get(i));
                        filtered_description.add(master_description.get(i));
                    }
                }
                customAdapter.updateData(
                        filtered_id,
                        filtered_name,
                        filtered_location,
                        filtered_date,
                        filtered_length,
                        filtered_level,
                        filtered_available,
                        filtered_description
                );
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        arrange_button = findViewById(R.id.btnArrange);
        sortText = findViewById(R.id.sort);
        arrange_button.setOnClickListener(v -> {
            sortMode = (sortMode + 1) % 4;
            switch (sortMode) {
                case 0:
                    sortText.setText("Sort By Number");
                    sortById();
                    break;
                case 1:
                    sortText.setText("Sort By Name");
                    sortByName();
                    break;
                case 2:
                    sortText.setText("Sort By Length");
                    sortByLength();
                    break;
                case 3:
                    sortText.setText("Sort By Level");
                    sortByLevel();
                    break;
            }
        });
        String[] levels = {"Easy", "Moderate", "Very Hard"};
        ArrayAdapter<String> diffAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, levels);
        AutoCompleteTextView autoDiff = findViewById(R.id.autoFilterDifficulty);
        autoDiff.setAdapter(diffAdapter);
        autoDiff.setThreshold(1);
        autoDiff.setOnClickListener(v -> autoDiff.showDropDown());
        filterPanel = findViewById(R.id.filterPanel);
        btnFilters = findViewById(R.id.btnFilters);
        btnCloseFilter = findViewById(R.id.btnCloseFilter);
        btnApplyFilters = findViewById(R.id.btnApplyFilters);
        btnClearFilters = findViewById(R.id.btnClearFilters);
        btnFilters.setOnClickListener(v -> openFilterPanel());
        btnApplyFilters.setOnClickListener(v -> {
            String name = ((TextInputEditText) findViewById(R.id.etFilterName)).getText().toString().trim().toLowerCase();
            String location = ((TextInputEditText) findViewById(R.id.etFilterLocation)).getText().toString().trim().toLowerCase();
            String difficulty = ((AutoCompleteTextView) findViewById(R.id.autoFilterDifficulty)).getText().toString().trim();
            String minStr = ((TextInputEditText) findViewById(R.id.etFilterMin)).getText().toString().trim();
            String maxStr = ((TextInputEditText) findViewById(R.id.etFilterMax)).getText().toString().trim();
            int min = minStr.isEmpty() ? -1 : Integer.parseInt(minStr);
            int max = maxStr.isEmpty() ? -1 : Integer.parseInt(maxStr);

            if (name.isEmpty() && location.isEmpty() && difficulty.isEmpty() && min == -1 && max == -1) {
                restoreFromMaster();
                closeFilterPanel();
                return;
            }
            ArrayList<String> filtered_id = new ArrayList<>();
            ArrayList<String> filtered_name = new ArrayList<>();
            ArrayList<String> filtered_location = new ArrayList<>();
            ArrayList<String> filtered_date = new ArrayList<>();
            ArrayList<String> filtered_length = new ArrayList<>();
            ArrayList<String> filtered_level = new ArrayList<>();
            ArrayList<String> filtered_available = new ArrayList<>();
            ArrayList<String> filtered_description = new ArrayList<>();

            for (int i = 0; i < master_id.size(); i++) {
                boolean match = true;
                if (!name.isEmpty() && !master_name.get(i).toLowerCase().contains(name)) {
                    match = false;
                }
                if (!location.isEmpty() && !master_location.get(i).toLowerCase().contains(location)) {
                    match = false;
                }
                if (!difficulty.isEmpty() && !master_level.get(i).equalsIgnoreCase(difficulty)) {
                    match = false;
                }
                try {
                    int lengthVal = Integer.parseInt(master_length.get(i));
                    if (min != -1 && lengthVal < min) match = false;
                    if (max != -1 && lengthVal > max) match = false;
                } catch (NumberFormatException e) {
                    match = false;
                }
                if (match) {
                    filtered_id.add(master_id.get(i));
                    filtered_name.add(master_name.get(i));
                    filtered_location.add(master_location.get(i));
                    filtered_date.add(master_date.get(i));
                    filtered_length.add(master_length.get(i));
                    filtered_level.add(master_level.get(i));
                    filtered_available.add(master_available.get(i));
                    filtered_description.add(master_description.get(i));
                }
            }
            customAdapter.updateData(
                    filtered_id,
                    filtered_name,
                    filtered_location,
                    filtered_date,
                    filtered_length,
                    filtered_level,
                    filtered_available,
                    filtered_description
            );
            closeFilterPanel();
        });
        overlay = findViewById(R.id.overlay);
        btnCloseFilter.setOnClickListener(v -> closeFilterPanel());
        btnClearFilters.setOnClickListener(v -> {
            ((TextInputEditText) findViewById(R.id.etFilterName)).setText("");
            ((TextInputEditText) findViewById(R.id.etFilterLocation)).setText("");
            ((TextInputEditText) findViewById(R.id.etFilterMin)).setText("");
            ((TextInputEditText) findViewById(R.id.etFilterMax)).setText("");
            ((AutoCompleteTextView) findViewById(R.id.autoFilterDifficulty)).setText("");
            closeFilterPanel();
            restoreFromMaster();
        });
        loadHikeList();
    }
    private void loadHikeList() {
        Cursor cursor = myDB.readAllData();
        master_id.clear();
        master_name.clear();
        master_location.clear();
        master_date.clear();
        master_length.clear();
        master_level.clear();
        master_available.clear();
        master_description.clear();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                master_id.add(cursor.getString(0));
                master_name.add(cursor.getString(1));
                master_location.add(cursor.getString(2));
                master_date.add(cursor.getString(3));
                master_length.add(cursor.getString(4));
                master_level.add(cursor.getString(5));
                master_available.add(cursor.getString(6));
                master_description.add(cursor.getString(7));
            }
        }
        if (cursor != null) cursor.close();
        hike_id.clear();
        hike_id.addAll(master_id);
        hike_name.clear();
        hike_name.addAll(master_name);
        hike_location.clear();
        hike_location.addAll(master_location);
        hike_date.clear();
        hike_date.addAll(master_date);
        hike_length.clear();
        hike_length.addAll(master_length);
        hike_level.clear();
        hike_level.addAll(master_level);
        hike_available.clear();
        hike_available.addAll(master_available);
        hike_description.clear();
        hike_description.addAll(master_description);
        customAdapter.notifyDataSetChanged();
        if (hike_id.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
    }
    private void restoreFromMaster() {
        customAdapter.updateData(
                new ArrayList<>(master_id),
                new ArrayList<>(master_name),
                new ArrayList<>(master_location),
                new ArrayList<>(master_date),
                new ArrayList<>(master_length),
                new ArrayList<>(master_level),
                new ArrayList<>(master_available),
                new ArrayList<>(master_description)
        );
    }
    private int sortMode = 0;
    private void sortById() {
        ArrayList<Integer> order = new ArrayList<>();
        for (String id : hike_id) {
            order.add(Integer.parseInt(id));
        }
        sortAndReOrder(order);
    }
    private void sortByName() {
        ArrayList<String> order = new ArrayList<>(hike_name);
        sortAndReOrder(order);
    }
    private void sortByLevel() {
        ArrayList<String> order = new ArrayList<>(hike_level);
        sortAndReOrder(order);
    }
    private void sortByLength() {
        ArrayList<Double> order = new ArrayList<>();
        for (String len : hike_length) {
            order.add(Double.parseDouble(len));
        }
        sortAndReOrder(order);
    }
    private <T extends Comparable<T>> void sortAndReOrder(ArrayList<T> baseList) {
        ArrayList<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < baseList.size(); i++) indexList.add(i);
        Collections.sort(indexList, (a, b) -> baseList.get(a).compareTo(baseList.get(b)));
        ArrayList<String> new_id = new ArrayList<>();
        ArrayList<String> new_name = new ArrayList<>();
        ArrayList<String> new_location = new ArrayList<>();
        ArrayList<String> new_date = new ArrayList<>();
        ArrayList<String> new_length = new ArrayList<>();
        ArrayList<String> new_level = new ArrayList<>();
        ArrayList<String> new_available = new ArrayList<>();
        ArrayList<String> new_description = new ArrayList<>();
        for (int i : indexList) {
            new_id.add(hike_id.get(i));
            new_name.add(hike_name.get(i));
            new_location.add(hike_location.get(i));
            new_date.add(hike_date.get(i));
            new_length.add(hike_length.get(i));
            new_level.add(hike_level.get(i));
            new_available.add(hike_available.get(i));
            new_description.add(hike_description.get(i));
        }
        hike_id.clear(); hike_id.addAll(new_id);
        hike_name.clear(); hike_name.addAll(new_name);
        hike_location.clear(); hike_location.addAll(new_location);
        hike_date.clear(); hike_date.addAll(new_date);
        hike_length.clear(); hike_length.addAll(new_length);
        hike_level.clear(); hike_level.addAll(new_level);
        hike_available.clear(); hike_available.addAll(new_available);
        hike_description.clear(); hike_description.addAll(new_description);
        customAdapter.updateData(
                new ArrayList<>(hike_id),
                new ArrayList<>(hike_name),
                new ArrayList<>(hike_location),
                new ArrayList<>(hike_date),
                new ArrayList<>(hike_length),
                new ArrayList<>(hike_level),
                new ArrayList<>(hike_available),
                new ArrayList<>(hike_description)
        );
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadHikeList();
    }
    private void openFilterPanel() {
        overlay.setVisibility(View.VISIBLE);
        filterPanel.animate()
                .translationX(0)
                .setDuration(250)
                .start();
        isFilterOpen = true;
    }
    private void closeFilterPanel() {
        filterPanel.animate()
                .translationX(filterPanel.getWidth())
                .setDuration(250)
                .withEndAction(() -> overlay.setVisibility(View.GONE))
                .start();
        isFilterOpen = false;
    }
}
