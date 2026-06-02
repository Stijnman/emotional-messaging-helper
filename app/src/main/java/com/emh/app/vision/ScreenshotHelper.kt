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
     * Lower quality = faster vision analysis, smaller payload.
     */
    fun getRecommendedQuality(): Int = 70
}