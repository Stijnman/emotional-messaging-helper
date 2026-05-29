package com.emh.app.vision

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build

/**
 * Manages the MediaProjection flow needed for real screenshot capture.
 * This is required for reliable vision analysis on modern Android.
 */
object ScreenCaptureManager {

    const val REQUEST_CODE_SCREEN_CAPTURE = 4242

    /**
     * Preferred modern method: Use this to get the intent, then launch it with an ActivityResultLauncher.
     */
    fun createScreenCaptureIntent(context: Context): Intent {
        val projectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        return projectionManager.createScreenCaptureIntent()
    }

    /**
     * Handle result from the modern ActivityResultLauncher.
     */
    fun handleScreenCaptureResult(
        resultCode: Int,
        data: Intent?,
        onSuccess: (resultCode: Int, data: Intent) -> Unit
    ) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            onSuccess(resultCode, data)
        }
    }

    // Kept only for backward compatibility during heavy autonomous development.
    @Deprecated("Use createScreenCaptureIntent + launcher instead", level = DeprecationLevel.WARNING)
    fun requestScreenCapture(activity: Activity) {
        val intent = createScreenCaptureIntent(activity)
        @Suppress("DEPRECATION")
        activity.startActivityForResult(intent, REQUEST_CODE_SCREEN_CAPTURE)
    }
}