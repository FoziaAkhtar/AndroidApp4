package com.foziaakhtar.superpodcast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// ============================================================
// EPISODE ADAPTER
//
// PURPOSE:
// Connects podcast episode data to the RecyclerView.
//
// RESPONSIBILITIES:
// 1. Display episode title.
// 2. Display publication date.
// 3. Display episode description.
// 4. Display AUDIO or VIDEO media type.
// 5. Handle the Play Episode button.
// 6. Update the episode list when new RSS data is loaded.
//
// ASSIGNMENT 8:
// The adapter uses Episode.mediaType and
// PodcastRssParser.isVideoMedia() to identify video episodes.
// ============================================================

class EpisodeAdapter(
    private var episodeList: List<Episode>,
    private val onPlayClick: (Episode) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    // ========================================================
    // VIEW HOLDER
    //
    // Holds references to the views used by one episode item.
    // ========================================================

    class EpisodeViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        // ----------------------------------------------------
        // Episode title.
        // ----------------------------------------------------

        val title: TextView =
            itemView.findViewById(
                R.id.textViewEpisodeTitle
            )

        // ----------------------------------------------------
        // Publication date.
        // ----------------------------------------------------

        val date: TextView =
            itemView.findViewById(
                R.id.textViewEpisodeDate
            )

        // ----------------------------------------------------
        // Episode description.
        // ----------------------------------------------------

        val description: TextView =
            itemView.findViewById(
                R.id.textViewEpisodeDescription
            )

        // ----------------------------------------------------
        // AUDIO / VIDEO label.
        //
        // This view must exist in item_episode.xml.
        // ----------------------------------------------------

        val mediaType: TextView =
            itemView.findViewById(
                R.id.textViewEpisodeType
            )

        // ----------------------------------------------------
        // Play button.
        // ----------------------------------------------------

        val playButton: Button =
            itemView.findViewById(
                R.id.buttonPlayEpisode
            )
    }

    // ========================================================
    // CREATE VIEW HOLDER
    //
    // Inflates item_episode.xml for each episode.
    // ========================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EpisodeViewHolder {

        val view =
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_episode,
                parent,
                false
            )

        return EpisodeViewHolder(
            view
        )
    }

    // ========================================================
    // BIND EPISODE DATA
    //
    // Places the selected Episode information into the
    // RecyclerView item.
    // ========================================================

    override fun onBindViewHolder(
        holder: EpisodeViewHolder,
        position: Int
    ) {

        val episode =
            episodeList[position]

        // ----------------------------------------------------
        // EPISODE TITLE
        // ----------------------------------------------------

        holder.title.text =
            episode.title.ifBlank {
                "Unknown Episode"
            }

        // ----------------------------------------------------
        // PUBLICATION DATE
        // ----------------------------------------------------

        holder.date.text =
            if (
                episode.pubDate.isBlank()
            ) {

                "Publication date unavailable"

            } else {

                episode.pubDate
            }

        // ----------------------------------------------------
        // EPISODE DESCRIPTION
        // ----------------------------------------------------

        holder.description.text =
            if (
                episode.description.isBlank()
            ) {

                "No episode description available."

            } else {

                episode.description
            }

        // ----------------------------------------------------
        // AUDIO / VIDEO TYPE
        //
        // PodcastRssParser checks:
        // 1. RSS MIME type.
        // 2. Video file extension.
        // ----------------------------------------------------

        val isVideo =
            PodcastRssParser.isVideoMedia(
                episode.mediaType,
                episode.audioUrl
            )

        holder.mediaType.text =
            if (isVideo) {
                "VIDEO"
            } else {
                "AUDIO"
            }

        // ----------------------------------------------------
        // PLAY EPISODE BUTTON
        //
        // Sends the selected Episode back to the Activity.
        // ----------------------------------------------------

        holder.playButton.setOnClickListener {

            onPlayClick(
                episode
            )
        }
    }

    // ========================================================
    // ITEM COUNT
    // ========================================================

    override fun getItemCount(): Int {

        return episodeList.size
    }

    // ========================================================
    // UPDATE EPISODE LIST
    //
    // Replaces the current list with newly loaded RSS data.
    // ========================================================

    fun updateList(
        newList: List<Episode>
    ) {

        episodeList =
            newList

        notifyDataSetChanged()
    }
}


