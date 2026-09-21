package com.foziaakhtar.superpodcast

import androidx.room.Entity
import androidx.room.PrimaryKey

// ============================================================
// ROOM ENTITY: SubscribedPodcast
//
// PURPOSE:
// Represents one podcast saved in the user's subscription list.
//
// Room stores each SubscribedPodcast object as one row in the
// "subscribed_podcasts" database table.
//
// This replaces the older SharedPreferences-only subscription
// storage with a proper Room database.
// ============================================================

@Entity(tableName = "subscribed_podcasts")
data class SubscribedPodcast(

    // ========================================================
    // PRIMARY KEY
    //
    // iTunes provides a unique trackId for each podcast.
    // This prevents the same podcast from being inserted
    // multiple times.
    // ========================================================

    @PrimaryKey
    val trackId: Long,

    // ========================================================
    // PODCAST INFORMATION
    // ========================================================

    val collectionName: String,

    val artistName: String,

    val artworkUrl100: String,

    // ========================================================
    // FEED INFORMATION
    //
    // feedUrl is required so the application can later load
    // RSS episodes for a subscribed podcast.
    // ========================================================

    val feedUrl: String,

    // ========================================================
    // COLLECTION URL
    //
    // Used when opening the podcast externally if available.
    // ========================================================

    val collectionViewUrl: String
)

