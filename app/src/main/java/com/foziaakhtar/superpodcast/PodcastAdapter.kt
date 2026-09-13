package com.foziaakhtar.superpodcast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// ============================================================
// PODCAST ADAPTER
//
// Connects podcast data returned by iTunes to the RecyclerView.
//
// Responsibilities:
// 1. Display podcast artwork.
// 2. Display podcast title.
// 3. Display podcast creator.
// 4. Handle podcast selection.
// 5. Handle the Subscribe button.
// ============================================================

class PodcastAdapter(
    private var podcastList: List<Podcast>,
    private val onPodcastClick: (Podcast) -> Unit,
    private val onSubscribeClick: (Podcast) -> Unit
) : RecyclerView.Adapter<PodcastAdapter.PodcastViewHolder>() {

    // ========================================================
    // VIEW HOLDER
    // ========================================================

    class PodcastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val artwork: ImageView =
            itemView.findViewById(R.id.imageViewArtwork)

        val title: TextView =
            itemView.findViewById(R.id.textViewPodcastTitle)

        val artist: TextView =
            itemView.findViewById(R.id.textViewArtist)

        val subscribeButton: Button =
            itemView.findViewById(R.id.buttonSubscribe)
    }

    // ========================================================
    // CREATE VIEW HOLDER
    // ========================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PodcastViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_podcast, parent, false)

        return PodcastViewHolder(view)
    }

    // ========================================================
    // BIND PODCAST DATA
    // ========================================================

    override fun onBindViewHolder(
        holder: PodcastViewHolder,
        position: Int
    ) {

        val podcast = podcastList[position]

        // ----------------------------------------------------
        // PODCAST TITLE
        // ----------------------------------------------------

        holder.title.text =
            podcast.collectionName
                ?: podcast.trackName
                        ?: "Unknown Podcast"

        // ----------------------------------------------------
        // PODCAST CREATOR
        // ----------------------------------------------------

        holder.artist.text =
            podcast.artistName
                ?: "Unknown Creator"

        // ----------------------------------------------------
        // PODCAST ARTWORK
        // ----------------------------------------------------

        Glide.with(holder.itemView.context)
            .load(podcast.artworkUrl100)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .into(holder.artwork)

        // ----------------------------------------------------
        // PODCAST CLICK
        //
        // Tapping the podcast itself opens the podcast.
        // ----------------------------------------------------

        holder.itemView.setOnClickListener {

            onPodcastClick(podcast)
        }

        // ----------------------------------------------------
        // SUBSCRIBE BUTTON
        //
        // Tapping Subscribe sends the selected podcast back
        // to MainActivity.
        // ----------------------------------------------------

        holder.subscribeButton.setOnClickListener {

            onSubscribeClick(podcast)
        }
    }

    // ========================================================
    // ITEM COUNT
    // ========================================================

    override fun getItemCount(): Int {

        return podcastList.size
    }

    // ========================================================
    // UPDATE LIST
    // ========================================================

    fun updateList(newList: List<Podcast>) {

        podcastList = newList

        notifyDataSetChanged()
    }
}