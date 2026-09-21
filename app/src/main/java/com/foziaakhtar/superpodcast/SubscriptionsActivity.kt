package com.foziaakhtar.superpodcast

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

// ============================================================
// SUBSCRIPTIONS ACTIVITY
//
// Displays podcasts that the user has subscribed to.
//
// Features:
// 1. Displays saved podcast subscriptions.
// 2. Displays podcast artwork, title, and creator.
// 3. Opens Podcast Details when a podcast is selected.
// 4. Allows the user to remove a subscription.
// 5. Provides a Back to Search button.
// 6. Handles system-bar spacing.
// ============================================================

class SubscriptionsActivity : AppCompatActivity() {

    private lateinit var recyclerViewSubscriptions: RecyclerView
    private lateinit var textViewEmptyMessage: TextView
    private lateinit var buttonBackToSearch: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(
            R.layout.activity_subscriptions
        )

        // ========================================================
        // CONNECT XML VIEWS
        // ========================================================

        recyclerViewSubscriptions =
            findViewById(
                R.id.recyclerViewSubscriptions
            )

        textViewEmptyMessage =
            findViewById(
                R.id.textViewEmptyMessage
            )

        buttonBackToSearch =
            findViewById(
                R.id.buttonBackToSearch
            )

        // ========================================================
        // HANDLE SYSTEM BAR INSETS
        //
        // Apply spacing to the whole subscriptions screen so
        // the title does not sit underneath the status bar.
        // ========================================================

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.subscriptionsRoot)
        ) { view, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            view.setPadding(
                systemBars.left + 16,
                systemBars.top + 16,
                systemBars.right + 16,
                systemBars.bottom + 16
            )

            insets
        }

        // ========================================================
        // BACK TO SEARCH BUTTON
        //
        // Returns the user to MainActivity.
        // ========================================================

        buttonBackToSearch.setOnClickListener {

            val intent =
                Intent(
                    this,
                    MainActivity::class.java
                )

            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
            )

            startActivity(
                intent
            )

            finish()
        }

        // ========================================================
        // SET UP RECYCLER VIEW
        // ========================================================

        recyclerViewSubscriptions.layoutManager =
            LinearLayoutManager(this)

        // ========================================================
        // LOAD SAVED PODCASTS
        // ========================================================

        loadSubscriptions()
    }

    // ============================================================
    // LOAD SUBSCRIPTIONS
    //
    // Reads saved podcast JSON objects from SharedPreferences.
    // ============================================================

    private fun loadSubscriptions() {

        val sharedPreferences =
            getSharedPreferences(
                "SuperPodcastSubscriptions",
                MODE_PRIVATE
            )

        val savedSubscriptions =
            sharedPreferences.all

        // ========================================================
        // CHECK WHETHER ANY DATA EXISTS
        // ========================================================

        if (savedSubscriptions.isEmpty()) {

            showEmptyMessage()

            return
        }

        // ========================================================
        // CONVERT SAVED JSON INTO PODCAST OBJECTS
        // ========================================================

        val gson =
            Gson()

        val subscribedPodcasts =
            mutableListOf<Podcast>()

        for ((key, value) in savedSubscriptions) {

            if (
                key.startsWith("subscription_") &&
                value is String
            ) {

                try {

                    val podcast =
                        gson.fromJson(
                            value,
                            Podcast::class.java
                        )

                    if (podcast != null) {

                        subscribedPodcasts.add(
                            podcast
                        )
                    }

                } catch (exception: Exception) {

                    // Ignore invalid saved entries.
                }
            }
        }

        // ========================================================
        // CHECK FOR VALID PODCASTS
        // ========================================================

        if (subscribedPodcasts.isEmpty()) {

            showEmptyMessage()

            return
        }

        // ========================================================
        // DISPLAY SUBSCRIPTION COUNT
        // ========================================================

        textViewEmptyMessage.text =
            "${subscribedPodcasts.size} podcast subscription(s)"

        textViewEmptyMessage.visibility =
            TextView.VISIBLE

        // ========================================================
        // CREATE SUBSCRIPTION ADAPTER
        // ========================================================

        val subscriptionAdapter =
            PodcastAdapter(
                subscribedPodcasts,

                // ------------------------------------------------
                // PODCAST CLICK
                //
                // Opens the Podcast Details screen.
                // ------------------------------------------------

                onPodcastClick = { podcast ->

                    openPodcastDetails(
                        podcast
                    )
                },

                // ------------------------------------------------
                // SUBSCRIBE BUTTON
                //
                // This button is hidden in subscription mode.
                // ------------------------------------------------

                onSubscribeClick = { podcast ->

                    Toast.makeText(
                        this,
                        "Already subscribed to this podcast.",
                        Toast.LENGTH_SHORT
                    ).show()
                },

                // ------------------------------------------------
                // REMOVE SUBSCRIPTION
                //
                // Deletes the selected podcast from
                // SharedPreferences.
                // ------------------------------------------------

                onRemoveSubscriptionClick = { podcast ->

                    removeSubscription(
                        podcast
                    )
                }
            )

        recyclerViewSubscriptions.adapter =
            subscriptionAdapter
    }

    // ============================================================
    // REMOVE SUBSCRIPTION
    //
    // Removes the selected podcast from SharedPreferences.
    // ============================================================

    private fun removeSubscription(
        podcast: Podcast
    ) {

        val sharedPreferences =
            getSharedPreferences(
                "SuperPodcastSubscriptions",
                MODE_PRIVATE
            )

        // ========================================================
        // FIND THE SAME ID USED WHEN THE PODCAST WAS SAVED
        // ========================================================

        val podcastId =
            podcast.trackId?.toString()
                ?: podcast.collectionName
                ?: podcast.trackName
                ?: "unknown"

        val subscriptionKey =
            "subscription_$podcastId"

        // ========================================================
        // REMOVE THE PODCAST
        // ========================================================

        sharedPreferences
            .edit()
            .remove(
                subscriptionKey
            )
            .apply()

        // ========================================================
        // SHOW CONFIRMATION
        // ========================================================

        Toast.makeText(
            this,
            "Subscription removed.",
            Toast.LENGTH_SHORT
        ).show()

        // ========================================================
        // REFRESH THE SUBSCRIPTIONS SCREEN
        // ========================================================

        loadSubscriptions()
    }

    // ============================================================
    // SHOW EMPTY MESSAGE
    //
    // Displays the message when there are no subscriptions.
    // ============================================================

    private fun showEmptyMessage() {

        textViewEmptyMessage.text =
            "You have not subscribed to any podcasts yet."

        textViewEmptyMessage.visibility =
            TextView.VISIBLE

        recyclerViewSubscriptions.adapter =
            null
    }

    // ============================================================
    // OPEN PODCAST DETAILS
    //
    // Sends the selected Podcast object to
    // PodcastDetailsActivity as JSON.
    // ============================================================

    private fun openPodcastDetails(
        podcast: Podcast
    ) {

        val podcastJson =
            Gson().toJson(
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
    // OPEN PODCAST
    //
    // Opens the podcast link using an application available
    // on the device.
    // ============================================================

    private fun openPodcast(
        podcast: Podcast
    ) {

        val podcastUrl =
            podcast.feedUrl
                ?: podcast.collectionViewUrl

        if (podcastUrl.isNullOrBlank()) {

            Toast.makeText(
                this,
                "No podcast link is available.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        try {

            val intent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        podcastUrl
                    )
                )

            startActivity(
                intent
            )

        } catch (exception: Exception) {

            Toast.makeText(
                this,
                "Unable to open this podcast.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
