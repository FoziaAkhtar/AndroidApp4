package com.foziaakhtar.superpodcast

// ============================================================
// PODCAST DATA MODEL
//
// Represents one podcast returned by the iTunes Search API.
// ============================================================

data class Podcast(

    // --------------------------------------------------------
    // Unique iTunes ID for the podcast.
    // --------------------------------------------------------

    val trackId: Long? = null,

    // --------------------------------------------------------
    // Podcast title.
    // --------------------------------------------------------

    val collectionName: String? = null,

    // --------------------------------------------------------
    // Name of the podcast creator/company.
    // --------------------------------------------------------

    val artistName: String? = null,

    // --------------------------------------------------------
    // URL for the podcast artwork.
    // --------------------------------------------------------

    val artworkUrl100: String? = null,

    // --------------------------------------------------------
    // Podcast RSS/feed URL.
    //
    // This will allow us to open the podcast when the user
    // selects it.
    // --------------------------------------------------------

    val feedUrl: String? = null,

    // --------------------------------------------------------
    // Track name can be returned by some iTunes results.
    // --------------------------------------------------------

    val trackName: String? = null,

    // --------------------------------------------------------
    // iTunes page for the podcast.
    //
    // This provides a backup URL if a feed URL is unavailable.
    // --------------------------------------------------------

    val collectionViewUrl: String? = null
)
