package com.foziaakhtar.superpodcast

import retrofit2.http.GET
import retrofit2.http.Query

// ============================================================
// ITUNES PODCAST API
// Defines the Retrofit request used to search Apple's
// iTunes Podcast Search API.
// ============================================================

interface ITunesApi {

    // --------------------------------------------------------
    // SEARCH PODCASTS
    //
    // Example request:
    // https://itunes.apple.com/search?term=technology&media=podcast
    //
    // The suspend keyword allows this network request to be
    // called from a Kotlin coroutine.
    // --------------------------------------------------------

    @GET("search")
    suspend fun searchPodcasts(
        @Query("term") searchTerm: String,
        @Query("media") media: String = "podcast"
    ): PodcastResponse
}