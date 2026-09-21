package com.foziaakhtar.superpodcast

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ============================================================
// ROOM DATABASE: AppDatabase
//
// PURPOSE:
// Provides the Room database used by SuperPodcast.
//
// The database currently contains:
// 1. SubscribedPodcast
//
// The database gives the application access to:
// SubscriptionDao
//
// DATABASE FILE:
// superpodcast_database
// ============================================================

@Database(
    entities = [
        SubscribedPodcast::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // ========================================================
    // DATABASE ACCESS
    //
    // Provides the DAO used to insert, delete, and query
    // subscribed podcasts.
    // ========================================================

    abstract fun subscriptionDao(): SubscriptionDao

    companion object {

        // ====================================================
        // SINGLETON INSTANCE
        //
        // @Volatile ensures that changes to INSTANCE are
        // immediately visible to all threads.
        //
        // Only one database instance should be created for
        // the entire application.
        // ====================================================

        @Volatile
        private var INSTANCE: AppDatabase? = null

        // ====================================================
        // GET DATABASE INSTANCE
        //
        // Creates the database the first time it is requested.
        //
        // synchronized prevents multiple database instances
        // from being created at the same time.
        // ====================================================

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "superpodcast_database"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}