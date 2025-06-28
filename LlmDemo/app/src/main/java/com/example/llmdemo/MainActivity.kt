package com.example.llmdemo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInference.LlmInferenceOptions


class MainActivity : AppCompatActivity() {
    private lateinit var llm: LlmInference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Initialize the LLM Inference task
        val options = LlmInferenceOptions.builder()
            .setModelPath("/data/local/tmp/llm/model.task")  // path where adb pushed it
            .setMaxTopK(64)
            .setMaxTokens(512)
            .build()
        llm = LlmInference.createFromOptions(this, options)


        val promptEditText = findViewById<EditText>(R.id.promptEditText)
        val runButton = findViewById<Button>(R.id.runButton)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)

        runButton.setOnClickListener {
            val prompt = promptEditText.text.toString().trim()
            if (prompt.isNotEmpty()) {
                val result = llm.generateResponse(prompt)
                resultTextView.text = result

                // 2. Hook up the button to run inference
                runButton.setOnClickListener {
                    val prompt = promptEditText.text.toString().trim()
                    if (prompt.isNotEmpty()) {
                        // Synchronous call (blocks UI thread briefly on small models)
                        val result = llm.generateResponse(prompt)
                        resultTextView.text = result
                    }
                }
            }
        }
    }}