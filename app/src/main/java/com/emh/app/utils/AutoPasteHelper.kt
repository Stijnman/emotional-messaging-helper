package com.emh.app.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * Best-effort helper for getting text into WhatsApp.
 *
 * True auto-paste into WhatsApp is difficult because WhatsApp actively fights automation.
 * Current best approaches:
 * 1. Copy to clipboard (reliable) + user pastes
 * 2. AccessibilityService simulation (fragile, often detected)
 */
object AutoPasteHelper {

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("EMH Suggested Reply", text)
        clipboard.setPrimaryClip(clip)
    }

    /**
     * Note: Real direct paste logic lives in WhatsAppAccessibilityService.pasteTextIntoWhatsApp
     * (uses ACTION_SET_TEXT + focus + send heuristics). This helper is kept for compatibility
     * and explicit clipboard path with clear user messaging.
     */
    fun attemptDirectPaste(
        context: Context,
        text: String,
        accessibilityService: android.accessibilityservice.AccessibilityService?
    ): Boolean {
        // Delegate note: prefer service for direct, fallback here
        copyToClipboard(context, text)
        return false // Indicates we fell back to clipboard (service handles the success case)
    }

    // AUTONOMOUS IMPROVEMENT (testing phase): Tracked last method for diagnostics and tests.
    private var lastMethod = "clipboard_fallback"
    fun getLastPasteMethod(): String = lastMethod
}