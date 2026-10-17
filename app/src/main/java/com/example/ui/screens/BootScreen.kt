package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.CyberBlueBright
import com.example.ui.theme.CyberBlueDim
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DedsecPink
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TextBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

enum class BootPhase {
    HARDWARE_POST,
    MINIMAL_X,
    SYSTEM_LOADING,
    HEXAGON_CIPHER,
    CTOS_BEAM_LOGO,
    ISOMETRIC_EMBLEM,
    DEDSEC_HIJACK_BOXES,
    BOOT_COMPLETE
}

/**
 * Longer, authentic Watch Dogs ctOS & DecSec 1.0 Boot Sequence.
 * Styled in crisp white and cyber blue font colors.
 * Features:
 * - Stage 1: Cold Hardware POST & Memory bus diagnostics
 * - Stage 2: Minimalist crosshair targeting
 * - Stage 3: Stepped System Loading diagnostics
 * - Stage 4: Subnet Hexagon Cipher permutation
 * - Stage 5: ctOS Beam Authentication handshake
 * - Stage 6: 3D Wireframe Polyhedron telemetry
 * - Stage 7: DecSec 1.0 Hijack boxed letter-by-letter reveal
 * - Tap anywhere to skip instantly
 */
@Composable
fun BootScreen(
    soundManager: TerminalSoundManager,
    onBootFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var phase by remember { mutableStateOf(BootPhase.HARDWARE_POST) }
    var cipherString by remember { mutableStateOf("0x7F001A8") }
    var bootProgress by remember { mutableStateOf(0.05f) }
    var postMemAddress by remember { mutableStateOf("0x00000000") }

    // Target boxed letters sequence: D E C S E C / 1 . 0
    val targetWords = listOf(
        listOf("D", "E", "C", "S", "E", "C"),
        listOf("1", ".", "0", " ", "/", "/", " ", "c", "T", "O", "S")
    )

    var revealedCount by remember { mutableIntStateOf(0) }
    val totalLetters = targetWords.sumOf { it.size }
    val cipherCharset = "0123456789ABCDEF$#@%&*?><Ø§"

    // Longer, cinematic boot progression (~13 seconds total, or instant on tap)
    LaunchedEffect(Unit) {
        soundManager.playBootBeep()

        // Phase 1: Hardware POST & Memory check (~1.8s)
        phase = BootPhase.HARDWARE_POST
        bootProgress = 0.08f
        for (step in 1..6) {
            postMemAddress = "0x%08X".format(step * 0x1A4000)
            soundManager.playLetterLoadSound()
            delay(280)
        }

        // Phase 2: Minimalist 'X' (~1.6s)
        phase = BootPhase.MINIMAL_X
        soundManager.playTileMoveSound()
        bootProgress = 0.22f
        delay(1600)

        // Phase 3: Stepped System Loading bars (~1.7s)
        phase = BootPhase.SYSTEM_LOADING
        soundManager.playTerminalBeep()
        bootProgress = 0.40f
        delay(1700)

        // Phase 4: Hexagon Cipher text stream (~1.9s)
        phase = BootPhase.HEXAGON_CIPHER
        soundManager.playDataStream()
        bootProgress = 0.58f
        repeat(7) {
            cipherString = (1..11).map { cipherCharset.random() }.joinToString("")
            soundManager.playLetterLoadSound()
            delay(260)
        }

        // Phase 5: Horizontal beam + "ctOS" logo (~1.8s)
        phase = BootPhase.CTOS_BEAM_LOGO
        soundManager.playBootBeep()
        bootProgress = 0.74f
        delay(1800)

        // Phase 6: Isometric Wireframe Polyhedron (~1.7s)
        phase = BootPhase.ISOMETRIC_EMBLEM
        soundManager.playSonarPing()
        bootProgress = 0.88f
        delay(1700)

        // Phase 7: DecSec Hijack & Boxed Letters Decryption (~2.5s)
        phase = BootPhase.DEDSEC_HIJACK_BOXES
        soundManager.playHackStartSound()
        bootProgress = 1.0f

        for (i in 1..totalLetters) {
            delay(120)
            revealedCount = i
            soundManager.playKeyClick()
        }

        delay(800)
        soundManager.playHackSuccessSound()
        delay(1200)
        phase = BootPhase.BOOT_COMPLETE
        onBootFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { onBootFinished() } // Tap anywhere to skip
            .testTag("boot_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Top Bar: System Status & Skip Chip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 42.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DECSEC 1.0 // CTOS KERNEL BOOT",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyberBlueBright,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
            )

            Surface(
                color = CyberBlueBright.copy(alpha = 0.15f),
                shape = CutCornerShape(4.dp),
                border = BorderStroke(1.dp, CyberBlueBright.copy(alpha = 0.6f)),
                modifier = Modifier
                    .clickable { onBootFinished() }
                    .testTag("skip_boot_button")
            ) {
                Text(
                    text = "TAP TO SKIP [▶▶]",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        // Center Content based on BootPhase
        when (phase) {
            BootPhase.HARDWARE_POST -> {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(28.dp)
                ) {
                    Text(
                        text = "BLUME CORPORATION // ctOS CORE BIOS v2.08",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberBlueBright,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "CPU: QUANTUM-ARMv9 64-BIT // 16 CORES ONLINE",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWhite,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "RAM CHECK: $postMemAddress OK",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CyberBlue,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "SCADA TUNNELS: BOUND TO LOCAL MESH SUBNET",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWhite.copy(alpha = 0.85f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "[SYS] LOADING SYSTEM SERVICES...",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberBlueBright,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            BootPhase.MINIMAL_X -> {
                Canvas(modifier = Modifier.size(90.dp)) {
                    val w = size.width
                    val h = size.height
                    drawLine(color = Color.White, start = Offset(0f, 0f), end = Offset(w, h), strokeWidth = 2.5f)
                    drawLine(color = Color.White, start = Offset(0f, h), end = Offset(w, 0f), strokeWidth = 2.5f)
                    drawCircle(color = CyberBlueBright.copy(alpha = 0.5f), radius = w * 0.6f, style = Stroke(1.2f))
                }
            }

            BootPhase.SYSTEM_LOADING -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Surface(color = Color.White, modifier = Modifier.width(32.dp).height(12.dp)) {}
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(color = CyberBlueBright, modifier = Modifier.width(64.dp).height(12.dp)) {}
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(color = Color.White, modifier = Modifier.width(96.dp).height(12.dp)) {}
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "System Loading",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextWhite,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "ctOS Subnet Handshake...",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CyberBlueBright,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            BootPhase.HEXAGON_CIPHER -> {
                Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.width * 0.45f
                        val path = Path()
                        val sides = 6
                        for (i in 0 until sides) {
                            val angle = Math.toRadians((i * (360.0 / sides) - 15.0))
                            val x = center.x + (radius * cos(angle)).toFloat()
                            val y = center.y + (radius * sin(angle)).toFloat()
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        path.close()
                        drawPath(path = path, color = CyberBlueBright, style = Stroke(width = 2f))
                        drawCircle(color = Color.White.copy(alpha = 0.4f), radius = radius * 0.7f, center = center, style = Stroke(1f))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = cipherString,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = TextWhite,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "[DECRYPTING]",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberBlueBright,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            BootPhase.CTOS_BEAM_LOGO -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.68f)
                            .height(44.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color.White, CyberBlueBright, CyberBlueBright.copy(alpha = 0.8f), Color.Transparent)
                                )
                            )
                    )
                    Row(
                        modifier = Modifier.padding(start = 110.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "CT",
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        color = TextWhite,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "OS",
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        color = TextWhite,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 58.sp,
                                        letterSpacing = (-1).sp
                                    )
                                )
                            }
                            Text(
                                text = "ctOS Authorized Device // Bay Area Cell",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CyberBlueBright,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            BootPhase.ISOMETRIC_EMBLEM -> {
                Box(modifier = Modifier.size(180.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val outerRadius = size.width * 0.46f

                        drawCircle(color = CyberBlueBright, radius = outerRadius, center = center, style = Stroke(width = 1.8f))
                        val r = outerRadius * 0.58f
                        val top = Offset(center.x, center.y - r)
                        val bottom = Offset(center.x, center.y + r)
                        val leftTop = Offset(center.x - r * 0.866f, center.y - r * 0.5f)
                        val leftBottom = Offset(center.x - r * 0.866f, center.y + r * 0.5f)
                        val rightTop = Offset(center.x + r * 0.866f, center.y - r * 0.5f)
                        val rightBottom = Offset(center.x + r * 0.866f, center.y + r * 0.5f)

                        val hexPath = Path().apply {
                            moveTo(top.x, top.y)
                            lineTo(rightTop.x, rightTop.y)
                            lineTo(rightBottom.x, rightBottom.y)
                            lineTo(bottom.x, bottom.y)
                            lineTo(leftBottom.x, leftBottom.y)
                            lineTo(leftTop.x, leftTop.y)
                            close()
                        }
                        drawPath(hexPath, color = Color.White, style = Stroke(width = 1.8f))
                        drawLine(color = CyberBlueBright, start = center, end = top, strokeWidth = 1.8f)
                        drawLine(color = CyberBlueBright, start = center, end = leftBottom, strokeWidth = 1.8f)
                        drawLine(color = CyberBlueBright, start = center, end = rightBottom, strokeWidth = 1.8f)
                        drawLine(color = Color.White, start = leftTop, end = center, strokeWidth = 1.2f)
                        drawLine(color = Color.White, start = rightTop, end = center, strokeWidth = 1.2f)
                        drawLine(color = Color.White, start = bottom, end = center, strokeWidth = 1.2f)
                    }
                }
            }

            BootPhase.DEDSEC_HIJACK_BOXES, BootPhase.BOOT_COMPLETE -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "[!] cTOS 2.0 PROTOCOL OVERRIDDEN",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = DedsecPink,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "DECSEC 1.0 OPERATIVE SUITE INITIALIZED",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextBlue)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    var globalLetterCounter = 0
                    targetWords.forEach { wordLetters ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 5.dp)
                        ) {
                            wordLetters.forEach { letter ->
                                val letterGlobalIndex = globalLetterCounter
                                globalLetterCounter++

                                val isRevealed = letterGlobalIndex < revealedCount
                                val isCurrentlyScrambling = letterGlobalIndex == revealedCount

                                val boxChar = when {
                                    isRevealed -> letter
                                    isCurrentlyScrambling -> cipherCharset.random().toString()
                                    else -> " "
                                }

                                val boxBorderColor = when {
                                    isRevealed -> CyberBlueBright
                                    isCurrentlyScrambling -> DedsecPink
                                    else -> TerminalBorder
                                }

                                val boxBgColor = when {
                                    isRevealed -> CyberBlack
                                    isCurrentlyScrambling -> DedsecPink.copy(alpha = 0.2f)
                                    else -> Color(0xFF040A12)
                                }

                                val boxTextColor = when {
                                    isRevealed -> TextWhite
                                    isCurrentlyScrambling -> DedsecPink
                                    else -> Color.Transparent
                                }

                                Surface(
                                    color = boxBgColor,
                                    shape = CutCornerShape(4.dp),
                                    border = BorderStroke(1.5.dp, boxBorderColor),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = boxChar,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                color = boxTextColor,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 18.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Surface(
                        color = CyberBlueBright.copy(alpha = 0.2f),
                        shape = CutCornerShape(4.dp),
                        border = BorderStroke(1.dp, CyberBlueBright),
                        modifier = Modifier
                            .clickable { onBootFinished() }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "INITIALIZATION COMPLETE // TAP TO ENTER",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Bottom Progress Bar in Cyber Blue and White
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = { bootProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = if (phase == BootPhase.DEDSEC_HIJACK_BOXES) CyberBlueBright else Color.White,
                trackColor = Color.White.copy(alpha = 0.15f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "TAP SCREEN TO SKIP",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 10.sp
                )
            )
        }
    }
}
