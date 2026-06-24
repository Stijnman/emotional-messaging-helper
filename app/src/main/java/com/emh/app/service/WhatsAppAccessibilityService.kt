package com.emh.app.service

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.emh.app.EMHApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Detects new incoming WhatsApp messages and triggers the floating EMH panel.
 */
class WhatsAppAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var lastMessageHash: Int = 0
    private var lastContact: String = ""
    private var lastTriggerTime: Long = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName != "com.whatsapp" && packageName != "com.whatsapp.w4b") return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED,
            AccessibilityEvent.TYPE_VIEW_SCROLLED,
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val root = rootInActiveWindow ?: return
                processConversation(root)
            }
        }
    }

    private fun processConversation(root: AccessibilityNodeInfo) {
        val contactName = extractContactName(root) ?: return
        val latestIncoming = findLatestIncomingMessage(root) ?: return

        val messageHash = (contactName + latestIncoming).hashCode()
        if (messageHash == lastMessageHash && contactName == lastContact) return

        val now = System.currentTimeMillis()
        if (now - lastTriggerTime < 1500) return

        lastMessageHash = messageHash
        lastContact = contactName
        lastTriggerTime = now

        val app = applicationContext as? EMHApplication ?: return
        scope.launch {
            if (!app.settingsRepository.isAutoAnalyzeEnabled()) return@launch
            FloatingOverlayService.showForMessage(
                applicationContext,
                contactKey = contactName,
                message = latestIncoming
            )
        }
    }

    private fun extractContactName(root: AccessibilityNodeInfo): String? {
        val possibleIds = listOf(
            "com.whatsapp:id/conversation_contact_name",
            "com.whatsapp:id/title",
            "com.whatsapp:id/conversation_title",
            "com.whatsapp:id/contact_name"
        )

        for (id in possibleIds) {
            root.findAccessibilityNodeInfosByViewId(id)
                .firstOrNull()
                ?.text
                ?.toString()
                ?.takeIf { it.isNotBlank() }
                ?.let { return it }
        }

        return findHeaderText(root)
    }

    private fun findHeaderText(node: AccessibilityNodeInfo, depth: Int = 0): String? {
        if (depth > 6) return null

        if (node.className == "android.widget.TextView" && node.text != null) {
            val text = node.text.toString()
            if (text.length in 2..40 &&
                !text.contains("WhatsApp", ignoreCase = true) &&
                !text.contains("online", ignoreCase = true) &&
                !text.contains("typing", ignoreCase = true)
            ) {
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
     * Prefer the latest message bubble on the left (incoming), skip outgoing (right / ticks).
     */
    private fun findLatestIncomingMessage(root: AccessibilityNodeInfo): String? {
        val screenWidth = resources.displayMetrics.widthPixels
        val midX = screenWidth / 2

        val messageIds = listOf(
            "com.whatsapp:id/message_text",
            "com.whatsapp:id/conversation_text",
            "com.whatsapp:id/text"
        )

        var bestText: String? = null
        var bestBottom = -1

        for (id in messageIds) {
            for (node in root.findAccessibilityNodeInfosByViewId(id)) {
                val text = node.text?.toString()?.trim().orEmpty()
                if (text.isBlank()) continue
                if (isOutgoingMessage(node)) continue

                val rect = Rect()
                node.getBoundsInScreen(rect)
                if (rect.isEmpty) continue

                val centerX = (rect.left + rect.right) / 2
                if (centerX > midX + screenWidth * 0.1) continue

                if (rect.bottom > bestBottom) {
                    bestBottom = rect.bottom
                    bestText = text
                }
            }
        }

        return bestText
    }

    private fun isOutgoingMessage(node: AccessibilityNodeInfo): Boolean {
        var current: AccessibilityNodeInfo? = node.parent
        var depth = 0
        while (current != null && depth < 10) {
            val tickIds = listOf(
                "com.whatsapp:id/status",
                "com.whatsapp:id/message_status",
                "com.whatsapp:id/timestamp_and_status",
                "com.whatsapp:id/status_layout"
            )
            for (id in tickIds) {
                if (current.findAccessibilityNodeInfosByViewId(id).isNotEmpty()) return true
            }
            current = current.parent
            depth++
        }
        return false
    }

    override fun onInterrupt() {}

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    fun pasteTextIntoWhatsApp(text: String): Boolean {
        val root = rootInActiveWindow ?: return false

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

        for (id in inputIds) {
            for (node in root.findAccessibilityNodeInfosByViewId(id)) {
                if (!node.isEditable &&
                    node.className?.contains("EditText", ignoreCase = true) != true
                ) continue

                node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                val arguments = android.os.Bundle().apply {
                    putCharSequence(
                        AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        text
                    )
                }
                var success = node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                if (!success) {
                    success = node.performAction(AccessibilityNodeInfo.ACTION_PASTE, arguments)
                }
                if (success) {
                    tryClickSendButton(root)
                    return true
                }
            }
        }
        return false
    }

    private fun tryClickSendButton(root: AccessibilityNodeInfo) {
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
        findAndClickSendRecursively(root)
    }

    private fun findAndClickSendRecursively(node: AccessibilityNodeInfo) {
        if (node.isClickable) {
            val desc = node.contentDescription?.toString()?.lowercase().orEmpty()
            val text = node.text?.toString()?.lowercase().orEmpty()
            if (desc.contains("send") || text.contains("send")) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                return
            }
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { findAndClickSendRecursively(it) }
        }
    }

    companion object {
        var instance: WhatsAppAccessibilityService? = null
            private set
    }
}