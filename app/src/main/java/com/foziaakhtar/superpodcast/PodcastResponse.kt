package com.foziaakhtar.superpodcast

// ============================================================
// PODCAST API RESPONSE
// The iTunes Search API returns an object containing a
// resultCount and a list of podcast results.
// ============================================================

data class PodcastResponse(

    // Number of results returned by iTunes.
    val resultCount: Int = 0,

    // List of podcasts returned by the search.
    val results: List<Podcast> = emptyList()
)
