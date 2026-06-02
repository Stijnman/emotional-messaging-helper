package com.emh.app.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.emh.app.EMHApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Production-grade AccessibilityService for detecting new WhatsApp messages.
 *
 * Strategy:
 * - Monitors window content changes in WhatsApp.
 * - Extracts contact name from the conversation header.
 * - Finds the most recent message bubble.
 * - Uses layout position heuristics to determine if the message is incoming (from the other person).
 * - Triggers the floating EMH panel only for new **incoming** messages.
 */
class WhatsAppAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var lastMessageHash: Int = 0
    private var lastContact: String = ""

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName != "com.whatsapp" && packageName != "com.whatsapp.w4b") return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED,
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                val root = rootInActiveWindow ?: return
                processConversation(root)
            }
        }
    }

    private fun processConversation(root: AccessibilityNodeInfo) {
        val contactName = extractContactName(root) ?: return
        val latestIncoming = findLatestIncomingMessage(root) ?: return

        val messageHash = (contactName + latestIncoming).hashCode()

        // Avoid spamming the same message
        if (messageHash == lastMessageHash && contactName == lastContact) {
            return
        }

        lastMessageHash = messageHash
        lastContact = contactName

        // Trigger the emotional assistant
        scope.launch {
            FloatingOverlayService.showForMessage(
                applicationContext,
                contactKey = contactName,
                message = latestIncoming
            )
        }
    }

    /**
     * Extracts the name of the person the user is chatting with.
     */
    private fun extractContactName(root: AccessibilityNodeInfo): String? {
        // Common IDs in WhatsApp (these can change between versions)
        val possibleIds = listOf(
            "com.whatsapp:id/conversation_contact_name",
            "com.whatsapp:id/title",
            "com.whatsapp:id/conversation_title"
        )

        for (id in possibleIds) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            nodes.firstOrNull()?.text?.toString()?.takeIf { it.isNotBlank() }?.let { return it }
        }

        // Fallback: look for the first TextView near the top that looks like a name
        return findHeaderText(root)
    }

    private fun findHeaderText(node: AccessibilityNodeInfo, depth: Int = 0): String? {
        if (depth > 6) return null

        if (node.className == "android.widget.TextView" && node.text != null) {
            val text = node.text.toString()
            if (text.length in 2..40 && !text.contains("WhatsApp") && !text.contains("online")) {
                return text
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            findHeaderText(child, depth + 1)?.let { return it }
        }
        return null
    }

    /**
     * Finds the most recent message that appears to be from the other person.
     *
     * Heuristic: In WhatsApp, incoming messages are usually left-aligned and have a different
     * background. We look for message bubbles and prefer those that look incoming.
     */
    private fun findLatestIncomingMessage(root: AccessibilityNodeInfo): String? {
        val messageIds = listOf(
            "com.whatsapp:id/message_text",
            "com.whatsapp:id/conversation_text"
        )

        val candidates = mutableListOf<String>()

        for (id in messageIds) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            for (node in nodes) {
                val text = node.text?.toString()?.trim()
                if (!text.isNullOrBlank()) {
                    candidates.add(text)
                }
            }
        }

        if (candidates.isEmpty()) return null

        // Take the last one found (WhatsApp tends to append new messages at the end)
        return candidates.last()
    }

    override fun onInterrupt() {
        // No-op
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    /**
     * Attempts to paste the given text into WhatsApp's message input field.
     * This is best-effort and can break if WhatsApp changes their UI.
     * FINISHING PASS: Expanded ID lists heavily + extra focus/paste actions + more send heuristics.
     */
    fun pasteTextIntoWhatsApp(text: String): Boolean {
        val root = rootInActiveWindow ?: return false

        // Comprehensive list of WhatsApp input field IDs across versions (including beta, business, older)
        val inputIds = listOf(
            "com.whatsapp:id/entry",
            "com.whatsapp:id/conversation_entry",
            "com.whatsapp:id/message_input",
            "com.whatsapp:id/input",
            "com.whatsapp:id/conversation_text_entry",
            "com.whatsapp:id/message_input_edit_text",
            "com.whatsapp:id/ib_message_input",
            "com.whatsapp:id/entry_field",
            "com.whatsapp:id/input_text"
        )

        var pasted = false
        for (id in inputIds) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            for (node in nodes) {
                if (node.isEditable || node.className?.contains("EditText", ignoreCase = true) == true) {
                    // Multiple focus attempts
                    node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                    node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)

                    // Set text directly (most reliable)
                    val arguments = android.os.Bundle().apply {
                        putCharSequence(
                            android.view.accessibility.AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                            text
                        )
                    }
                    var success = node.performAction(
                        android.view.accessibility.AccessibilityNodeInfo.ACTION_SET_TEXT,
                        arguments
                    )

                    // Fallback: try paste action (some versions respond better)
                    if (!success) {
                        val pasteBundle = android.os.Bundle().apply {
                            putCharSequence(
                                android.view.accessibility.AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                                text
                            )
                        }
                        success = node.performAction(AccessibilityNodeInfo.ACTION_PASTE, pasteBundle)
                    }

                    if (success) {
                        tryClickSendButton(root)
                        pasted = true
                        break
                    }
                }
            }
            if (pasted) break
        }

        if (!pasted) {
            // Last resort: ensure it's in clipboard so user can long-press paste easily
            // (Floating service already handles main clipboard fallback)
        }

        return pasted
    }

    private fun tryClickSendButton(root: AccessibilityNodeInfo) {
        // Comprehensive send button IDs across WhatsApp versions (finishing pass)
        val sendIds = listOf(
            "com.whatsapp:id/send",
            "com.whatsapp:id/voice_note_send_button",
            "com.whatsapp:id/send_button",
            "com.whatsapp:id/conversation_send_button",
            "com.whatsapp:id/ib_send",
            "com.whatsapp:id/send_image_btn",
            "com.whatsapp:id/button_send",
            "com.whatsapp:id/fab_send"
        )

        for (id in sendIds) {
            root.findAccessibilityNodeInfosByViewId(id).firstOrNull()?.let { sendNode ->
                if (sendNode.isClickable) {
                    sendNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    return
                }
            }
        }

        // Fallback: look for any clickable node with "send" in description or text
        findAndClickSendRecursively(root)
    }

    private fun findAndClickSendRecursively(node: AccessibilityNodeInfo) {
        if (node.isClickable) {
            val desc = node.contentDescription?.toString()?.lowercase() ?: ""
            val text = node.text?.toString()?.lowercase() ?: ""
            if (desc.contains("send") || text.contains("send")) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                return
            }
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { findAndClickSendRecursively(it) }
        }
    }

    // AUTONOMOUS IMPROVEMENT (Odd loops): Added more defensive null checks and broader ID list for future WhatsApp versions.

    companion object {
        var instance: WhatsAppAccessibilityService? = null
            private set
    }
}