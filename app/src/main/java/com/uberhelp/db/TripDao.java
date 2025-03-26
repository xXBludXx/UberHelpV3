package com.uberhelp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.uberhelp.model.TripData;

import java.util.List;

/**
 * Data Access Object (DAO) interface for trip data operations.
 */
@Dao
public interface TripDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTrip(TripData tripData);
    
    @Update
    void updateTrip(TripData tripData);
    
    @Delete
    void deleteTrip(TripData tripData);
    
    @Query("DELETE FROM trips")
    void deleteAllTrips();
    
    @Query("SELECT * FROM trips ORDER BY timestamp DESC")
    LiveData<List<TripData>> getAllTrips();
    
    @Query("SELECT * FROM trips WHERE id = :id")
    TripData getTripById(long id);
    
    @Query("SELECT * FROM trips WHERE tripId = :tripId")
    TripData getTripByTripId(String tripId);
    
    @Query("SELECT * FROM trips ORDER BY timestamp DESC LIMIT 50")
    List<TripData> getRecentTrips();
    
    @Query("SELECT * FROM trips WHERE tripStatus = 'accepted' AND wasAutomaticallyAccepted = 1 ORDER BY timestamp DESC")
    List<TripData> getAutoAcceptedTrips();
    
    @Query("SELECT AVG(estimatedEarnings) FROM trips WHERE tripStatus = 'completed'")
    double getAverageEarnings();
    
    @Query("SELECT * FROM trips WHERE tripStatus = 'completed' ORDER BY timestamp DESC LIMIT 20")
    List<TripData> getRecentCompletedTrips();
} 