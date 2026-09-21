package com.foziaakhtar.superpodcast

// ============================================================
// EPISODE MODEL
//
// Represents one podcast episode from an RSS feed.
//
// The RSS feed normally provides information such as:
// 1. Episode title
// 2. Episode description
// 3. Episode publication date
// 4. Episode audio URL
// 5. Episode image
// ============================================================

data class Episode(

    // ========================================================
    // EPISODE TITLE
    // ========================================================

    val title: String,

    // ========================================================
    // EPISODE DESCRIPTION
    // ========================================================

    val description: String,

    // ========================================================
    // PUBLICATION DATE
    // ========================================================

    val pubDate: String,

    // ========================================================
    // AUDIO URL
    //
    // This URL will eventually be used by Media3 to
    // play the episode inside the application.
    // ========================================================

    val audioUrl: String,

    // ========================================================
    // EPISODE IMAGE
    //
    // Some RSS feeds provide an image specifically for
    // an episode. This value can be empty when unavailable.
    // ========================================================

    val imageUrl: String = ""
)