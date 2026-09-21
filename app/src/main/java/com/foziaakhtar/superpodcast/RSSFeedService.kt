package com.foziaakhtar.superpodcast

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

// ============================================================
// RSS FEED SERVICE
//
// Downloads and reads a podcast RSS feed.
//
// Responsibilities:
// 1. Connect to the podcast RSS feed.
// 2. Read the XML document.
// 3. Find podcast episode items.
// 4. Extract episode information.
// 5. Return a list of Episode objects.
//
// The service runs network work on the IO dispatcher so
// the Android main UI thread is not blocked.
// ============================================================

object RSSFeedService {

    // ========================================================
    // LOAD EPISODES
    //
    // Downloads the RSS feed and converts its episode
    // information into Episode objects.
    // ========================================================

    suspend fun loadEpisodes(
        feedUrl: String
    ): List<Episode> = withContext(
        Dispatchers.IO
    ) {

        // ====================================================
        // CREATE URL CONNECTION
        // ====================================================

        val url =
            URL(feedUrl)

        val connection =
            url.openConnection() as HttpURLConnection

        // ====================================================
        // CONNECTION SETTINGS
        // ====================================================

        connection.requestMethod =
            "GET"

        connection.connectTimeout =
            15_000

        connection.readTimeout =
            15_000

        connection.setRequestProperty(
            "User-Agent",
            "SuperPodcast Android App"
        )

        try {

            // ====================================================
            // CHECK SERVER RESPONSE
            // ====================================================

            val responseCode =
                connection.responseCode

            if (
                responseCode !in 200..299
            ) {

                throw Exception(
                    "RSS feed returned HTTP $responseCode"
                )
            }

            // ====================================================
            // CREATE XML DOCUMENT
            // ====================================================

            val documentBuilder =
                DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()

            val document =
                connection.inputStream.use { inputStream ->

                    documentBuilder.parse(
                        inputStream
                    )
                }

            // ====================================================
            // NORMALIZE XML DOCUMENT
            // ====================================================

            document.documentElement.normalize()

            // ====================================================
            // FIND EPISODE ITEMS
            //
            // Standard podcast RSS feeds use <item>
            // elements for individual episodes.
            // ====================================================

            val itemNodes =
                document.getElementsByTagName(
                    "item"
                )

            val episodes =
                mutableListOf<Episode>()

            // ====================================================
            // READ EACH EPISODE
            // ====================================================

            for (
            index in 0 until itemNodes.length
            ) {

                val item =
                    itemNodes.item(
                        index
                    )

                if (
                    item !is Element
                ) {
                    continue
                }

                // ==================================================
                // EPISODE TITLE
                // ==================================================

                val title =
                    getElementText(
                        item,
                        "title"
                    )

                // ==================================================
                // EPISODE DESCRIPTION
                //
                // Some feeds use description while others may
                // provide content through a different namespace.
                // We use description as the standard RSS value.
                // ==================================================

                val description =
                    getElementText(
                        item,
                        "description"
                    )

                // ==================================================
                // PUBLICATION DATE
                // ==================================================

                val pubDate =
                    getElementText(
                        item,
                        "pubDate"
                    )

                // ==================================================
                // AUDIO URL
                //
                // Podcast audio is normally stored inside an
                // <enclosure> element.
                // ==================================================

                val audioUrl =
                    getAudioUrl(
                        item
                    )

                // ==================================================
                // EPISODE IMAGE
                //
                // Try the iTunes image first.
                // ==================================================

                val imageUrl =
                    getEpisodeImage(
                        item
                    )

                // ==================================================
                // ONLY ADD VALID EPISODES
                //
                // An RSS item without a title is not useful
                // as an episode in the application.
                // ==================================================

                if (
                    title.isNotBlank()
                ) {

                    episodes.add(
                        Episode(
                            title = title,
                            description = description,
                            pubDate = pubDate,
                            audioUrl = audioUrl,
                            imageUrl = imageUrl
                        )
                    )
                }
            }

            // ====================================================
            // RETURN EPISODES
            //
            // Limit the result to the latest 20 episodes so
            // the Details screen remains manageable.
            // ====================================================

            episodes
                .take(20)

        } finally {

            // ====================================================
            // CLOSE CONNECTION
            // ====================================================

            connection.disconnect()
        }
    }

    // ============================================================
    // GET ELEMENT TEXT
    //
    // Finds a child XML element and returns its text.
    // ============================================================

    private fun getElementText(
        parent: Element,
        tagName: String
    ): String {

        val nodes =
            parent.getElementsByTagName(
                tagName
            )

        if (
            nodes.length == 0
        ) {

            return ""
        }

        return nodes
            .item(0)
            ?.textContent
            ?.trim()
            ?: ""
    }

    // ============================================================
    // GET AUDIO URL
    //
    // Podcast audio is normally stored as:
    //
    // <enclosure url="..." />
    //
    // We read the URL attribute from that element.
    // ============================================================

    private fun getAudioUrl(
        item: Element
    ): String {

        val enclosureNodes =
            item.getElementsByTagName(
                "enclosure"
            )

        if (
            enclosureNodes.length == 0
        ) {

            return ""
        }

        val enclosure =
            enclosureNodes.item(
                0
            )

        if (
            enclosure !is Element
        ) {

            return ""
        }

        return enclosure
            .getAttribute(
                "url"
            )
            .trim()
    }

    // ============================================================
    // GET EPISODE IMAGE
    //
    // Podcast RSS feeds may use an iTunes image element:
    //
    // <itunes:image href="..." />
    //
    // XML namespaces mean the tag may appear as
    // "itunes:image".
    // ============================================================

    private fun getEpisodeImage(
        item: Element
    ): String {

        // ========================================================
        // TRY STANDARD iTunes IMAGE TAG
        // ========================================================

        val imageNodes =
            item.getElementsByTagName(
                "itunes:image"
            )

        if (
            imageNodes.length > 0
        ) {

            val imageElement =
                imageNodes.item(
                    0
                )

            if (
                imageElement is Element
            ) {

                val href =
                    imageElement
                        .getAttribute(
                            "href"
                        )
                        .trim()

                if (
                    href.isNotBlank()
                ) {

                    return href
                }
            }
        }

        // ========================================================
        // TRY GENERIC IMAGE ELEMENT
        // ========================================================

        val genericImageNodes =
            item.getElementsByTagName(
                "image"
            )

        if (
            genericImageNodes.length > 0
        ) {

            val imageElement =
                genericImageNodes.item(
                    0
                )

            if (
                imageElement is Element
            ) {

                val href =
                    imageElement
                        .getAttribute(
                            "href"
                        )
                        .trim()

                if (
                    href.isNotBlank()
                ) {

                    return href
                }
            }
        }

        // ========================================================
        // NO EPISODE IMAGE
        // ========================================================

        return ""
    }
}