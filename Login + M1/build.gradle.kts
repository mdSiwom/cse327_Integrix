// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.11.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0" apply false // Match your Kotlin version

    // ← Add this line so you can apply 'com.google.gms.google-services' in your app module
    id("com.google.gms.google-services") version "4.4.3" apply false
}

