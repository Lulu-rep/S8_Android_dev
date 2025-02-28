package fr.isen.repplinger.isensmartcompanion.models

import android.annotation.SuppressLint
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import fr.isen.repplinger.isensmartcompanion.BuildConfig

@SuppressLint("SuspiciousIndentation")
suspend fun askGeminiAi(question: String): String {
    return try {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.API_KEY
        )
        Log.i("Generative Model", generativeModel.apiKey)
        val response = generativeModel.generateContent(question)
        val answer = response.text.toString()

        answer
    } catch (e: Exception) {
        Log.e("MainPage", "Error asking Gemini AI: ${e.message}")
        "Error: ${e.message}"
    }
}