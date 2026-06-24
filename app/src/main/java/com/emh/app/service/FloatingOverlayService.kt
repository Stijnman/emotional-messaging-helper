package com.emh.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.setViewTreeLifecycleOwner
import com.emh.app.R
import com.emh.app.ui.EmotionalPanel

/**
 * Floating emotional assistant panel that appears over WhatsApp.
 */
class FloatingOverlayService : LifecycleService() {

    private lateinit var windowManager: WindowManager
    private var floatingView: ComposeView? = null
    private var windowLayoutParams: WindowManager.LayoutParams? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val contact = intent?.getStringExtra(EXTRA_CONTACT) ?: "Contact"
        val message = intent?.getStringExtra(EXTRA_MESSAGE) ?: ""

        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(
                this,
                getString(R.string.overlay_permission_needed),
                Toast.LENGTH_LONG
            ).show()
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID, buildNotification())
        showFloatingPanel(contact, message)
        return START_NOT_STICKY
    }

    private fun showFloatingPanel(contactKey: String, originalMessage: String) {
        floatingView?.let {
            windowManager.removeView(it)
            floatingView = null
        }

        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FloatingOverlayService)
            setContent {
                EmotionalPanel(
                    contactKey = contactKey,
                    originalMessage = originalMessage,
                    onClose = { stopSelf() },
                    onVoiceListeningChanged = { listening ->
                        windowLayoutParams?.let { wmParams ->
                            if (listening) {
                                wmParams.flags = wmParams.flags and
                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
                            } else {
                                wmParams.flags = wmParams.flags or
                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            }
                            floatingView?.let { view ->
                                windowManager.updateViewLayout(view, wmParams)
                            }
                        }
                    },
                    onSendToWhatsApp = { text ->
                        val service = WhatsAppAccessibilityService.instance
                        val pasted = service?.pasteTextIntoWhatsApp(text) == true
                        if (pasted) {
                            Toast.makeText(
                                this@FloatingOverlayService,
                                "Sent to WhatsApp",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            copyToClipboard(text)
                            Toast.makeText(
                                this@FloatingOverlayService,
                                "Copied to clipboard (paste manually)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 16
            y = 120
        }

        floatingView = composeView
        windowLayoutParams = params
        try {
            windowManager.addView(composeView, params)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not show overlay: ${e.message}", Toast.LENGTH_LONG).show()
            stopSelf()
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("EMH Reply", text))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "EMH Overlay",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when the emotional assistant panel is active"
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Floating assistant active")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        floatingView?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {
            }
            floatingView = null
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = super.onBind(intent)

    companion object {
        const val EXTRA_CONTACT = "extra_contact"
        const val EXTRA_MESSAGE = "extra_message"
        private const val CHANNEL_ID = "emh_overlay"
        private const val NOTIFICATION_ID = 1001

        fun showForMessage(context: Context, contactKey: String, message: String) {
            if (!Settings.canDrawOverlays(context)) {
                Toast.makeText(
                    context,
                    context.getString(R.string.overlay_permission_needed),
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            val intent = Intent(context, FloatingOverlayService::class.java).apply {
                putExtra(EXTRA_CONTACT, contactKey)
                putExtra(EXTRA_MESSAGE, message)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}