package com.foziaakhtar.superpodcast

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ============================================================
// SUPERPODCAST MAIN ACTIVITY
//
// PURPOSE:
// Main screen for searching and browsing podcasts.
//
// RESPONSIBILITIES:
// 1. Search Apple's iTunes Podcast API.
// 2. Apply the minimum title-word criterion.
// 3. Display podcast search results.
// 4. Open the Podcast Details screen.
// 5. Open the My Subscriptions screen.
// 6. Subscribe to podcasts using the Room database.
//
// IMPORTANT:
// Room is now the single source of truth for subscriptions.
//
// The old SharedPreferences subscription system has been
// removed so that Search, Podcast Details, and My Subscriptions
// all use the same database.
// ============================================================

class MainActivity : AppCompatActivity() {

    // ========================================================
    // UI REFERENCES
    // ========================================================

    private lateinit var editTextSearch: EditText
    private lateinit var editTextMinWords: EditText
    private lateinit var buttonSearch: Button
    private lateinit var buttonSubscriptions: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    // ========================================================
    // PODCAST ADAPTER
    // ========================================================

    private lateinit var podcastAdapter: PodcastAdapter

    // ========================================================
    // ROOM DATABASE
    // ========================================================

    private lateinit var database: AppDatabase

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        // ====================================================
        // ENABLE EDGE-TO-EDGE
        // ====================================================

        enableEdgeToEdge()

        // ====================================================
        // LOAD MAIN SCREEN
        // ====================================================

        setContentView(
            R.layout.activity_main
        )

        // ========================================================
        // HANDLE SYSTEM BAR INSETS
        // ========================================================

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // ========================================================
        // CONNECT XML VIEWS
        // ========================================================

        editTextSearch =
            findViewById(
                R.id.editTextSearch
            )

        editTextMinWords =
            findViewById(
                R.id.editTextMinWords
            )

        buttonSearch =
            findViewById(
                R.id.buttonSearch
            )

        buttonSubscriptions =
            findViewById(
                R.id.buttonSubscriptions
            )

        progressBar =
            findViewById(
                R.id.progressBar
            )

        recyclerView =
            findViewById(
                R.id.recyclerView
            )

        // ========================================================
        // INITIALIZE ROOM DATABASE
        //
        // This is now used by the search-result Subscribe button.
        // ========================================================

        database =
            AppDatabase.getDatabase(
                applicationContext
            )

        // ========================================================
        // CREATE PODCAST ADAPTER
        // ========================================================

        podcastAdapter =
            PodcastAdapter(
                emptyList(),

                // ------------------------------------------------
                // PODCAST CARD CLICK
                // ------------------------------------------------
                onPodcastClick = { podcast ->

                    openPodcastDetails(
                        podcast
                    )
                },

                // ------------------------------------------------
                // SUBSCRIBE BUTTON
                //
                // Uses Room instead of SharedPreferences.
                // ------------------------------------------------
                onSubscribeClick = { podcast ->

                    subscribeToPodcast(
                        podcast
                    )
                }
            )

        // ========================================================
        // SET UP RECYCLERVIEW
        // ========================================================

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter =
            podcastAdapter

        // ========================================================
        // SEARCH BUTTON
        // ========================================================

        buttonSearch.setOnClickListener {

            searchPodcasts()
        }

        // ========================================================
        // MY SUBSCRIPTIONS BUTTON
        //
        // Opens the Room-based subscriptions screen.
        // ========================================================

        buttonSubscriptions.setOnClickListener {

            val intent =
                Intent(
                    this,
                    SubscriptionsActivity::class.java
                )

            startActivity(
                intent
            )
        }
    }

    // ============================================================
    // OPEN PODCAST DETAILS
    //
    // Converts the Podcast object into JSON and sends it to
    // PodcastDetailsActivity.
    // ============================================================

    private fun openPodcastDetails(
        podcast: Podcast
    ) {

        val podcastJson =
            com.google.gson.Gson().toJson(
                podcast
            )

        val intent =
            Intent(
                this,
                PodcastDetailsActivity::class.java
            )

        intent.putExtra(
            "podcast_json",
            podcastJson
        )

        startActivity(
            intent
        )
    }

    // ============================================================
    // SUBSCRIBE TO PODCAST
    //
    // IMPORTANT:
    // This function now saves subscriptions directly into Room.
    //
    // This keeps the subscription system consistent:
    //
    // Search
    //    ↓
    // Room
    //    ↓
    // My Subscriptions
    //
    // Podcast Details also uses the same Room database.
    // ============================================================

    private fun subscribeToPodcast(
        podcast: Podcast
    ) {

        // ========================================================
        // REQUIRED PODCAST ID
        // ========================================================

        val trackId =
            podcast.trackId

        if (trackId == null) {

            Toast.makeText(
                this,
                "Podcast ID is unavailable.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // ========================================================
        // REQUIRED RSS FEED URL
        //
        // Assignment 8 needs the RSS URL for future episode
        // update checks with WorkManager.
        // ========================================================

        val feedUrl =
            podcast.feedUrl

        if (feedUrl.isNullOrBlank()) {

            Toast.makeText(
                this,
                "This podcast does not have an RSS feed.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // ========================================================
        // SAVE TO ROOM
        //
        // Database work runs on the IO dispatcher so the main
        // Android UI thread is not blocked.
        // ========================================================

        lifecycleScope.launch {

            try {

                withContext(
                    Dispatchers.IO
                ) {

                    val subscribedPodcast =
                        SubscribedPodcast(
                            trackId = trackId,
                            collectionName =
                                podcast.collectionName
                                    ?: podcast.trackName
                                    ?: "Unknown Podcast",
                            artistName =
                                podcast.artistName
                                    ?: "Unknown Creator",
                            artworkUrl100 =
                                podcast.artworkUrl100
                                    ?: "",
                            feedUrl =
                                feedUrl,
                            collectionViewUrl =
                                podcast.collectionViewUrl
                                    ?: ""
                        )

                    database
                        .subscriptionDao()
                        .insert(
                            subscribedPodcast
                        )
                }

                // =================================================
                // CONFIRM SUCCESS
                // =================================================

                Toast.makeText(
                    this@MainActivity,
                    "Subscribed to ${
                        podcast.collectionName
                            ?: "podcast"
                    }",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (
                exception: Exception
            ) {

                // =================================================
                // HANDLE DATABASE ERROR
                // =================================================

                Toast.makeText(
                    this@MainActivity,
                    "Unable to save subscription.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ============================================================
    // SEARCH PODCASTS
    //
    // Calls the iTunes Search API and applies the advanced
    // minimum-title-word criterion.
    // ============================================================

    private fun searchPodcasts() {

        val searchTerm =
            editTextSearch.text
                .toString()
                .trim()

        // ========================================================
        // VALIDATE SEARCH TEXT
        // ========================================================

        if (searchTerm.isEmpty()) {

            Toast.makeText(
                this,
                "Please enter a podcast topic to search.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // ========================================================
        // READ MINIMUM WORD FILTER
        // ========================================================

        val minimumWords =
            editTextMinWords.text
                .toString()
                .trim()
                .toIntOrNull()
                ?: 0

        // ========================================================
        // SHOW LOADING INDICATOR
        // ========================================================

        progressBar.visibility =
            View.VISIBLE

        buttonSearch.isEnabled =
            false

        // ========================================================
        // CALL ITUNES API
        // ========================================================

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitInstance.api.searchPodcasts(
                        searchTerm
                    )

                // ====================================================
                // APPLY MINIMUM TITLE-WORD FILTER
                // ====================================================

                val filteredResults =
                    response.results.filter { podcast ->

                        val title =
                            podcast.collectionName
                                ?: podcast.trackName
                                ?: ""

                        val wordCount =
                            title
                                .trim()
                                .split(
                                    Regex("\\s+")
                                )
                                .filter { word ->
                                    word.isNotBlank()
                                }
                                .size

                        wordCount >= minimumWords
                    }

                // ====================================================
                // DISPLAY RESULTS
                // ====================================================

                podcastAdapter.updateList(
                    filteredResults
                )

                // ====================================================
                // SHOW RESULT MESSAGE
                // ====================================================

                if (filteredResults.isEmpty()) {

                    Toast.makeText(
                        this@MainActivity,
                        "No podcasts matched your search and filter.",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this@MainActivity,
                        "${filteredResults.size} podcast(s) found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (
                exception: Exception
            ) {

                // ====================================================
                // HANDLE NETWORK ERROR
                // ====================================================

                Toast.makeText(
                    this@MainActivity,
                    "Unable to load podcasts. Please check your internet connection.",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                // ====================================================
                // HIDE LOADING INDICATOR
                // ====================================================

                progressBar.visibility =
                    View.GONE

                buttonSearch.isEnabled =
                    true
            }
        }
    }
}


