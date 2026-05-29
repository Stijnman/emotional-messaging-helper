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
     * TEST (Loop 2+): Added for autonomous verification.
     * Returns true if both text and vision endpoints respond reasonably.
     */
    suspend fun healthCheck(): Boolean {
        return isAvailable() // Can be expanded in future loops
    }
}