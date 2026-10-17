package com.example.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.example.ui.theme.CyberBlueBright
import com.example.ui.theme.ShareTechMonoFontFamily
import kotlinx.coroutines.delay

private const val GLYPH_POOL = "01#@%$&><§Ø!?*+=-_/\\[]"

/**
 * Hacker Scramble Text effect that deciphers and loads letter-by-letter.
 * Text font color defaults to futuristic cyber blue and white.
 * Triggers audio on each individual letter reveal.
 */
@Composable
fun HackerScrambleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = CyberBlueBright,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    triggerKey: Any? = null,
    onLetterLoaded: (() -> Unit)? = null
) {
    var revealedCount by remember(text, triggerKey) { mutableIntStateOf(0) }
    var scrambleChar by remember(text, triggerKey) { mutableStateOf(' ') }

    LaunchedEffect(text, triggerKey) {
        revealedCount = 0
        for (i in text.indices) {
            // Scramble for 2-3 ticks before locking character
            repeat(2) {
                scrambleChar = GLYPH_POOL.random()
                delay(16)
            }
            revealedCount = i + 1
            onLetterLoaded?.invoke()
            delay(18)
        }
    }

    val displayText = buildString {
        for (i in text.indices) {
            if (i < revealedCount) {
                append(text[i])
            } else if (i == revealedCount) {
                append(scrambleChar)
            } else {
                append(' ')
            }
        }
    }

    Text(
        text = displayText,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = ShareTechMonoFontFamily,
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight
        )
    )
}
