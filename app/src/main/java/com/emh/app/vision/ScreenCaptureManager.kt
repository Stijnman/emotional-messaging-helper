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

    fun requestScreenCapture(activity: Activity) {
        val projectionManager = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val intent = projectionManager.createScreenCaptureIntent()
        activity.startActivityForResult(intent, REQUEST_CODE_SCREEN_CAPTURE)
    }

    fun handleScreenCaptureResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        onSuccess: (resultCode: Int, data: Intent) -> Unit
    ) {
        if (requestCode == REQUEST_CODE_SCREEN_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            onSuccess(resultCode, data)
        }
    }
}