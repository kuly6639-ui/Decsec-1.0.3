package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LineType
import com.example.model.TerminalLine
import com.example.ui.components.CrtScanlineOverlay
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DedsecPink
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.NeonGreenDim
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalDark
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TerminalSurfaceVariant
import com.example.ui.theme.TextBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WarningAmber

@Composable
fun TerminalScreen(
    terminalLines: List<TerminalLine>,
    inputText: String,
    onInputChange: (String) -> Unit,
    onExecuteInput: () -> Unit,
    onQuickCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new output
    LaunchedEffect(terminalLines.size) {
        if (terminalLines.isNotEmpty()) {
            listState.animateScrollToItem(terminalLines.size - 1)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_blink"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .testTag("terminal_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            // Terminal Header Bar
            Surface(
                color = PureBlack,
                shape = CutCornerShape(topStart = 6.dp, topEnd = 6.dp),
                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "CLI Terminal",
                            tint = NeonGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DECSEC 1.0 SHELL // /dev/pts/1",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = NeonGreenBright,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = "BAUD 115200 // TTY",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }
            }

            // Quick command chips row
            val quickChips = listOf(
                "ping", "ping 8.8.8.8", "devices", "wiretap DEV-PH-02", "scan", "profile", "blackout", "reboot", "exploit CTOS-TRF-881", "decrypt", "botnet", "matrix", "clear"
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickChips.forEach { chipCmd ->
                    Surface(
                        color = PureBlack,
                        shape = CutCornerShape(4.dp),
                        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .clickable { onQuickCommand(chipCmd) }
                            .testTag("chip_$chipCmd")
                    ) {
                        Text(
                            text = chipCmd,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (chipCmd.startsWith("ping")) NeonGreenBright else NeonGreen
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Output lines lazy column
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(PureBlack, CutCornerShape(4.dp))
                    .border(1.dp, NeonGreen.copy(alpha = 0.35f), CutCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                items(terminalLines, key = { it.id }) { line ->
                    TerminalLineRow(line = line)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Command Prompt Input
            Surface(
                color = PureBlack,
                shape = CutCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp),
                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "decsec@ctos:~# ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        BasicTextField(
                            value = inputText,
                            onValueChange = onInputChange,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = NeonGreenBright
                            ),
                            cursorBrush = SolidColor(NeonGreen),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { onExecuteInput() }),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("terminal_input_field")
                        )

                        if (inputText.isEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "type 'ping' or 'help'...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextMuted.copy(alpha = 0.6f)
                                    )
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "█",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = NeonGreen.copy(alpha = cursorAlpha)
                                    )
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onExecuteInput,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("terminal_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Execute Command",
                            tint = NeonGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Overlay CRT scanlines
        CrtScanlineOverlay(scanlineAlpha = 0.07f)
    }
}

@Composable
private fun TerminalLineRow(line: TerminalLine) {
    if (line.type == LineType.PING_PROGRESS) {
        RetroFlickeringPingRow(line = line)
        return
    }

    val textColor = when (line.type) {
        LineType.COMMAND -> NeonGreenBright
        LineType.CYBER_BANNER -> NeonGreenBright
        LineType.SUCCESS -> NeonGreenBright
        LineType.ERROR -> Color(0xFFFF2A4B)
        LineType.WARNING -> NeonGreen
        LineType.HEX_DUMP -> NeonGreenDim
        LineType.SYSTEM -> NeonGreen
        LineType.OUTPUT -> NeonGreen
        LineType.GLITCH -> NeonGreenBright
        LineType.PING_PROGRESS -> NeonGreenBright
    }

    Text(
        text = line.text,
        style = MaterialTheme.typography.bodySmall.copy(
            color = textColor,
            fontWeight = if (line.type == LineType.COMMAND || line.type == LineType.CYBER_BANNER || line.type == LineType.SUCCESS) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        modifier = Modifier.padding(vertical = 1.dp)
    )
}

/**
 * Flickering, retro-styled network latency progress bar for the 'ping' command.
 * Visualizes RTT latency, packet loss, and signal quality across connected nodes with
 * authentic segmented LED blocks, phosphor flicker, and high-contrast green & black theme.
 */
@Composable
private fun RetroFlickeringPingRow(line: TerminalLine) {
    val infiniteTransition = rememberInfiniteTransition(label = "ping_flicker")
    val flickerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(70, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ping_flicker_alpha"
    )

    val signalPercent = (line.progress * 100).toInt().coerceIn(10, 100)
    val latency = line.latencyMs
    val totalSegments = 20
    val activeSegments = (line.progress * totalSegments).toInt().coerceIn(1, totalSegments)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(PureBlack)
            .border(1.dp, NeonGreen.copy(alpha = 0.4f * flickerAlpha), CutCornerShape(4.dp))
            .padding(8.dp)
    ) {
        // Node Header & Latency Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = line.text.substringBefore(" | RTT:"),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = NeonGreenBright.copy(alpha = flickerAlpha),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )

            // High-contrast badge: Green background with Black text (or Black with Green text)
            Surface(
                color = NeonGreen,
                shape = CutCornerShape(2.dp)
            ) {
                Text(
                    text = "${latency}ms [ONLINE]",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Flickering Retro-styled Segmented Progress Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PureBlack)
                .border(1.dp, NeonGreen.copy(alpha = 0.6f), CutCornerShape(2.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            for (seg in 0 until totalSegments) {
                val isActive = seg < activeSegments
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .background(
                            if (isActive) NeonGreen.copy(alpha = flickerAlpha) else Color.Black
                        )
                        .border(
                            0.5.dp,
                            if (isActive) NeonGreenBright else NeonGreen.copy(alpha = 0.15f)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Signal telemetry text in green/black
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "LATENCY: ${latency}ms // 0% LOSS // ${signalPercent}% QUALITY",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = NeonGreenDim,
                    fontSize = 9.sp
                )
            )
            Text(
                text = if (latency < 20) "STATUS: OPTIMAL" else "STATUS: ROUTED",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = NeonGreenBright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            )
        }
    }
}
