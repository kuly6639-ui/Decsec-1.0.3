package com.example.model

enum class LineType {
    COMMAND,
    OUTPUT,
    SUCCESS,
    ERROR,
    WARNING,
    CYBER_BANNER,
    HEX_DUMP,
    SYSTEM,
    GLITCH,
    PING_PROGRESS
}

data class TerminalLine(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val type: LineType = LineType.OUTPUT,
    val timestamp: Long = System.currentTimeMillis(),
    val progress: Float = 0f,
    val latencyMs: Long = 0L,
    val isFlickering: Boolean = false
)

enum class NodeType {
    WIFI_HOST,
    CTOS_TRAFFIC_LIGHT,
    CTOS_SURVEILLANCE_CAM,
    CTOS_CELL_TOWER,
    CTOS_POWER_GRID,
    CTOS_DRONE_PATROL,
    CTOS_SMART_METER,
    BLUME_DATA_RELAY
}

enum class ExploitStatus {
    SECURE,
    VULNERABLE,
    BREACHING,
    EXPLOITED,
    BLACKOUT
}

data class NodeExploit(
    val id: String,
    val name: String,
    val description: String,
    val botnetCost: Int = 1000,
    val followerReward: Int = 5000
)

data class NetworkNode(
    val id: String,
    val name: String,
    val type: NodeType,
    val ip: String,
    val mac: String,
    val signalDbm: Int,
    val openPorts: List<Int>,
    val osFingerprint: String,
    val encryption: String,
    val status: ExploitStatus = ExploitStatus.VULNERABLE,
    val exploits: List<NodeExploit>
)

data class RealNetworkInfo(
    val interfaceName: String = "wlan0",
    val localIp: String = "192.168.1.105",
    val subnetMask: String = "255.255.255.0",
    val gateway: String = "192.168.1.1",
    val dnsServer: String = "8.8.8.8",
    val networkType: String = "WI-FI",
    val isConnected: Boolean = true,
    val linkSpeedMbps: Int = 144
)

enum class ThreatLevel {
    LOW,
    MODERATE,
    HIGH,
    CRITICAL
}

data class ProfilerTarget(
    val id: String,
    val name: String,
    val age: Int,
    val occupation: String,
    val incomeFormatted: String,
    val cryptoBalance: Double,
    val threatLevel: ThreatLevel,
    val ctOSFactoid: String,
    val phoneModel: String,
    val isHacked: Boolean = false,
    val isDistracted: Boolean = false
)

data class CipherCell(
    val row: Int,
    val col: Int,
    val hexValue: String,
    val isSelected: Boolean = false,
    val isMatched: Boolean = false
)

data class CipherGame(
    val grid: List<List<CipherCell>>,
    val targetSequence: List<String>,
    val currentInput: List<String>,
    val movesRemaining: Int,
    val isSolved: Boolean = false,
    val isFailed: Boolean = false,
    val activeRow: Int? = null,
    val activeCol: Int? = null
)

data class BotnetStatus(
    val followers: Long = 4_892_104L,
    val followersTarget: Long = 5_000_000L,
    val activeBots: Int = 38_420,
    val computePowerPflops: Double = 124.6,
    val multiplier: Double = 1.85
)

enum class DeviceCategory {
    SMART_TV,
    SMARTPHONE,
    SMART_SPEAKER,
    LAPTOP,
    WEARABLE,
    SURVEILLANCE
}

data class InterceptedCallMessage(
    val speaker: String,
    val text: String,
    val timeOffsetSec: Int
)

data class NearbyElectronicDevice(
    val id: String,
    val name: String,
    val category: DeviceCategory,
    val vendor: String,
    val model: String,
    val distanceMeters: Float,
    val signalDbm: Int,
    val ip: String,
    val protocol: String,
    val isHacked: Boolean = false,
    val isTapped: Boolean = false,
    val callSubject: String = "Encrypted Voice Call",
    val callerNumber: String = "+1 (415) 555-0198",
    val callTranscript: List<InterceptedCallMessage> = emptyList()
)

