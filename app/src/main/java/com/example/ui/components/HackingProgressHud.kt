package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HackingProgress
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.CyberBlueBright
import com.example.ui.theme.DedsecPink
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalDark
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TextBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

/**
 * Retro-Tech Hacking Progress HUD Overlay.
 * Appears during randomized hacking operations with authentic retro audio,
 * segmented progress ladder, and telemetry in white & blue styling.
 */
@Composable
fun HackingProgressHud(
    hackingProgress: HackingProgress?,
    onAbort: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = hackingProgress != null,
        enter = fadeIn() + scaleIn(initialScale = 0.92f),
        exit = fadeOut() + scaleOut(targetScale = 0.95f),
        modifier = modifier
    ) {
        if (hackingProgress == null) return@AnimatedVisibility

        val infiniteTransition = rememberInfiniteTransition(label = "flicker")
        val flickerAlpha by infiniteTransition.animateFloat(
            initialValue = 0.82f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(140, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "flickerAlpha"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.78f))
                .clickable { /* Block clicks behind */ }
                .padding(20.dp)
                .testTag("hacking_progress_hud"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = CutCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                border = BorderStroke(
                    1.5.dp,
                    if (hackingProgress.isComplete) {
                        if (hackingProgress.isSuccess) CyberBlueBright else AlertRed
                    } else {
                        CyberBlueBright.copy(alpha = flickerAlpha)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    // Header Row: Title & Abort Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (hackingProgress.isComplete && !hackingProgress.isSuccess) Icons.Default.Warning else Icons.Default.LockOpen,
                                contentDescription = "Hack Icon",
                                tint = if (hackingProgress.isComplete && !hackingProgress.isSuccess) AlertRed else CyberBlueBright,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DECSEC 1.0 // EXPLOIT ENGINE",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextWhite,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        if (!hackingProgress.isComplete) {
                            Surface(
                                color = AlertRed.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.8f)),
                                shape = CutCornerShape(4.dp),
                                modifier = Modifier
                                    .clickable { onAbort() }
                                    .testTag("abort_hack_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Abort",
                                        tint = AlertRed,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "ABORT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = AlertRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Target Name & ID
                    Text(
                        text = "TARGET: ${hackingProgress.targetName.uppercase()}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )

                    Text(
                        text = "TARGET ID: [${hackingProgress.targetId}] // DURATION: ${(hackingProgress.totalDurationMs / 100.0) / 10.0}s",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextBlue,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Segmented Retro-Style Progress Bar Ladder
                    val totalBlocks = 20
                    val filledBlocks = (hackingProgress.progress * totalBlocks).toInt()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        for (i in 0 until totalBlocks) {
                            val isFilled = i < filledBlocks
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(16.dp)
                                    .background(
                                        if (isFilled) {
                                            if (hackingProgress.isComplete && !hackingProgress.isSuccess) AlertRed else CyberBlueBright
                                        } else {
                                            TerminalSurface
                                        },
                                        CutCornerShape(2.dp)
                                    )
                                    .border(
                                        0.8.dp,
                                        if (isFilled) CyberBlue else TerminalBorder,
                                        CutCornerShape(2.dp)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status and percentage
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = hackingProgress.statusText,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (hackingProgress.isComplete && !hackingProgress.isSuccess) AlertRed else CyberBlueBright,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )

                        Text(
                            text = "${(hackingProgress.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = TextWhite,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Live step telemetry
                    Surface(
                        color = CyberBlack,
                        shape = CutCornerShape(4.dp),
                        border = BorderStroke(1.dp, TerminalBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = ">> ${hackingProgress.currentStep}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextWhite.copy(alpha = 0.9f),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
