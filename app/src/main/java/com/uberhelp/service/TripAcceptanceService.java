package com.uberhelp.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Service responsible for automatically accepting profitable trips.
 */
public class TripAcceptanceService {
    private static final String TAG = "TripAcceptanceService";
    private final Context context;
    
    public TripAcceptanceService(Context context) {
        this.context = context;
    }
    
    /**
     * Attempt to accept a trip by finding and clicking the "Accept" button.
     * 
     * @param service The accessibility service to perform the action
     * @return true if acceptance was attempted, false otherwise
     */
    public boolean acceptTrip(AccessibilityService service) {
        if (service == null) {
            Log.e(TAG, "Cannot accept trip: AccessibilityService is null");
            return false;
        }
        
        try {
            // Get root node
            AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
            if (rootNode == null) {
                Log.e(TAG, "Cannot accept trip: Root node is null");
                return false;
            }
            
            // Find "Accept" button
            List<AccessibilityNodeInfo> acceptButtons = rootNode.findAccessibilityNodeInfosByText("Accept");
            
            // Try to click the Accept button
            boolean clicked = false;
            
            for (AccessibilityNodeInfo nodeInfo : acceptButtons) {
                if (nodeInfo.isClickable()) {
                    // Found a clickable "Accept" button
                    Log.d(TAG, "Found clickable Accept button, performing click");
                    clicked = performClick(service, nodeInfo);
                    break;
                } else {
                    // If node itself is not clickable, check its parent
                    AccessibilityNodeInfo parent = nodeInfo.getParent();
                    if (parent != null && parent.isClickable()) {
                        Log.d(TAG, "Found Accept button with clickable parent, performing click");
                        clicked = performClick(service, parent);
                        parent.recycle();
                        break;
                    }
                }
            }
            
            // Recycle nodes
            for (AccessibilityNodeInfo node : acceptButtons) {
                node.recycle();
            }
            rootNode.recycle();
            
            if (!clicked) {
                // If we couldn't find a clickable button, try to perform a gesture tap
                // in the bottom half of the screen where "Accept" button is likely to be
                Log.d(TAG, "No clickable Accept button found, trying gesture tap");
                return performGestureTapInBottomHalf(service);
            }
            
            return clicked;
            
        } catch (Exception e) {
            Log.e(TAG, "Error accepting trip", e);
            return false;
        }
    }
    
    /**
     * Perform a click on the given node.
     */
    private boolean performClick(AccessibilityService service, AccessibilityNodeInfo nodeInfo) {
        boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        
        if (result) {
            Log.d(TAG, "Trip acceptance click performed successfully");
        } else {
            Log.e(TAG, "Failed to perform trip acceptance click");
        }
        
        return result;
    }
    
    /**
     * Perform a gesture tap in the bottom half of the screen.
     * This is a fallback method if we can't find the Accept button.
     */
    private boolean performGestureTapInBottomHalf(AccessibilityService service) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Gesture tap not supported on this Android version");
            return false;
        }
        
        try {
            // Get display dimensions
            Rect displayBounds = new Rect();
            service.getRootInActiveWindow().getBoundsInScreen(displayBounds);
            
            // Calculate tap position in bottom half (where Accept button likely is)
            int x = displayBounds.centerX();
            int y = displayBounds.centerY() + (displayBounds.height() / 4); // 3/4 down the screen
            
            // Create a tap gesture
            Path tapPath = new Path();
            tapPath.moveTo(x, y);
            
            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(tapPath, 0, 100));
            
            // Dispatch the gesture
            boolean dispatchResult = service.dispatchGesture(gestureBuilder.build(), new GestureResultCallback(), null);
            
            if (dispatchResult) {
                Log.d(TAG, "Gesture tap dispatched successfully at position " + x + "," + y);
            } else {
                Log.e(TAG, "Failed to dispatch gesture tap");
            }
            
            return dispatchResult;
            
        } catch (Exception e) {
            Log.e(TAG, "Error performing gesture tap", e);
            return false;
        }
    }
    
    /**
     * Callback for gesture results.
     */
    private static class GestureResultCallback extends AccessibilityService.GestureResultCallback {
        @Override
        public void onCompleted(GestureDescription gestureDescription) {
            Log.d(TAG, "Gesture completed successfully");
        }
        
        @Override
        public void onCancelled(GestureDescription gestureDescription) {
            Log.e(TAG, "Gesture cancelled");
        }
    }
} 