package com.emh.app.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import com.emh.app.ui.EmotionalPanel

/**
 * Floating emotional assistant panel that appears over WhatsApp.
 * Now powered by Jetpack Compose.
 */
class FloatingOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var floatingView: ComposeView? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val contact = intent?.getStringExtra(EXTRA_CONTACT) ?: "Contact"
        val message = intent?.getStringExtra(EXTRA_MESSAGE) ?: ""

        showFloatingPanel(contact, message)
        return START_NOT_STICKY
    }

    private fun showFloatingPanel(contactKey: String, originalMessage: String) {
        floatingView?.let {
            windowManager.removeView(it)
            floatingView = null
        }

        val composeView = ComposeView(this).apply {
            setContent {
                EmotionalPanel(
                    contactKey = contactKey,
                    originalMessage = originalMessage,
                    onClose = { stopSelf() },
                    onSendToWhatsApp = { text ->
                        val service = com.emh.app.service.WhatsAppAccessibilityService.instance
                        val pasted = service?.pasteTextIntoWhatsApp(text) == true
                        if (pasted) {
                            android.widget.Toast.makeText(this@FloatingOverlayService, "Sent to WhatsApp", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            copyToClipboard(text)
                            android.widget.Toast.makeText(this@FloatingOverlayService, "Copied to clipboard (paste manually)", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        // AUTONOMOUS IMPROVEMENT (Loop 1+): Added explicit feedback for better user experience in all paste paths.
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
            x = 30
            y = 100
        }

        floatingView = composeView
        windowManager.addView(composeView, params)
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("EMH Reply", text))
    }

    override fun onDestroy() {
        floatingView?.let {
            windowManager.removeView(it)
            floatingView = null
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val EXTRA_CONTACT = "extra_contact"
        const val EXTRA_MESSAGE = "extra_message"

        fun showForMessage(context: Context, contactKey: String, message: String) {
            val intent = Intent(context, FloatingOverlayService::class.java).apply {
                putExtra(EXTRA_CONTACT, contactKey)
                putExtra(EXTRA_MESSAGE, message)
            }
            context.startService(intent)
        }
    }
}