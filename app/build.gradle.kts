plugins {
    alias(libs.plugins.android.application)

    // ============================================================
    // KSP
    // Required by Room Database for code generation.
    // ============================================================

    id("com.google.devtools.ksp")
}

android {
    namespace = "com.foziaakhtar.superpodcast"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.foziaakhtar.superpodcast"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    // ============================================================
    // ANDROIDX / MATERIAL
    // ============================================================

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)


    // ============================================================
    // RECYCLERVIEW
    // Used to display the list of podcast search results.
    // ============================================================

    implementation("androidx.recyclerview:recyclerview:1.4.0")


    // ============================================================
    // LIFECYCLE / COROUTINES
    // lifecycleScope allows network requests to run safely
    // without blocking the Android user interface.
    // ============================================================

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.3")


    // ============================================================
    // RETROFIT
    // Retrofit communicates with the iTunes Podcast API.
    // ============================================================

    implementation("com.squareup.retrofit2:retrofit:3.0.0")

    // Gson converts the JSON response from iTunes into Kotlin objects.
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")


    // ============================================================
    // GLIDE
    // Glide loads podcast artwork images from the internet.
    // ============================================================

    implementation("com.github.bumptech.glide:glide:4.16.0")


    // ============================================================
    // GSON
    // Used for JSON data conversion.
    // ============================================================

    implementation("com.google.code.gson:gson:2.13.2")


    // ============================================================
    // ROOM DATABASE
    // Room will store podcast subscriptions locally.
    // ============================================================

    implementation("androidx.room:room-runtime:2.8.0")
    implementation("androidx.room:room-ktx:2.8.0")
    ksp("androidx.room:room-compiler:2.8.0")


    // ============================================================
    // WORKMANAGER
    // WorkManager will periodically check for new podcast episodes.
    // ============================================================

    implementation("androidx.work:work-runtime-ktx:2.10.5")


    // ============================================================
    // MEDIA3 / EXOPLAYER
    // Media3 will provide podcast audio playback.
    // ============================================================

    implementation("androidx.media3:media3-exoplayer:1.8.0")
    implementation("androidx.media3:media3-ui:1.8.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.8.0")


    // ============================================================
    // TESTING
    // ============================================================

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}