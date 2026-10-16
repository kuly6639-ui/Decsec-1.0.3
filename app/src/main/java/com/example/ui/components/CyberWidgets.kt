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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BotnetStatus
import com.example.model.NetworkNode
import com.example.model.RealNetworkInfo
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DedsecPink
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.NeonGreenMuted
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TerminalSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WarningAmber
import kotlin.math.cos
import kotlin.math.sin

/**
 * Watch Dogs 2 DedSec Botnet Followers Tracker Widget
 */
@Composable
fun BotnetMeterWidget(
    botnetStatus: BotnetStatus,
    onBoostClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
        colors = CardDefaults.cardColors(containerColor = TerminalSurface),
        border = BorderStroke(1.dp, TerminalBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("botnet_meter_widget")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(DedsecPink, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DEDSEC BOTNET POWER",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = DedsecPink,
                            letterSpacing = 1.sp
                        )
                    )
                }
                Text(
                    text = "TARGET: 5.0M",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Follower count big display
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "%,d".format(botnetStatus.followers),
                    style = MaterialTheme.typography.displayMedium.copy(
                        color = NeonGreenBright,
                        fontWeight = FontWeight.Black
                    ),
                    modifier = Modifier.testTag("botnet_follower_count")
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "FOLLOWERS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val progress = (botnetStatus.followers.toFloat() / botnetStatus.followersTarget).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = NeonGreen,
                trackColor = TerminalSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("ACTIVE BOTS", style = MaterialTheme.typography.labelSmall)
                    Text("%,d".format(botnetStatus.activeBots), style = MaterialTheme.typography.bodyMedium.copy(color = CyberCyan))
                }
                Column {
                    Text("COMPUTE HASH", style = MaterialTheme.typography.labelSmall)
                    Text("%.1f PFLOPS".format(botnetStatus.computePowerPflops), style = MaterialTheme.typography.bodyMedium.copy(color = WarningAmber))
                }
                Column {
                    Text("MOMENTUM", style = MaterialTheme.typography.labelSmall)
                    Text("%.2fx MULTIPLIER".format(botnetStatus.multiplier), style = MaterialTheme.typography.bodyMedium.copy(color = DedsecPink))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Boost action
            Surface(
                color = TerminalSurfaceVariant,
                shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBoostClicked() }
                    .testTag("botnet_boost_button")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Boost",
                        tint = NeonGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DEPLOY BOTNET DDOS HARVEST (+15K FOLLOWERS)",
                        style = MaterialTheme.typography.labelMedium.copy(color = NeonGreen)
                    )
                }
            }
        }
    }
}

/**
 * Animated Sonar Radar Visualizer for ctOS Network Nodes
 */
@Composable
fun CtosRadarWidget(
    nodes: List<NetworkNode>,
    onNodeSelected: (NetworkNode) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Card(
        shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
        colors = CardDefaults.cardColors(containerColor = TerminalSurface),
        border = BorderStroke(1.dp, TerminalBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("ctos_radar_widget")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = "Radar",
                        tint = CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "cTOS 2.0 SCADA RADAR SWEEP",
                        style = MaterialTheme.typography.labelMedium.copy(color = CyberCyan)
                    )
                }
                Text(
                    text = "${nodes.size} NODES LOCKED",
                    style = MaterialTheme.typography.bodySmall.copy(color = NeonGreen)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Radar display canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .background(CyberBlack, CutCornerShape(8.dp))
                    .border(1.dp, TerminalBorder, CutCornerShape(8.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val maxRadius = minOf(size.width, size.height) * 0.44f

                    // Concentric radar rings
                    drawCircle(color = TerminalBorder, radius = maxRadius * 0.33f, center = center, style = Stroke(1f))
                    drawCircle(color = TerminalBorder, radius = maxRadius * 0.66f, center = center, style = Stroke(1f))
                    drawCircle(color = CyberCyan.copy(alpha = 0.3f), radius = maxRadius, center = center, style = Stroke(1.5f))

                    // Crosshairs
                    drawLine(
                        color = TerminalBorder,
                        start = Offset(center.x - maxRadius, center.y),
                        end = Offset(center.x + maxRadius, center.y),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = TerminalBorder,
                        start = Offset(center.x, center.y - maxRadius),
                        end = Offset(center.x, center.y + maxRadius),
                        strokeWidth = 1f
                    )

                    // Rotating sweep line
                    val rad = Math.toRadians(angle.toDouble())
                    val endX = center.x + (maxRadius * cos(rad)).toFloat()
                    val endY = center.y + (maxRadius * sin(rad)).toFloat()
                    drawLine(
                        brush = Brush.linearGradient(
                            colors = listOf(NeonGreen.copy(alpha = 0.8f), NeonGreen.copy(alpha = 0f)),
                            start = center,
                            end = Offset(endX, endY)
                        ),
                        start = center,
                        end = Offset(endX, endY),
                        strokeWidth = 2.5f
                    )

                    // Draw node blips
                    nodes.forEachIndexed { index, node ->
                        val nodeAngle = (index * (360f / maxOf(1, nodes.size)) + 45f) % 360f
                        val nodeDist = maxRadius * (0.35f + (index % 3) * 0.25f)
                        val nRad = Math.toRadians(nodeAngle.toDouble())
                        val bx = center.x + (nodeDist * cos(nRad)).toFloat()
                        val by = center.y + (nodeDist * sin(nRad)).toFloat()

                        val blipColor = when {
                            node.id.contains("TRF") -> WarningAmber
                            node.id.contains("PWR") -> AlertRed
                            node.id.contains("CAM") -> CyberCyan
                            else -> NeonGreen
                        }

                        // Blip halo & dot
                        drawCircle(color = blipColor.copy(alpha = 0.3f), radius = 8f, center = Offset(bx, by))
                        drawCircle(color = blipColor, radius = 3.5f, center = Offset(bx, by))
                    }
                }
            }
        }
    }
}

/**
 * System Telemetry HUD Card
 */
@Composable
fun SystemTelemetryWidget(
    networkInfo: RealNetworkInfo,
    modifier: Modifier = Modifier
) {
    Card(
        shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
        colors = CardDefaults.cardColors(containerColor = TerminalSurface),
        border = BorderStroke(1.dp, TerminalBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("system_telemetry_widget")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Telemetry",
                        tint = NeonGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HARDWARE & ADAPTER TELEMETRY",
                        style = MaterialTheme.typography.labelMedium.copy(color = NeonGreen)
                    )
                }
                Text(
                    text = "ONLINE",
                    style = MaterialTheme.typography.bodySmall.copy(color = NeonGreenBright, fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TelemetryMetricItem("INTERFACE", networkInfo.interfaceName, CyberCyan)
                TelemetryMetricItem("LOCAL IP", networkInfo.localIp, NeonGreen)
                TelemetryMetricItem("GATEWAY", networkInfo.gateway, TextMuted)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TelemetryMetricItem("TRANSPORT", networkInfo.networkType, WarningAmber)
                TelemetryMetricItem("LINK SPEED", "${networkInfo.linkSpeedMbps} Mbps", NeonGreen)
                TelemetryMetricItem("DNS", networkInfo.dnsServer, CyberCyan)
            }
        }
    }
}

@Composable
private fun TelemetryMetricItem(label: String, value: String, valueColor: Color) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(color = valueColor, fontWeight = FontWeight.Bold)
        )
    }
}

/**
 * DedSec Top Banner with Clock and Operative ID
 */
@Composable
fun DedsecHeaderBanner(
    onAudioToggle: () -> Unit,
    isAudioOn: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CyberBlack,
        border = BorderStroke(1.dp, TerminalBorder),
        shape = CutCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "[DECSEC 1.0 // cTOS 2.0]",
                        style = MaterialTheme.typography.labelLarge.copy(color = NeonGreenBright)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(NeonGreen, RoundedCornerShape(3.dp))
                    )
                }
                Text(
                    text = "STATUS: CTOS COMPROMISED // STEALTH",
                    style = MaterialTheme.typography.labelSmall.copy(color = DedsecPink)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Audio Toggle Chip
                Surface(
                    color = if (isAudioOn) NeonGreenMuted else TerminalSurfaceVariant,
                    shape = CutCornerShape(4.dp),
                    border = BorderStroke(1.dp, if (isAudioOn) NeonGreen else TerminalBorder),
                    modifier = Modifier
                        .clickable { onAudioToggle() }
                        .testTag("audio_toggle_chip")
                ) {
                    Text(
                        text = if (isAudioOn) "SFX: ON" else "SFX: MUTED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isAudioOn) NeonGreenBright else TextMuted
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
