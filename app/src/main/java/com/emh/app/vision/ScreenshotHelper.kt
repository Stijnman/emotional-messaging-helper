package com.emh.app.vision

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.WindowManager
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * Helper for preparing screenshots for vision models (Llava etc).
 *
 * NOTE: The actual screen capture is performed by ScreenCaptureService using
 * MediaProjection (started via MainActivity + ScreenCaptureManager).
 * The result is stored in ScreenCaptureService.lastScreenshotBase64 and
 * consumed by EmotionalPanel.
 *
 * This object now only contains the JPEG base64 conversion helpers used
 * by the real capture path.
 */
object ScreenshotHelper {

    /**
     * @deprecated Real capture uses MediaProjection in ScreenCaptureService.
     * This always returned null and is kept only for backward compatibility.
     */
    @Deprecated("Use ScreenCaptureService + lastScreenshotBase64 instead", ReplaceWith("null"))
    suspend fun captureScreen(context: Context): ImageBitmap? = withContext(Dispatchers.IO) {
        null
    }

    fun bitmapToBase64(bitmap: Bitmap, quality: Int = 75): String {
        val outputStream = ByteArrayOutputStream()
        // AUTONOMOUS IMPROVEMENT: Made quality configurable (default 75 for good balance)
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(40, 90), outputStream)
        val byteArray = outputStream.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
    }

    /**
     * Returns recommended quality level based on device/network conditions.
     * Lower quality on mobile data for smaller payloads and faster uploads to local Ollama.
     */
    fun getRecommendedQuality(onMobileData: Boolean = false): Int {
        return if (onMobileData) 55 else 70
    }

    /**
     * Best-effort detection of mobile data (Phase 3 hardening).
     * Callers should pass the result to getRecommendedQuality.
     * Requires no special permissions beyond normal network state.
     */
    fun isLikelyOnMobileData(context: android.content.Context): Boolean {
        return try {
            val cm = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
            val network = cm?.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) &&
                !caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Multi-frame helper (Phase 3 implemented). Used by callers that have raw Bitmaps.
     * The active path (ScreenCaptureService ring buffer + panel) works with already-encoded base64.
     */
    fun bitmapsToBase64Multi(bitmaps: List<Bitmap>, quality: Int = 70): List<String> {
        return bitmaps.map { bitmapToBase64(it, quality) }
    }
}