package com.uberhelp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity that guides the user through setting up the accessibility service.
 */
public class GuideActivity extends AppCompatActivity {
    private static final String TAG = "GuideActivity";
    
    private Button openSettingsButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        
        // Initialize views
        openSettingsButton = findViewById(R.id.openSettingsButton);
        
        // Set up button listener
        openSettingsButton.setOnClickListener(v -> openAccessibilitySettings());
    }
    
    /**
     * Open the system accessibility settings.
     */
    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
} 