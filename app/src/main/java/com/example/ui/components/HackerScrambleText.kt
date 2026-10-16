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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PureBlackText
import com.example.ui.theme.ShareTechMonoFontFamily
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val GLYPH_POOL = "01#@%$&><§Ø!?*+=-_/\\[]"

/**
 * Hacker Scramble Text effect that deciphers and loads letter-by-letter.
 * Text is styled in either phosphor green or black on black background.
 */
@Composable
fun HackerScrambleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = NeonGreen,
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
            // Scramble for 2-3 ticks before locking
            repeat(2) {
                scrambleChar = GLYPH_POOL.random()
                delay(18)
            }
            revealedCount = i + 1
            onLetterLoaded?.invoke()
            delay(15)
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
