package com.uberhelp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.uberhelp.accessibility.UberAccessibilityService;
import com.uberhelp.ml.ModelRetraining;
import com.uberhelp.util.ErrorHandler;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String ACCESSIBILITY_SERVICE = "com.uberhelp.accessibility.UberAccessibilityService";
    
    private SwitchMaterial serviceToggle;
    private TextView statusText;
    private Button settingsButton;
    private Button guideButton;
    private ModelRetraining modelRetraining;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize views
        serviceToggle = findViewById(R.id.serviceToggle);
        statusText = findViewById(R.id.statusText);
        settingsButton = findViewById(R.id.settingsButton);
        guideButton = findViewById(R.id.guideButton);
        
        // Initialize model retraining
        modelRetraining = new ModelRetraining(this);
        
        // Set up toggle listener
        serviceToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (isAccessibilityServiceEnabled()) {
                    enableService();
                } else {
                    showAccessibilitySettings();
                    // Reset toggle since service isn't actually enabled yet
                    serviceToggle.setChecked(false);
                }
            } else {
                disableService();
            }
        });
        
        // Set up button listeners
        settingsButton.setOnClickListener(v -> openSettings());
        guideButton.setOnClickListener(v -> openGuide());
        
        // Register for service state changes
        registerReceiver(serviceStateReceiver, new IntentFilter("com.uberhelp.ACTION_SERVICE_STATE_CHANGED"));
        
        // Initial UI update
        updateUI();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(serviceStateReceiver);
        } catch (Exception e) {
            Log.w(TAG, "Error unregistering receiver", e);
        }
        
        if (modelRetraining != null) {
            modelRetraining.shutdown();
        }
    }
    
    /**
     * Update UI based on current service state.
     */
    private void updateUI() {
        boolean isEnabled = isAccessibilityServiceEnabled() && isServiceRunning();
        
        serviceToggle.setChecked(isEnabled);
        statusText.setText(isEnabled ? R.string.toggle_on : R.string.toggle_off);
    }
    
    /**
     * Open the settings screen.
     */
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    
    /**
     * Open the accessibility guide screen.
     */
    private void openGuide() {
        Intent intent = new Intent(this, GuideActivity.class);
        startActivity(intent);
    }
    
    /**
     * Enable the accessibility service.
     */
    private void enableService() {
        // Set service state flag
        Log.d(TAG, "Enabling service");
        
        // Start model retraining
        modelRetraining.startScheduledRetraining();
        
        // Update UI
        statusText.setText(R.string.toggle_on);
    }
    
    /**
     * Disable the accessibility service.
     */
    private void disableService() {
        Log.d(TAG, "Disabling service");
        
        // Stop model retraining
        modelRetraining.stopScheduledRetraining();
        
        // Update UI
        statusText.setText(R.string.toggle_off);
    }
    
    /**
     * Check if the accessibility service is enabled in system settings.
     */
    private boolean isAccessibilityServiceEnabled() {
        try {
            String enabledServices = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            );
            
            if (enabledServices != null) {
                return enabledServices.contains(getPackageName() + "/" + ACCESSIBILITY_SERVICE);
            }
        } catch (Exception e) {
            ErrorHandler.logError(TAG, "Error checking accessibility service state", e);
        }
        
        return false;
    }
    
    /**
     * Check if the service is actually running.
     * This is a simplified check for demo purposes.
     */
    private boolean isServiceRunning() {
        // In a real app, we would check if the service is running
        // For demo purposes, we'll assume it's running if it's enabled
        return isAccessibilityServiceEnabled();
    }
    
    /**
     * Show the system accessibility settings to enable our service.
     */
    private void showAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
    
    /**
     * Receiver for service state change broadcasts.
     */
    private final BroadcastReceiver serviceStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.uberhelp.ACTION_SERVICE_STATE_CHANGED".equals(intent.getAction())) {
                boolean isEnabled = intent.getBooleanExtra("isEnabled", false);
                serviceToggle.setChecked(isEnabled);
                statusText.setText(isEnabled ? R.string.toggle_on : R.string.toggle_off);
            }
        }
    };
} 