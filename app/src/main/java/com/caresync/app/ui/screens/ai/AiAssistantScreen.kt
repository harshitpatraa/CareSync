package com.caresync.app.ui.screens.ai

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.caresync.app.components.AppLockGuard
import com.caresync.app.database.PatientEntity
import com.caresync.app.model.ChatMessage
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncButtonShape
import com.caresync.app.theme.CareSyncCyan
import com.caresync.app.theme.CriticalRed
import com.caresync.app.viewmodel.AiToolsUiState

@Composable
fun AiAssistantScreen(
    state: AiToolsUiState,
    patients: List<PatientEntity>,
    onSelectPatient: (String?) -> Unit,
    onSendMessage: (String) -> Unit,
    onVoiceError: (String) -> Unit,
    onSpeakMessage: (String) -> Unit,
    onStopSpeaking: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(state.chatMessages.size, state.isChatTyping) {
        val lastIdx = (state.chatMessages.size - 1).coerceAtLeast(0)
        if (state.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(lastIdx)
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                inputText = spoken
            }
        }
    }

    val quickPrompts = listOf(
        "Fever & dengue watch protocol",
        "ANC high BP warning signs",
        "Type 2 diabetes meal plan",
        "Chest pain emergency triage",
        "Child MMR vaccination guide",
        "First aid for burns & wounds"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Patient Context Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = state.selectedPatientId == null,
                onClick = { onSelectPatient(null) },
                label = { Text("General Care") }
            )
            patients.take(6).forEach { p ->
                FilterChip(
                    selected = state.selectedPatientId == p.id,
                    onClick = { onSelectPatient(p.id) },
                    label = { Text(p.fullName) }
                )
            }
        }

        // Chat Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.chatMessages, key = { it.id }) { msg ->
                ChatBubble(
                    message = msg,
                    isSpeaking = state.isSpeaking,
                    onSpeak = { onSpeakMessage(msg.text) },
                    onStopSpeaking = onStopSpeaking
                )
            }
            if (state.isChatTyping) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = if (state.isOfflineOnlyMode) {
                                "Sehat Setu is thinking offline..."
                            } else {
                                "Sehat Setu is thinking..."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        // Error Banner (surfaces real Gemini / App Check / Voice / timeout errors)
        state.aiErrorMessage?.let { err ->
            Surface(
                color = CriticalRed.copy(alpha = 0.12f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = CareSyncButtonShape
            ) {
                Text(
                    text = err,
                    color = CriticalRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        // Quick Prompts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickPrompts.forEach { prompt ->
                AssistChip(
                    onClick = { onSendMessage(prompt) },
                    label = { Text(prompt, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        // Composer with Voice Input + Send
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak symptoms or clinical question")
                        }
                        AppLockGuard.suppressNextAutoLock = true
                        runCatching { speechLauncher.launch(intent) }.onFailure {
                            AppLockGuard.suppressNextAutoLock = false
                            onVoiceError("Speech recognition unavailable on this device. Please type your clinical query.")
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = CareSyncCyan.copy(alpha = 0.14f),
                        contentColor = CareSyncBlue
                    ),
                    modifier = Modifier.testTag("ai_voice_input_button")
                ) {
                    Icon(Icons.Rounded.Mic, contentDescription = "Voice input")
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask Sehat Setu clinical assistant...") },
                    singleLine = true,
                    shape = CareSyncButtonShape,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_chat_input")
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = CareSyncBlue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("ai_chat_send_button")
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "Send message")
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    isSpeaking: Boolean,
    onSpeak: () -> Unit,
    onStopSpeaking: () -> Unit
) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (message.isUser) CareSyncBlue else MaterialTheme.colorScheme.surface
    val textColor = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = bubbleColor,
            tonalElevation = if (message.isUser) 0.dp else 1.dp,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (!message.isUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.SmartToy, contentDescription = null, tint = CareSyncCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sehat Setu", style = MaterialTheme.typography.labelMedium, color = CareSyncCyan, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = { if (isSpeaking) onStopSpeaking() else onSpeak() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Rounded.Stop else Icons.AutoMirrored.Rounded.VolumeUp,
                                contentDescription = if (isSpeaking) "Stop speech" else "Read message aloud",
                                tint = CareSyncBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
                if (message.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        message.tags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = CareSyncBlue.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CareSyncBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        Text(
            text = message.timestampLabel,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
