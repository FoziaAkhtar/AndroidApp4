package com.foziaakhtar.superpodcast

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// ============================================================
// ROOM DAO: SubscriptionDao
//
// PURPOSE:
// Provides database operations for subscribed podcasts.
//
// The DAO (Data Access Object) is the layer that communicates
// directly with the Room database.
//
// It allows the application to:
// 1. Add a subscription.
// 2. Remove a subscription.
// 3. Observe all subscriptions.
// 4. Find a podcast by its iTunes trackId.
// 5. Check whether a podcast is already subscribed.
// ============================================================

@Dao
interface SubscriptionDao {

    // ========================================================
    // INSERT SUBSCRIPTION
    //
    // REPLACE prevents duplicate entries when the same
    // podcast trackId is inserted again.
    // ========================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(podcast: SubscribedPodcast)

    // ========================================================
    // DELETE SUBSCRIPTION
    // ========================================================

    @Delete
    suspend fun delete(podcast: SubscribedPodcast)

    // ========================================================
    // GET ALL SUBSCRIPTIONS
    //
    // Flow automatically sends updated data to the UI whenever
    // the subscribed_podcasts table changes.
    //
    // COLLATE NOCASE makes alphabetical sorting
    // case-insensitive.
    // ========================================================

    @Query(
        "SELECT * FROM subscribed_podcasts " +
                "ORDER BY collectionName COLLATE NOCASE"
    )
    fun getAll(): Flow<List<SubscribedPodcast>>

    // ========================================================
    // FIND SUBSCRIPTION BY TRACK ID
    //
    // LIMIT 1 ensures that at most one matching podcast
    // is returned.
    // ========================================================

    @Query(
        "SELECT * FROM subscribed_podcasts " +
                "WHERE trackId = :trackId " +
                "LIMIT 1"
    )
    suspend fun getByTrackId(trackId: Long): SubscribedPodcast?

    // ========================================================
    // CHECK SUBSCRIPTION STATUS
    //
    // Returns true when the podcast already exists in the
    // subscribed_podcasts table.
    // ========================================================

    @Query(
        "SELECT EXISTS(" +
                "SELECT 1 FROM subscribed_podcasts " +
                "WHERE trackId = :trackId" +
                ")"
    )
    suspend fun isSubscribed(trackId: Long): Boolean
}