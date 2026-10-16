package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.BotnetStatus
import com.example.model.NetworkNode
import com.example.model.RealNetworkInfo
import com.example.ui.components.BotnetMeterWidget
import com.example.ui.components.CrtScanlineOverlay
import com.example.ui.components.CtosRadarWidget
import com.example.ui.components.SystemTelemetryWidget
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DedsecPink
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalDark
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TerminalSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WarningAmber

@Composable
fun WidgetsScreen(
    botnetStatus: BotnetStatus,
    networkInfo: RealNetworkInfo,
    nodes: List<NetworkNode>,
    onBoostBotnet: () -> Unit,
    onTriggerBlackout: () -> Unit,
    onQuickHackAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalDark)
            .testTag("widgets_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botnet Followers Meter Widget
            BotnetMeterWidget(
                botnetStatus = botnetStatus,
                onBoostClicked = onBoostBotnet
            )

            // ctOS Radar Sweep Visualizer Widget
            CtosRadarWidget(
                nodes = nodes,
                onNodeSelected = { }
            )

            // Real-time System Telemetry Widget
            SystemTelemetryWidget(
                networkInfo = networkInfo
            )

            // Tactical Quick-Hack Deck
            TacticalQuickHackDeck(
                onTriggerBlackout = onTriggerBlackout,
                onQuickHackAction = onQuickHackAction
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        CrtScanlineOverlay(scanlineAlpha = 0.05f)
    }
}

@Composable
private fun TacticalQuickHackDeck(
    onTriggerBlackout: () -> Unit,
    onQuickHackAction: (String) -> Unit
) {
    Card(
        shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
        colors = CardDefaults.cardColors(containerColor = TerminalSurface),
        border = BorderStroke(1.dp, TerminalBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tactical_deck")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Tactical",
                    tint = DedsecPink,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TACTICAL 1-TAP OVERRIDES",
                    style = MaterialTheme.typography.labelMedium.copy(color = DedsecPink)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TacticalHackRow(
                    title = "CITY SECTOR BLACKOUT",
                    subtitle = "High-voltage surge shutdown",
                    icon = Icons.Default.FlashOn,
                    iconColor = AlertRed,
                    actionTag = "blackout_hack_button",
                    onClick = onTriggerBlackout
                )

                TacticalHackRow(
                    title = "TRAFFIC GRID CHAOS",
                    subtitle = "Cycle intersections all green",
                    icon = Icons.Default.Traffic,
                    iconColor = WarningAmber,
                    actionTag = "traffic_hack_button",
                    onClick = { onQuickHackAction("exploit CTOS-TRF-881") }
                )

                TacticalHackRow(
                    title = "PATROL DRONE CRASH",
                    subtitle = "Rotor overdrive override",
                    icon = Icons.Default.FlightTakeoff,
                    iconColor = CyberCyan,
                    actionTag = "drone_hack_button",
                    onClick = { onQuickHackAction("exploit CTOS-DRN-007") }
                )

                TacticalHackRow(
                    title = "WIRETAP BLUME OPERATIVE",
                    subtitle = "Intercept live audio call & decrypt speech",
                    icon = Icons.Default.PhoneInTalk,
                    iconColor = DedsecPink,
                    actionTag = "wiretap_hack_button",
                    onClick = { onQuickHackAction("wiretap DEV-PH-02") }
                )

                TacticalHackRow(
                    title = "PING BLUME BACKBONE",
                    subtitle = "Measure ICMP packet round-trip",
                    icon = Icons.Default.NetworkCheck,
                    iconColor = NeonGreen,
                    actionTag = "ping_blume_button",
                    onClick = { onQuickHackAction("ping ctos.blume.net") }
                )

                TacticalHackRow(
                    title = "REBOOT CTOS 2.0 SYSTEM",
                    subtitle = "Play cold boot startup video sequence",
                    icon = Icons.Default.RestartAlt,
                    iconColor = CyberCyan,
                    actionTag = "reboot_hack_button",
                    onClick = { onQuickHackAction("reboot") }
                )
            }
        }
    }
}

@Composable
private fun TacticalHackRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: androidx.compose.ui.graphics.Color,
    actionTag: String,
    onClick: () -> Unit
) {
    Surface(
        color = TerminalSurfaceVariant,
        shape = CutCornerShape(4.dp),
        border = BorderStroke(1.dp, iconColor.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(actionTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(iconColor.copy(alpha = 0.15f), CutCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.labelMedium.copy(color = NeonGreenBright))
                    Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                }
            }

            Surface(
                color = iconColor.copy(alpha = 0.2f),
                shape = CutCornerShape(4.dp),
                border = BorderStroke(1.dp, iconColor)
            ) {
                Text(
                    text = "EXEC",
                    style = MaterialTheme.typography.labelSmall.copy(color = iconColor),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
