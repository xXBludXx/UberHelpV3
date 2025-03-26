package com.uberhelp.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.uberhelp.db.TripDatabaseHelper;
import com.uberhelp.ml.TripEvaluationModel;
import com.uberhelp.model.TripData;
import com.uberhelp.service.TripAcceptanceService;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Accessibility service that monitors the Uber Driver app for trip details
 * and triggers evaluation and acceptance when appropriate.
 */
public class UberAccessibilityService extends AccessibilityService {
    private static final String TAG = "UberAccessibilityService";
    private static final String UBER_DRIVER_PACKAGE = "com.ubercab.driver";
    
    // Regular expressions for extracting trip data from text
    private static final Pattern EARNINGS_PATTERN = Pattern.compile("\\$([0-9]+\\.[0-9]{2})");
    private static final Pattern DISTANCE_PATTERN = Pattern.compile("([0-9]+\\.[0-9]+)\\s*mi");
    private static final Pattern DURATION_PATTERN = Pattern.compile("([0-9]+)\\s*min");
    
    private TripEvaluationModel evaluationModel;
    private TripAcceptanceService acceptanceService;
    private boolean isServiceEnabled = false;
    private SharedPreferences sharedPreferences;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Accessibility Service created");
        
        evaluationModel = new TripEvaluationModel(this);
        acceptanceService = new TripAcceptanceService(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
    
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!isServiceEnabled) {
            return;
        }
        
        // Check if the event is from Uber Driver app
        if (event.getPackageName() == null || !event.getPackageName().equals(UBER_DRIVER_PACKAGE)) {
            return;
        }
        
        int eventType = event.getEventType();
        
        // We're interested in window content changes and window state changes
        if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || 
            eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                // Check if this is a trip offer screen
                if (isTripOfferScreen(rootNode)) {
                    TripData tripData = extractTripData(rootNode);
                    if (tripData != null) {
                        processTripData(tripData);
                    }
                }
                rootNode.recycle();
            }
        }
    }
    
    /**
     * Determines if the current screen is a trip offer screen.
     */
    private boolean isTripOfferScreen(AccessibilityNodeInfo rootNode) {
        // This is a simplified implementation and would need to be enhanced
        // to reliably detect trip offer screens in the Uber Driver app
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText("Accept");
        if (!nodes.isEmpty()) {
            for (AccessibilityNodeInfo node : nodes) {
                if (node.isClickable()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Extracts trip data from the accessibility node tree.
     */
    private TripData extractTripData(AccessibilityNodeInfo rootNode) {
        try {
            // This is a simplified implementation of data extraction
            // In a real app, this would be enhanced to properly parse the Uber Driver UI
            
            // Get all text on screen
            StringBuilder screenText = new StringBuilder();
            gatherNodeText(rootNode, screenText);
            String fullText = screenText.toString();
            
            // Extract data using regular expressions
            double earnings = extractEarnings(fullText);
            double distance = extractDistance(fullText);
            int durationMinutes = extractDuration(fullText);
            
            if (earnings > 0 && distance > 0 && durationMinutes > 0) {
                // Create new trip data
                TripData tripData = new TripData(
                    generateTripId(),
                    "Current Location", // Simplified: we don't extract actual locations
                    "Destination",      // Simplified: we don't extract actual locations
                    earnings,
                    distance,
                    durationMinutes,
                    System.currentTimeMillis(),
                    "pending",
                    false
                );
                
                Log.d(TAG, "Extracted trip data: " + tripData);
                return tripData;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting trip data", e);
        }
        
        return null;
    }
    
    /**
     * Recursively gathers text from node tree.
     */
    private void gatherNodeText(AccessibilityNodeInfo node, StringBuilder textBuilder) {
        if (node == null) return;
        
        CharSequence text = node.getText();
        if (text != null && text.length() > 0) {
            textBuilder.append(text).append(" ");
        }
        
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo childNode = node.getChild(i);
            if (childNode != null) {
                gatherNodeText(childNode, textBuilder);
                childNode.recycle();
            }
        }
    }
    
    /**
     * Extract earnings value from text.
     */
    private double extractEarnings(String text) {
        Matcher matcher = EARNINGS_PATTERN.matcher(text);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing earnings value", e);
            }
        }
        return 0;
    }
    
    /**
     * Extract distance value from text.
     */
    private double extractDistance(String text) {
        Matcher matcher = DISTANCE_PATTERN.matcher(text);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing distance value", e);
            }
        }
        return 0;
    }
    
    /**
     * Extract duration value from text.
     */
    private int extractDuration(String text) {
        Matcher matcher = DURATION_PATTERN.matcher(text);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing duration value", e);
            }
        }
        return 0;
    }
    
    /**
     * Generate a unique trip ID.
     */
    private String generateTripId() {
        return "trip_" + System.currentTimeMillis();
    }
    
    /**
     * Process extracted trip data - evaluate and potentially accept.
     */
    private void processTripData(TripData tripData) {
        // Check if we should store this trip in the database
        boolean storeData = sharedPreferences.getBoolean("store_historical_data", true);
        if (storeData) {
            // Insert trip data in a background thread
            new Thread(() -> {
                TripDatabaseHelper.getInstance(this).tripDao().insertTrip(tripData);
                Log.d(TAG, "Trip data saved to database");
            }).start();
        }
        
        // Evaluate if the trip is worth accepting
        boolean shouldAccept = evaluationModel.evaluateTrip(tripData);
        Log.d(TAG, "Trip evaluation result: " + (shouldAccept ? "ACCEPT" : "DECLINE"));
        
        if (shouldAccept) {
            // Trip is profitable, attempt to accept it
            acceptanceService.acceptTrip(this);
            
            // Update trip data status
            tripData.setTripStatus("accepted");
            tripData.setWasAutomaticallyAccepted(true);
            
            // Update the trip in the database
            if (storeData) {
                new Thread(() -> {
                    TripDatabaseHelper.getInstance(this).tripDao().updateTrip(tripData);
                }).start();
            }
        }
    }
    
    @Override
    public void onInterrupt() {
        Log.d(TAG, "Accessibility Service interrupted");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (evaluationModel != null) {
            evaluationModel.close();
        }
        Log.d(TAG, "Accessibility Service destroyed");
    }
    
    /**
     * Enable or disable the service.
     */
    public void setServiceEnabled(boolean enabled) {
        this.isServiceEnabled = enabled;
        Log.d(TAG, "Service " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Check if the service is currently enabled.
     */
    public boolean isServiceEnabled() {
        return isServiceEnabled;
    }
    
    /**
     * Broadcast an intent when service state changes.
     */
    private void broadcastServiceState() {
        Intent intent = new Intent("com.uberhelp.ACTION_SERVICE_STATE_CHANGED");
        intent.putExtra("isEnabled", isServiceEnabled);
        sendBroadcast(intent);
    }
} 