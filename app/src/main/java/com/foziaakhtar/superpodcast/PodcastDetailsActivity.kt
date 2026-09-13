package com.foziaakhtar.superpodcast

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson

// ============================================================
// PODCAST DETAILS ACTIVITY
//
// Displays detailed information about a selected podcast.
//
// Features:
// 1. Back to previous screen.
// 2. Display podcast artwork.
// 3. Display podcast title.
// 4. Display podcast creator.
// 5. Subscribe to the podcast.
// 6. Open or play the podcast.
// ============================================================

class PodcastDetailsActivity : AppCompatActivity() {

    private lateinit var buttonBack: Button
    private lateinit var imageViewPodcastArtwork: ImageView
    private lateinit var textViewPodcastTitle: TextView
    private lateinit var textViewPodcastArtist: TextView
    private lateinit var buttonSubscribeDetails: Button
    private lateinit var buttonOpenPodcast: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_podcast_details
        )

        // ========================================================
        // CONNECT XML VIEWS
        // ========================================================

        buttonBack =
            findViewById(
                R.id.buttonBack
            )

        imageViewPodcastArtwork =
            findViewById(
                R.id.imageViewPodcastArtwork
            )

        textViewPodcastTitle =
            findViewById(
                R.id.textViewPodcastTitle
            )

        textViewPodcastArtist =
            findViewById(
                R.id.textViewPodcastArtist
            )

        buttonSubscribeDetails =
            findViewById(
                R.id.buttonSubscribeDetails
            )

        buttonOpenPodcast =
            findViewById(
                R.id.buttonOpenPodcast
            )

        // ========================================================
        // BACK BUTTON
        //
        // Returns to whichever screen opened the details screen.
        //
        // Search -> Details -> Back -> Search
        //
        // Subscriptions -> Details -> Back -> Subscriptions
        // ========================================================

        buttonBack.setOnClickListener {

            finish()
        }

        // ========================================================
        // RECEIVE PODCAST FROM PREVIOUS SCREEN
        // ========================================================

        val podcastJson =
            intent.getStringExtra(
                "podcast_json"
            )

        if (podcastJson.isNullOrBlank()) {

            Toast.makeText(
                this,
                "Podcast information is unavailable.",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }

        // ========================================================
        // CONVERT JSON INTO PODCAST OBJECT
        // ========================================================

        val podcast =
            try {

                Gson().fromJson(
                    podcastJson,
                    Podcast::class.java
                )

            } catch (exception: Exception) {

                null
            }

        // ========================================================
        // CHECK PODCAST DATA
        // ========================================================

        if (podcast == null) {

            Toast.makeText(
                this,
                "Unable to load podcast information.",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }

        // ========================================================
        // DISPLAY PODCAST TITLE
        // ========================================================

        textViewPodcastTitle.text =
            podcast.collectionName
                ?: podcast.trackName
                        ?: "Unknown Podcast"

        // ========================================================
        // DISPLAY PODCAST CREATOR
        // ========================================================

        textViewPodcastArtist.text =
            podcast.artistName
                ?: "Unknown Creator"

        // ========================================================
        // LOAD PODCAST ARTWORK
        // ========================================================

        Glide.with(this)
            .load(podcast.artworkUrl100)
            .placeholder(
                android.R.drawable.ic_menu_gallery
            )
            .error(
                android.R.drawable.ic_menu_gallery
            )
            .into(imageViewPodcastArtwork)

        // ========================================================
        // SUBSCRIBE BUTTON
        // ========================================================

        buttonSubscribeDetails.setOnClickListener {

            subscribeToPodcast(
                podcast
            )
        }

        // ========================================================
        // OPEN / PLAY PODCAST BUTTON
        // ========================================================

        buttonOpenPodcast.setOnClickListener {

            openPodcast(
                podcast
            )
        }
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

        // ========================================================
        // CONVERT PODCAST TO JSON
        // ========================================================

        val podcastJson =
            Gson().toJson(
                podcast
            )

        // ========================================================
        // SAVE PODCAST
        // ========================================================

        sharedPreferences
            .edit()
            .putString(
                "subscription_$podcastId",
                podcastJson
            )
            .apply()

        // ========================================================
        // CONFIRM SUBSCRIPTION
        // ========================================================

        Toast.makeText(
            this,
            "Subscribed to ${podcast.collectionName ?: "podcast"}",
            Toast.LENGTH_SHORT
        ).show()
    }

    // ============================================================
    // OPEN / PLAY PODCAST
    //
    // Opens the podcast URL using an application available
    // on the device.
    // ============================================================

    private fun openPodcast(
        podcast: Podcast
    ) {

        val podcastUrl =
            podcast.feedUrl
                ?: podcast.collectionViewUrl

        // ========================================================
        // CHECK FOR PODCAST URL
        // ========================================================

        if (podcastUrl.isNullOrBlank()) {

            Toast.makeText(
                this,
                "No podcast link is available.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // ========================================================
        // OPEN PODCAST URL
        // ========================================================

        try {

            val intent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(podcastUrl)
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


