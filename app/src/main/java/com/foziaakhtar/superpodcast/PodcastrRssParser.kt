package com.foziaakhtar.superpodcast

import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.net.HttpURLConnection
import java.net.URL

// ============================================================
// PODCAST RSS PARSER
//
// PURPOSE:
// Downloads and reads a podcast RSS feed.
//
// RESPONSIBILITIES:
// 1. Connect to the RSS feed.
// 2. Read RSS XML using XmlPullParser.
// 3. Find podcast episode <item> elements.
// 4. Read episode information.
// 5. Read enclosure/media URLs.
// 6. Read audio/video media types.
// 7. Support media:content.
// 8. Support content:encoded descriptions.
// 9. Detect video media.
// 10. Return the latest 20 episodes.
//
// NETWORKING:
// Network work runs on Dispatchers.IO so the Android UI
// thread is never blocked.
// ============================================================

object PodcastRssParser {

    // ========================================================
    // RSS NAMESPACE CONSTANTS
    // ========================================================

    private const val CONTENT_NAMESPACE =
        "http://purl.org/rss/1.0/modules/content/"

    private const val MEDIA_NAMESPACE =
        "http://search.yahoo.com/mrss/"

    private const val ITUNES_NAMESPACE =
        "http://www.itunes.com/dtds/podcast-1.0.dtd"

    // ========================================================
    // PARSE RSS FEED
    //
    // Downloads the supplied RSS feed and converts its
    // episode items into Episode objects.
    // ========================================================

    suspend fun parseFeed(
        feedUrl: String
    ): List<Episode> = withContext(Dispatchers.IO) {

        // ----------------------------------------------------
        // Open the RSS feed connection.
        // ----------------------------------------------------

        val url = URL(feedUrl)

        val connection =
            url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"

        // ----------------------------------------------------
        // Connection timeout:
        // Maximum time to establish the connection.
        // ----------------------------------------------------

        connection.connectTimeout = 15_000

        // ----------------------------------------------------
        // Read timeout:
        // Maximum time waiting for RSS data.
        // ----------------------------------------------------

        connection.readTimeout = 15_000

        // ----------------------------------------------------
        // Some podcast servers expect a normal browser-style
        // User-Agent.
        // ----------------------------------------------------

        connection.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0"
        )

        try {

            // ------------------------------------------------
            // Check the HTTP response.
            // ------------------------------------------------

            val responseCode =
                connection.responseCode

            if (responseCode !in 200..299) {

                throw Exception(
                    "RSS feed returned HTTP $responseCode"
                )
            }

            // ------------------------------------------------
            // Create the XML Pull Parser.
            // ------------------------------------------------

            val parser =
                Xml.newPullParser()

            val episodes =
                mutableListOf<Episode>()

            connection.inputStream.use { inputStream ->

                parser.setInput(
                    inputStream,
                    null
                )

                // ------------------------------------------------
                // Indicates whether the parser is currently
                // inside an RSS <item>.
                // ------------------------------------------------

                var insideItem = false

                // ------------------------------------------------
                // Temporary values for the current episode.
                // ------------------------------------------------

                var guid = ""
                var title = ""
                var description = ""
                var pubDate = ""
                var mediaUrl = ""
                var mediaType = ""
                var imageUrl = ""

                // ------------------------------------------------
                // Read the complete RSS document.
                // ------------------------------------------------

                while (
                    parser.eventType !=
                    XmlPullParser.END_DOCUMENT
                ) {

                    when (parser.eventType) {

                        // ========================================
                        // START TAG
                        // ========================================

                        XmlPullParser.START_TAG -> {

                            val namespace =
                                parser.namespace

                            val tagName =
                                parser.name

                            // ------------------------------------
                            // Start of an RSS episode.
                            // Reset all episode values.
                            // ------------------------------------

                            if (
                                tagName.equals(
                                    "item",
                                    ignoreCase = true
                                )
                            ) {

                                insideItem = true

                                guid = ""
                                title = ""
                                description = ""
                                pubDate = ""
                                mediaUrl = ""
                                mediaType = ""
                                imageUrl = ""
                            }

                            // ------------------------------------
                            // Read fields only while inside
                            // an RSS <item>.
                            // ------------------------------------

                            else if (insideItem) {

                                // ====================================
                                // GUID
                                // ====================================

                                if (
                                    namespace.isNullOrEmpty() &&
                                    tagName.equals(
                                        "guid",
                                        ignoreCase = true
                                    )
                                ) {

                                    guid =
                                        parser.nextText().trim()
                                }

                                // ====================================
                                // TITLE
                                // ====================================

                                else if (
                                    namespace.isNullOrEmpty() &&
                                    tagName.equals(
                                        "title",
                                        ignoreCase = true
                                    )
                                ) {

                                    title =
                                        parser.nextText().trim()
                                }

                                // ====================================
                                // DESCRIPTION
                                // ====================================

                                else if (
                                    namespace.isNullOrEmpty() &&
                                    tagName.equals(
                                        "description",
                                        ignoreCase = true
                                    )
                                ) {

                                    description =
                                        parser.nextText().trim()
                                }

                                // ====================================
                                // CONTENT:ENCODED
                                //
                                // Some podcasts store the complete
                                // episode description here.
                                // ====================================

                                else if (
                                    namespace ==
                                    CONTENT_NAMESPACE &&
                                    tagName.equals(
                                        "encoded",
                                        ignoreCase = true
                                    )
                                ) {

                                    val encodedDescription =
                                        parser.nextText().trim()

                                    if (
                                        description.isBlank() &&
                                        encodedDescription.isNotBlank()
                                    ) {

                                        description =
                                            encodedDescription
                                    }
                                }

                                // ====================================
                                // PUBLICATION DATE
                                // ====================================

                                else if (
                                    namespace.isNullOrEmpty() &&
                                    tagName.equals(
                                        "pubDate",
                                        ignoreCase = true
                                    )
                                ) {

                                    pubDate =
                                        parser.nextText().trim()
                                }

                                // ====================================
                                // ENCLOSURE
                                //
                                // Standard podcast RSS feeds usually
                                // provide playable media here.
                                //
                                // Example:
                                //
                                // <enclosure
                                //     url="episode.mp4"
                                //     type="video/mp4" />
                                // ====================================

                                else if (
                                    namespace.isNullOrEmpty() &&
                                    tagName.equals(
                                        "enclosure",
                                        ignoreCase = true
                                    )
                                ) {

                                    val enclosureUrl =
                                        parser.getAttributeValue(
                                            null,
                                            "url"
                                        )?.trim() ?: ""

                                    val enclosureType =
                                        parser.getAttributeValue(
                                            null,
                                            "type"
                                        )?.trim() ?: ""

                                    if (
                                        enclosureUrl.isNotBlank()
                                    ) {

                                        mediaUrl =
                                            enclosureUrl
                                    }

                                    if (
                                        enclosureType.isNotBlank()
                                    ) {

                                        mediaType =
                                            enclosureType
                                    }
                                }

                                // ====================================
                                // MEDIA:CONTENT
                                //
                                // Supports Media RSS feeds.
                                // ====================================

                                else if (
                                    namespace ==
                                    MEDIA_NAMESPACE &&
                                    tagName.equals(
                                        "content",
                                        ignoreCase = true
                                    )
                                ) {

                                    val contentUrl =
                                        parser.getAttributeValue(
                                            null,
                                            "url"
                                        )?.trim() ?: ""

                                    val contentType =
                                        parser.getAttributeValue(
                                            null,
                                            "type"
                                        )?.trim() ?: ""

                                    if (
                                        mediaUrl.isBlank() &&
                                        contentUrl.isNotBlank()
                                    ) {

                                        mediaUrl =
                                            contentUrl
                                    }

                                    if (
                                        mediaType.isBlank() &&
                                        contentType.isNotBlank()
                                    ) {

                                        mediaType =
                                            contentType
                                    }
                                }

                                // ====================================
                                // ITUNES EPISODE IMAGE
                                // ====================================

                                else if (
                                    namespace ==
                                    ITUNES_NAMESPACE &&
                                    tagName.equals(
                                        "image",
                                        ignoreCase = true
                                    )
                                ) {

                                    val href =
                                        parser.getAttributeValue(
                                            null,
                                            "href"
                                        )?.trim() ?: ""

                                    if (
                                        href.isNotBlank()
                                    ) {

                                        imageUrl =
                                            href
                                    }
                                }

                                // ====================================
                                // GENERIC IMAGE
                                // ====================================

                                else if (
                                    tagName.equals(
                                        "image",
                                        ignoreCase = true
                                    )
                                ) {

                                    val href =
                                        parser.getAttributeValue(
                                            null,
                                            "href"
                                        )?.trim() ?: ""

                                    if (
                                        imageUrl.isBlank() &&
                                        href.isNotBlank()
                                    ) {

                                        imageUrl =
                                            href
                                    }
                                }
                            }
                        }

                        // ========================================
                        // END TAG
                        // ========================================

                        XmlPullParser.END_TAG -> {

                            val tagName =
                                parser.name

                            // ------------------------------------
                            // Finished reading one RSS episode.
                            // ------------------------------------

                            if (
                                tagName.equals(
                                    "item",
                                    ignoreCase = true
                                )
                            ) {

                                if (
                                    insideItem &&
                                    mediaUrl.isNotBlank()
                                ) {

                                    // --------------------------------
                                    // GUID FALLBACK
                                    //
                                    // If no GUID exists, use the
                                    // media URL as the identifier.
                                    // --------------------------------

                                    val episodeGuid =
                                        if (
                                            guid.isNotBlank()
                                        ) {
                                            guid
                                        } else {
                                            mediaUrl
                                        }

                                    // --------------------------------
                                    // TITLE FALLBACK
                                    // --------------------------------

                                    val episodeTitle =
                                        if (
                                            title.isNotBlank()
                                        ) {
                                            title
                                        } else {
                                            "Untitled Episode"
                                        }

                                    // --------------------------------
                                    // Determine whether the episode
                                    // is video when the RSS feed did
                                    // not provide a MIME type.
                                    // --------------------------------

                                    val detectedMediaType =
                                        if (
                                            mediaType.isNotBlank()
                                        ) {
                                            mediaType
                                        } else if (
                                            isVideoMedia(
                                                "",
                                                mediaUrl
                                            )
                                        ) {
                                            "video/*"
                                        } else {
                                            "audio/*"
                                        }

                                    // --------------------------------
                                    // Create the Episode object.
                                    // --------------------------------

                                    episodes.add(
                                        Episode(
                                            guid = episodeGuid,
                                            title = episodeTitle,
                                            description = description,
                                            pubDate = pubDate,
                                            audioUrl = mediaUrl,
                                            mediaType = detectedMediaType,
                                            imageUrl = imageUrl
                                        )
                                    )
                                }

                                insideItem = false
                            }
                        }
                    }

                    // ------------------------------------------------
                    // Move to the next XML event.
                    // ------------------------------------------------

                    parser.next()
                }
            }

            // ----------------------------------------------------
            // RSS feeds normally list newest episodes first.
            // Keep the latest 20 episodes.
            // ----------------------------------------------------

            episodes.take(20)

        } finally {

            // ----------------------------------------------------
            // Always close the network connection.
            // ----------------------------------------------------

            connection.disconnect()
        }
    }

    // ============================================================
    // DETERMINE PODCAST TYPE
    //
    // Returns:
    // Audio
    // Video
    // Audio / Video
    // Unknown
    // ============================================================

    suspend fun determinePodcastType(
        feedUrl: String
    ): String {

        return try {

            val episodes =
                parseFeed(feedUrl)

            if (episodes.isEmpty()) {
                return "Unknown"
            }

            val hasVideo =
                episodes.any {
                    isVideoMedia(
                        it.mediaType,
                        it.audioUrl
                    )
                }

            val hasAudio =
                episodes.any {
                    !isVideoMedia(
                        it.mediaType,
                        it.audioUrl
                    )
                }

            when {

                hasVideo && hasAudio ->
                    "Audio / Video"

                hasVideo ->
                    "Video"

                hasAudio ->
                    "Audio"

                else ->
                    "Unknown"
            }

        } catch (
            exception: Exception
        ) {

            // ----------------------------------------------------
            // Feed errors should not crash the application.
            // ----------------------------------------------------

            "Unknown"
        }
    }

    // ============================================================
    // VIDEO MEDIA DETECTION
    //
    // Checks the RSS MIME type first.
    //
    // If MIME type is unavailable, checks the file extension.
    //
    // Supported video extensions:
    // .mp4
    // .m4v
    // .mov
    // .webm
    // .mkv
    // ============================================================

    fun isVideoMedia(
        mediaType: String,
        mediaUrl: String
    ): Boolean {

        val normalizedType =
            mediaType.trim().lowercase()

        // --------------------------------------------------------
        // MIME type tells us directly that this is video.
        // --------------------------------------------------------

        if (
            normalizedType.startsWith(
                "video/"
            )
        ) {
            return true
        }

        // --------------------------------------------------------
        // Explicit audio MIME type means this is not video.
        // --------------------------------------------------------

        if (
            normalizedType.startsWith(
                "audio/"
            )
        ) {
            return false
        }

        // --------------------------------------------------------
        // Remove URL query parameters before checking extension.
        //
        // Example:
        // episode.mp4?download=true
        // becomes:
        // episode.mp4
        // --------------------------------------------------------

        val cleanUrl =
            mediaUrl
                .substringBefore("?")
                .lowercase()

        return cleanUrl.endsWith(".mp4") ||
                cleanUrl.endsWith(".m4v") ||
                cleanUrl.endsWith(".mov") ||
                cleanUrl.endsWith(".webm") ||
                cleanUrl.endsWith(".mkv")
    }
}

