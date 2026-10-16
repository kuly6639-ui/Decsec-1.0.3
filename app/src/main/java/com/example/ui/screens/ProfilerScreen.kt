package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProfilerTarget
import com.example.model.ThreatLevel
import com.example.ui.components.CrtScanlineOverlay
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DedsecPink
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalDark
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TerminalSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WarningAmber

@Composable
fun ProfilerScreen(
    targets: List<ProfilerTarget>,
    onDrainCrypto: (ProfilerTarget) -> Unit,
    onDistract: (ProfilerTarget) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalDark)
            .testTag("profiler_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            // Profiler HUD Header
            Card(
                shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                border = BorderStroke(1.dp, TerminalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Profiler HUD",
                            tint = DedsecPink,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "cTOS 2.0 BIOMETRIC PROFILER",
                                style = MaterialTheme.typography.titleMedium.copy(color = DedsecPink)
                            )
                            Text(
                                text = "FACIAL RECOGNITION & BANK SNOOPING ONLINE",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(targets, key = { it.id }) { target ->
                    ProfilerTargetCard(
                        target = target,
                        onDrain = { onDrainCrypto(target) },
                        onDistract = { onDistract(target) }
                    )
                }
            }
        }

        CrtScanlineOverlay(scanlineAlpha = 0.05f)
    }
}

@Composable
private fun ProfilerTargetCard(
    target: ProfilerTarget,
    onDrain: () -> Unit,
    onDistract: () -> Unit
) {
    val (threatColor, threatLabel) = when (target.threatLevel) {
        ThreatLevel.CRITICAL -> Pair(AlertRed, "THREAT: CRITICAL")
        ThreatLevel.HIGH -> Pair(WarningAmber, "THREAT: HIGH")
        ThreatLevel.MODERATE -> Pair(CyberCyan, "THREAT: MODERATE")
        ThreatLevel.LOW -> Pair(NeonGreen, "THREAT: LOW")
    }

    Card(
        shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
        colors = CardDefaults.cardColors(containerColor = TerminalSurface),
        border = BorderStroke(
            1.dp,
            if (target.isHacked) NeonGreen else TerminalBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("target_card_${target.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Avatar icon + Name + Age + Threat Level Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(TerminalSurfaceVariant, CutCornerShape(4.dp))
                            .border(1.dp, threatColor, CutCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Target",
                            tint = threatColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "${target.name}, ${target.age}",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        )
                        Text(
                            text = target.occupation,
                            style = MaterialTheme.typography.bodySmall.copy(color = CyberCyan)
                        )
                    }
                }

                Surface(
                    color = threatColor.copy(alpha = 0.15f),
                    shape = CutCornerShape(4.dp),
                    border = BorderStroke(1.dp, threatColor)
                ) {
                    Text(
                        text = threatLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = threatColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Income & Crypto row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberBlack, CutCornerShape(4.dp))
                    .border(1.dp, TerminalBorder, CutCornerShape(4.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("ANNUAL INCOME", style = MaterialTheme.typography.labelSmall)
                    Text(target.incomeFormatted, style = MaterialTheme.typography.bodyMedium.copy(color = NeonGreenBright, fontWeight = FontWeight.Bold))
                }
                Column {
                    Text("CRYPTO VAULT", style = MaterialTheme.typography.labelSmall)
                    Text(
                        if (target.isHacked) "0.00 BTC [DRAINED]" else "%.2f BTC".format(target.cryptoBalance),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (target.isHacked) AlertRed else WarningAmber,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Column {
                    Text("DEVICE", style = MaterialTheme.typography.labelSmall)
                    Text(target.phoneModel.take(14), style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ctOS Factoid
            Surface(
                color = TerminalSurfaceVariant,
                shape = CutCornerShape(4.dp),
                border = BorderStroke(1.dp, TerminalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Factoid",
                        tint = WarningAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ctOS FACT: ${target.ctOSFactoid}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextWhite, fontSize = 11.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Siphon Crypto button
                Button(
                    onClick = onDrain,
                    enabled = !target.isHacked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (target.isHacked) TerminalSurfaceVariant else NeonGreen,
                        contentColor = CyberBlack
                    ),
                    shape = CutCornerShape(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("drain_crypto_${target.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.CurrencyBitcoin,
                        contentDescription = "Siphon Crypto",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (target.isHacked) "DRAINED" else "SIPHON BANK",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Distract button
                Button(
                    onClick = onDistract,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DedsecPink,
                        contentColor = TextWhite
                    ),
                    shape = CutCornerShape(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("distract_${target.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Distract",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (target.isDistracted) "DISTRACTED" else "DISTRACT",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
