package com.emh.app.vision

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Service that performs actual screen capture using MediaProjection.
 * Started after user grants screen capture permission.
 */
class ScreenCaptureService : Service() {

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                "emh_capture",
                "EMH Vision Capture",
                android.app.NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resultCode = intent?.getIntExtra("resultCode", 0) ?: 0
        val data = intent?.getParcelableExtra<Intent>("data")

        if (resultCode == 0 || data == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        // Start as foreground service (required for mediaProjection on Android 14+)
        val notification = android.app.Notification.Builder(this, "emh_capture")
            .setContentTitle("EMH Vision Capture")
            .setContentText("Capturing screen for emotional analysis...")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .build()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            startForeground(4242, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        } else {
            startForeground(4242, notification)
        }

        val projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = projectionManager.getMediaProjection(resultCode, data)

        startCapture()

        return START_NOT_STICKY
    }

    private fun startCapture() {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.densityDpi

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "EMH_ScreenCapture",
            width, height, density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, null
        )

        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener

            scope.launch {
                val planes = image.planes
                val buffer = planes[0].buffer
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val rowPadding = rowStride - pixelStride * width

                val bitmap = Bitmap.createBitmap(
                    width + rowPadding / pixelStride,
                    height,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.copyPixelsFromBuffer(buffer)

                // Crop to actual screen size
                val cropped = Bitmap.createBitmap(bitmap, 0, 0, width, height)

                // Convert to base64 JPEG for Ollama vision (using improved helper)
                // Dynamic quality (Phase 3) + multi-frame via ring buffer (see getRecentVisionBase64).
                val quality = ScreenshotHelper.getRecommendedQuality() // can extend for onMobileData hint

                val base64Image = ScreenshotHelper.bitmapToBase64(cropped, quality)

                // Store for the floating panel to pick up (standardized name)
                lastScreenshotBase64 = base64Image

                // Multi-frame ring buffer (newest first). Enables sending several recent frames to vision model.
                synchronized(recentScreenshots) {
                    recentScreenshots.add(0, base64Image)
                    while (recentScreenshots.size > MAX_RECENT) {
                        recentScreenshots.removeAt(recentScreenshots.lastIndex)
                    }
                }

                android.util.Log.d("EMH", "Screenshot captured and ready for vision: ${cropped.width}x${cropped.height} (quality=$quality) recent=${recentScreenshots.size}")

                // Notify user
                android.widget.Toast.makeText(
                    this@ScreenCaptureService,
                    "Screenshot captured for EMH vision",
                    android.widget.Toast.LENGTH_SHORT
                ).show()

                // AUTONOMOUS LOOP IMPROVEMENT: Added explicit success notification for better user feedback in vision flows.
                // Image is JPEG compressed in ScreenshotHelper for faster Ollama vision roundtrips.

                image.close()
                stopSelf()
            }
        }, null)
    }

    override fun onDestroy() {
        virtualDisplay?.release()
        imageReader?.close()
        mediaProjection?.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        /** Standardized holder for the last captured screenshot base64 (JPEG for Ollama vision).
         * Used by EmotionalPanel and others.
         */
        var lastScreenshotBase64: String? = null

        // Phase 3 multi-frame support: ring buffer of recent captures (newest first).
        // Allows EmotionalPanel to send 1-2 (or more) images to llava for richer visual context.
        private val recentScreenshots = mutableListOf<String>()
        private const val MAX_RECENT = 3

        /** Returns up to [max] most recent screenshot base64 strings (newest first). */
        @JvmStatic
        fun getRecentVisionBase64(max: Int = 2): List<String> {
            synchronized(recentScreenshots) {
                return recentScreenshots.take(max).toList()
            }
        }

        /** Clears both single and multi-frame buffers (used on contact switch / after use). */
        @JvmStatic
        fun clearVisionBuffers() {
            lastScreenshotBase64 = null
            synchronized(recentScreenshots) { recentScreenshots.clear() }
        }
    }
}