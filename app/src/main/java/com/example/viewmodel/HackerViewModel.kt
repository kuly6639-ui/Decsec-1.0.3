package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.CentralAudioController
import com.example.model.BotnetStatus
import com.example.model.CipherCell
import com.example.model.CipherGame
import com.example.model.DeviceCategory
import com.example.model.ExploitStatus
import com.example.model.InterceptedCallMessage
import com.example.model.LineType
import com.example.model.NearbyElectronicDevice
import com.example.model.NetworkNode
import com.example.model.NodeExploit
import com.example.model.NodeType
import com.example.model.ProfilerTarget
import com.example.model.RealNetworkInfo
import com.example.model.TerminalLine
import com.example.model.ThreatLevel
import com.example.network.NetworkScannerEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class HackerViewModel(application: Application) : AndroidViewModel(application) {

    val audioController = CentralAudioController(application)
    val soundManager = audioController
    private val scannerEngine = NetworkScannerEngine(application)

    private val _terminalLines = MutableStateFlow<List<TerminalLine>>(emptyList())
    val terminalLines: StateFlow<List<TerminalLine>> = _terminalLines.asStateFlow()

    private val _networkNodes = MutableStateFlow<List<NetworkNode>>(emptyList())
    val networkNodes: StateFlow<List<NetworkNode>> = _networkNodes.asStateFlow()

    private val _realNetworkInfo = MutableStateFlow(scannerEngine.getRealNetworkInfo())
    val realNetworkInfo: StateFlow<RealNetworkInfo> = _realNetworkInfo.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _profilerTargets = MutableStateFlow<List<ProfilerTarget>>(emptyList())
    val profilerTargets: StateFlow<List<ProfilerTarget>> = _profilerTargets.asStateFlow()

    private val _botnetStatus = MutableStateFlow(BotnetStatus())
    val botnetStatus: StateFlow<BotnetStatus> = _botnetStatus.asStateFlow()

    private val _cipherGame = MutableStateFlow(createInitialCipherGame())
    val cipherGame: StateFlow<CipherGame> = _cipherGame.asStateFlow()

    private val _isBlackoutActive = MutableStateFlow(false)
    val isBlackoutActive: StateFlow<Boolean> = _isBlackoutActive.asStateFlow()

    private val _showBootScreen = MutableStateFlow(true)
    val showBootScreen: StateFlow<Boolean> = _showBootScreen.asStateFlow()

    private val _nearbyDevices = MutableStateFlow<List<NearbyElectronicDevice>>(emptyList())
    val nearbyDevices: StateFlow<List<NearbyElectronicDevice>> = _nearbyDevices.asStateFlow()

    private val _activeTappedDevice = MutableStateFlow<NearbyElectronicDevice?>(null)
    val activeTappedDevice: StateFlow<NearbyElectronicDevice?> = _activeTappedDevice.asStateFlow()

    private val _currentPromptInput = MutableStateFlow("")
    val currentPromptInput: StateFlow<String> = _currentPromptInput.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    init {
        initDedsecWelcome()
        initProfilerTargets()
        initNearbyDevices()
        startNetworkScan()
    }

    fun finishBoot() {
        _showBootScreen.value = false
        audioController.playTerminalExecute()
    }

    fun restartBoot() {
        _showBootScreen.value = true
        audioController.playBootRelaySound()
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
        audioController.playTileMoveSound()
    }

    fun onTileMoved() {
        audioController.playTileMoveSound()
    }

    fun updatePromptInput(text: String) {
        _currentPromptInput.value = text
        audioController.playTerminalKeystroke()
    }

    private fun initDedsecWelcome() {
        val banner = listOf(
            TerminalLine(text = "==========================================================", type = LineType.CYBER_BANNER),
            TerminalLine(text = " [!] DECSEC 1.0 // cTOS 2.0 ZERO-DAY EXPLOIT TOOLKIT", type = LineType.CYBER_BANNER),
            TerminalLine(text = " [!] OPERATIVE: MARCUS // BAY AREA CELL // STATUS: STEALTH", type = LineType.CYBER_BANNER),
            TerminalLine(text = " [!] JOIN THE RESISTANCE. WE ARE THE FUTURE.", type = LineType.CYBER_BANNER),
            TerminalLine(text = "==========================================================", type = LineType.CYBER_BANNER),
            TerminalLine(text = "[SYS] Initializing socket tunnels on ${_realNetworkInfo.value.interfaceName}...", type = LineType.SYSTEM),
            TerminalLine(text = "[NET] Local IP bound to ${_realNetworkInfo.value.localIp} via ${_realNetworkInfo.value.networkType}", type = LineType.SYSTEM),
            TerminalLine(text = "[AUTH] Blume ctOS master certificate bypassed via DecSec 1.0 overflow.", type = LineType.SUCCESS),
            TerminalLine(text = "Type 'help' for command suite, or tap quick hack chips below.", type = LineType.OUTPUT)
        )
        _terminalLines.value = banner
    }

    private fun initProfilerTargets() {
        _profilerTargets.value = listOf(
            ProfilerTarget(
                id = "TGT-001",
                name = "Miranda Comay",
                age = 34,
                occupation = "Blume Chief Security Architect",
                incomeFormatted = "$285,000",
                cryptoBalance = 4.82,
                threatLevel = ThreatLevel.CRITICAL,
                ctOSFactoid = "Secretly leaking ctOS biometric telemetry to offshore servers",
                phoneModel = "Blume Quantum Secure Phone 14"
            ),
            ProfilerTarget(
                id = "TGT-002",
                name = "Marcus 'Wrench' Jr.",
                age = 26,
                occupation = "Hardware Modder & Anarchist",
                incomeFormatted = "$42,000",
                cryptoBalance = 1.15,
                threatLevel = ThreatLevel.LOW,
                ctOSFactoid = "3D printing EMP grenades in garage workshop",
                phoneModel = "Custom DedSec Linux Handheld"
            ),
            ProfilerTarget(
                id = "TGT-003",
                name = "Horatio Carlin",
                age = 29,
                occupation = "Nudle Big Data Analyst",
                incomeFormatted = "$168,000",
                cryptoBalance = 2.45,
                threatLevel = ThreatLevel.MODERATE,
                ctOSFactoid = "Discovered algorithmic bias in city bail sentencing database",
                phoneModel = "Nudle Pixel 9 Pro"
            ),
            ProfilerTarget(
                id = "TGT-004",
                name = "Agent Raymond Kenney",
                age = 52,
                occupation = "Ex-ctOS Core Developer / Paranoiac",
                incomeFormatted = "$0 (OFF-GRID)",
                cryptoBalance = 18.90,
                threatLevel = ThreatLevel.HIGH,
                ctOSFactoid = "Lives inside a junk yard bunker armed with signal jammers",
                phoneModel = "Air-gapped Toughbook"
            )
        )
    }

    private fun initNearbyDevices() {
        _nearbyDevices.value = listOf(
            NearbyElectronicDevice(
                id = "DEV-TV-01",
                name = "Samsung Neo QLED 65\" 4K TV",
                category = DeviceCategory.SMART_TV,
                vendor = "Samsung Electronics",
                model = "QN90C Smart Hub",
                distanceMeters = 3.2f,
                signalDbm = -48,
                ip = "192.168.1.42",
                protocol = "Tizen OS / Cast / UPnP",
                callSubject = "Smart Assistant Voice Stream",
                callerNumber = "TV_MIC://LivingRoom_Hub",
                callTranscript = listOf(
                    InterceptedCallMessage("Owner", "Alexa, dim the living room neon lights to 30%", 2),
                    InterceptedCallMessage("Assistant", "Living room lights set to 30% cyber green.", 5),
                    InterceptedCallMessage("Owner", "Now play DedSec radio stream on maximum bass.", 9)
                )
            ),
            NearbyElectronicDevice(
                id = "DEV-PH-02",
                name = "iPhone 16 Pro (Blume Operative)",
                category = DeviceCategory.SMARTPHONE,
                vendor = "Apple Inc.",
                model = "A3102 Pro Max",
                distanceMeters = 1.8f,
                signalDbm = -35,
                ip = "192.168.1.66",
                protocol = "AirDrop / BLE / Wi-Fi 7",
                callSubject = "Blume Security Dispatch",
                callerNumber = "+1 (415) 555-0812",
                callTranscript = listOf(
                    InterceptedCallMessage("Blume Operative", "Have we patched the ctOS zero-day in the bay traffic grid?", 1),
                    InterceptedCallMessage("Dispatch", "Negative. DedSec hijacked the Blume master certificate.", 4),
                    InterceptedCallMessage("Blume Operative", "Deploy combat drones to the data facility immediately.", 8)
                )
            ),
            NearbyElectronicDevice(
                id = "DEV-PH-03",
                name = "Google Pixel 9 Pro",
                category = DeviceCategory.SMARTPHONE,
                vendor = "Google",
                model = "Pixel 9 Pro",
                distanceMeters = 2.5f,
                signalDbm = -42,
                ip = "192.168.1.89",
                protocol = "Nearby Share / Wi-Fi",
                callSubject = "Facility Gate Passcode Call",
                callerNumber = "+1 (415) 555-3491",
                callTranscript = listOf(
                    InterceptedCallMessage("Driver", "Hey, I'm dropping off the server rack hardware at the bay gate.", 2),
                    InterceptedCallMessage("Security", "Security gate passcode is 7-4-9-2 pound. Do not write it down.", 6),
                    InterceptedCallMessage("Driver", "Got it, 7492#. Heading into the underground garage now.", 10)
                )
            ),
            NearbyElectronicDevice(
                id = "DEV-SPK-04",
                name = "Amazon Echo Dot (4th Gen)",
                category = DeviceCategory.SMART_SPEAKER,
                vendor = "Amazon",
                model = "Echo Dot Gen 4",
                distanceMeters = 4.1f,
                signalDbm = -55,
                ip = "192.168.1.15",
                protocol = "Zigbee / Alexa Voice",
                callSubject = "Room Microphone Audio Stream",
                callerNumber = "ECHO_AUDIO://Room_Mic",
                callTranscript = listOf(
                    InterceptedCallMessage("Citizen", "Where did I save the cold wallet seed phrase?", 3),
                    InterceptedCallMessage("Citizen", "Right, encrypted USB drive inside the desk drawer.", 7),
                    InterceptedCallMessage("Assistant", "I found 3 search results for USB drive.", 11)
                )
            ),
            NearbyElectronicDevice(
                id = "DEV-TV-05",
                name = "LG C3 OLED 55\" Smart TV",
                category = DeviceCategory.SMART_TV,
                vendor = "LG Electronics",
                model = "OLED55C3PUA",
                distanceMeters = 5.4f,
                signalDbm = -62,
                ip = "192.168.1.103",
                protocol = "webOS / Miracast",
                callSubject = "Conference Boardroom Meeting",
                callerNumber = "LG_CONF://Room_B",
                callTranscript = listOf(
                    InterceptedCallMessage("Director", "The city council approved the ctOS 2.0 biometric contract.", 2),
                    InterceptedCallMessage("Counsel", "If DedSec leaks this to the public, there will be riots.", 6)
                )
            ),
            NearbyElectronicDevice(
                id = "DEV-LAP-06",
                name = "MacBook Pro M3 Max",
                category = DeviceCategory.LAPTOP,
                vendor = "Apple Inc.",
                model = "MacBookPro18,2",
                distanceMeters = 2.0f,
                signalDbm = -38,
                ip = "192.168.1.22",
                protocol = "SSH / TLS / mDNS",
                callSubject = "VoIP Developer Standup",
                callerNumber = "VOIP://DevOps_Bridge",
                callTranscript = listOf(
                    InterceptedCallMessage("DevOps Lead", "Deploying build v2.4.9 to the Blume cloud cluster.", 3),
                    InterceptedCallMessage("Engineer", "Confirmed. Keep the fallback firewall port 8080 open.", 7)
                )
            )
        )
    }

    fun tapDeviceCall(device: NearbyElectronicDevice) {
        soundManager.playPhoneRing()
        _activeTappedDevice.value = device
        _nearbyDevices.update { list ->
            list.map { if (it.id == device.id) it.copy(isTapped = true, isHacked = true) else it }
        }
        addLine(TerminalLine(text = "[WIRETAP ACTIVE] Intercepting encrypted voice stream from ${device.name} (${device.ip})...", type = LineType.SUCCESS))
    }

    fun closeWiretap() {
        _activeTappedDevice.value = null
        soundManager.playTerminalBeep()
    }

    fun extractIntelFromCall(device: NearbyElectronicDevice) {
        val gain = 12500
        _botnetStatus.update { it.copy(followers = it.followers + gain) }
        soundManager.playAccessGranted()
        addLine(TerminalLine(text = "[INTEL EXTRACTED] Cryptographic audio decrypted from ${device.name}! +12,500 Followers!", type = LineType.CYBER_BANNER))
        closeWiretap()
    }

    fun hackNearbyDevice(device: NearbyElectronicDevice) {
        viewModelScope.launch {
            soundManager.playDataStream()
            delay(300)
            _nearbyDevices.update { list ->
                list.map { if (it.id == device.id) it.copy(isHacked = true) else it }
            }
            soundManager.playAccessGranted()
            val gain = 8000
            _botnetStatus.update { it.copy(followers = it.followers + gain) }
            addLine(TerminalLine(text = "[DEVICE BREACHED] ${device.name} compromised via CVE-2026-BLE exploit! +8,000 Followers!", type = LineType.SUCCESS))
        }
    }

    fun executeCurrentInput() {
        val input = _currentPromptInput.value.trim()
        if (input.isEmpty()) return
        _currentPromptInput.value = ""
        executeCommand(input)
    }

    fun executeCommand(commandStr: String) {
        val line = commandStr.trim()
        if (line.isEmpty()) return

        // Add user command line
        addLine(TerminalLine(text = "dedsec@ctos:~# $line", type = LineType.COMMAND))
        soundManager.playTerminalBeep()

        val tokens = line.split("\\s+".toRegex())
        val cmd = tokens[0].lowercase()
        val arg = tokens.getOrNull(1)?.lowercase() ?: ""

        viewModelScope.launch {
            when (cmd) {
                "help" -> showHelp()
                "scan", "netscan" -> {
                    addLine(TerminalLine(text = "[*] Initiating high-speed ctOS 2.0 Wi-Fi and SCADA subnet sweep...", type = LineType.SYSTEM))
                    startNetworkScan()
                }
                "ctos" -> {
                    when (arg) {
                        "nodes" -> showCtosNodes()
                        "blackout" -> triggerBlackout()
                        else -> {
                            addLine(TerminalLine(text = "ctOS Grid Status: ONLINE (COMPROMISED BY DEDSEC)", type = LineType.SUCCESS))
                            addLine(TerminalLine(text = "Nodes in range: ${_networkNodes.value.size} | Vulnerable: ${_networkNodes.value.count { it.status == ExploitStatus.VULNERABLE }}", type = LineType.OUTPUT))
                            addLine(TerminalLine(text = "Usage: ctos [nodes | blackout | scan]", type = LineType.WARNING))
                        }
                    }
                }
                "profile" -> {
                    if (arg.isEmpty()) {
                        addLine(TerminalLine(text = "[!] ctOS Facial & Network Profiler Active: ${profilerTargets.value.size} entities locked.", type = LineType.SYSTEM))
                        profilerTargets.value.forEach { tgt ->
                            addLine(TerminalLine(text = " > [${tgt.name}] - ${tgt.occupation} ($${tgt.incomeFormatted}) | Threat: ${tgt.threatLevel}", type = LineType.OUTPUT))
                        }
                        addLine(TerminalLine(text = "Switch to PROFILER tab for biometric drilldown and bank siphon.", type = LineType.WARNING))
                    } else {
                        val found = profilerTargets.value.find { it.name.lowercase().contains(arg) }
                        if (found != null) {
                            addLine(TerminalLine(text = "[TARGET ACQUIRED] ${found.name} (${found.age})", type = LineType.SUCCESS))
                            addLine(TerminalLine(text = "Occupation: ${found.occupation} | Income: ${found.incomeFormatted}", type = LineType.OUTPUT))
                            addLine(TerminalLine(text = "Crypto Vault: ${found.cryptoBalance} BTC", type = LineType.OUTPUT))
                            addLine(TerminalLine(text = "ctOS Factoid: ${found.ctOSFactoid}", type = LineType.WARNING))
                        } else {
                            addLine(TerminalLine(text = "Target '$arg' not found in optical database.", type = LineType.ERROR))
                        }
                    }
                }
                "ping" -> {
                    val target = if (arg.isNotEmpty()) arg else "CONNECTED_NODES"
                    addLine(TerminalLine(text = "[>] INITIATING NETWORK LATENCY SCAN ACROSS CONNECTED NODES...", type = LineType.SYSTEM))
                    delay(180)

                    val nodesToPing = if (target == "CONNECTED_NODES") {
                        val base = mutableListOf(
                            Triple("ctos.blume.net [MASTER BACKBONE]", 16L, 0.95f),
                            Triple("192.168.1.1 [SUBNET GATEWAY]", 8L, 0.98f),
                            Triple("CTOS-TRF-881 [TRAFFIC SCADA JUNCTION]", 24L, 0.88f),
                            Triple("CTOS-PWR-092 [SUBSTATION RELAY]", 34L, 0.78f)
                        )
                        _nearbyDevices.value.take(2).forEach { dev ->
                            base.add(Triple("${dev.name} [${dev.ip}]", Random.nextLong(12, 36), 0.92f))
                        }
                        base
                    } else {
                        val nearbyMatch = _nearbyDevices.value.find { it.name.lowercase().contains(arg) || it.id.lowercase() == arg }
                        val name = nearbyMatch?.let { "${it.name} [${it.ip}]" } ?: target
                        listOf(
                            Triple(name, Random.nextLong(12, 42), 0.92f)
                        )
                    }

                    for ((nodeName, latency, signal) in nodesToPing) {
                        audioController.playPingPulse()
                        delay(220)
                        val barFill = (signal * 18).toInt()
                        val barStr = "█".repeat(barFill) + "░".repeat(18 - barFill)
                        addLine(
                            TerminalLine(
                                text = "PING $nodeName | RTT: ${latency}ms | [$barStr] ${(signal * 100).toInt()}% [ONLINE]",
                                type = LineType.PING_PROGRESS,
                                progress = signal,
                                latencyMs = latency,
                                isFlickering = true
                            )
                        )
                    }
                    delay(120)
                    audioController.playAccessGranted()
                    addLine(TerminalLine(text = "[✓] PING COMPLETE: ${nodesToPing.size} NODES LOCKED (0% PACKET LOSS // AVG RTT 18.2ms)", type = LineType.SUCCESS))
                }
                "exploit" -> {
                    if (arg.isEmpty()) {
                        addLine(TerminalLine(text = "Usage: exploit <node_id> (e.g. exploit CTOS-TRF-881)", type = LineType.WARNING))
                    } else {
                        val targetNode = _networkNodes.value.find { it.id.lowercase() == arg }
                        if (targetNode != null) {
                            triggerNodeExploit(targetNode, targetNode.exploits.firstOrNull())
                        } else {
                            addLine(TerminalLine(text = "Node '$arg' unreachable or non-existent in active subnet.", type = LineType.ERROR))
                            soundManager.playAccessDenied()
                        }
                    }
                }
                "blackout" -> triggerBlackout()
                "decrypt", "cipher" -> {
                    addLine(TerminalLine(text = "[*] Launching hex buffer injection matrix puzzle...", type = LineType.SYSTEM))
                    _selectedTab.value = 3 // Switch to cipher screen
                }
                "botnet" -> {
                    val b = _botnetStatus.value
                    addLine(TerminalLine(text = "=== DEDSEC GLOBAL BOTNET TELEMETRY ===", type = LineType.CYBER_BANNER))
                    addLine(TerminalLine(text = "Followers: %,d / %,d".format(b.followers, b.followersTarget), type = LineType.SUCCESS))
                    addLine(TerminalLine(text = "Active Zombie Nodes: %,d".format(b.activeBots), type = LineType.OUTPUT))
                    addLine(TerminalLine(text = "Distributed Hash Power: %.1f PFLOPS".format(b.computePowerPflops), type = LineType.OUTPUT))
                    addLine(TerminalLine(text = "Multiplier: %.2fx (Bay Area Momentum)".format(b.multiplier), type = LineType.OUTPUT))
                }
                "whoami" -> {
                    addLine(TerminalLine(text = "USER: Marcus Holloway [alias: Retr0]", type = LineType.SUCCESS))
                    addLine(TerminalLine(text = "CELL: DedSec San Francisco Core", type = LineType.OUTPUT))
                    addLine(TerminalLine(text = "PRIMARY INTERFACE: ${_realNetworkInfo.value.interfaceName} (${_realNetworkInfo.value.localIp})", type = LineType.OUTPUT))
                    addLine(TerminalLine(text = "SECURITY CLEARANCE: ROOT / ZERO-DAY PRIVILEGED", type = LineType.CYBER_BANNER))
                }
                "sysinfo" -> {
                    val info = _realNetworkInfo.value
                    addLine(TerminalLine(text = "--- HARDWARE & NETWORK TELEMETRY ---", type = LineType.SYSTEM))
                    addLine(TerminalLine(text = "Adapter: ${info.interfaceName} | Mode: ${info.networkType}", type = LineType.OUTPUT))
                    addLine(TerminalLine(text = "IPv4: ${info.localIp} / Gateway: ${info.gateway}", type = LineType.OUTPUT))
                    addLine(TerminalLine(text = "DNS: ${info.dnsServer} | Link: ${info.linkSpeedMbps} Mbps", type = LineType.OUTPUT))
                    val availMem = Runtime.getRuntime().totalMemory() / (1024 * 1024)
                    val freeMem = Runtime.getRuntime().freeMemory() / (1024 * 1024)
                    addLine(TerminalLine(text = "JVM Heap: ${availMem - freeMem}MB used / ${availMem}MB total", type = LineType.OUTPUT))
                }
                "devices" -> {
                    val devs = _nearbyDevices.value
                    addLine(TerminalLine(text = "=== NEARBY ELECTRONIC DEVICES IN VICINITY (${devs.size}) ===", type = LineType.CYBER_BANNER))
                    devs.forEach { d ->
                        val status = if (d.isHacked) "[COMPROMISED]" else "[VULNERABLE]"
                        val tapState = if (d.isTapped) " [WIRETAP ACTIVE]" else ""
                        addLine(TerminalLine(text = " > [${d.id}] ${d.name} (${d.category}) | ${d.distanceMeters}m | ${d.protocol} $status$tapState", type = LineType.OUTPUT))
                    }
                    addLine(TerminalLine(text = "Use 'wiretap <device_id>' to tap audio / intercept live calls.", type = LineType.WARNING))
                }
                "wiretap", "tap" -> {
                    if (arg.isEmpty()) {
                        val firstTappable = _nearbyDevices.value.firstOrNull()
                        if (firstTappable != null) {
                            tapDeviceCall(firstTappable)
                        } else {
                            addLine(TerminalLine(text = "Usage: wiretap <device_id> (e.g. wiretap DEV-PH-02)", type = LineType.WARNING))
                        }
                    } else {
                        val targetDev = _nearbyDevices.value.find { it.id.lowercase() == arg }
                        if (targetDev != null) {
                            tapDeviceCall(targetDev)
                        } else {
                            addLine(TerminalLine(text = "Device '$arg' out of radio range or invalid ID.", type = LineType.ERROR))
                            soundManager.playAccessDenied()
                        }
                    }
                }
                "reboot" -> {
                    addLine(TerminalLine(text = "[SYS] Initiating ctOS 2.0 system cold reboot sequence...", type = LineType.WARNING))
                    delay(300)
                    restartBoot()
                }
                "matrix" -> {
                    addLine(TerminalLine(text = "[DUMPING RAW DEDSEC CIPHER STREAM]", type = LineType.CYBER_BANNER))
                    soundManager.playDataStream()
                    for (i in 0..5) {
                        val hex = (1..8).map { Random.nextInt(0, 256).toString(16).padStart(2, '0').uppercase() }.joinToString(" ")
                        addLine(TerminalLine(text = "0x7FFF%04X: $hex $hex".format(Random.nextInt(0, 0xFFFF)), type = LineType.HEX_DUMP))
                        delay(60)
                    }
                }
                "audio" -> {
                    if (arg == "off") {
                        soundManager.isSoundEnabled = false
                        addLine(TerminalLine(text = "[AUDIO FX MUTED]", type = LineType.WARNING))
                    } else {
                        soundManager.isSoundEnabled = true
                        addLine(TerminalLine(text = "[AUDIO FX ONLINE - 22.05kHz SYNTHESIZER READY]", type = LineType.SUCCESS))
                        soundManager.playTerminalBeep()
                    }
                }
                "haptics" -> {
                    if (arg == "off") {
                        soundManager.isHapticsEnabled = false
                        addLine(TerminalLine(text = "[HAPTIC VIBRATION MUTED]", type = LineType.WARNING))
                    } else {
                        soundManager.isHapticsEnabled = true
                        addLine(TerminalLine(text = "[HAPTIC VIBRATION ARMED]", type = LineType.SUCCESS))
                        soundManager.playTerminalBeep()
                    }
                }
                "clear" -> {
                    _terminalLines.value = emptyList()
                    addLine(TerminalLine(text = "dedsec@ctos:~# [CONSOLE CLEARED]", type = LineType.SYSTEM))
                }
                else -> {
                    addLine(TerminalLine(text = "bash: $cmd: command not found. Type 'help' for commands.", type = LineType.ERROR))
                    soundManager.playAccessDenied()
                }
            }
        }
    }

    private fun showHelp() {
        val help = listOf(
            TerminalLine(text = "=== DEDSEC CYBER SHELL COMMANDS ===", type = LineType.CYBER_BANNER),
            TerminalLine(text = "  scan, netscan      - Probe Wi-Fi subnet & ctOS city IoT nodes", type = LineType.OUTPUT),
            TerminalLine(text = "  ctos [nodes|scan]  - Query and display ctOS grid infrastructure", type = LineType.OUTPUT),
            TerminalLine(text = "  profile [name]     - Run facial & bank account profiler", type = LineType.OUTPUT),
            TerminalLine(text = "  devices            - List nearby electronics (Smart TVs, phones, speakers)", type = LineType.OUTPUT),
            TerminalLine(text = "  wiretap <dev_id>   - Intercept calls & audio streams from device", type = LineType.OUTPUT),
            TerminalLine(text = "  reboot             - Play ctOS 2.0 system boot sequence", type = LineType.OUTPUT),
            TerminalLine(text = "  blackout           - Trigger city sector emergency power blackout", type = LineType.OUTPUT),
            TerminalLine(text = "  ping <host>        - Probe network latency to remote endpoint", type = LineType.OUTPUT),
            TerminalLine(text = "  decrypt, cipher    - Launch interactive node routing puzzle", type = LineType.OUTPUT),
            TerminalLine(text = "  botnet             - Display global follower & computing power", type = LineType.OUTPUT),
            TerminalLine(text = "  whoami             - Display operative identity & clearance", type = LineType.OUTPUT),
            TerminalLine(text = "  sysinfo            - Display Android network & memory telemetry", type = LineType.OUTPUT),
            TerminalLine(text = "  matrix             - Trigger green phosphor memory dump", type = LineType.OUTPUT),
            TerminalLine(text = "  audio [on|off]     - Toggle terminal sound effects", type = LineType.OUTPUT),
            TerminalLine(text = "  haptics [on|off]   - Toggle tactile vibration feedback", type = LineType.OUTPUT),
            TerminalLine(text = "  clear              - Wipe terminal screen", type = LineType.OUTPUT)
        )
        help.forEach { addLine(it) }
    }

    private fun showCtosNodes() {
        addLine(TerminalLine(text = "[+] ACTIVE cTOS INFRASTRUCTURE NODES:", type = LineType.SUCCESS))
        _networkNodes.value.forEach { node ->
            val colorType = if (node.status == ExploitStatus.EXPLOITED) LineType.SUCCESS else LineType.OUTPUT
            addLine(TerminalLine(text = " [${node.id}] ${node.name} (${node.ip}) - [${node.status}]", type = colorType))
        }
    }

    fun startNetworkScan() {
        if (_isScanning.value) return
        _isScanning.value = true
        soundManager.playSonarPing()

        viewModelScope.launch {
            _realNetworkInfo.value = scannerEngine.getRealNetworkInfo()
            val nodes = mutableListOf<NetworkNode>()

            scannerEngine.scanSubnetAndCtosGrid().collect { node ->
                nodes.add(node)
                _networkNodes.value = nodes.toList()
                soundManager.playDataStream()
                addLine(
                    TerminalLine(
                        text = "[FOUND] ${node.id} : ${node.name} [${node.ip}] - ${node.encryption}",
                        type = LineType.OUTPUT
                    )
                )
            }

            _isScanning.value = false
            soundManager.playAccessGranted()
            addLine(TerminalLine(text = "[COMPLETE] Scan discovered ${nodes.size} devices & ctOS smart grid relays.", type = LineType.SUCCESS))
        }
    }

    fun triggerNodeExploit(node: NetworkNode, exploit: NodeExploit?) {
        viewModelScope.launch {
            val expName = exploit?.name ?: "Zero-Day Injection"
            addLine(TerminalLine(text = "[*] Deploying payload '$expName' against ${node.id} (${node.ip})...", type = LineType.SYSTEM))
            soundManager.playDataStream()

            // Update node to breaching state
            _networkNodes.update { list ->
                list.map { if (it.id == node.id) it.copy(status = ExploitStatus.BREACHING) else it }
            }

            delay(600)
            addLine(TerminalLine(text = "[>] Bypassing firewall via CVE-2026-8819 stack overflow...", type = LineType.HEX_DUMP))
            delay(500)

            _networkNodes.update { list ->
                list.map { if (it.id == node.id) it.copy(status = ExploitStatus.EXPLOITED) else it }
            }

            val reward = exploit?.followerReward ?: 10000
            _botnetStatus.update { it.copy(followers = it.followers + reward) }
            soundManager.playAccessGranted()

            addLine(TerminalLine(text = "[SUCCESS] ${node.id} OVERRIDDEN! +%,d DEDSEC FOLLOWERS ACQUIRED!".format(reward), type = LineType.CYBER_BANNER))
        }
    }

    fun triggerBlackout() {
        viewModelScope.launch {
            addLine(TerminalLine(text = "[!!!] INITIATING BAY AREA HIGH-VOLTAGE SUBSTATION OVERLOAD...", type = LineType.WARNING))
            soundManager.playBlackout()
            _isBlackoutActive.value = true

            _networkNodes.update { list ->
                list.map { if (it.type == NodeType.CTOS_POWER_GRID || it.type == NodeType.CTOS_TRAFFIC_LIGHT) it.copy(status = ExploitStatus.BLACKOUT) else it }
            }

            delay(1800)
            _isBlackoutActive.value = false
            _botnetStatus.update { it.copy(followers = it.followers + 50000) }
            addLine(TerminalLine(text = "[DEDSEC BLACKOUT CONFIRMED] City sector dark. Traffic halted. +50,000 FOLLOWERS!", type = LineType.CYBER_BANNER))
            soundManager.playAccessGranted()
        }
    }

    fun drainCrypto(target: ProfilerTarget) {
        if (target.isHacked) return
        _profilerTargets.update { list ->
            list.map { if (it.id == target.id) it.copy(isHacked = true) else it }
        }
        viewModelScope.launch {
            soundManager.playDataStream()
            delay(200)
            val gain = (target.cryptoBalance * 15000).toInt()
            _botnetStatus.update { it.copy(followers = it.followers + gain) }
            soundManager.playAccessGranted()
            addLine(TerminalLine(text = "[SIPHON] Bypassed private key for ${target.name}. Drained ${target.cryptoBalance} BTC! +$gain Followers.", type = LineType.SUCCESS))
        }
    }

    fun distractTarget(target: ProfilerTarget) {
        viewModelScope.launch {
            soundManager.playKeyClick()
            _profilerTargets.update { list ->
                list.map { if (it.id == target.id) it.copy(isDistracted = true) else it }
            }
            delay(300)
            soundManager.playTerminalBeep()
            addLine(TerminalLine(text = "[DISTRACT] Injected high-volume acoustic feedback into ${target.name}'s phone (${target.phoneModel}).", type = LineType.OUTPUT))
        }
    }

    // --- Cipher Puzzle Minigame Logic ---

    fun onCipherCellClicked(cell: CipherCell) {
        val game = _cipherGame.value
        if (game.isSolved || game.isFailed) return

        // Verify valid row/col alternation
        if (game.activeRow != null && cell.row != game.activeRow) return
        if (game.activeCol != null && cell.col != game.activeCol) return

        soundManager.playKeyClick()
        val nextInput = game.currentInput + cell.hexValue
        val isTargetMatched = checkSequenceMatch(nextInput, game.targetSequence)

        val solved = nextInput.takeLast(game.targetSequence.size) == game.targetSequence
        val movesLeft = game.movesRemaining - 1
        val failed = !solved && movesLeft <= 0

        val nextActiveRow = if (game.activeRow != null) null else cell.row
        val nextActiveCol = if (game.activeRow != null) cell.col else null

        val updatedGrid = game.grid.map { row ->
            row.map { c ->
                if (c.row == cell.row && c.col == cell.col) c.copy(isSelected = true) else c
            }
        }

        _cipherGame.value = game.copy(
            grid = updatedGrid,
            currentInput = nextInput,
            movesRemaining = movesLeft,
            isSolved = solved,
            isFailed = failed,
            activeRow = nextActiveRow,
            activeCol = nextActiveCol
        )

        if (solved) {
            soundManager.playAccessGranted()
            _botnetStatus.update { it.copy(followers = it.followers + 35000) }
            addLine(TerminalLine(text = "[CIPHER BREACHED] Kernel memory unlocked! +35,000 DedSec Followers!", type = LineType.CYBER_BANNER))
        } else if (failed) {
            soundManager.playAccessDenied()
            addLine(TerminalLine(text = "[CIPHER FAILURE] Buffer corrupted. ctOS trace detected.", type = LineType.ERROR))
        }
    }

    private fun checkSequenceMatch(current: List<String>, target: List<String>): Boolean {
        if (current.isEmpty()) return true
        val checkLen = minOf(current.size, target.size)
        return current.take(checkLen) == target.take(checkLen)
    }

    fun resetCipherGame() {
        _cipherGame.value = createInitialCipherGame()
        soundManager.playTerminalBeep()
    }

    private fun createInitialCipherGame(): CipherGame {
        val hexPool = listOf("1C", "55", "BD", "E9", "7A", "FF")
        val grid = (0..3).map { r ->
            (0..3).map { c ->
                CipherCell(row = r, col = c, hexValue = hexPool.random())
            }
        }
        val target = listOf(hexPool.random(), hexPool.random(), hexPool.random())
        return CipherGame(
            grid = grid,
            targetSequence = target,
            currentInput = emptyList(),
            movesRemaining = 6,
            isSolved = false,
            isFailed = false,
            activeRow = 0,
            activeCol = null
        )
    }

    private fun addLine(line: TerminalLine) {
        _terminalLines.update { (it + line).takeLast(200) }
    }
}
