package com.google.mediapipe.examples.llminference

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private var inferenceModel: InferenceModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(inferenceModel.uiState)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _textInputEnabled = MutableStateFlow(true)
    val isTextInputEnabled: StateFlow<Boolean> = _textInputEnabled.asStateFlow()

    /** Swap in a fresh session/model */
    fun resetInferenceModel(newModel: InferenceModel) {
        inferenceModel = newModel
        _uiState.value = inferenceModel.uiState
        _textInputEnabled.value = true
    }

    /** Send a message and stream back the model’s reply */
    fun sendMessage(userMessage: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1) Post user message + loading bubble on Main
            viewModelScope.launch(Dispatchers.Main) {
                _uiState.value.addMessage(userMessage, USER_PREFIX)
                _uiState.value.createLoadingMessage()
                _textInputEnabled.value = false
            }

            try {
                // 2) Fire off the async generation
                val future: ListenableFuture<String> =
                    inferenceModel.generateResponseAsync(userMessage) { chunk, done ->
                        viewModelScope.launch(Dispatchers.Main) {
                            _uiState.value.appendMessage(chunk)
                            if (done) {
                                _textInputEnabled.value = true
                            }
                        }
                    }

                // 3) When fully complete, nothing extra to do
                future.addListener({
                    /* no‑op */
                }, Dispatchers.Main.asExecutor())

            } catch (e: Exception) {
                // 4) If something goes wrong, show error
                viewModelScope.launch(Dispatchers.Main) {
                    _uiState.value.addMessage(
                        e.localizedMessage ?: "Unknown error",
                        MODEL_PREFIX
                    )
                    _textInputEnabled.value = true
                }
            }
        }
    }

    companion object {
        fun getFactory(context: Context) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val model = InferenceModel.getInstance(context)
                @Suppress("UNCHECKED_CAST")
                return ChatViewModel(model) as T
            }
        }
    }
}
