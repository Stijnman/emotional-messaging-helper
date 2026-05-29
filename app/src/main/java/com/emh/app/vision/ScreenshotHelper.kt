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

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val byteArray = outputStream.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
    }
}