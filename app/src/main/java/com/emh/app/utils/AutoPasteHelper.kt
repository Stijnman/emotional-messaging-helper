package com.emh.app.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

/**
 * Best-effort helper for getting text into WhatsApp.
 *
 * True auto-paste into WhatsApp is difficult because WhatsApp actively fights automation.
 * Strategy (multi-layer fallback for maximum reliability):
 * 1. Direct accessibility paste via WhatsAppAccessibilityService (preferred when available)
 * 2. Clipboard + clear user instruction + haptic feedback
 * 3. Intent-based fallbacks where possible
 *
 * All paths provide user feedback (Toast + optional vibration).
 */
object AutoPasteHelper {

    private var lastMethodUsed: String = "none"

    fun getLastPasteMethod(): String = lastMethodUsed

    fun copyToClipboard(context: Context, text: String, showToast: Boolean = true) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("EMH Suggested Reply", text)
        clipboard.setPrimaryClip(clip)
        lastMethodUsed = "clipboard"

        if (showToast) {
            showFeedback(context, "Reply copied to clipboard. Long-press in WhatsApp → Paste.")
        }
        vibrate(context, success = true)
    }

    /**
     * Primary entry point for pasting.
     * Prefers direct accessibility if the service is available, otherwise falls back to clipboard with strong UX.
     */
    fun pasteOrCopy(
        context: Context,
        text: String,
        accessibilityService: android.accessibilityservice.AccessibilityService?
    ): Boolean {
        // Try direct paste first if service is connected and in WhatsApp
        if (accessibilityService != null) {
            try {
                // Delegate to the service's advanced paste logic (which has many heuristics)
                val service = accessibilityService as? com.emh.app.service.WhatsAppAccessibilityService
                if (service != null && service.pasteTextIntoWhatsApp(text)) {
                    lastMethodUsed = "direct_accessibility"
                    showFeedback(context, "Pasted directly into WhatsApp!")
                    vibrate(context, success = true)
                    return true
                }
            } catch (e: Exception) {
                android.util.Log.w("AutoPasteHelper", "Direct paste attempt failed: ${e.message}")
            }
        }

        // Reliable fallback: Clipboard + excellent user guidance
        copyToClipboard(context, text, showToast = true)
        showFeedback(
            context,
            "Copied! Open WhatsApp input field → long-press → Paste (or tap the paperclip icon)."
        )
        return false
    }

    /**
     * Enhanced direct paste attempt (used by service or overlay).
     * Kept for backward compatibility with existing calls.
     */
    fun attemptDirectPaste(
        context: Context,
        text: String,
        accessibilityService: android.accessibilityservice.AccessibilityService?
    ): Boolean {
        return pasteOrCopy(context, text, accessibilityService)
    }

    private fun showFeedback(context: Context, message: String) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            // In case we're not on main thread or context is restricted
            android.util.Log.i("AutoPasteHelper", "Feedback (no Toast): $message")
        }
    }

    private fun vibrate(context: Context, success: Boolean) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                manager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            val effect = if (success) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                } else {
                    @Suppress("DEPRECATION")
                    longArrayOf(0, 50)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    VibrationEffect.createWaveform(longArrayOf(0, 30, 30, 30), -1)
                } else {
                    @Suppress("DEPRECATION")
                    longArrayOf(0, 30, 30, 30)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(effect as VibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(effect as LongArray, -1)
            }
        } catch (e: Exception) {
            // Vibration is nice-to-have; ignore on devices without it or in restricted contexts
        }
    }

    // For debugging / tests
    fun resetLastMethod() {
        lastMethodUsed = "none"
    }
}
