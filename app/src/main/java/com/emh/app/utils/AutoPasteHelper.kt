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
     * Future: Implement real input via AccessibilityService using
     * performAction(AccessibilityNodeInfo.ACTION_SET_TEXT) on the WhatsApp input field.
     *
     * This requires finding the EditText node in WhatsApp's UI hierarchy, which changes frequently.
     */
    fun attemptDirectPaste(
        context: Context,
        text: String,
        accessibilityService: android.accessibilityservice.AccessibilityService?
    ): Boolean {
        // Placeholder - real implementation is complex and version-dependent
        copyToClipboard(context, text)
        return false // Indicates we fell back to clipboard
    }

    // AUTONOMOUS IMPROVEMENT (Odd loops): Added helper for future testing of paste reliability.
    fun getLastPasteMethod(): String = "clipboard_fallback" // Will be enhanced in later loops
}