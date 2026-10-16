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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Memory
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.TerminalSoundManager
import com.example.model.CipherCell
import com.example.model.CipherGame
import com.example.ui.components.CrtScanlineOverlay
import com.example.ui.components.HackerScrambleText
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DedsecPink
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.NeonGreenDim
import com.example.ui.theme.NeonGreenMuted
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureBlackText
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WarningAmber

@Composable
fun CipherScreen(
    cipherGame: CipherGame,
    onCellClicked: (CipherCell) -> Unit,
    onReset: () -> Unit,
    soundManager: TerminalSoundManager = TerminalSoundManager(LocalContext.current),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .testTag("cipher_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Card(
                shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                colors = CardDefaults.cardColors(containerColor = PureBlack),
                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f)),
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
                            imageVector = Icons.Default.Memory,
                            contentDescription = "Cipher",
                            tint = NeonGreenBright,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "DECSEC 1.0 // BUFFER OVERFLOW",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = NeonGreenBright,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "CIRCUIT MEMORY INJECTION PROTOCOL",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }

                    Surface(
                        color = if (cipherGame.movesRemaining <= 2) AlertRed else PureBlack,
                        border = BorderStroke(1.dp, NeonGreen),
                        shape = CutCornerShape(2.dp)
                    ) {
                        Text(
                            text = "MOVES: ${cipherGame.movesRemaining}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (cipherGame.movesRemaining <= 2) PureBlackText else NeonGreenBright,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Target Sequence Box
            Card(
                shape = CutCornerShape(6.dp),
                colors = CardDefaults.cardColors(containerColor = PureBlack),
                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "REQUIRED CODE MATRIX SEQUENCE:",
                        style = MaterialTheme.typography.labelSmall.copy(color = NeonGreen)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        cipherGame.targetSequence.forEachIndexed { idx, hex ->
                            val matched = idx < cipherGame.currentInput.size && cipherGame.currentInput[idx] == hex
                            Surface(
                                color = if (matched) NeonGreen else PureBlack,
                                shape = CutCornerShape(4.dp),
                                border = BorderStroke(1.dp, NeonGreen)
                            ) {
                                Text(
                                    text = hex,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = if (matched) PureBlackText else NeonGreenBright,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Buffer input display
            Card(
                shape = CutCornerShape(6.dp),
                colors = CardDefaults.cardColors(containerColor = PureBlack),
                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BUFFER [${cipherGame.currentInput.size}/6]: ",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (cipherGame.currentInput.isEmpty()) "[EMPTY]" else cipherGame.currentInput.joinToString(" "),
                        style = MaterialTheme.typography.bodyMedium.copy(color = NeonGreenBright, fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4x4 Code Matrix Tiles
            Card(
                shape = CutCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = PureBlack),
                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    cipherGame.grid.forEachIndexed { rIdx, row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            row.forEachIndexed { cIdx, cell ->
                                val isRowActive = cipherGame.activeRow == rIdx
                                val isColActive = cipherGame.activeCol == cIdx
                                val isClickable = !cipherGame.isSolved && !cipherGame.isFailed && !cell.isSelected &&
                                        (cipherGame.activeRow == null || cipherGame.activeRow == rIdx) &&
                                        (cipherGame.activeCol == null || cipherGame.activeCol == cIdx)

                                val borderColor = when {
                                    cell.isSelected -> NeonGreenBright
                                    isClickable -> NeonGreen
                                    isRowActive || isColActive -> NeonGreenDim
                                    else -> NeonGreenMuted
                                }

                                // Background is strictly black, except active selected tile changes to green with black text
                                val bgColor = when {
                                    cell.isSelected -> NeonGreen
                                    else -> PureBlack
                                }

                                // Text changes to black or green
                                val textColor = when {
                                    cell.isSelected -> PureBlackText
                                    isClickable -> NeonGreenBright
                                    isRowActive || isColActive -> NeonGreen
                                    else -> TextMuted
                                }

                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(bgColor, CutCornerShape(4.dp))
                                        .border(1.dp, borderColor, CutCornerShape(4.dp))
                                        .clickable(enabled = isClickable) {
                                            soundManager.playTileMoveSound()
                                            onCellClicked(cell)
                                        }
                                        .testTag("cipher_cell_${rIdx}_${cIdx}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (cell.isSelected) "--" else cell.hexValue,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = textColor,
                                            fontWeight = if (isClickable || cell.isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Status feedback or reset
            if (cipherGame.isSolved) {
                Surface(
                    color = NeonGreen,
                    shape = CutCornerShape(6.dp),
                    border = BorderStroke(1.dp, NeonGreenBright),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Solved", tint = PureBlackText)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("SECURITY BYPASSED!", style = MaterialTheme.typography.titleMedium.copy(color = PureBlackText, fontWeight = FontWeight.Bold))
                            Text("+35,000 DECSEC FOLLOWERS REWARDED", style = MaterialTheme.typography.bodySmall.copy(color = PureBlackText))
                        }
                    }
                }
            } else if (cipherGame.isFailed) {
                Surface(
                    color = PureBlack,
                    shape = CutCornerShape(6.dp),
                    border = BorderStroke(1.dp, AlertRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Error, contentDescription = "Failed", tint = AlertRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("BUFFER OVERFLOW FAILED! TRACE INITIATED.", style = MaterialTheme.typography.labelMedium.copy(color = AlertRed))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    soundManager.playTileMoveSound()
                    onReset()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PureBlack,
                    contentColor = NeonGreen
                ),
                shape = CutCornerShape(4.dp),
                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.7f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_cipher_button")
            ) {
                Icon(imageVector = Icons.Default.LockReset, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("RELOAD NEW MEMORY BUFFER", color = NeonGreen)
            }
        }

        CrtScanlineOverlay(scanlineAlpha = 0.05f)
    }
}
