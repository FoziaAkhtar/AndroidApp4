🎙️ SuperPodcast — AndroidApp4

## 📱 Project Overview

**SuperPodcast** is an Android podcast discovery application built with **Kotlin** and **Android Studio**.

The application allows users to search for podcasts using the **iTunes Search API**, browse podcast results, view detailed podcast information, subscribe to podcasts, and manage their personal subscription list.

The project was developed as part of the **AndroidApp4 assignment** and demonstrates Android networking, RecyclerView, image loading, coroutines, API integration, activity navigation, and user-controlled filtering.

---

## ✨ Features

### 🔎 Podcast Search

Users can enter a topic or keyword and search for podcasts using the iTunes Search API.

Examples:

* Technology
* Business
* Education
* Health
* Comedy
* News
* Science

---

### 🎯 Custom Search Filter

The application includes a custom filtering feature:

**Minimum Title Words**

Users can enter the minimum number of words that must appear in a podcast title.

For example:

```text
Minimum title words: 3
```

Only podcasts with titles containing at least three words will be displayed.

This provides an additional user-controlled search criterion beyond the basic API search.

---

### 🖼️ Podcast Artwork

Podcast artwork is downloaded and displayed using **Glide**.

The RecyclerView displays:

* Podcast artwork
* Podcast title
* Podcast creator
* Subscribe button

---

### 📄 Podcast Details

Selecting a podcast opens a dedicated details screen.

The details screen provides:

* Podcast artwork
* Podcast title
* Podcast creator
* Subscribe option
* Open Podcast option
* Back navigation

---

### ⭐ Subscriptions

Users can subscribe to podcasts directly from the search results or podcast details screen.

Subscriptions are stored locally using:

```text
SharedPreferences
```

The application includes a dedicated:

**My Subscriptions**

screen where saved podcasts can be viewed and opened again.

---

### 🌐 Open Podcast

Users can open the podcast's available web/feed URL from the details screen.

The application uses the device's available browser/application to open the podcast.

---

### ⏳ Loading Indicator

A progress indicator is displayed while podcast search results are being retrieved.

The search button is temporarily disabled during the API request to prevent duplicate searches.

---

### ⚠️ Error and Empty Result Handling

The application handles several situations safely:

* Empty search
* No matching podcasts
* Internet/API errors
* Missing podcast information
* Missing podcast URLs

Users receive appropriate messages instead of the application crashing.

---

## 🛠️ Technologies Used

| Technology                 | Purpose                        |
| -------------------------- | ------------------------------ |
| Kotlin                     | Main programming language      |
| Android Studio             | Development environment        |
| Retrofit                   | API networking                 |
| Gson                       | JSON parsing                   |
| iTunes Search API          | Podcast data                   |
| Glide                      | Podcast artwork loading        |
| RecyclerView               | Podcast result lists           |
| Coroutines                 | Asynchronous API operations    |
| Lifecycle / lifecycleScope | Coroutine lifecycle management |
| SharedPreferences          | Local subscription storage     |
| Android Activities         | Screen navigation              |
| XML Layouts                | User interface                 |

---

## 🔌 API

SuperPodcast uses the **iTunes Search API** to retrieve podcast information.

The application sends a search request based on the user's entered topic and receives podcast information including:

* Podcast ID
* Podcast title
* Artist/creator
* Artwork
* Feed URL
* Collection URL

The API response is converted into Kotlin data models using Gson.

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
│           │               ├── PodcastDetailsActivity.kt
│           │               └── SubscriptionsActivity.kt
│           │
│           ├── res/
│           │   │
│           │   └── layout/
│           │       │
│           │       ├── activity_main.xml
│           │       ├── activity_podcast_details.xml
│           │       ├── activity_subscriptions.xml
│           │       └── item_podcast.xml
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
* Advanced minimum-title-word filter
* Search button
* My Subscriptions button
* Podcast results

The interface was designed with a clean layout and clear visual hierarchy.

---

### Podcast Results

Search results are displayed using a RecyclerView.

Each result includes:

```text
Podcast Artwork
Podcast Title
Podcast Creator
Subscribe
```

Selecting the podcast opens its details.

---

### Podcast Details

The details screen allows the user to:

* View podcast information
* Subscribe
* Open the podcast
* Return to search results

---

### My Subscriptions

The subscriptions screen displays podcasts saved locally by the user.

A subscribed podcast can be selected to return to its details screen.

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
                    ┌──────────┴──────────┐
                    ▼                     ▼
          ┌─────────────────┐   ┌────────────────────┐
          │ Podcast Details │   │     Subscribe      │
          └────────┬────────┘   └─────────┬──────────┘
                   │                      │
                   ▼                      ▼
          ┌─────────────────┐   ┌────────────────────┐
          │ Open Podcast    │   │ My Subscriptions   │
          └─────────────────┘   └────────────────────┘
```

---

## 💾 Local Data Storage

Podcast subscriptions are stored locally using Android `SharedPreferences`.

The application uses the preference collection:

```text
SuperPodcastSubscriptions
```

Each saved podcast is stored locally so that subscriptions remain available when the user returns to the application.

---

## 🧪 Testing

The following functionality was tested during development:

### Search Testing

* [x] Application launches successfully
* [x] Search with a valid topic
* [x] Search with an empty field
* [x] Search with a topic returning results
* [x] Search with a topic returning no results
* [x] Minimum title-word filter
* [x] Loading indicator
* [x] API error handling

### Podcast Testing

* [x] Podcast artwork loads
* [x] Podcast title displays
* [x] Podcast creator displays
* [x] Podcast can be selected
* [x] Details screen opens
* [x] Back button works
* [x] Subscribe works
* [x] Open Podcast works

### Subscription Testing

* [x] Podcast can be subscribed to
* [x] My Subscriptions opens
* [x] Saved podcast appears
* [x] Saved podcast can be selected
* [x] Podcast details can be reopened

---

## 🎯 Assignment Requirements Demonstrated

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
* SharedPreferences
* User input validation
* Custom filtering
* Error handling
* Loading states
* Local data persistence

---

## 👩‍💻 Developer

**Fozia Akhtar**

Android Development Student

Project:

**AndroidApp4 — SuperPodcast**

---

## 📌 Project Status

**Status: Complete**

The SuperPodcast application includes the required podcast search functionality, API integration, RecyclerView results, image loading, podcast details, subscriptions, custom filtering, and polished user interface.

The final project has been committed and pushed to GitHub on the `main` branch.

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

---

## 🏁 Conclusion

SuperPodcast provides a simple and user-friendly way to discover podcasts while demonstrating important Android development concepts including API communication, asynchronous programming, RecyclerView-based interfaces, image loading, navigation, local persistence, and custom search filtering.

````

### One important thing

Your **README change is not currently pushed** because we haven't added this new README content to Git yet.

After you paste/save it, run:

```bash
git add README.md
git commit -m "Update AndroidApp4 README"
git push origin main
````

Then:

```bash
git status
```

The `.idea` files can remain untracked. **Do not use `git add .`**, because we don't need those files.
