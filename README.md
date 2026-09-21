## 🎙️ # SuperPodcast — AndroidApp4

## 📱 Project Overview

**SuperPodcast** is an Android podcast discovery and playback application built with **Kotlin** and **Android Studio**.

The application allows users to:

* Search for podcasts using the iTunes Search API
* Browse podcast results
* Filter results using a minimum title-word requirement
* View detailed podcast information
* Subscribe and unsubscribe to podcasts
* Manage personal podcast subscriptions
* Load podcast episodes from RSS feeds
* Detect audio and video episodes
* Play podcast episodes using Android Media3
* Schedule background podcast updates using WorkManager
* Receive notifications when a new subscribed episode is detected

The project was developed as part of the **AndroidApp4 Assignment 8** and demonstrates Android networking, REST API integration, RSS parsing, RecyclerView, Room database persistence, image loading, coroutines, Media3 playback, WorkManager background processing, notifications, activity navigation, and user-controlled filtering.

---

## ✨ Features

### 🔎 Podcast Search

Users can enter a topic or keyword and search for podcasts using the **iTunes Search API**.

Examples:

* Technology
* Business
* Education
* Health
* Comedy
* News
* Science

Search results are displayed using a RecyclerView.

---

### 🎯 Custom Search Filter

The application includes a custom filtering feature:

**Minimum Title Words**

Users can enter the minimum number of words that must appear in a podcast title.

For example:

```text
Minimum title words: 3
```

Only podcasts with titles containing at least three words are displayed.

This provides an additional user-controlled search criterion beyond the basic API search.

---

### 🖼️ Podcast Artwork

Podcast artwork is downloaded and displayed using **Glide**.

Podcast results display:

* Podcast artwork
* Podcast title
* Podcast creator
* Subscribe option

---

### 📄 Podcast Details

Selecting a podcast opens a dedicated details screen.

The details screen provides:

* Podcast artwork
* Podcast title
* Podcast creator
* Subscribe / Unsubscribe
* Open Podcast
* Podcast episodes
* Episode descriptions
* AUDIO / VIDEO episode labels
* Episode playback
* Back navigation

---

### ⭐ Subscriptions

Users can subscribe to podcasts from the search results or podcast details screen.

For Assignment 8, subscriptions are stored locally using the **Room persistence library**.

The Room database contains:

```text
subscribed_podcasts
```

Each subscription stores information including:

* Podcast ID
* Podcast title
* Podcast creator
* Artwork URL
* RSS feed URL
* Collection URL

Users can access their saved podcasts through:

**My Subscriptions**

---

### 📚 My Subscriptions

The My Subscriptions screen displays podcasts saved in the local Room database.

Users can:

* View subscribed podcasts
* View podcast artwork
* View podcast title
* View podcast creator
* Select a podcast
* Return to the podcast details screen

Subscriptions remain available after restarting the application because they are persisted locally.

---

### 🌐 Open Podcast

Users can open the available podcast feed or collection URL from the details screen.

The application uses the device's available browser or application to open the URL.

---

## 🎙️ RSS Podcast Episodes

Assignment 8 adds RSS feed support.

When a podcast provides an RSS feed, SuperPodcast downloads and parses the feed using the custom:

```text
PodcastRssParser
```

The parser extracts information including:

* Episode GUID
* Episode title
* Episode description
* Publication date
* Media URL
* Media type
* Episode artwork

The parser supports standard RSS elements as well as podcast namespace elements such as:

```text
content:encoded
media:content
itunes:image
enclosure
```

The application displays up to the latest 20 available episodes.

---

## 🎥 Audio and Video Detection

SuperPodcast identifies whether an episode contains audio or video media.

Episode rows display:

```text
AUDIO
```

or:

```text
VIDEO
```

Video detection uses the RSS media type when available.

The application also checks common video file extensions including:

```text
.mp4
.m4v
.mov
.webm
.mkv
```

This allows the application to distinguish video podcast episodes from standard audio episodes.

---

## ▶️ Episode Playback

Podcast episodes can be selected using the:

**▶ Play Episode**

button.

The application opens the episode player and passes:

* Episode title
* Episode media URL
* Episode media type

The project uses **AndroidX Media3 / ExoPlayer** for media playback.

Media3 dependencies include:

```text
media3-exoplayer
media3-exoplayer-hls
media3-ui
```

HLS streams such as `.m3u8` are also supported by the playback implementation.

---

## 🔔 Background Podcast Updates

Assignment 8 adds background podcast update checking using **WorkManager**.

The application schedules podcast update work using:

```text
PodcastUpdateScheduler
```

The background worker:

```text
PodcastUpdateWorker
```

checks the user's subscribed podcasts and retrieves their RSS feeds.

The worker:

1. Loads subscriptions from Room.
2. Reads each podcast RSS feed.
3. Determines the newest episode.
4. Compares it with the previously recorded episode.
5. Saves the newest episode information.
6. Notifies the user when a new episode is detected.

The WorkManager task uses a network-connected constraint.

A periodic update schedule is also configured so the application can check for new podcast episodes in the background.

---

## 🔔 Podcast Notifications

SuperPodcast includes Android notification support for new podcast episodes.

Notifications are managed by:

```text
NotificationHelper
```

The notification system:

* Creates a notification channel on Android 8.0+
* Handles Android 13+ notification permission
* Displays the podcast name
* Displays the new episode title
* Opens the podcast details screen when selected

The application uses the Android notification system to alert users when a subscribed podcast has a new episode.

---

## ⏳ Loading Indicator

Loading messages are displayed while podcast searches and RSS episode requests are being processed.

The application also prevents duplicate search requests while a search operation is in progress.

---

## ⚠️ Error and Empty Result Handling

The application handles several situations safely, including:

* Empty searches
* No matching podcasts
* Internet/API errors
* RSS feed errors
* Empty RSS feeds
* Missing podcast information
* Missing RSS feed URLs
* Missing playable media URLs
* Invalid podcast information

Users receive appropriate messages instead of the application crashing.

---

## 🛠️ Technologies Used

| Technology                 | Purpose                              |
| -------------------------- | ------------------------------------ |
| Kotlin                     | Main programming language            |
| Android Studio             | Development environment              |
| Retrofit                   | iTunes API networking                |
| Gson                       | JSON parsing                         |
| iTunes Search API          | Podcast search and metadata          |
| Glide                      | Podcast artwork loading              |
| RecyclerView               | Podcast and episode lists            |
| Coroutines                 | Asynchronous operations              |
| Lifecycle / lifecycleScope | Lifecycle-aware coroutine management |
| Room                       | Local subscription database          |
| SQLite                     | Room database storage                |
| WorkManager                | Background podcast update checks     |
| Media3 ExoPlayer           | Audio/video playback                 |
| Media3 UI                  | Player interface                     |
| Android Notifications      | New episode alerts                   |
| HttpURLConnection          | RSS feed networking                  |
| XML Pull Parser            | RSS XML parsing                      |
| Material Components        | Android UI components                |
| XML Layouts                | User interface                       |
| Android Activities         | Screen navigation                    |

---

## 🔌 APIs and Data Sources

### iTunes Search API

SuperPodcast uses the **iTunes Search API** to retrieve podcast information.

The application receives information including:

* Podcast ID
* Podcast title
* Artist/creator
* Artwork
* RSS feed URL
* Collection URL

The response is converted into Kotlin data models using Gson.

### Podcast RSS Feeds

Podcast episode information is retrieved from the RSS feed provided by each podcast.

RSS feeds are parsed locally using:

```text
PodcastRssParser
```

This allows the application to display current podcast episodes and identify audio/video media.

---

## 🏗️ Project Structure

```text
SuperPodcast/
│
├── app/
│   │
│   └── src/
│       │
│       └── main/
│           │
│           ├── java/
│           │   └── com/
│           │       └── foziaakhtar/
│           │           └── superpodcast/
│           │               │
│           │               ├── MainActivity.kt
│           │               ├── ITunesApi.kt
│           │               ├── Podcast.kt
│           │               ├── PodcastAdapter.kt
│           │               ├── PodcastResponse.kt
│           │               ├── RetrofitInstance.kt
│           │               │
│           │               ├── PodcastDetailsActivity.kt
│           │               ├── SubscriptionsActivity.kt
│           │               ├── SubscriptionAdapter.kt
│           │               ├── SubscribedPodcast.kt
│           │               ├── SubscriptionDao.kt
│           │               ├── AppDatabase.kt
│           │               │
│           │               ├── Episode.kt
│           │               ├── EpisodeAdapter.kt
│           │               ├── EpisodePlayerActivity.kt
│           │               ├── PodcastRssParser.kt
│           │               ├── RSSFeedService.kt
│           │               │
│           │               ├── NotificationHelper.kt
│           │               ├── PodcastUpdateScheduler.kt
│           │               └── PodcastUpdateWorker.kt
│           │
│           ├── res/
│           │   │
│           │   └── layout/
│           │       │
│           │       ├── activity_main.xml
│           │       ├── activity_podcast_details.xml
│           │       ├── activity_subscriptions.xml
│           │       ├── activity_episode_player.xml
│           │       ├── item_podcast.xml
│           │       ├── item_episode.xml
│           │       └── item_subscription.xml
│           │
│           └── AndroidManifest.xml
│
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 📱 Application Screens

### Main Search Screen

The main screen provides:

* SuperPodcast branding
* Podcast search
* Minimum-title-word filter
* Search button
* My Subscriptions button
* Podcast results

---

### Podcast Results

Search results are displayed using a RecyclerView.

Each podcast result includes:

```text
Podcast Artwork
Podcast Title
Podcast Creator
Subscribe
```

Selecting a podcast opens the podcast details screen.

---

### Podcast Details

The details screen allows users to:

* View podcast information
* Subscribe or unsubscribe
* Open the podcast
* View RSS episodes
* View episode descriptions
* Identify AUDIO / VIDEO episodes
* Play an episode
* Return to the previous screen

---

### Episode List

The episode list displays:

```text
Episode Title
AUDIO / VIDEO
Publication Date
Episode Description
▶ Play Episode
```

This provides users with direct access to the latest available RSS episodes.

---

### Episode Player

The Episode Player screen uses Android Media3 to play the selected podcast media.

The player receives the selected episode's media URL and media type.

---

### My Subscriptions

The subscriptions screen displays podcasts saved in the Room database.

A subscribed podcast can be selected to return to its podcast details.

---

## 🔄 Application Flow

```text
                    ┌─────────────────────┐
                    │   SuperPodcast App   │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │   Main Search       │
                    │      Screen         │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Enter Search Topic  │
                    │ Optional Filter     │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │  iTunes Search API  │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │  Filter Results     │
                    │ Minimum Title Words │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │  Podcast Results    │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │  Podcast Details    │
                    └──────────┬──────────┘
                               │
                  ┌────────────┼────────────┐
                  ▼            ▼            ▼
            ┌──────────┐ ┌──────────┐ ┌──────────────┐
            │Subscribe │ │ Episodes │ │ Open Podcast │
            └────┬─────┘ └────┬─────┘ └──────────────┘
                 │             │
                 ▼             ▼
        ┌────────────────┐ ┌───────────────┐
        │ Room Database  │ │ Episode Player│
        └───────┬────────┘ └───────────────┘
                │
                ▼
        ┌────────────────────┐
        │ My Subscriptions   │
        └────────────────────┘

Background:

        ┌─────────────────────┐
        │    WorkManager      │
        └──────────┬──────────┘
                   ▼
        ┌─────────────────────┐
        │ PodcastUpdateWorker │
        └──────────┬──────────┘
                   ▼
        ┌─────────────────────┐
        │    RSS Feed Check   │
        └──────────┬──────────┘
                   ▼
        ┌─────────────────────┐
        │ New Episode Found?  │
        └──────────┬──────────┘
                   ▼
        ┌─────────────────────┐
        │ Android Notification│
        └─────────────────────┘
```

---

## 💾 Local Data Storage

SuperPodcast uses **Room** for local subscription persistence.

The database is:

```text
superpodcast_database
```

The subscription table is:

```text
subscribed_podcasts
```

Room provides:

* `SubscribedPodcast` entity
* `SubscriptionDao`
* `AppDatabase`

The DAO supports:

* Insert subscription
* Delete subscription
* Retrieve all subscriptions
* Find a subscription by podcast ID
* Check whether a podcast is subscribed

Subscriptions are therefore stored locally without requiring a remote account.

---

## 🧪 Testing

The following functionality has been tested during development.

### Search Testing

* Application launches successfully
* Search with a valid topic
* Search with an empty field
* Search with a topic returning results
* Search with a topic returning no results
* Minimum title-word filter
* Loading indicator
* API error handling

### Podcast Testing

* Podcast artwork loads
* Podcast title displays
* Podcast creator displays
* Podcast can be selected
* Details screen opens
* Back button works
* Subscribe works
* Unsubscribe works
* Open Podcast works

### Subscription Testing

* Podcast can be subscribed to
* My Subscriptions opens
* Saved podcast appears
* Saved podcast can be selected
* Podcast details can be reopened
* Subscription state is stored using Room

### RSS Testing

* RSS feed can be requested
* RSS episodes are parsed
* Episode title is displayed
* Episode description is displayed
* Publication date is displayed
* Episode media URL is detected
* AUDIO episodes are identified
* VIDEO episodes are identified

### Playback Testing

* Episode player opens
* Podcast episode media can be selected
* Media3 playback is integrated
* HLS media support is included

### Background Processing

* WorkManager dependencies are configured
* Podcast update scheduler is implemented
* Podcast update worker is implemented
* Connected-network constraint is used
* New episode detection is implemented
* Notification handling is implemented

---

## 🎯 Assignment 8 Requirements Demonstrated

This project demonstrates the following Android development concepts:

* Android Studio project development
* Kotlin programming
* XML layouts
* Activity navigation
* REST API integration
* Retrofit
* Gson
* RecyclerView
* Glide image loading
* Kotlin Coroutines
* Lifecycle-aware asynchronous operations
* Custom filtering
* User input validation
* Error handling
* Loading states
* RSS feed networking
* XML RSS parsing
* RSS namespace handling
* Audio/video media detection
* Media3 ExoPlayer
* HLS playback support
* Room database
* Room DAO
* Local data persistence
* WorkManager
* Background processing
* Android notifications
* Notification channels
* Android 13+ notification permission
* Podcast subscriptions
* Episode discovery and playback

---

## 👩‍💻 Developer

**Fozia Akhtar**

Android Development Student

Project:

**AndroidApp4 — SuperPodcast**

---

## 📌 Project Status

**Status: Assignment 8 Development Complete — Final Verification Pending**

The SuperPodcast application currently includes:

* Podcast search
* iTunes API integration
* Custom search filtering
* RecyclerView podcast results
* Glide artwork loading
* Podcast details
* Subscribe / unsubscribe
* Room subscription persistence
* My Subscriptions
* RSS episode loading
* RSS namespace support
* AUDIO / VIDEO detection
* Media3 episode playback
* WorkManager background update checking
* New episode notifications

Final verification of the complete Assignment 8 workflow, final README update, and final Git branch synchronization should be completed before submission.

---

## 🚀 How to Run

1. Clone the repository.
2. Open the project in Android Studio.
3. Allow Gradle to synchronize.
4. Connect an Android device or start an emulator.
5. Build the project.
6. Run the application.
7. Search for a podcast topic.

Example:

```text
technology
```

Then optionally enter a minimum title-word value and select:

**Search Podcasts**

Select a podcast to view its details and available RSS episodes.

---

## 🏁 Conclusion

SuperPodcast provides a complete podcast discovery and playback experience while demonstrating important Android development concepts.

The project combines the iTunes Search API with RSS podcast feeds, local Room persistence, Media3 playback, WorkManager background processing, and Android notifications.

The application demonstrates how multiple Android technologies can work together to create a functional podcast application with search, subscriptions, episode discovery, media playback, and background updates.
