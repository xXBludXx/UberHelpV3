package com.uberhelp.util;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.uberhelp.R;

/**
 * Simple error handler for UberHelpV8 app that shows restart dialog
 * when critical errors occur.
 */
public class ErrorHandler {
    private static final String TAG = "ErrorHandler";
    
    /**
     * Show a dialog to the user advising them to restart their device.
     * 
     * @param context The context to show the dialog in
     * @param errorMessage The error message to log (not shown to the user)
     */
    public static void showRestartDialog(Context context, String errorMessage) {
        if (context == null) {
            Log.e(TAG, "Cannot show error dialog - context is null. Error: " + errorMessage);
            return;
        }
        
        Log.e(TAG, "Critical error occurred: " + errorMessage);
        
        try {
            new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(context.getString(R.string.error_restart))
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
        } catch (Exception e) {
            Log.e(TAG, "Failed to show error dialog", e);
        }
    }
    
    /**
     * Log an error without showing a dialog.
     * 
     * @param tag The tag for the log message
     * @param message The error message
     * @param exception The exception that occurred (optional)
     */
    public static void logError(String tag, String message, Throwable exception) {
        if (exception != null) {
            Log.e(tag, message, exception);
        } else {
            Log.e(tag, message);
        }
    }
} 