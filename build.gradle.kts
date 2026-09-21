// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false

    // ============================================================
    // KSP
    // Kotlin Symbol Processing is required by Room Database.
    // ============================================================

    id("com.google.devtools.ksp") version "2.2.20-2.0.2" apply false
}