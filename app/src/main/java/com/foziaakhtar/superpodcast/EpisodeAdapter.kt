
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
// Connects podcast episode data to the RecyclerView.
//
// Responsibilities:
// 1. Display episode title.
// 2. Display publication date.
// 3. Display episode description.
// 4. Handle the Play Episode button.
// 5. Update the episode list when new RSS data is loaded.
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

        val title: TextView =
            itemView.findViewById(
                R.id.textViewEpisodeTitle
            )

        val date: TextView =
            itemView.findViewById(
                R.id.textViewEpisodeDate
            )

        val description: TextView =
            itemView.findViewById(
                R.id.textViewEpisodeDescription
            )

        val playButton: Button =
            itemView.findViewById(
                R.id.buttonPlayEpisode
            )
    }

    // ========================================================
    // CREATE VIEW HOLDER
    //
    // Creates the visual layout for one episode.
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
    // Places the Episode information into the views.
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
        // PLAY EPISODE BUTTON
        //
        // Sends the selected Episode back to the Activity.
        // The Activity will later use Media3 to play it.
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

