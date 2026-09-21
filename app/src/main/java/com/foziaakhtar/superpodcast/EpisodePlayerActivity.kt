package com.foziaakhtar.superpodcast

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

// ============================================================
// EPISODE PLAYER ACTIVITY
//
// Provides audio playback for a selected podcast episode.
//
// Media3 is used to:
// 1. Load the episode audio URL.
// 2. Start audio playback.
// 3. Provide Play/Pause controls.
// 4. Display playback progress.
// 5. Release the player when the Activity closes.
// ============================================================

class EpisodePlayerActivity : AppCompatActivity() {

    // ========================================================
    // MEDIA3 PLAYER
    // ========================================================

    private var player: ExoPlayer? = null

    // ========================================================
    // PLAYER VIEW
    // ========================================================

    private lateinit var playerView: PlayerView

    // ========================================================
    // EPISODE TITLE
    // ========================================================

    private lateinit var textViewPlayerEpisodeTitle: TextView

    // ========================================================
    // CLOSE BUTTON
    // ========================================================

    private lateinit var buttonClosePlayer: Button

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        // ========================================================
        // LOAD PLAYER SCREEN
        // ========================================================

        setContentView(
            R.layout.activity_episode_player
        )

        // ========================================================
        // CONNECT XML VIEWS
        // ========================================================

        playerView =
            findViewById(
                R.id.playerView
            )

        textViewPlayerEpisodeTitle =
            findViewById(
                R.id.textViewPlayerEpisodeTitle
            )

        buttonClosePlayer =
            findViewById(
                R.id.buttonClosePlayer
            )

        // ========================================================
        // CLOSE BUTTON
        //
        // Returns the user to the Podcast Details screen.
        // ========================================================

        buttonClosePlayer.setOnClickListener {

            finish()
        }

        // ========================================================
        // RECEIVE EPISODE INFORMATION
        // ========================================================

        val episodeTitle =
            intent.getStringExtra(
                "episode_title"
            )
                ?: "Podcast Episode"

        val audioUrl =
            intent.getStringExtra(
                "episode_audio_url"
            )

        // ========================================================
        // DISPLAY EPISODE TITLE
        // ========================================================

        textViewPlayerEpisodeTitle.text =
            episodeTitle

        // ========================================================
        // CHECK AUDIO URL
        // ========================================================

        if (
            audioUrl.isNullOrBlank()
        ) {

            Toast.makeText(
                this,
                "No audio is available for this episode.",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }

        // ========================================================
        // INITIALIZE MEDIA3 PLAYER
        // ========================================================

        initializePlayer(
            audioUrl
        )
    }

    // ============================================================
    // INITIALIZE MEDIA3 PLAYER
    //
    // Creates an ExoPlayer instance and loads the selected
    // podcast episode.
    // ============================================================

    private fun initializePlayer(
        audioUrl: String
    ) {

        // ========================================================
        // CREATE EXOPLAYER
        // ========================================================

        player =
            ExoPlayer.Builder(
                this
            ).build()

        // ========================================================
        // CONNECT PLAYER TO PLAYER VIEW
        // ========================================================

        playerView.player =
            player

        // ========================================================
        // CREATE MEDIA ITEM
        //
        // MediaItem represents the audio source that Media3
        // will play.
        // ========================================================

        val mediaItem =
            MediaItem.fromUri(
                audioUrl
            )

        // ========================================================
        // LOAD AUDIO
        // ========================================================

        player?.setMediaItem(
            mediaItem
        )

        // ========================================================
        // PREPARE PLAYER
        // ========================================================

        player?.prepare()

        // ========================================================
        // START PLAYBACK
        //
        // Playback begins automatically when the player screen
        // opens.
        // ========================================================

        player?.playWhenReady =
            true
    }

    // ============================================================
    // RELEASE MEDIA3 PLAYER
    //
    // Releases the player when this Activity is destroyed.
    //
    // This prevents audio resources from remaining in memory.
    // ============================================================

    override fun onDestroy() {

        playerView.player =
            null

        player?.release()

        player =
            null

        super.onDestroy()
    }
}



