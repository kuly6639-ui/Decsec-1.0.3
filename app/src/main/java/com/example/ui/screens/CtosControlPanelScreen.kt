package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.TerminalSoundManager
import com.example.model.BotnetStatus
import com.example.model.NearbyElectronicDevice
import com.example.model.NetworkNode
import com.example.model.RealNetworkInfo
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/**
 * Watch Dogs ctOS Control Panel Screen.
 * Faithful redesign modeled after the user's uploaded ctOS Control Panel screenshot:
 * - ctOS Control Panel header with segmented digital clock boxes
 * - Weather & Mission Briefing Card
 * - Battery / Li-Ion Gauge
 * - WeAreData Point-Cloud Topographical City Map with live animated nodes
 * - Interactive Fingerprint Biometric Scanner with laser scan and audio authorization
 * - Voice Recognition audio visualizer
 * - Quick-access ctOS Database & Modules: People, Messages, Call Log, Nearby Electronics, Terminal
 */
@Composable
fun CtosControlPanelScreen(
    soundManager: TerminalSoundManager,
    networkInfo: RealNetworkInfo,
    nearbyDevices: List<NearbyElectronicDevice>,
    botnetStatus: BotnetStatus,
    onNavigateTab: (Int) -> Unit,
    onTapNearbyDevice: (NearbyElectronicDevice) -> Unit,
    onExecuteCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isFingerprintScanning by remember { mutableStateOf(false) }
    var isFingerprintAuthorized by remember { mutableStateOf(false) }

    // Live clock formatted into [HH] and [mm]
    val now = remember { Date() }
    val hourStr = remember { SimpleDateFormat("hh", Locale.getDefault()).format(now) }
    val minStr = remember { SimpleDateFormat("mm", Locale.getDefault()).format(now) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(com.example.ui.theme.PureBlack)
            .testTag("ctos_control_panel_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Top Bar: DecSec 1.0 // ctOS Control Panel Title & View System Report
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DecSec 1.0 // ctOS Control Panel",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = NeonGreenBright,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "EVERYTHING IS CONNECTED // DEDSEC CORE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonGreen.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = CutCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    modifier = Modifier.clickable {
                        soundManager.playTerminalBeep()
                        onExecuteCommand("sysinfo")
                        onNavigateTab(0) // go to terminal
                    }
                ) {
                    Text(
                        text = "view_system_report",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Time & Schedule Row (Segmented Dual Boxes)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "TIME",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    // Hour Box
                    Surface(
                        color = Color.White,
                        shape = CutCornerShape(2.dp),
                        modifier = Modifier.size(width = 34.dp, height = 30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = hourStr,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Minute Box
                    Surface(
                        color = Color.White,
                        shape = CutCornerShape(2.dp),
                        modifier = Modifier.size(width = 34.dp, height = 30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = minStr,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Next Alarm
                    Surface(
                        color = CyberCyan.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
                        shape = CutCornerShape(2.dp)
                    ) {
                        Text(
                            text = "NEXT ALARM: 07:00 AM MON",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }

                // Timeline badge
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "TIMELINE",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp)
                    )
                    Text(
                        text = "14_nov_2016_mon",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Weather & Battery Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Weather / Mission Card
                Card(
                    shape = CutCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1821)),
                    border = BorderStroke(1.dp, TerminalBorder),
                    modifier = Modifier.weight(1.8f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "WEATHER",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            )
                            Surface(
                                color = CyberCyan.copy(alpha = 0.3f),
                                shape = CutCornerShape(2.dp)
                            ) {
                                Text(
                                    text = "LOCATION : SAN FRANCISCO",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CyberCyan, fontSize = 9.sp),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "IT IS 18.55 C WITH HUMIDITY OF 52% AND WIND IS CLEAR IN NORTHWEST",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 10.sp,
                                lineHeight = 13.sp
                            )
                        )
                        Text(
                            text = "DETAILS: CLEAR SKY // SENSORS CALIBRATED",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "NEXT MISSION: 05:00 AM // INFILTRATE BLUME TOWER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DedsecPink,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                // Battery / Power Card
                Card(
                    shape = CutCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1821)),
                    border = BorderStroke(1.dp, TerminalBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "hi_power",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "91%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = NeonGreenBright,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "3641V",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Text(
                            text = "CHARGING",
                            style = MaterialTheme.typography.labelSmall.copy(color = NeonGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "TECH : LI-ION",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Two-Column Section: Left (WeAreData Map) + Right (Biometric Fingerprint Scanner & Voice Recognition)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Left: WeAreData Map Card
                Card(
                    shape = CutCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0F14)),
                    border = BorderStroke(1.dp, TerminalBorder),
                    modifier = Modifier.weight(1.4f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "WeareData",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = CyberCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "SAN FRANCISCO // CTOS-SF-01",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 8.sp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Point-cloud wireframe map canvas
                        WeAreDataMapCanvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        )
                    }
                }

                // Right: ctOS Biometric Panel & Fingerprint Scanner
                Card(
                    shape = CutCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1821)),
                    border = BorderStroke(1.dp, TerminalBorder),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // ctOS barcode logo
                        Text(
                            text = "CTOS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 2.sp
                            )
                        )
                        Text(
                            text = "connection is power",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 8.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Voice recognition indicator
                        Text(
                            text = "RECOGNIZING VOICE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberCyan,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        VoiceRecognitionSpectrumCanvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "SCAN YOUR FINGER HERE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 8.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Interactive Fingerprint Scanner Pad
                        Box(
                            modifier = Modifier
                                .size(width = 68.dp, height = 74.dp)
                                .background(CyberBlack, CutCornerShape(4.dp))
                                .border(
                                    1.dp,
                                    if (isFingerprintAuthorized) NeonGreen else if (isFingerprintScanning) DedsecPink else CyberCyan,
                                    CutCornerShape(4.dp)
                                )
                                .clickable {
                                    soundManager.playSonarPing()
                                    isFingerprintScanning = true
                                    isFingerprintAuthorized = false
                                }
                                .testTag("fingerprint_scanner_pad"),
                            contentAlignment = Alignment.Center
                        ) {
                            FingerprintScannerCanvas(
                                isScanning = isFingerprintScanning,
                                isAuthorized = isFingerprintAuthorized
                            )
                        }

                        LaunchedEffect(isFingerprintScanning) {
                            if (isFingerprintScanning) {
                                kotlinx.coroutines.delay(800)
                                soundManager.playAccessGranted()
                                isFingerprintScanning = false
                                isFingerprintAuthorized = true
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isFingerprintAuthorized) "IDENTITY MATCHED" else if (isFingerprintScanning) "AUTHORIZING..." else "WAITING SCAN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isFingerprintAuthorized) NeonGreenBright else if (isFingerprintScanning) WarningAmber else TextMuted,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Network Telemetry
                        Text(
                            text = "10.43.158.134 // 34:FC:EF:B4:9E:AB",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 7.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ctOS DATABASE Search Bar
            Surface(
                color = Color(0xFF0F1821),
                shape = CutCornerShape(4.dp),
                border = BorderStroke(1.dp, TerminalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ctOS DATABASE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HACK INTO ctOS DATABASE TO SEARCH...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-header: LOGGING IN... // SYSTEM LOG
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "LOGGING IN...",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DedsecPink,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SYSTEM LOG",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
                Text(
                    text = "DEDSEC BOTNET: %,d FOLLOWERS".format(botnetStatus.followers),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = NeonGreenBright,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Interactive Modules / Tiles List with Electronic Sounds & Black/Green Styling
            var selectedTileIndex by remember { mutableStateOf(0) }

            // Navigation bar to move to another tile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = com.example.ui.theme.PureBlack,
                    shape = CutCornerShape(4.dp),
                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.7f)),
                    modifier = Modifier.clickable {
                        selectedTileIndex = if (selectedTileIndex > 0) selectedTileIndex - 1 else 5
                        soundManager.playTileMoveSound()
                    }
                ) {
                    Text(
                        text = "< PREV TILE",
                        style = MaterialTheme.typography.labelSmall.copy(color = NeonGreenBright),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = NeonGreen,
                    shape = CutCornerShape(4.dp)
                ) {
                    Text(
                        text = "TILE ${selectedTileIndex + 1}/6 [ACTIVE]",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = com.example.ui.theme.PureBlackText,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = com.example.ui.theme.PureBlack,
                    shape = CutCornerShape(4.dp),
                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.7f)),
                    modifier = Modifier.clickable {
                        selectedTileIndex = if (selectedTileIndex < 5) selectedTileIndex + 1 else 0
                        soundManager.playTileMoveSound()
                    }
                ) {
                    Text(
                        text = "NEXT TILE >",
                        style = MaterialTheme.typography.labelSmall.copy(color = NeonGreenBright),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // 1. PEOPLE / PROFILER Module
                CtosModuleRow(
                    icon = Icons.Default.PersonSearch,
                    title = "PEOPLE",
                    subtitle = "HACK INTO THE ctOS PEOPLE DIRECTORY TO VIEW TRUE IDENTITY AND OCCUPATION",
                    badge = "PROFILER ONLINE",
                    badgeColor = NeonGreen,
                    isSelected = selectedTileIndex == 0,
                    soundManager = soundManager,
                    onClick = {
                        if (selectedTileIndex != 0) {
                            selectedTileIndex = 0
                            soundManager.playTileMoveSound()
                        } else {
                            soundManager.playTerminalBeep()
                            onNavigateTab(3) // navigate to Profiler
                        }
                    }
                )

                // 2. INBOX / MESSAGES Module
                CtosModuleRow(
                    icon = Icons.Default.Mail,
                    title = "INBOX",
                    subtitle = "RECENT COMMUNICATIONS // UNREAD MESSAGES : 202",
                    badge = "202 NEW",
                    badgeColor = NeonGreen,
                    isSelected = selectedTileIndex == 1,
                    soundManager = soundManager,
                    onClick = {
                        if (selectedTileIndex != 1) {
                            selectedTileIndex = 1
                            soundManager.playTileMoveSound()
                        } else {
                            soundManager.playDataStream()
                            onExecuteCommand("matrix")
                            onNavigateTab(1)
                        }
                    }
                )

                // 3. CALL LOG / WIRETAP Module
                CtosModuleRow(
                    icon = Icons.Default.PhoneInTalk,
                    title = "CALL LOG",
                    subtitle = "INTERCEPTED COMMUNICATIONS // MISSED CALLS : 0 // TAP TO LISTEN TO CALLS",
                    badge = "WIRETAP READY",
                    badgeColor = NeonGreen,
                    isSelected = selectedTileIndex == 2,
                    soundManager = soundManager,
                    onClick = {
                        if (selectedTileIndex != 2) {
                            selectedTileIndex = 2
                            soundManager.playTileMoveSound()
                        } else {
                            soundManager.playPhoneRing()
                            val targetDev = nearbyDevices.firstOrNull { it.callTranscript.isNotEmpty() } ?: nearbyDevices.firstOrNull()
                            if (targetDev != null) {
                                onTapNearbyDevice(targetDev)
                            } else {
                                onNavigateTab(2)
                            }
                        }
                    }
                )

                // 4. NEARBY ELECTRONICS SCANNER Module
                CtosModuleRow(
                    icon = Icons.Default.Tv,
                    title = "NEARBY ELECTRONICS",
                    subtitle = "${nearbyDevices.size} DEVICES IN VICINITY (SMART TVS, PHONES, SPEAKERS, LAPTOPS)",
                    badge = "${nearbyDevices.size} NEARBY",
                    badgeColor = NeonGreen,
                    isSelected = selectedTileIndex == 3,
                    soundManager = soundManager,
                    onClick = {
                        if (selectedTileIndex != 3) {
                            selectedTileIndex = 3
                            soundManager.playTileMoveSound()
                        } else {
                            soundManager.playSonarPing()
                            onNavigateTab(2) // navigate to Network Scanner
                        }
                    }
                )

                // 5. COMMAND LINE CLI Module
                CtosModuleRow(
                    icon = Icons.Default.Terminal,
                    title = "DECSEC SHELL",
                    subtitle = "ROOT COMMAND LINE TERMINAL INTERFACE // CTOS ZERO-DAY ACCESS",
                    badge = "CLI READY",
                    badgeColor = NeonGreenBright,
                    isSelected = selectedTileIndex == 4,
                    soundManager = soundManager,
                    onClick = {
                        if (selectedTileIndex != 4) {
                            selectedTileIndex = 4
                            soundManager.playTileMoveSound()
                        } else {
                            soundManager.playTerminalBeep()
                            onNavigateTab(1) // navigate to Terminal
                        }
                    }
                )

                // 6. CIPHER OVERFLOW MEMORY BYPASS Module
                CtosModuleRow(
                    icon = Icons.Default.Memory,
                    title = "BUFFER CIPHER",
                    subtitle = "CIRCUIT MEMORY BYPASS MINIGAME // UNLOCK RESTRICTED CTOS KERNELS",
                    badge = "PUZZLE",
                    badgeColor = NeonGreen,
                    isSelected = selectedTileIndex == 5,
                    soundManager = soundManager,
                    onClick = {
                        if (selectedTileIndex != 5) {
                            selectedTileIndex = 5
                            soundManager.playTileMoveSound()
                        } else {
                            soundManager.playTerminalBeep()
                            onNavigateTab(4) // navigate to Cipher minigame
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Overlay CRT scanlines
        CrtScanlineOverlay(scanlineAlpha = 0.05f)
    }
}

@Composable
private fun CtosModuleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    isSelected: Boolean,
    soundManager: TerminalSoundManager,
    onClick: () -> Unit
) {
    Surface(
        color = com.example.ui.theme.PureBlack,
        shape = CutCornerShape(4.dp),
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) NeonGreenBright else NeonGreen.copy(alpha = 0.35f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("ctos_module_${title.lowercase().replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (isSelected) NeonGreen else com.example.ui.theme.PureBlack,
                            CutCornerShape(4.dp)
                        )
                        .border(1.dp, NeonGreen, CutCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isSelected) com.example.ui.theme.PureBlackText else NeonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    if (isSelected) {
                        com.example.ui.components.HackerScrambleText(
                            text = title,
                            color = NeonGreenBright,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            onLetterLoaded = { soundManager.playLetterLoadSound() }
                        )
                    } else {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = NeonGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        )
                    }
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isSelected) NeonGreen.copy(alpha = 0.85f) else TextMuted,
                            fontSize = 10.sp,
                            lineHeight = 13.sp
                        ),
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                color = if (isSelected) NeonGreen else com.example.ui.theme.PureBlack,
                shape = CutCornerShape(3.dp),
                border = BorderStroke(1.dp, NeonGreen)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isSelected) com.example.ui.theme.PureBlackText else NeonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}

/**
 * Animated Point-Cloud / Vector Topographical City Map (WeAreData)
 */
@Composable
private fun WeAreDataMapCanvas(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "map")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Background subtle grid
        val step = 20f
        var gx = 0f
        while (gx < w) {
            drawLine(color = Color.White.copy(alpha = 0.05f), start = Offset(gx, 0f), end = Offset(gx, h), strokeWidth = 0.8f)
            gx += step
        }
        var gy = 0f
        while (gy < h) {
            drawLine(color = Color.White.copy(alpha = 0.05f), start = Offset(0f, gy), end = Offset(w, gy), strokeWidth = 0.8f)
            gy += step
        }

        // Topographical elevation curves
        val path1 = Path().apply {
            moveTo(0f, h * 0.7f)
            cubicTo(w * 0.3f, h * 0.4f, w * 0.6f, h * 0.85f, w, h * 0.55f)
        }
        drawPath(path1, color = CyberCyan.copy(alpha = 0.4f), style = Stroke(width = 1.2f))

        val path2 = Path().apply {
            moveTo(0f, h * 0.5f)
            cubicTo(w * 0.25f, h * 0.2f, w * 0.7f, h * 0.65f, w, h * 0.35f)
        }
        drawPath(path2, color = CyberCyan.copy(alpha = 0.6f), style = Stroke(width = 1.5f))

        // Map City Nodes
        val nodes = listOf(
            Offset(w * 0.22f, h * 0.32f),
            Offset(w * 0.55f, h * 0.48f),
            Offset(w * 0.78f, h * 0.28f),
            Offset(w * 0.42f, h * 0.75f)
        )

        // Draw connections between nodes
        for (i in 0 until nodes.size - 1) {
            drawLine(
                color = CyberCyan.copy(alpha = 0.5f),
                start = nodes[i],
                end = nodes[i + 1],
                strokeWidth = 1f
            )
        }

        // Animated pulse wave along nodes
        nodes.forEachIndexed { idx, pt ->
            val pRadius = 5f + 4f * sin(pulse + idx.toFloat())
            drawCircle(color = CyberCyan.copy(alpha = 0.25f), radius = pRadius.coerceAtLeast(3f), center = pt)
            drawCircle(color = NeonGreenBright, radius = 3f, center = pt)
        }
    }
}

/**
 * Live audio voice frequency spectrum bars
 */
@Composable
private fun VoiceRecognitionSpectrumCanvas(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "voice")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val bars = 22
        val barWidth = w / (bars * 1.5f)
        val cy = h / 2f

        for (i in 0 until bars) {
            val x = i * (w / bars) + barWidth / 2f
            val wave = (sin(animOffset + i * 0.5f) + 1.2f) * 0.4f
            val barH = (h * wave).coerceAtLeast(3f)
            drawLine(
                color = CyberCyan,
                start = Offset(x, cy - barH / 2f),
                end = Offset(x, cy + barH / 2f),
                strokeWidth = barWidth
            )
        }
    }
}

/**
 * Fingerprint Biometric Scanner with laser line sweep
 */
@Composable
private fun FingerprintScannerCanvas(
    isScanning: Boolean,
    isAuthorized: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fp")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    Canvas(modifier = modifier.fillMaxSize().padding(6.dp)) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2f, h / 2f)

        // Draw biometric fingerprint arches
        val tint = if (isAuthorized) NeonGreenBright else if (isScanning) DedsecPink else CyberCyan
        val rings = 5
        for (i in 1..rings) {
            val r = (w * 0.16f) * i
            drawCircle(
                color = tint.copy(alpha = 0.5f),
                radius = r,
                center = center,
                style = Stroke(width = 1.2f)
            )
        }

        // Draw laser scanning line if scanning
        if (isScanning) {
            val ly = h * laserY
            drawLine(
                color = DedsecPink,
                start = Offset(0f, ly),
                end = Offset(w, ly),
                strokeWidth = 2.5f
            )
        }
    }
}
