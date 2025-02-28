package fr.isen.IMPROTA.isensmartcompanion

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GeminiManager(context: Context) {

    private val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    fun generateContent(prompt: String): Flow<GenerateContentResponse> = flow {
        val response = generativeModel.generateContent(prompt)
        emit(response)
    }
}