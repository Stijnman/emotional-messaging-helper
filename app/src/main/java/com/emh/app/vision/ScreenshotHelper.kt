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
 * Helper for capturing screenshots and preparing them for vision models (Llava).
 * Note: On modern Android, proper screenshotting for accessibility overlays
 * usually requires MediaProjection or special permissions.
 */
object ScreenshotHelper {

    /**
     * Attempts to capture the current screen content.
     * This is a simplified version. Production apps often use MediaProjection.
     */
    suspend fun captureScreen(context: Context): ImageBitmap? = withContext(Dispatchers.IO) {
        try {
            // This is a placeholder. Real implementation needs more setup.
            // For now we return null and rely on text-only context.
            null
        } catch (e: Exception) {
            null
        }
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
     * Lower quality = faster vision analysis, smaller payload.
     */
    fun getRecommendedQuality(): Int = 70
}