package com.foziaakhtar.superpodcast

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.launch

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
// 6. Open the podcast.
// 7. Load latest podcast episodes from RSS.
// 8. Display episodes in a RecyclerView.
// 9. Open selected episodes in the Media3 player.
// ============================================================

class PodcastDetailsActivity : AppCompatActivity() {

    // ========================================================
    // PODCAST DETAIL VIEWS
    // ========================================================

    private lateinit var buttonBack: Button

    private lateinit var imageViewPodcastArtwork: ImageView

    private lateinit var textViewPodcastTitle: TextView

    private lateinit var textViewPodcastArtist: TextView

    private lateinit var buttonSubscribeDetails: Button

    private lateinit var buttonOpenPodcast: Button

    // ========================================================
    // EPISODE VIEWS
    // ========================================================

    private lateinit var textViewEpisodesLoading: TextView

    private lateinit var recyclerViewEpisodes: RecyclerView

    // ========================================================
    // EPISODE ADAPTER
    // ========================================================

    private lateinit var episodeAdapter: EpisodeAdapter

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_podcast_details
        )

        // ========================================================
        // CONNECT PODCAST DETAIL VIEWS
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
        // CONNECT EPISODE VIEWS
        // ========================================================

        textViewEpisodesLoading =
            findViewById(
                R.id.textViewEpisodesLoading
            )

        recyclerViewEpisodes =
            findViewById(
                R.id.recyclerViewEpisodes
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

        if (
            podcastJson.isNullOrBlank()
        ) {

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

            } catch (
                exception: Exception
            ) {

                null
            }

        // ========================================================
        // CHECK PODCAST DATA
        // ========================================================

        if (
            podcast == null
        ) {

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
            .load(
                podcast.artworkUrl100
            )
            .placeholder(
                android.R.drawable.ic_menu_gallery
            )
            .error(
                android.R.drawable.ic_menu_gallery
            )
            .into(
                imageViewPodcastArtwork
            )

        // ========================================================
        // SUBSCRIBE BUTTON
        // ========================================================

        buttonSubscribeDetails.setOnClickListener {

            subscribeToPodcast(
                podcast
            )
        }

        // ========================================================
        // OPEN PODCAST BUTTON
        // ========================================================

        buttonOpenPodcast.setOnClickListener {

            openPodcast(
                podcast
            )
        }

        // ========================================================
        // SET UP EPISODE RECYCLERVIEW
        // ========================================================

        episodeAdapter =
            EpisodeAdapter(
                emptyList()
            ) { episode ->

                playEpisode(
                    episode
                )
            }

        recyclerViewEpisodes.layoutManager =
            LinearLayoutManager(
                this
            )

        recyclerViewEpisodes.adapter =
            episodeAdapter

        // ========================================================
        // LOAD RSS EPISODES
        // ========================================================

        loadPodcastEpisodes(
            podcast
        )
    }

    // ============================================================
    // LOAD PODCAST EPISODES
    //
    // Uses the feedUrl provided by the iTunes Search API.
    //
    // RSSFeedService performs the network request on the
    // background IO dispatcher.
    // ============================================================

    private fun loadPodcastEpisodes(
        podcast: Podcast
    ) {

        val feedUrl =
            podcast.feedUrl

        // --------------------------------------------------------
        // CHECK RSS FEED URL
        // --------------------------------------------------------

        if (
            feedUrl.isNullOrBlank()
        ) {

            textViewEpisodesLoading.text =
                "Episodes are unavailable for this podcast."

            return
        }

        // --------------------------------------------------------
        // SHOW LOADING MESSAGE
        // --------------------------------------------------------

        textViewEpisodesLoading.text =
            "Loading episodes..."

        textViewEpisodesLoading.visibility =
            TextView.VISIBLE

        // --------------------------------------------------------
        // LOAD RSS FEED
        // --------------------------------------------------------

        lifecycleScope.launch {

            try {

                val episodes =
                    RSSFeedService.loadEpisodes(
                        feedUrl
                    )

                // ------------------------------------------------
                // UPDATE UI
                //
                // RSSFeedService performs network work away
                // from the main UI thread.
                //
                // lifecycleScope returns to the main thread
                // after the suspend function completes.
                // ------------------------------------------------

                if (
                    episodes.isEmpty()
                ) {

                    textViewEpisodesLoading.text =
                        "No episodes were found."

                    recyclerViewEpisodes.visibility =
                        RecyclerView.GONE

                } else {

                    textViewEpisodesLoading.visibility =
                        TextView.GONE

                    recyclerViewEpisodes.visibility =
                        RecyclerView.VISIBLE

                    episodeAdapter.updateList(
                        episodes
                    )
                }

            } catch (
                exception: Exception
            ) {

                // ------------------------------------------------
                // RSS LOADING ERROR
                // ------------------------------------------------

                textViewEpisodesLoading.text =
                    "Unable to load episodes."

                textViewEpisodesLoading.visibility =
                    TextView.VISIBLE

                recyclerViewEpisodes.visibility =
                    RecyclerView.GONE
            }
        }
    }

    // ============================================================
    // PLAY EPISODE
    //
    // Opens the Media3 Episode Player screen for the
    // selected podcast episode.
    // ============================================================

    private fun playEpisode(
        episode: Episode
    ) {

        // ========================================================
        // CHECK AUDIO URL
        //
        // An episode cannot be played without an audio URL.
        // ========================================================

        if (
            episode.audioUrl.isBlank()
        ) {

            Toast.makeText(
                this,
                "No audio is available for this episode.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // ========================================================
        // CREATE PLAYER INTENT
        // ========================================================

        val intent =
            Intent(
                this,
                EpisodePlayerActivity::class.java
            )

        // ========================================================
        // SEND EPISODE TITLE
        // ========================================================

        intent.putExtra(
            "episode_title",
            episode.title
        )

        // ========================================================
        // SEND EPISODE AUDIO URL
        //
        // EpisodePlayerActivity will give this URL to Media3.
        // ========================================================

        intent.putExtra(
            "episode_audio_url",
            episode.audioUrl
        )

        // ========================================================
        // OPEN MEDIA3 PLAYER
        // ========================================================

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

        val podcastJson =
            Gson().toJson(
                podcast
            )

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
    // OPEN PODCAST
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

        if (
            podcastUrl.isNullOrBlank()
        ) {

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

        } catch (
            exception: Exception
        ) {

            Toast.makeText(
                this,
                "Unable to open this podcast.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

