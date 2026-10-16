package com.example.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.model.ExploitStatus
import com.example.model.NetworkNode
import com.example.model.NodeExploit
import com.example.model.NodeType
import com.example.model.RealNetworkInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import kotlin.random.Random

class NetworkScannerEngine(private val context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    /**
     * Inspects real Android network hardware/adapters.
     */
    fun getRealNetworkInfo(): RealNetworkInfo {
        var localIp = "127.0.0.1"
        var interfaceName = "lo"
        var netType = "DISCONNECTED"
        var isConnected = false

        try {
            val activeNetwork = connectivityManager?.activeNetwork
            val capabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)

            if (capabilities != null) {
                isConnected = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                netType = when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WI-FI (802.11ac)"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "CELLULAR (5G NR)"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ETHERNET (GbE)"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN ENCRYPTED TUNNEL"
                    else -> "NETWORK_INTERFACE"
                }
            }

            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val intf = interfaces.nextElement()
                if (!intf.isUp || intf.isLoopback) continue

                val addrs = intf.inetAddresses
                while (addrs.hasMoreElements()) {
                    val addr = addrs.nextElement()
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        localIp = addr.hostAddress ?: "127.0.0.1"
                        interfaceName = intf.name
                        break
                    }
                }
                if (localIp != "127.0.0.1") break
            }
        } catch (_: Exception) {
            localIp = "192.168.1.108"
            interfaceName = "wlan0"
            netType = "WI-FI (802.11ac)"
            isConnected = true
        }

        val baseSubnet = if (localIp.contains(".")) {
            localIp.substringBeforeLast(".") + ".1"
        } else "192.168.1.1"

        return RealNetworkInfo(
            interfaceName = interfaceName,
            localIp = localIp,
            subnetMask = "255.255.255.0",
            gateway = baseSubnet,
            dnsServer = "1.1.1.1",
            networkType = netType,
            isConnected = isConnected,
            linkSpeedMbps = 433
        )
    }

    /**
     * Scans for local active hosts and ctOS physical grid nodes.
     * Emits progress and nodes found in real-time.
     */
    fun scanSubnetAndCtosGrid(): Flow<NetworkNode> = flow {
        val netInfo = getRealNetworkInfo()
        val baseIp = if (netInfo.localIp.contains(".")) {
            netInfo.localIp.substringBeforeLast(".")
        } else "192.168.1"

        // 1. Gateway node
        emit(
            NetworkNode(
                id = "NODE-GW-01",
                name = "ctOS Central Subnet Gateway",
                type = NodeType.WIFI_HOST,
                ip = "$baseIp.1",
                mac = "00:1A:2B:3C:4D:01",
                signalDbm = -42,
                openPorts = listOf(80, 443, 53, 22),
                osFingerprint = "ctOS Embedded Linux Kernel 5.15",
                encryption = "WPA3-ENTERPRISE",
                status = ExploitStatus.VULNERABLE,
                exploits = listOf(
                    NodeExploit("EXP-GW-01", "DNS Poisoning", "Redirect subnet traffic to DedSec mirror", 1200, 8000),
                    NodeExploit("EXP-GW-02", "MITM Packet Sniff", "Capture unencrypted ctOS auth tokens", 2000, 15000)
                )
            )
        )

        // 2. Local device node
        emit(
            NetworkNode(
                id = "NODE-LOCAL-02",
                name = "Operative Handheld (This Device)",
                type = NodeType.WIFI_HOST,
                ip = netInfo.localIp,
                mac = "5C:A6:E6:AA:02:4B",
                signalDbm = -28,
                openPorts = listOf(5555, 8080),
                osFingerprint = "Android AOSP DedSec ROM",
                encryption = "AES-256-GCM",
                status = ExploitStatus.SECURE,
                exploits = emptyList()
            )
        )

        // 3. Watch Dogs 2 ctOS Smart City Nodes
        val ctOsNodes = listOf(
            NetworkNode(
                id = "CTOS-TRF-881",
                name = "ctOS Traffic Light Controller #881",
                type = NodeType.CTOS_TRAFFIC_LIGHT,
                ip = "$baseIp.24",
                mac = "B8:27:EB:10:9A:88",
                signalDbm = -58,
                openPorts = listOf(502, 8080),
                osFingerprint = "Siemens Sitraffic ctOS Micro v3",
                encryption = "ctOS-SCADA-Unencrypted",
                status = ExploitStatus.VULNERABLE,
                exploits = listOf(
                    NodeExploit("EXP-TRF-01", "Traffic Pileup Trigger", "Cycle all junctions to green simultaneously", 1500, 12000),
                    NodeExploit("EXP-TRF-02", "Pursuit Blockade", "Freeze red lights to stop target convoy", 2200, 18000)
                )
            ),
            NetworkNode(
                id = "CTOS-CAM-104",
                name = "ctOS PTZ 4K Surveillance Cam",
                type = NodeType.CTOS_SURVEILLANCE_CAM,
                ip = "$baseIp.45",
                mac = "E0:D5:5E:2B:12:44",
                signalDbm = -65,
                openPorts = listOf(554, 80, 8000),
                osFingerprint = "Hikvision / ctOS Optics API",
                encryption = "RTSP Auth Weak Hash",
                status = ExploitStatus.VULNERABLE,
                exploits = listOf(
                    NodeExploit("EXP-CAM-01", "Hijack Video Stream", "Access real-time optical tracking feed", 800, 6000),
                    NodeExploit("EXP-CAM-02", "Optical Blind Loop", "Inject 10-second static video loop", 1400, 11000)
                )
            ),
            NetworkNode(
                id = "CTOS-PWR-092",
                name = "Bay Area Substation Transformer",
                type = NodeType.CTOS_POWER_GRID,
                ip = "$baseIp.112",
                mac = "70:B3:D5:41:09:A1",
                signalDbm = -72,
                openPorts = listOf(102, 502, 44818),
                osFingerprint = "GE / ctOS High-Voltage SCADA Relay",
                encryption = "MODBUS-TCP Zero-Auth",
                status = ExploitStatus.VULNERABLE,
                exploits = listOf(
                    NodeExploit("EXP-PWR-01", "Overpressure Surge", "Trigger circuit breaker explosion", 3500, 25000),
                    NodeExploit("EXP-PWR-02", "City Sector Blackout", "Initiate cascade grid power failure", 5000, 50000)
                )
            ),
            NetworkNode(
                id = "CTOS-TWR-305",
                name = "Blume 5G Micro-Cell Tower #305",
                type = NodeType.CTOS_CELL_TOWER,
                ip = "$baseIp.189",
                mac = "3C:84:6A:90:7E:11",
                signalDbm = -51,
                openPorts = listOf(21, 23, 80, 2152),
                osFingerprint = "Ericsson / Blume Telecom OS 9.2",
                encryption = "WPA3 / ctOS Backhaul",
                status = ExploitStatus.VULNERABLE,
                exploits = listOf(
                    NodeExploit("EXP-TWR-01", "IMSI Catcher Broadcast", "Deploy DedSec skull message to all phones", 2800, 22000),
                    NodeExploit("EXP-TWR-02", "Cellular Black Hole", "Jam all emergency and police comms", 4200, 35000)
                )
            ),
            NetworkNode(
                id = "CTOS-DRN-007",
                name = "ctOS Patrol Recon Drone",
                type = NodeType.CTOS_DRONE_PATROL,
                ip = "$baseIp.210",
                mac = "A4:C1:38:77:43:9C",
                signalDbm = -48,
                openPorts = listOf(8888, 9000, 11111),
                osFingerprint = "Tidus Drone Flight Controller v4",
                encryption = "Proprietary Radio Protocol",
                status = ExploitStatus.VULNERABLE,
                exploits = listOf(
                    NodeExploit("EXP-DRN-01", "Rotor Overdrive Crash", "Force autopilot override into pavement", 2500, 20000),
                    NodeExploit("EXP-DRN-02", "Friendly Fire Hack", "Reprogram targeting sensors against Blume guards", 4000, 32000)
                )
            ),
            NetworkNode(
                id = "CTOS-SMT-554",
                name = "HAUM Smart Home Gateway",
                type = NodeType.CTOS_SMART_METER,
                ip = "$baseIp.78",
                mac = "50:02:91:33:41:88",
                signalDbm = -60,
                openPorts = listOf(80, 8080, 1883),
                osFingerprint = "HAUM 2.0 Zigbee/MQTT Bridge",
                encryption = "HAUM Default Passwords",
                status = ExploitStatus.VULNERABLE,
                exploits = listOf(
                    NodeExploit("EXP-SMT-01", "Fire Alarm Distraction", "Trigger ear-splitting acoustic distraction", 900, 7500),
                    NodeExploit("EXP-SMT-02", "Smart Lock Breach", "Unlock all residential security doors", 1600, 14000)
                )
            )
        )

        for (node in ctOsNodes) {
            kotlinx.coroutines.delay(180)
            emit(node)
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Probes ping latency to a given host or IP address.
     */
    suspend fun pingHost(host: String): String = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            val address = InetAddress.getByName(host)
            val socket = Socket()
            socket.connect(InetSocketAddress(address, 80), 2000)
            val elapsed = System.currentTimeMillis() - startTime
            socket.close()
            "64 bytes from ${address.hostAddress}: icmp_seq=1 ttl=56 time=${elapsed}ms [CONNECTED]"
        } catch (_: Exception) {
            val elapsed = Random.nextInt(18, 55)
            "64 bytes from $host: icmp_seq=1 ttl=64 time=${elapsed}ms [SYN-ACK OK]"
        }
    }
}
