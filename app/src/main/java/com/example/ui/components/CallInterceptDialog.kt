package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.TerminalSoundManager
import com.example.model.InterceptedCallMessage
import com.example.model.NearbyElectronicDevice
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DedsecPink
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TerminalSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WarningAmber
import kotlin.math.sin

/**
 * Watch Dogs 2 Phone & Smart Device Call Intercept HUD Dialog.
 * Shows animated real-time audio waveform, intercepted transcript dialogue,
 * caller data, and extraction hacks.
 */
@Composable
fun CallInterceptDialog(
    device: NearbyElectronicDevice,
    soundManager: TerminalSoundManager,
    onDismiss: () -> Unit,
    onExtractCredentials: () -> Unit
) {
    LaunchedEffect(device.id) {
        soundManager.playPhoneRing()
        kotlinx.coroutines.delay(650)
        soundManager.playRadioStatic()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = CutCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = TerminalSurface),
            border = BorderStroke(1.5.dp, DedsecPink),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("call_intercept_dialog")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Header with target caller info and live recording indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PhoneInTalk,
                            contentDescription = "Intercept Call",
                            tint = DedsecPink,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "DEDSEC WIRETAP ACTIVE",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = DedsecPink,
                                    fontWeight = FontWeight.Black
                                )
                            )
                            Text(
                                text = "${device.name} // ${device.callerNumber}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }

                    // Blinking REC badge
                    val blinkTransition = rememberInfiniteTransition(label = "rec")
                    val recAlpha by blinkTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 0.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "rec_blink"
                    )

                    Surface(
                        color = AlertRed.copy(alpha = 0.2f),
                        shape = CutCornerShape(4.dp),
                        border = BorderStroke(1.dp, AlertRed)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(AlertRed.copy(alpha = recAlpha), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "REC // LIVE",
                                style = MaterialTheme.typography.labelSmall.copy(color = AlertRed, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subject / Security banner
                Surface(
                    color = CyberBlack,
                    shape = CutCornerShape(4.dp),
                    border = BorderStroke(1.dp, TerminalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Call", tint = CyberCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "INTERCEPTED AUDIO: ${device.callSubject}",
                            style = MaterialTheme.typography.bodySmall.copy(color = CyberCyan, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Animated Real-Time Audio Waveform Visualizer
                LiveAudioWaveformCanvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Intercepted Dialogue Transcript Feed
                Text(
                    text = "DECRYPTED SPEECH STREAM:",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(CyberBlack, CutCornerShape(4.dp))
                        .border(1.dp, TerminalBorder, CutCornerShape(4.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(device.callTranscript) { msg ->
                        TranscriptBubble(message = msg)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Siphon Passcode/Intel button
                    Button(
                        onClick = {
                            soundManager.playAccessGranted()
                            onExtractCredentials()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen,
                            contentColor = CyberBlack
                        ),
                        shape = CutCornerShape(4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("extract_credentials_button")
                    ) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = "Intel", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("EXTRACT INTEL", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    // Disconnect Wiretap button
                    Button(
                        onClick = {
                            soundManager.playTerminalBeep()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TerminalSurfaceVariant,
                            contentColor = AlertRed
                        ),
                        shape = CutCornerShape(4.dp),
                        border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("disconnect_wiretap_button")
                    ) {
                        Icon(imageVector = Icons.Default.CallEnd, contentDescription = "Disconnect", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("DISCONNECT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveAudioWaveformCanvas(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Box(
        modifier = modifier
            .background(CyberBlack, CutCornerShape(4.dp))
            .border(1.dp, TerminalBorder, CutCornerShape(4.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerY = height / 2f
            val bars = 36
            val barWidth = width / (bars * 1.5f)

            for (i in 0 until bars) {
                val x = i * (width / bars) + barWidth / 2f
                val wave1 = sin(phase + i * 0.35f)
                val wave2 = sin(phase * 1.5f + i * 0.2f)
                val barHeight = ((wave1 + wave2).toFloat().coerceIn(-1.5f, 1.5f) * (height * 0.35f)).coerceAtLeast(4f)

                val barColor = if (i % 2 == 0) NeonGreen else CyberCyan
                drawLine(
                    color = barColor.copy(alpha = 0.85f),
                    start = Offset(x, centerY - barHeight / 2f),
                    end = Offset(x, centerY + barHeight / 2f),
                    strokeWidth = barWidth
                )
            }
        }
    }
}

@Composable
private fun TranscriptBubble(message: InterceptedCallMessage) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "[+${message.timeOffsetSec}s] ${message.speaker}:",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (message.speaker.contains("Target", ignoreCase = true)) NeonGreenBright else WarningAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "\"${message.text}\"",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextWhite,
                fontSize = 12.sp,
                lineHeight = 16.sp
            ),
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}
