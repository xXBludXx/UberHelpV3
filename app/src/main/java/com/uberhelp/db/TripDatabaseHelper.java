package com.uberhelp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.uberhelp.model.TripData;

/**
 * Room database implementation for storing trip data locally.
 */
@Database(entities = {TripData.class}, version = 1, exportSchema = false)
public abstract class TripDatabaseHelper extends RoomDatabase {
    
    private static final String DATABASE_NAME = "trip_database";
    private static TripDatabaseHelper instance;
    
    public abstract TripDao tripDao();
    
    /**
     * Get singleton instance of the database.
     */
    public static synchronized TripDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    TripDatabaseHelper.class,
                    DATABASE_NAME
            ).fallbackToDestructiveMigration() // Wipe and rebuild when version changes
             .build();
        }
        return instance;
    }
    
    /**
     * Clear all data in the database.
     */
    public void clearAllData() {
        if (instance != null) {
            new Thread(() -> tripDao().deleteAllTrips()).start();
        }
    }
} 