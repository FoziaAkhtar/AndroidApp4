plugins {
    alias(libs.plugins.android.application)
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
    // TESTING
    // ============================================================

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}