import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Google Services plugin for Firebase
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose")

}

android {
    namespace = "com.google.mediapipe.examples.llminference"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.google.mediapipe.examples.llminference"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // (For OAuth redirect URI, replace with your reversed client ID)
        manifestPlaceholders["appAuthRedirectScheme"] = ""

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Load Hugging Face access token from local.properties
        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { properties.load(it) }
        }
        val hfAccessToken = properties.getProperty("HF_ACCESS_TOKEN", "")
        buildConfigField("String", "HF_ACCESS_TOKEN", "\"$hfAccessToken\"")
    }

    buildFeatures {
        buildConfig = true
        compose     = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core & Lifecycle
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Compose & Navigation
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Material Design
    implementation("com.google.android.material:material:1.12.0")

    // Mediapipe GenAI
    implementation("com.google.mediapipe:tasks-genai:0.10.22")

    // Networking & OAuth / Security
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("net.openid:appauth:0.11.1")
    implementation("androidx.security:security-crypto:1.0.0")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")
    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
