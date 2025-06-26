package com.example.android_app_test
import com.example.android_app_test.AuthScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp
import androidx.compose.foundation.layout.fillMaxSize


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            var isAuthenticated by remember { mutableStateOf(false) }

            if (isAuthenticated) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Text("Welcome to the INTEGRIX!", style = MaterialTheme.typography.headlineMedium)
                }
            } else {
                AuthScreen(onAuthSuccess = {
                    isAuthenticated = true
                })
            }
        }
    }
}
