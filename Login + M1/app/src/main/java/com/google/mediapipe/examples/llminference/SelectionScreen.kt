package com.google.mediapipe.examples.llminference


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.mediapipe.examples.llminference.ui.theme.LLMInferenceTheme
import androidx.compose.foundation.lazy.itemsIndexed


@Composable
internal fun SelectionRoute(
    userName: String,
    onModelSelected: () -> Unit = {},
    onLogout:        () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Welcome header
        Text(
            text = "Welcome, $userName!",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Only take the first two models, label them M1 and M2
        val models = Model.entries.take(2)
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.Center,
            modifier              = Modifier.fillMaxSize()
        ) {
            itemsIndexed(models) { index, model ->
                Button(
                    onClick = {
                        InferenceModel.model = model
                        onModelSelected()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = if (index == 0) "M1" else "M2")
                }
            }
        }
    }
}
