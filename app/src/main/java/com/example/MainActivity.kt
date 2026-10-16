package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.audio.TerminalSoundManager
import com.example.ui.components.BlackoutOverlay
import com.example.ui.components.CallInterceptDialog
import com.example.ui.components.DedsecHeaderBanner
import com.example.ui.components.HackerScrambleText
import com.example.ui.screens.BootScreen
import com.example.ui.screens.CipherScreen
import com.example.ui.screens.CtosControlPanelScreen
import com.example.ui.screens.NetworkScannerScreen
import com.example.ui.screens.ProfilerScreen
import com.example.ui.screens.TerminalScreen
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DedsecPink
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureBlackText
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalDark
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TerminalSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.viewmodel.HackerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DedsecApp()
            }
        }
    }
}

@Composable
fun DedsecApp(viewModel: HackerViewModel = viewModel()) {
    val showBootScreen by viewModel.showBootScreen.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val terminalLines by viewModel.terminalLines.collectAsState()
    val currentInput by viewModel.currentPromptInput.collectAsState()
    val networkInfo by viewModel.realNetworkInfo.collectAsState()
    val networkNodes by viewModel.networkNodes.collectAsState()
    val nearbyDevices by viewModel.nearbyDevices.collectAsState()
    val activeTappedDevice by viewModel.activeTappedDevice.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val profilerTargets by viewModel.profilerTargets.collectAsState()
    val botnetStatus by viewModel.botnetStatus.collectAsState()
    val cipherGame by viewModel.cipherGame.collectAsState()
    val isBlackoutActive by viewModel.isBlackoutActive.collectAsState()

    // If initial boot sequence is active, show the Watch Dogs ctOS startup animation
    if (showBootScreen) {
        BootScreen(
            soundManager = viewModel.soundManager,
            onBootFinished = { viewModel.finishBoot() }
        )
        return
    }

    // Handle back button: return to ctOS Panel (tab 0) if on secondary tab
    BackHandler(enabled = selectedTab != 0) {
        viewModel.setSelectedTab(0)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PureBlack)
                    .statusBarsPadding()
            ) {
                DedsecHeaderBanner(
                    onAudioToggle = {
                        viewModel.soundManager.isSoundEnabled = !viewModel.soundManager.isSoundEnabled
                        viewModel.executeCommand("audio " + if (viewModel.soundManager.isSoundEnabled) "on" else "off")
                    },
                    isAudioOn = viewModel.soundManager.isSoundEnabled
                )
            }
        },
        bottomBar = {
            DedsecBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.setSelectedTab(it) },
                soundManager = viewModel.soundManager,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PureBlack)
        ) {
            when (selectedTab) {
                0 -> CtosControlPanelScreen(
                    soundManager = viewModel.soundManager,
                    networkInfo = networkInfo,
                    nearbyDevices = nearbyDevices,
                    botnetStatus = botnetStatus,
                    onNavigateTab = { viewModel.setSelectedTab(it) },
                    onTapNearbyDevice = { dev -> viewModel.tapDeviceCall(dev) },
                    onExecuteCommand = { cmd -> viewModel.executeCommand(cmd) }
                )
                1 -> TerminalScreen(
                    terminalLines = terminalLines,
                    inputText = currentInput,
                    onInputChange = { viewModel.updatePromptInput(it) },
                    onExecuteInput = { viewModel.executeCurrentInput() },
                    onQuickCommand = { cmd -> viewModel.executeCommand(cmd) }
                )
                2 -> NetworkScannerScreen(
                    networkInfo = networkInfo,
                    nodes = networkNodes,
                    nearbyDevices = nearbyDevices,
                    isScanning = isScanning,
                    onRescan = { viewModel.startNetworkScan() },
                    onExploitNode = { node, exp -> viewModel.triggerNodeExploit(node, exp) },
                    onTapDeviceCall = { dev -> viewModel.tapDeviceCall(dev) },
                    onHackDevice = { dev -> viewModel.hackNearbyDevice(dev) }
                )
                3 -> ProfilerScreen(
                    targets = profilerTargets,
                    onDrainCrypto = { tgt -> viewModel.drainCrypto(tgt) },
                    onDistract = { tgt -> viewModel.distractTarget(tgt) }
                )
                4 -> CipherScreen(
                    cipherGame = cipherGame,
                    onCellClicked = { cell -> viewModel.onCipherCellClicked(cell) },
                    onReset = { viewModel.resetCipherGame() },
                    soundManager = viewModel.soundManager
                )
            }

            // Blackout visual effect overlay
            BlackoutOverlay(isActive = isBlackoutActive)

            // Live Call Intercept / Wiretap Audio HUD Dialog
            activeTappedDevice?.let { tappedDev ->
                CallInterceptDialog(
                    device = tappedDev,
                    soundManager = viewModel.soundManager,
                    onDismiss = { viewModel.closeWiretap() },
                    onExtractCredentials = { viewModel.extractIntelFromCall(tappedDev) }
                )
            }
        }
    }
}

@Composable
fun DedsecBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    soundManager: TerminalSoundManager,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavigationItem("PANEL", Icons.Default.Dashboard, "nav_panel"),
        NavigationItem("TERMINAL", Icons.Default.Terminal, "nav_terminal"),
        NavigationItem("SCANNER", Icons.Default.Wifi, "nav_scanner"),
        NavigationItem("PROFILER", Icons.Default.Person, "nav_profiler"),
        NavigationItem("CIPHER", Icons.Default.Memory, "nav_cipher")
    )

    NavigationBar(
        containerColor = PureBlack,
        tonalElevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, NeonGreen.copy(alpha = 0.5f), CutCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .testTag("dedsec_bottom_navigation")
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedTab == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    if (isSelected) {
                        HackerScrambleText(
                            text = item.label,
                            color = PureBlackText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            onLetterLoaded = { soundManager.playLetterLoadSound() }
                        )
                    } else {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = NeonGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Normal,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PureBlackText,
                    selectedTextColor = PureBlackText,
                    indicatorColor = NeonGreen,
                    unselectedIconColor = NeonGreen.copy(alpha = 0.7f),
                    unselectedTextColor = NeonGreen.copy(alpha = 0.7f)
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}

data class NavigationItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)
