package com.foziaakhtar.superpodcast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// ============================================================
// SUBSCRIPTION ADAPTER
//
// PURPOSE:
// Displays podcasts saved in the Room subscription database.
//
// Each subscription row displays:
// 1. Podcast artwork
// 2. Podcast title
// 3. Podcast creator
//
// When the user taps a podcast, the supplied callback is called
// so SubscriptionsActivity can open the podcast details screen.
// ============================================================

class SubscriptionAdapter(
    private var subscriptions: List<SubscribedPodcast>,
    private val onPodcastClick: (SubscribedPodcast) -> Unit
) : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    // ========================================================
    // UPDATE LIST
    //
    // Replaces the current subscription list and refreshes
    // the RecyclerView.
    // ========================================================

    fun updateList(
        newSubscriptions: List<SubscribedPodcast>
    ) {
        subscriptions = newSubscriptions
        notifyDataSetChanged()
    }

    // ========================================================
    // CREATE VIEW HOLDER
    //
    // Inflates one subscription row from item_subscription.xml.
    // ========================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubscriptionViewHolder {

        val view =
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_subscription,
                parent,
                false
            )

        return SubscriptionViewHolder(
            view
        )
    }

    // ========================================================
    // BIND VIEW HOLDER
    //
    // Sends the subscription data to the row.
    // ========================================================

    override fun onBindViewHolder(
        holder: SubscriptionViewHolder,
        position: Int
    ) {

        holder.bind(
            subscriptions[position]
        )
    }

    // ========================================================
    // ITEM COUNT
    // ========================================================

    override fun getItemCount(): Int =
        subscriptions.size

    // ========================================================
    // VIEW HOLDER
    //
    // Holds references to the views inside one subscription
    // row.
    // ========================================================

    inner class SubscriptionViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(
        itemView
    ) {

        private val imageViewArtwork =
            itemView.findViewById<ImageView>(
                R.id.imageViewArtwork
            )

        private val textViewTitle =
            itemView.findViewById<TextView>(
                R.id.textViewTitle
            )

        private val textViewArtist =
            itemView.findViewById<TextView>(
                R.id.textViewArtist
            )

        // ====================================================
        // BIND SUBSCRIPTION
        // ====================================================

        fun bind(
            subscription: SubscribedPodcast
        ) {

            // ------------------------------------------------
            // PODCAST TITLE
            // ------------------------------------------------

            textViewTitle.text =
                subscription.collectionName

            // ------------------------------------------------
            // PODCAST CREATOR
            // ------------------------------------------------

            textViewArtist.text =
                subscription.artistName

            // ------------------------------------------------
            // PODCAST ARTWORK
            //
            // Glide loads the artwork URL into the ImageView.
            // ------------------------------------------------

            Glide.with(
                itemView.context
            )
                .load(
                    subscription.artworkUrl100
                )
                .placeholder(
                    android.R.drawable.ic_menu_gallery
                )
                .error(
                    android.R.drawable.ic_menu_gallery
                )
                .into(
                    imageViewArtwork
                )

            // ------------------------------------------------
            // PODCAST CLICK
            //
            // Passes the selected subscription back to the
            // SubscriptionsActivity.
            // ------------------------------------------------

            itemView.setOnClickListener {

                onPodcastClick(
                    subscription
                )
            }
        }
    }
}