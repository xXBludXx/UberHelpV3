package com.uberhelp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.uberhelp.db.TripDatabaseHelper;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    
    private SharedPreferences sharedPreferences;
    private SwitchMaterial scheduleEnabledSwitch;
    private TimePicker startTimePicker;
    private TimePicker endTimePicker;
    private SwitchMaterial storeHistoricalDataSwitch;
    private Button clearDataButton;
    private Button saveSettingsButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        // Initialize views
        scheduleEnabledSwitch = findViewById(R.id.scheduleEnabledSwitch);
        startTimePicker = findViewById(R.id.startTimePicker);
        endTimePicker = findViewById(R.id.endTimePicker);
        storeHistoricalDataSwitch = findViewById(R.id.storeHistoricalDataSwitch);
        clearDataButton = findViewById(R.id.clearDataButton);
        saveSettingsButton = findViewById(R.id.saveSettingsButton);
        
        // Set up timepickers
        startTimePicker.setIs24HourView(true);
        endTimePicker.setIs24HourView(true);
        
        // Load saved settings
        loadSettings();
        
        // Set up listeners
        scheduleEnabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            startTimePicker.setEnabled(isChecked);
            endTimePicker.setEnabled(isChecked);
        });
        
        clearDataButton.setOnClickListener(v -> showClearDataConfirmation());
        saveSettingsButton.setOnClickListener(v -> saveSettings());
    }
    
    /**
     * Load saved settings from SharedPreferences.
     */
    private void loadSettings() {
        boolean scheduleEnabled = sharedPreferences.getBoolean("schedule_enabled", false);
        int startHour = sharedPreferences.getInt("schedule_start_hour", 8);
        int startMinute = sharedPreferences.getInt("schedule_start_minute", 0);
        int endHour = sharedPreferences.getInt("schedule_end_hour", 20);
        int endMinute = sharedPreferences.getInt("schedule_end_minute", 0);
        boolean storeData = sharedPreferences.getBoolean("store_historical_data", true);
        
        // Apply settings to UI
        scheduleEnabledSwitch.setChecked(scheduleEnabled);
        startTimePicker.setEnabled(scheduleEnabled);
        endTimePicker.setEnabled(scheduleEnabled);
        startTimePicker.setHour(startHour);
        startTimePicker.setMinute(startMinute);
        endTimePicker.setHour(endHour);
        endTimePicker.setMinute(endMinute);
        storeHistoricalDataSwitch.setChecked(storeData);
    }
    
    /**
     * Save settings to SharedPreferences.
     */
    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        // Store schedule settings
        boolean scheduleEnabled = scheduleEnabledSwitch.isChecked();
        editor.putBoolean("schedule_enabled", scheduleEnabled);
        editor.putInt("schedule_start_hour", startTimePicker.getHour());
        editor.putInt("schedule_start_minute", startTimePicker.getMinute());
        editor.putInt("schedule_end_hour", endTimePicker.getHour());
        editor.putInt("schedule_end_minute", endTimePicker.getMinute());
        
        // Store data preferences
        editor.putBoolean("store_historical_data", storeHistoricalDataSwitch.isChecked());
        
        // Apply changes
        editor.apply();
        
        // Show confirmation
        showSettingsSavedConfirmation();
    }
    
    /**
     * Show confirmation dialog for clearing data.
     */
    private void showClearDataConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("Clear Data")
            .setMessage("Are you sure you want to clear all stored trip data? This action cannot be undone.")
            .setPositiveButton("Clear", (dialog, which) -> clearAllData())
            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
            .show();
    }
    
    /**
     * Clear all stored trip data.
     */
    private void clearAllData() {
        try {
            TripDatabaseHelper.getInstance(this).clearAllData();
            showToast("All trip data has been cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing data", e);
            showToast("Error clearing data");
        }
    }
    
    /**
     * Show a confirmation message that settings were saved.
     */
    private void showSettingsSavedConfirmation() {
        showToast("Settings saved");
        finish();
    }
    
    /**
     * Show a toast message (simplified implementation using a dialog for demo purposes).
     */
    private void showToast(String message) {
        new AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
            .show();
    }
} 