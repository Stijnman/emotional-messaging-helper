package com.emh.app.ai

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * Client for communicating with local Ollama instance.
 * Supports both text and vision (Llava-style) models.
 */
class OllamaClient(
    private var baseUrl: String = "http://localhost:11434",
    private val timeoutSeconds: Long = 180
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    data class GenerateRequest(
        val model: String,
        val prompt: String,
        val stream: Boolean = false,
        val options: Map<String, Any>? = null,
        val images: List<String>? = null   // base64 encoded images for vision models
    )

    data class GenerateResponse(
        val model: String,
        val created_at: String,
        val response: String,
        val done: Boolean
    )

    fun updateBaseUrl(newUrl: String) {
        baseUrl = newUrl.trimEnd('/')
    }

    val currentBaseUrl: String get() = baseUrl

    /**
     * Text-only generation.
     */
    suspend fun generate(
        model: String,
        prompt: String,
        temperature: Float = 0.7f
    ): Result<String> = generateInternal(model, prompt, temperature, images = null)

    /**
     * Vision generation. Pass base64 encoded JPEG/PNG images.
     */
    suspend fun generateWithImages(
        model: String,
        prompt: String,
        imagesBase64: List<String>,
        temperature: Float = 0.7f
    ): Result<String> = generateInternal(model, prompt, temperature, imagesBase64)

    private suspend fun generateInternal(
        model: String,
        prompt: String,
        temperature: Float,
        images: List<String>?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val requestBody = gson.toJson(
                GenerateRequest(
                    model = model,
                    prompt = prompt,
                    stream = false,
                    options = mapOf("temperature" to temperature),
                    images = images
                )
            ).toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url("$baseUrl/api/generate")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string()
                    return@withContext Result.failure(Exception("Ollama error ${response.code}: $errorBody"))
                }

                val body = response.body?.string() ?: ""
                val generateResponse = gson.fromJson(body, GenerateResponse::class.java)
                Result.success(generateResponse.response.trim())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/api/tags")
                .get()
                .build()
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * AUTONOMOUS IMPROVEMENT (testing phase): More thorough health check.
     * Pings /api/tags and verifies basic responsiveness. Useful for panel "Check Ollama".
     */
    suspend fun healthCheck(): Boolean = withContext(Dispatchers.IO) {
        try {
            val tagsRequest = Request.Builder()
                .url("$baseUrl/api/tags")
                .get()
                .build()
            client.newCall(tagsRequest).execute().use { resp ->
                if (!resp.isSuccessful) return@withContext false
                // Optional: could parse models list but for health just success is enough
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Lists available models from Ollama (for settings UI and auto-suggest).
     */
    suspend fun listModels(): List<String> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/api/tags")
                .get()
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val body = response.body?.string() ?: return@withContext emptyList()
                // Minimal parse: look for model names
                val names = mutableListOf<String>()
                val regex = """"name"\s*:\s*"([^"]+)"""".toRegex()
                regex.findAll(body).forEach { m ->
                    names.add(m.groupValues[1])
                }
                names
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Recommended vision models for EMH.
     * These work well for screenshot analysis.
     */
    companion object {
        val RECOMMENDED_VISION_MODELS = listOf(
            "llava",
            "llava-llama3",
            "moondream",
            "bakllava"
        )

        /**
         * Simple heuristic to suggest if a model name looks like a vision model.
         * Used for better UX and warnings.
         */
        fun isLikelyVisionModel(model: String): Boolean {
            val lower = model.lowercase()
            return lower.contains("llava") || lower.contains("vision") || lower.contains("moondream")
        }

        /**
         * AUTONOMOUS IMPROVEMENT: Returns a recommended vision model if the current one doesn't look like one.
         */
        fun suggestVisionModelIfNeeded(currentModel: String): String {
            return if (isLikelyVisionModel(currentModel)) currentModel else "llava"
        }
    }
}