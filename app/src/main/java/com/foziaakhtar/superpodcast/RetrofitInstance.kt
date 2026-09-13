package com.foziaakhtar.superpodcast

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// ============================================================
// RETROFIT INSTANCE
// Creates the Retrofit connection to Apple's iTunes Search API.
// ============================================================

object RetrofitInstance {

    // Base URL for the iTunes Search API.
    private const val BASE_URL = "https://itunes.apple.com/"

    // Retrofit object configured with the iTunes base URL
    // and Gson converter.
    private val retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API interface used by MainActivity.
    val api: ITunesApi by lazy {

        retrofit.create(ITunesApi::class.java)
    }
}

