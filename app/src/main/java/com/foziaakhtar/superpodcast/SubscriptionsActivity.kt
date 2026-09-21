package com.foziaakhtar.superpodcast

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

// ============================================================
// SUBSCRIPTIONS ACTIVITY
//
// PURPOSE:
// Displays podcasts that the user has subscribed to.
//
// ROOM DATABASE:
// Subscriptions are loaded from AppDatabase instead of the
// older SharedPreferences/Gson subscription system.
//
// FEATURES:
// 1. Displays saved podcast subscriptions.
// 2. Displays podcast artwork, title, and creator.
// 3. Opens Podcast Details when a podcast is selected.
// 4. Automatically refreshes when subscriptions change.
// 5. Provides a Back to Search button.
// 6. Handles system-bar spacing.
// ============================================================

class SubscriptionsActivity : AppCompatActivity() {

    // ========================================================
    // UI REFERENCES
    // ========================================================

    private lateinit var recyclerViewSubscriptions: RecyclerView
    private lateinit var textViewEmptyMessage: TextView
    private lateinit var buttonBackToSearch: Button

    // ========================================================
    // ROOM DATABASE
    // ========================================================

    private lateinit var database: AppDatabase

    // ========================================================
    // SUBSCRIPTION ADAPTER
    // ========================================================

    private lateinit var subscriptionAdapter: SubscriptionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ====================================================
        // ENABLE EDGE-TO-EDGE
        // ====================================================

        enableEdgeToEdge()

        setContentView(
            R.layout.activity_subscriptions
        )

        // ====================================================
        // CONNECT XML VIEWS
        // ====================================================

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

        // ====================================================
        // HANDLE SYSTEM BAR INSETS
        //
        // Prevents the screen content from appearing underneath
        // the status bar or navigation bar.
        // ====================================================

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

        // ====================================================
        // BACK TO SEARCH BUTTON
        //
        // Returns the user to MainActivity.
        // ====================================================

        buttonBackToSearch.setOnClickListener {

            val intent =
                Intent(
                    this,
                    MainActivity::class.java
                )

            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
            )

            startActivity(intent)

            finish()
        }

        // ====================================================
        // INITIALIZE ROOM DATABASE
        // ====================================================

        database =
            AppDatabase.getDatabase(
                applicationContext
            )

        // ====================================================
        // SET UP RECYCLER VIEW
        // ====================================================

        recyclerViewSubscriptions.layoutManager =
            LinearLayoutManager(this)

        // ====================================================
        // CREATE SUBSCRIPTION ADAPTER
        // ====================================================

        subscriptionAdapter =
            SubscriptionAdapter(
                emptyList()
            ) { subscription ->

                // ============================================
                // OPEN SELECTED PODCAST
                // ============================================

                openPodcastDetails(
                    subscription
                )
            }

        recyclerViewSubscriptions.adapter =
            subscriptionAdapter

        // ====================================================
        // OBSERVE ROOM SUBSCRIPTIONS
        //
        // Flow automatically provides the latest subscription
        // list whenever the database changes.
        // ====================================================

        observeSubscriptions()
    }

    // ============================================================
    // OBSERVE SUBSCRIPTIONS
    //
    // Reads the subscription Flow from Room.
    //
    // Whenever a podcast is added or removed, Room emits a new
    // list and the RecyclerView updates automatically.
    // ============================================================

    private fun observeSubscriptions() {

        lifecycleScope.launch {

            database
                .subscriptionDao()
                .getAll()
                .collect { subscriptions ->

                    // ========================================
                    // UPDATE RECYCLER VIEW
                    // ========================================

                    subscriptionAdapter.updateList(
                        subscriptions
                    )

                    // ========================================
                    // UPDATE EMPTY/COUNT MESSAGE
                    // ========================================

                    if (subscriptions.isEmpty()) {

                        showEmptyMessage()

                    } else {

                        textViewEmptyMessage.text =
                            "${subscriptions.size} podcast subscription(s)"

                        textViewEmptyMessage.visibility =
                            TextView.VISIBLE

                        recyclerViewSubscriptions.visibility =
                            RecyclerView.VISIBLE
                    }
                }
        }
    }

    // ============================================================
    // SHOW EMPTY MESSAGE
    //
    // Displays when there are no saved subscriptions.
    // ============================================================

    private fun showEmptyMessage() {

        textViewEmptyMessage.text =
            "You have not subscribed to any podcasts yet."

        textViewEmptyMessage.visibility =
            TextView.VISIBLE

        recyclerViewSubscriptions.visibility =
            RecyclerView.GONE
    }

    // ============================================================
    // OPEN PODCAST DETAILS
    //
    // Converts the Room SubscribedPodcast object into the
    // Podcast model expected by PodcastDetailsActivity.
    //
    // This allows the existing details screen to continue
    // working with the new Room subscription system.
    // ============================================================

    private fun openPodcastDetails(
        subscription: SubscribedPodcast
    ) {

        val podcast =
            Podcast(
                trackId = subscription.trackId,
                collectionName = subscription.collectionName,
                artistName = subscription.artistName,
                artworkUrl100 = subscription.artworkUrl100,
                feedUrl = subscription.feedUrl,
                collectionViewUrl = subscription.collectionViewUrl
            )

        val intent =
            Intent(
                this,
                PodcastDetailsActivity::class.java
            )

        // ====================================================
        // SEND PODCAST INFORMATION TO DETAILS SCREEN
        // ====================================================

        intent.putExtra(
            "podcast_json",
            com.google.gson.Gson().toJson(
                podcast
            )
        )

        startActivity(intent)
    }
}
