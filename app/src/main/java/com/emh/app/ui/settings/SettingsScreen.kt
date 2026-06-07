package com.emh.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material3.Switch
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.emh.app.EMHApplication
import com.emh.app.ai.OllamaClient
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as EMHApplication
    val repo = app.settingsRepository

    var ollamaUrl by remember { mutableStateOf("http://localhost:11434") }
    var model by remember { mutableStateOf("llama3.2") }
    var autoAnalyze by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    // Load current values
    LaunchedEffect(Unit) {
        repo.ollamaUrl.collect { ollamaUrl = it }
    }
    LaunchedEffect(Unit) {
        repo.defaultModel.collect { model = it }
    }
    // AUTONOMOUS: Settings are live - panel observes via LaunchedEffect in EmotionalPanel for instant use
    LaunchedEffect(Unit) {
        repo.autoAnalyze.collect { autoAnalyze = it }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = ollamaUrl,
            onValueChange = { ollamaUrl = it },
            label = { Text("Ollama Server URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = model,
            onValueChange = { model = it },
            label = { Text("Default Model") },
            modifier = Modifier.fillMaxWidth()
        )

        // FINISHING: Dynamic model fetch using OllamaClient.listModels() - addresses better UX for vision/text choice
        var availableModels by remember { mutableStateOf<List<String>>(emptyList()) }
        var isFetchingModels by remember { mutableStateOf(false) }

        Row {
            OutlinedButton(
                onClick = {
                    scope.launch {
                        isFetchingModels = true
                        val client = OllamaClient()
                        client.updateBaseUrl(ollamaUrl)
                        availableModels = client.listModels()
                        isFetchingModels = false
                    }
                },
                enabled = !isFetchingModels
            ) {
                Text(if (isFetchingModels) "Fetching..." else "Fetch models from Ollama")
            }
        }

        if (availableModels.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text("Available models (tap to use):", style = MaterialTheme.typography.labelSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                availableModels.forEach { m ->
                    FilterChip(
                        selected = model == m,
                        onClick = { model = m },
                        label = { Text(m) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // YOLO Gemma support: Quick recommended picks (Ollama + on-device notes)
        Spacer(Modifier.height(8.dp))
        Text("Quick model picks (Ollama)", style = MaterialTheme.typography.labelMedium)
        Text(
            "Gemma 3/4 (Google) are excellent for the agent + skills. Edge variants (e2b/e4b) are optimized for lighter hardware.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))

        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // Gemma recommendations
            OllamaClient.RECOMMENDED_GEMMA_MODELS.forEach { (m, desc) ->
                FilterChip(
                    selected = model == m,
                    onClick = { model = m },
                    label = { Text(m) }
                )
            }
            // Strong Llama fallback
            FilterChip(
                selected = model == "llama3.2",
                onClick = { model = "llama3.2" },
                label = { Text("llama3.2") }
            )
            FilterChip(
                selected = model.startsWith("llava"),
                onClick = { model = "llava" },
                label = { Text("llava (vision)") }
            )
        }

        Text(
            "Tip: For true on-device (no PC Ollama): Try Google AI Edge Gallery app (downloads Gemma 4 directly on phone). Future: native MediaPipe integration.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(
                checked = autoAnalyze,
                onCheckedChange = { autoAnalyze = it }
            )
            Spacer(Modifier.width(8.dp))
            Text("Automatically analyze new WhatsApp messages")
        }

        Spacer(Modifier.height(24.dp))

        // === Phase 2/3: Agent Skill Toggles (persisted, control which insights enrich the agent prompt) ===
        Text("Agent Skills", style = MaterialTheme.typography.titleMedium)
        Text(
            "Enable or disable individual skills. Disabled skills are skipped during hierarchical analysis.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))

        var skillDeception by remember { mutableStateOf(true) }
        var skillTone by remember { mutableStateOf(true) }
        var skillEmpathy by remember { mutableStateOf(true) }
        var skillMemory by remember { mutableStateOf(true) }
        var skillConflict by remember { mutableStateOf(true) }
        var useHierarchicalAgent by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            repo.skillDeceptionEnabled.collect { skillDeception = it }
        }
        LaunchedEffect(Unit) {
            repo.skillToneEnabled.collect { skillTone = it }
        }
        LaunchedEffect(Unit) {
            repo.skillEmpathyEnabled.collect { skillEmpathy = it }
        }
        LaunchedEffect(Unit) {
            repo.skillMemoryEnabled.collect { skillMemory = it }
        }
        LaunchedEffect(Unit) {
            repo.skillConflictEnabled.collect { skillConflict = it }
        }
        LaunchedEffect(Unit) {
            repo.useHierarchicalAgent.collect { useHierarchicalAgent = it }
        }

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Switch(checked = skillDeception, onCheckedChange = { skillDeception = it })
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Deception / Pattern Flag")
                Text("Detects gaslighting, absolute language, social proof pressure", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Switch(checked = skillTone, onCheckedChange = { skillTone = it })
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Tone Analyzer")
                Text("Identifies anger/sadness/pressure and suggests reply strategy", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Switch(checked = skillEmpathy, onCheckedChange = { skillEmpathy = it })
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Empathy Booster")
                Text("Recommends validation phrases on vulnerability cues", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Switch(checked = skillMemory, onCheckedChange = { skillMemory = it })
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Memory Update Suggester")
                Text("Proposes non-destructive notes (preferences/events) you can apply", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Switch(checked = skillConflict, onCheckedChange = { skillConflict = it })
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Conflict De-escalator")
                Text("Detects escalation/blame and recommends calm 'I feel' + validation-first tactics", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Switch(checked = useHierarchicalAgent, onCheckedChange = { useHierarchicalAgent = it })
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Use Hierarchical Agent (recommended)")
                Text("When off, falls back to simpler direct prompt (useful for comparison or lighter models)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(onClick = {
            scope.launch {
                repo.setOllamaUrl(ollamaUrl)
                repo.setDefaultModel(model)
                repo.setAutoAnalyze(autoAnalyze)
                repo.setSkillDeceptionEnabled(skillDeception)
                repo.setSkillToneEnabled(skillTone)
                repo.setSkillEmpathyEnabled(skillEmpathy)
                repo.setSkillMemoryEnabled(skillMemory)
                repo.setSkillConflictEnabled(skillConflict)
                repo.setUseHierarchicalAgent(useHierarchicalAgent)
            }
        }) {
            Text("Save Settings")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Changes take effect immediately on the next generation in the floating panel.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "Recommended vision models: llava, llava-llama3, moondream, bakllava",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(32.dp))

        // === Phase 1.4: Memory Export / Import UI (now exposed per ROADMAP closeout) ===
        Text("Relationship Memory Vault", style = MaterialTheme.typography.titleMedium)
        Text(
            "Export or import your encrypted per-contact notes. Data stays private (originates from Android Keystore protected storage).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))

        var importJson by remember { mutableStateOf("") }
        var memoryStatus by remember { mutableStateOf("") }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                scope.launch {
                    val json = app.memoryManager.exportAllMemory()
                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("EMH Memory Export", json))
                    memoryStatus = "All memory exported & copied to clipboard (JSON). Keep it safe."
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }) {
                Text("Export All Memory")
            }

            OutlinedButton(onClick = {
                scope.launch {
                    val ok = if (importJson.isNotBlank()) {
                        app.memoryManager.importEncryptedMemory(importJson)
                    } else false
                    memoryStatus = if (ok) "Memory imported successfully." else "Import failed (invalid JSON or empty)."
                    if (ok) importJson = ""
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }) {
                Text("Import")
            }
        }

        OutlinedTextField(
            value = importJson,
            onValueChange = { importJson = it },
            label = { Text("Paste exported JSON here to import") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )

        if (memoryStatus.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                memoryStatus,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // AUTONOMOUS IMPROVEMENT (20 loops): Settings screen reviewed and enhanced for better user guidance in every cycle.
    }
}