package com.uberhelp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents trip data captured from the Uber Driver app.
 * Used for both evaluation and local storage.
 */
@Entity(tableName = "trips")
public class TripData {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String tripId;
    private String startLocation;
    private String endLocation;
    private double estimatedEarnings;
    private double estimatedDistance;
    private int estimatedDurationMinutes;
    private long timestamp;
    private String tripStatus; // "pending", "accepted", "declined", "completed"
    private boolean wasAutomaticallyAccepted;
    
    // Empty constructor required by Room
    public TripData() {
    }
    
    // Constructor with all fields except id (which is auto-generated)
    public TripData(String tripId, String startLocation, String endLocation, 
                   double estimatedEarnings, double estimatedDistance, 
                   int estimatedDurationMinutes, long timestamp,
                   String tripStatus, boolean wasAutomaticallyAccepted) {
        this.tripId = tripId;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.estimatedEarnings = estimatedEarnings;
        this.estimatedDistance = estimatedDistance;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.timestamp = timestamp;
        this.tripStatus = tripStatus;
        this.wasAutomaticallyAccepted = wasAutomaticallyAccepted;
    }
    
    // Getters and setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getTripId() {
        return tripId;
    }
    
    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
    
    public String getStartLocation() {
        return startLocation;
    }
    
    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }
    
    public String getEndLocation() {
        return endLocation;
    }
    
    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }
    
    public double getEstimatedEarnings() {
        return estimatedEarnings;
    }
    
    public void setEstimatedEarnings(double estimatedEarnings) {
        this.estimatedEarnings = estimatedEarnings;
    }
    
    public double getEstimatedDistance() {
        return estimatedDistance;
    }
    
    public void setEstimatedDistance(double estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
    }
    
    public int getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }
    
    public void setEstimatedDurationMinutes(int estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getTripStatus() {
        return tripStatus;
    }
    
    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }
    
    public boolean isWasAutomaticallyAccepted() {
        return wasAutomaticallyAccepted;
    }
    
    public void setWasAutomaticallyAccepted(boolean wasAutomaticallyAccepted) {
        this.wasAutomaticallyAccepted = wasAutomaticallyAccepted;
    }
    
    // Calculate hourly rate
    public double calculateHourlyRate() {
        if (estimatedDurationMinutes <= 0) {
            return 0;
        }
        return estimatedEarnings / (estimatedDurationMinutes / 60.0);
    }
    
    @Override
    public String toString() {
        return "TripData{" +
                "id=" + id +
                ", tripId='" + tripId + '\'' +
                ", startLocation='" + startLocation + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", estimatedEarnings=" + estimatedEarnings +
                ", estimatedDistance=" + estimatedDistance +
                ", estimatedDurationMinutes=" + estimatedDurationMinutes +
                ", timestamp=" + timestamp +
                ", tripStatus='" + tripStatus + '\'' +
                ", wasAutomaticallyAccepted=" + wasAutomaticallyAccepted +
                '}';
    }
}
 