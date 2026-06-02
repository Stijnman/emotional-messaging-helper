package com.emh.app.ui

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.emh.app.EMHApplication
import com.emh.app.ai.EmotionalPromptEngine
import com.emh.app.ai.OllamaClient
import com.emh.app.ui.templates.TemplateGallery
import com.emh.app.utils.AutoPasteHelper
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun EmotionalPanel(
    contactKey: String,
    originalMessage: String,
    onClose: () -> Unit,
    onSendToWhatsApp: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val app = context.applicationContext as EMHApplication

    var figurativeLevel by remember { mutableIntStateOf(6) }
    var selectedTone by remember { mutableStateOf("Warm & Supportive") }

    var suggestedReply by remember { mutableStateOf("") }
    var emotionalInsight by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var visionAttached by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }
    var lastUsedVision by remember { mutableStateOf(false) }

    // Auto-clear vision when contact changes (smart behavior)
    var lastContact by remember { mutableStateOf(contactKey) }
    LaunchedEffect(contactKey) {
        if (contactKey != lastContact) {
            com.emh.app.vision.ScreenCaptureService.lastScreenshotBase64 = null
            visionAttached = false
            lastContact = contactKey
        }
    }

    val scope = rememberCoroutineScope()
    val ollama = remember { OllamaClient() }
    val promptEngine = remember { EmotionalPromptEngine(app.memoryManager) }
    val haptic = LocalHapticFeedback.current

    // Load settings on composition
    LaunchedEffect(Unit) {
        app.settingsRepository.ollamaUrl.collect { url ->
            ollama.updateBaseUrl(url)
        }
    }
    val currentModel by app.settingsRepository.defaultModel.collectAsState(initial = "llama3.2")

    // Register for history restore requests
    LaunchedEffect(Unit) {
        com.emh.app.ui.PanelState.onRestoreRequest = { entry ->
            suggestedReply = entry.suggestedReply
            emotionalInsight = entry.emotionalInsight ?: ""
        }
    }

    // Watch for vision screenshot being captured
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(800)
            visionAttached = com.emh.app.vision.ScreenCaptureService.lastScreenshotBase64 != null
        }
    }

    // AUTONOMOUS IMPROVEMENT (Loop 1,3,...): Added continuous vision state polling for better UX reliability.

    DisposableEffect(Unit) {
        onDispose {
            com.emh.app.ui.PanelState.onRestoreRequest = null
        }
    }

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { tts?.shutdown() }
    }

    Column(
        modifier = Modifier
            .width(340.dp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = contactKey,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = originalMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        // Figurative Level Slider
        Text("Emotional Intensity: $figurativeLevel / 10", style = MaterialTheme.typography.labelMedium)
        Slider(
            value = figurativeLevel.toFloat(),
            onValueChange = { figurativeLevel = it.toInt() },
            valueRange = 0f..10f,
            steps = 9
        )

        // Tone Presets
        Text("Tone Preset", style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Warm", "Playful", "Deep", "Direct", "Vulnerable").forEach { tone ->
                FilterChip(
                    selected = selectedTone == tone,
                    onClick = { selectedTone = tone },
                    label = { Text(tone) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (visionAttached) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Text(
                            "📸 Vision screenshot attached",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = {
                            com.emh.app.vision.ScreenCaptureService.lastScreenshotBase64 = null
                            visionAttached = false
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }) {
                            Text("Clear", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    Text(
                        "Next generation will include visual context from the screenshot (compressed for faster analysis).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    // Small improvement: Allow recapturing without leaving the panel
                    TextButton(
                        onClick = {
                            // Trigger MainActivity to request new capture
                            val intent = Intent(context, com.emh.app.MainActivity::class.java).apply {
                                action = "REQUEST_SCREEN_CAPTURE"
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.align(androidx.compose.ui.Alignment.End)
                    ) {
                        Text("Recapture")
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    isLoading = true
                    val willUseVision = visionAttached || lastUsedVision
                    statusMessage = when {
                        willUseVision && !OllamaClient.isLikelyVisionModel(currentModel) -> "Auto-switching to vision model + analyzing screenshot..."
                        willUseVision -> "Analyzing screenshot with vision model..."
                        else -> "Crafting emotionally intelligent reply..."
                    }
                    scope.launch {
                        val imageBase64 = com.emh.app.vision.ScreenCaptureService.lastScreenshotBase64

                        val prompt = promptEngine.buildPrompt(
                            contactKey = contactKey,
                            originalMessage = originalMessage,
                            visionDescription = if (imageBase64 != null) "A screenshot of the conversation was captured for additional visual context." else null,
                            figurativeLevel = figurativeLevel,
                            tonePreset = selectedTone
                        )

                        lastUsedVision = imageBase64 != null

                        val effectiveModel = if (imageBase64 != null && !OllamaClient.isLikelyVisionModel(currentModel)) {
                            OllamaClient.suggestVisionModelIfNeeded(currentModel)
                        } else {
                            currentModel
                        }

                        val result = if (imageBase64 != null) {
                            ollama.generateWithImages(effectiveModel, prompt, listOf(imageBase64))
                        } else {
                            ollama.generate(currentModel, prompt)
                        }

                        result.onSuccess { raw ->
                            val parsed = promptEngine.parseResponse(raw)
                            suggestedReply = parsed.suggestedReply
                            emotionalInsight = parsed.emotionalInsight
                            statusMessage = ""
                            // Clear the used screenshot
                            com.emh.app.vision.ScreenCaptureService.lastScreenshotBase64 = null
                            visionAttached = false
                        }.onFailure { error ->
                            emotionalInsight = when {
                                error.message?.contains("timeout", ignoreCase = true) == true ->
                                    "Ollama took too long. Try a faster model or check your connection."
                                else -> "Could not reach Ollama at ${ollama.currentBaseUrl}. Check Settings or start Ollama."
                            }
                            suggestedReply = ""
                            statusMessage = ""
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                Text(if (isLoading) statusMessage else "Generate")
            }

            OutlinedButton(
                onClick = {
                    // Request screen capture permission (handled by MainActivity or a dedicated activity)
                    val intent = Intent(context, com.emh.app.MainActivity::class.java).apply {
                        action = "REQUEST_SCREEN_CAPTURE"
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Add Vision")
            }
        }

        if (emotionalInsight.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            Row {
                Text("Emotional Insight", style = MaterialTheme.typography.labelSmall)
                if (lastUsedVision) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "• with vision",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(emotionalInsight, style = MaterialTheme.typography.bodyMedium)
        }

        if (suggestedReply.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text("Suggested Reply", style = MaterialTheme.typography.labelSmall)
            Card {
                Text(
                    text = suggestedReply,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("EMH Reply", suggestedReply))
                }) {
                    Text("Copy")
                }

                OutlinedButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    tts?.speak(suggestedReply, TextToSpeech.QUEUE_FLUSH, null, "EMH")
                }) {
                    Text("Speak")
                }

                // FINISHING: Explicit manual "Paste Reply" button to satisfy auto-paste reliability issues (#2, #6).
                // Triggers accessibility direct paste (best effort) + always ensures clipboard fallback.
                Button(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    AutoPasteHelper.copyToClipboard(context, suggestedReply)
                    onSendToWhatsApp(suggestedReply)
                }) {
                    Text("Paste Reply")
                }
            }

            // Extra manual trigger row for when direct paste is flaky (addresses GitHub issues)
            if (suggestedReply.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        AutoPasteHelper.copyToClipboard(context, suggestedReply)
                        // Force the callback again for users who want to retry paste
                        onSendToWhatsApp(suggestedReply)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Try Direct Paste Again (or use clipboard)")
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        TemplateGallery(onTemplateSelected = { template ->
            suggestedReply = template
            emotionalInsight = "Using a quick template for a sincere response."
        })

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    scope.launch {
                        val available = ollama.isAvailable()
                        statusMessage = if (available) "Ollama connected ✓" else "Ollama not reachable"
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Check Ollama")
            }

            OutlinedButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClose()
            }, modifier = Modifier.weight(1f)) {
                Text("Close")
            }
        }
    }
}