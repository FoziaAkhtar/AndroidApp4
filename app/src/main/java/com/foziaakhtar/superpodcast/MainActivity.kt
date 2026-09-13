
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
import kotlinx.coroutines.launch

// ============================================================
// SUPERPODCAST MAIN ACTIVITY
//
// Main responsibilities:
// 1. Search Apple's iTunes Podcast API.
// 2. Apply the minimum title-word criterion.
// 3. Display podcast search results.
// 4. Open the Podcast Details screen.
// 5. Open the My Subscriptions screen.
// 6. Subscribe through the search results.
// ============================================================

class MainActivity : AppCompatActivity() {

    private lateinit var editTextSearch: EditText
    private lateinit var editTextMinWords: EditText
    private lateinit var buttonSearch: Button
    private lateinit var buttonSubscriptions: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    private lateinit var podcastAdapter: PodcastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

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
            findViewById(R.id.editTextSearch)

        editTextMinWords =
            findViewById(R.id.editTextMinWords)

        buttonSearch =
            findViewById(R.id.buttonSearch)

        buttonSubscriptions =
            findViewById(R.id.buttonSubscriptions)

        progressBar =
            findViewById(R.id.progressBar)

        recyclerView =
            findViewById(R.id.recyclerView)

        // ========================================================
        // CREATE PODCAST ADAPTER
        // ========================================================

        podcastAdapter = PodcastAdapter(
            emptyList(),

            // When the user taps the podcast itself,
            // open the Podcast Details screen.
            onPodcastClick = { podcast ->

                openPodcastDetails(
                    podcast
                )
            },

            // When the user taps Subscribe,
            // save the podcast locally.
            onSubscribeClick = { podcast ->

                subscribeToPodcast(
                    podcast
                )
            }
        )

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
    // Converts the Podcast object to JSON and sends it to
    // PodcastDetailsActivity through an Intent.
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
    // Saves the complete Podcast object as JSON.
    // ============================================================

    private fun subscribeToPodcast(
        podcast: Podcast
    ) {

        val sharedPreferences =
            getSharedPreferences(
                "SuperPodcastSubscriptions",
                MODE_PRIVATE
            )

        val podcastId =
            podcast.trackId?.toString()
                ?: podcast.collectionName
                ?: podcast.trackName
                ?: "unknown"

        // Convert Podcast object into JSON.
        val podcastJson =
            com.google.gson.Gson().toJson(
                podcast
            )

        // Save the complete podcast information.
        sharedPreferences
            .edit()
            .putString(
                "subscription_$podcastId",
                podcastJson
            )
            .apply()

        Toast.makeText(
            this,
            "Subscribed to ${podcast.collectionName ?: "podcast"}",
            Toast.LENGTH_SHORT
        ).show()
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

            } catch (exception: Exception) {

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


