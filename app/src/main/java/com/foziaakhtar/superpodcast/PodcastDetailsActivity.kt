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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ============================================================
// PODCAST DETAILS ACTIVITY
//
// PURPOSE:
// Displays detailed information about a selected podcast.
//
// FEATURES:
// 1. Back navigation.
// 2. Podcast artwork.
// 3. Podcast title.
// 4. Podcast creator.
// 5. Subscribe / Unsubscribe using Room.
// 6. Open podcast URL.
// 7. Load RSS podcast episodes.
// 8. Display episodes in RecyclerView.
// 9. Display AUDIO / VIDEO episode type.
// 10. Open selected episodes in the Media3 player.
//
// ASSIGNMENT 8:
// RSS episodes are loaded using PodcastRssParser.
// Room is the source of truth for subscriptions.
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

    // ========================================================
    // ROOM DATABASE
    // ========================================================

    private lateinit var database: AppDatabase

    // ========================================================
    // CURRENT PODCAST
    // ========================================================

    private lateinit var currentPodcast: Podcast

    // ========================================================
    // SUBSCRIPTION STATE
    // ========================================================

    private var isPodcastSubscribed = false

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_podcast_details
        )

        // ====================================================
        // CONNECT PODCAST DETAIL VIEWS
        // ====================================================

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

        // ====================================================
        // CONNECT EPISODE VIEWS
        // ====================================================

        textViewEpisodesLoading =
            findViewById(
                R.id.textViewEpisodesLoading
            )

        recyclerViewEpisodes =
            findViewById(
                R.id.recyclerViewEpisodes
            )

        // ====================================================
        // INITIALIZE ROOM DATABASE
        // ====================================================

        database =
            AppDatabase.getDatabase(
                applicationContext
            )

        // ====================================================
        // BACK BUTTON
        // ====================================================

        buttonBack.setOnClickListener {
            finish()
        }

        // ====================================================
        // RECEIVE PODCAST JSON
        // ====================================================

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

        // ====================================================
        // CONVERT JSON INTO PODCAST OBJECT
        // ====================================================

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

        // ====================================================
        // CHECK PODCAST DATA
        // ====================================================

        if (podcast == null) {

            Toast.makeText(
                this,
                "Unable to load podcast information.",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }

        // ====================================================
        // STORE CURRENT PODCAST
        // ====================================================

        currentPodcast =
            podcast

        // ====================================================
        // DISPLAY PODCAST TITLE
        // ====================================================

        textViewPodcastTitle.text =
            podcast.collectionName
                ?: podcast.trackName
                        ?: "Unknown Podcast"

        // ====================================================
        // DISPLAY PODCAST CREATOR
        // ====================================================

        textViewPodcastArtist.text =
            podcast.artistName
                ?: "Unknown Creator"

        // ====================================================
        // LOAD PODCAST ARTWORK
        // ====================================================

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

        // ====================================================
        // SUBSCRIBE / UNSUBSCRIBE BUTTON
        // ====================================================

        buttonSubscribeDetails.setOnClickListener {
            toggleSubscription()
        }

        // ====================================================
        // OPEN PODCAST BUTTON
        // ====================================================

        buttonOpenPodcast.setOnClickListener {
            openPodcast(
                currentPodcast
            )
        }

        // ====================================================
        // SET UP EPISODE RECYCLERVIEW
        // ====================================================

        episodeAdapter =
            EpisodeAdapter(
                emptyList()
            ) { episode ->

                playEpisode(
                    episode
                )
            }

        recyclerViewEpisodes.layoutManager =
            LinearLayoutManager(this)

        recyclerViewEpisodes.adapter =
            episodeAdapter

        // ====================================================
        // CHECK CURRENT SUBSCRIPTION STATUS
        // ====================================================

        checkSubscriptionStatus()

        // ====================================================
        // LOAD RSS EPISODES
        // ====================================================

        loadPodcastEpisodes(
            currentPodcast
        )
    }

    // ============================================================
    // CHECK SUBSCRIPTION STATUS
    //
    // Room is the source of truth.
    // ============================================================

    private fun checkSubscriptionStatus() {

        lifecycleScope.launch {

            val subscribed =
                withContext(
                    Dispatchers.IO
                ) {

                    database
                        .subscriptionDao()
                        .isSubscribed(
                            currentPodcast.trackId ?: -1L
                        )
                }

            isPodcastSubscribed =
                subscribed

            updateSubscriptionButton()
        }
    }

    // ============================================================
    // UPDATE SUBSCRIPTION BUTTON
    // ============================================================

    private fun updateSubscriptionButton() {

        buttonSubscribeDetails.text =
            if (isPodcastSubscribed) {

                "UNSUBSCRIBE"

            } else {

                "SUBSCRIBE"
            }
    }

    // ============================================================
    // TOGGLE SUBSCRIPTION
    //
    // Adds or removes the podcast from Room.
    // ============================================================

    private fun toggleSubscription() {

        lifecycleScope.launch {

            val trackId =
                currentPodcast.trackId

            // ----------------------------------------------------
            // TRACK ID IS REQUIRED
            // ----------------------------------------------------

            if (trackId == null) {

                Toast.makeText(
                    this@PodcastDetailsActivity,
                    "Podcast ID is unavailable.",
                    Toast.LENGTH_SHORT
                ).show()

                return@launch
            }

            // ----------------------------------------------------
            // FEED URL IS REQUIRED
            // ----------------------------------------------------

            val feedUrl =
                currentPodcast.feedUrl

            if (feedUrl.isNullOrBlank()) {

                Toast.makeText(
                    this@PodcastDetailsActivity,
                    "This podcast does not have an RSS feed.",
                    Toast.LENGTH_SHORT
                ).show()

                return@launch
            }

            // ----------------------------------------------------
            // CHANGE ROOM DATABASE
            // ----------------------------------------------------

            withContext(
                Dispatchers.IO
            ) {

                if (isPodcastSubscribed) {

                    // ==========================================
                    // REMOVE SUBSCRIPTION
                    // ==========================================

                    val existingPodcast =
                        database
                            .subscriptionDao()
                            .getByTrackId(
                                trackId
                            )

                    if (existingPodcast != null) {

                        database
                            .subscriptionDao()
                            .delete(
                                existingPodcast
                            )
                    }

                } else {

                    // ==========================================
                    // ADD SUBSCRIPTION
                    // ==========================================

                    val subscribedPodcast =
                        SubscribedPodcast(
                            trackId = trackId,
                            collectionName =
                                currentPodcast.collectionName
                                    ?: currentPodcast.trackName
                                    ?: "Unknown Podcast",
                            artistName =
                                currentPodcast.artistName
                                    ?: "Unknown Creator",
                            artworkUrl100 =
                                currentPodcast.artworkUrl100
                                    ?: "",
                            feedUrl =
                                feedUrl,
                            collectionViewUrl =
                                currentPodcast.collectionViewUrl
                                    ?: ""
                        )

                    database
                        .subscriptionDao()
                        .insert(
                            subscribedPodcast
                        )
                }
            }

            // ----------------------------------------------------
            // READ ROOM AGAIN
            //
            // Room remains the source of truth.
            // ----------------------------------------------------

            val databaseState =
                withContext(
                    Dispatchers.IO
                ) {

                    database
                        .subscriptionDao()
                        .isSubscribed(
                            trackId
                        )
                }

            // ----------------------------------------------------
            // UPDATE LOCAL STATE FROM ROOM
            // ----------------------------------------------------

            isPodcastSubscribed =
                databaseState

            // ----------------------------------------------------
            // UPDATE BUTTON
            // ----------------------------------------------------

            updateSubscriptionButton()

            // ----------------------------------------------------
            // CONFIRM ACTION TO USER
            // ----------------------------------------------------

            Toast.makeText(
                this@PodcastDetailsActivity,
                if (isPodcastSubscribed) {

                    "Subscribed to ${
                        currentPodcast.collectionName
                            ?: "podcast"
                    }"

                } else {

                    "Subscription removed."
                },
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // ============================================================
    // LOAD PODCAST EPISODES
    //
    // Uses PodcastRssParser instead of the older RSSFeedService.
    //
    // This is important because PodcastRssParser supports:
    // 1. RSS enclosure media types.
    // 2. Media RSS content.
    // 3. Video detection.
    // 4. GUID fallback.
    // 5. Content encoded descriptions.
    // ============================================================

    private fun loadPodcastEpisodes(
        podcast: Podcast
    ) {

        val feedUrl =
            podcast.feedUrl

        // --------------------------------------------------------
        // CHECK RSS FEED URL
        // --------------------------------------------------------

        if (feedUrl.isNullOrBlank()) {

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

                // =================================================
                // USE THE NEW ASSIGNMENT 8 RSS PARSER
                // =================================================

                val episodes =
                    PodcastRssParser.parseFeed(
                        feedUrl
                    )

                // ------------------------------------------------
                // UPDATE UI
                // ------------------------------------------------

                if (episodes.isEmpty()) {

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
                // RSS errors should not crash the application.
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
    // Opens the existing Media3 EpisodePlayerActivity.
    //
    // The same media URL can represent either audio or video.
    // ============================================================

    private fun playEpisode(
        episode: Episode
    ) {

        // ========================================================
        // CHECK MEDIA URL
        // ========================================================

        if (episode.audioUrl.isBlank()) {

            Toast.makeText(
                this,
                "No playable media is available for this episode.",
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
        // SEND EPISODE INFORMATION
        // ========================================================

        intent.putExtra(
            "episode_title",
            episode.title
        )

        intent.putExtra(
            "episode_audio_url",
            episode.audioUrl
        )

        // --------------------------------------------------------
        // Also send the media type.
        //
        // EpisodePlayerActivity can use this later if it needs
        // to make audio/video-specific playback decisions.
        // --------------------------------------------------------

        intent.putExtra(
            "episode_media_type",
            episode.mediaType
        )

        // ========================================================
        // OPEN MEDIA3 PLAYER
        // ========================================================

        startActivity(
            intent
        )
    }

    // ============================================================
    // OPEN PODCAST
    //
    // Opens the podcast URL using an available application.
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


