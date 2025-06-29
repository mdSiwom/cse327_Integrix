package com.example.gemma3ndemo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.InputStream
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInference.LlmInferenceOptions
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession
import com.google.mediapipe.tasks.genai.llminference.GraphOptions


class MainActivity : AppCompatActivity() {

    // Declare variables for UI components
    private lateinit var promptEditText: EditText
    private lateinit var runButton: Button
    private lateinit var selectImageButton: Button  // Button to trigger gallery selection
    private lateinit var resultTextView: TextView
    private lateinit var inputImageView: ImageView  // ImageView to display the selected image
    private lateinit var llmInference: LlmInference

    private val IMAGE_REQUEST_CODE = 1001  // Request code for selecting an image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        promptEditText = findViewById(R.id.promptEditText)
        runButton = findViewById(R.id.runButton)
        selectImageButton = findViewById(R.id.selectImageButton)  // Initialize the button
        resultTextView = findViewById(R.id.resultTextView)
        inputImageView = findViewById(R.id.inputImageView)  // Initialize the ImageView

        // Initialize LLM Inference task
        initializeLLMInference()

        // Set up click listener for the generate button
        runButton.setOnClickListener {
            val prompt = promptEditText.text.toString()

            // If the input prompt is not empty, generate a response
            if (prompt.isNotEmpty()) {
                val bitmap = getBitmapFromImageView()
                if (bitmap != null) {
                    generateResponse(prompt, bitmap)
                } else {
                    resultTextView.text = "Please select an image."
                }
            } else {
                resultTextView.text = "Please enter a prompt."
            }
        }

        // Set up click listener for the select image button
        selectImageButton.setOnClickListener {
            openGallery()
        }
    }

    // Initialize LLM Inference task
    private fun initializeLLMInference() {
        // Set configuration options for the LLM Inference task
        val taskOptions = LlmInference.LlmInferenceOptions.builder()
            .setModelPath("/data/local/tmp/llm/gemma-3n-E2B-it-int4.task")  // Path to your model
            .setMaxTopK(64)
            .build()

        // Create an instance of the LLM Inference task
        llmInference = LlmInference.createFromOptions(applicationContext, taskOptions)

        // Log to confirm successful initialization
        Log.i("LLM Inference", "LLM Inference task initialized successfully.")
    }

    // Open the gallery to select an image (using Storage Access Framework)
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    // Handle the result from the gallery selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_REQUEST_CODE) {
            data?.data?.let { uri ->
                // Get the image URI and convert it to a Bitmap
                val bitmap = getBitmapFromUri(uri)
                inputImageView.setImageBitmap(bitmap)  // Display the selected image
            }
        }
    }

    // Get the Bitmap from the selected image URI
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Get the Bitmap from the ImageView
    private fun getBitmapFromImageView(): Bitmap? {
        val drawable = inputImageView.drawable
        return if (drawable != null && drawable is android.graphics.drawable.BitmapDrawable) {
            drawable.bitmap
        } else {
            null
        }
    }

    // Generate a response based on the user input prompt and image
    private fun generateResponse(inputPrompt: String, image: Bitmap) {
        // Convert the Bitmap image to MPImage format
        val mpImage = BitmapImageBuilder(image).build()

        // Start a new session for the LLM Inference task with vision support enabled
        val sessionOptions = LlmInferenceSession.LlmInferenceSessionOptions.builder()
            .setTopK(10)
            .setTemperature(0.4f)
            .setGraphOptions(GraphOptions.builder().setEnableVisionModality(true).build())  // Enable vision modality
            .build()

        val session = LlmInferenceSession.createFromOptions(llmInference, sessionOptions)

        // Add text (prompt) to the session
        session.addQueryChunk(inputPrompt)

        // Add the image to the session
        session.addImage(mpImage)

        // Generate the response asynchronously
        val result = session.generateResponse()
        resultTextView.text = result // Display the result in the TextView
    }
}
