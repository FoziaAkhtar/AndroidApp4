package com.foziaakhtar.superpodcast

// ============================================================
// EPISODE MODEL
//
// PURPOSE:
// Represents one podcast episode read from an RSS feed.
//
// RSS INFORMATION STORED:
// 1. Episode GUID
// 2. Episode title
// 3. Episode description
// 4. Publication date
// 5. Playable media URL
// 6. Media type
// 7. Episode artwork
//
// MEDIA TYPE:
// The mediaType property allows the app to distinguish
// between AUDIO and VIDEO episodes.
//
// This is important for Assignment 8 because the app
// must support both audio and video podcast playback.
// ============================================================

data class Episode(

    // ========================================================
    // EPISODE IDENTIFIER
    //
    // The RSS GUID uniquely identifies an episode when
    // the feed provides one.
    //
    // The RSS parser can use the media URL as a fallback
    // when a GUID is not available.
    // ========================================================

    val guid: String = "",

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
    // MEDIA URL
    //
    // Despite the original property name audioUrl, this URL
    // can now contain either audio or video media.
    //
    // We keep the property name audioUrl for compatibility
    // with the existing EpisodePlayerActivity and other
    // existing project files.
    // ========================================================

    val audioUrl: String,

    // ========================================================
    // MEDIA TYPE
    //
    // Examples:
    // audio/mpeg
    // audio/mp4
    // video/mp4
    // video/x-m4v
    //
    // This can be blank when the RSS feed does not provide
    // a MIME type. In that situation the parser can inspect
    // the media URL extension.
    // ========================================================

    val mediaType: String = "",

    // ========================================================
    // EPISODE ARTWORK
    // ========================================================

    val imageUrl: String = ""
)

