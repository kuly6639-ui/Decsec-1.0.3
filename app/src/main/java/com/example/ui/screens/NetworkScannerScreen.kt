package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeviceCategory
import com.example.model.ExploitStatus
import com.example.model.NearbyElectronicDevice
import com.example.model.NetworkNode
import com.example.model.NodeExploit
import com.example.model.NodeType
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

@Composable
fun NetworkScannerScreen(
    networkInfo: RealNetworkInfo,
    nodes: List<NetworkNode>,
    nearbyDevices: List<NearbyElectronicDevice>,
    isScanning: Boolean,
    onRescan: () -> Unit,
    onExploitNode: (NetworkNode, NodeExploit?) -> Unit,
    onTapDeviceCall: (NearbyElectronicDevice) -> Unit,
    onHackDevice: (NearbyElectronicDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    // 0 = Nearby Electronics, 1 = ctOS SCADA Grid
    var selectedCategoryTab by remember { mutableIntStateOf(0) }
    var deviceFilter by remember { mutableStateOf("ALL") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalDark)
            .testTag("network_scanner_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            // Scanner Control Header
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
                    Column {
                        Text(
                            text = if (selectedCategoryTab == 0) "NEARBY ELECTRONICS SCANNER" else "cTOS 2.0 SCADA SCANNER",
                            style = MaterialTheme.typography.titleMedium.copy(color = NeonGreenBright)
                        )
                        Text(
                            text = if (selectedCategoryTab == 0)
                                "${nearbyDevices.size} SMART ELECTRONICS IN VICINITY // RF/BLE/WIFI"
                            else
                                "SUBNET: ${networkInfo.localIp.substringBeforeLast(".")}.0/24 // ${nodes.size} NODES DISCOVERED",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }

                    Button(
                        onClick = onRescan,
                        enabled = !isScanning,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen,
                            contentColor = CyberBlack
                        ),
                        shape = CutCornerShape(4.dp),
                        modifier = Modifier.testTag("rescan_network_button")
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = CyberBlack,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SCANNING...")
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Rescan",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SWEEP")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-category Selector: Nearby Electronics vs ctOS SCADA Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = if (selectedCategoryTab == 0) NeonGreen.copy(alpha = 0.2f) else TerminalSurface,
                    shape = CutCornerShape(4.dp),
                    border = BorderStroke(1.dp, if (selectedCategoryTab == 0) NeonGreen else TerminalBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedCategoryTab = 0 }
                        .testTag("tab_nearby_electronics")
                ) {
                    Text(
                        text = "NEARBY ELECTRONICS (${nearbyDevices.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (selectedCategoryTab == 0) NeonGreenBright else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
                    )
                }

                Surface(
                    color = if (selectedCategoryTab == 1) CyberCyan.copy(alpha = 0.2f) else TerminalSurface,
                    shape = CutCornerShape(4.dp),
                    border = BorderStroke(1.dp, if (selectedCategoryTab == 1) CyberCyan else TerminalBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedCategoryTab = 1 }
                        .testTag("tab_ctos_grid")
                ) {
                    Text(
                        text = "cTOS CITY GRID (${nodes.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (selectedCategoryTab == 1) CyberCyan else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedCategoryTab == 0) {
                // Filter chips for nearby electronics: ALL, TVS, PHONES, SPEAKERS, LAPTOPS
                val filterChips = listOf("ALL", "TVS", "PHONES", "SPEAKERS", "LAPTOPS")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    filterChips.forEach { chip ->
                        val isSelected = deviceFilter == chip
                        Surface(
                            color = if (isSelected) DedsecPink.copy(alpha = 0.2f) else TerminalSurfaceVariant,
                            shape = CutCornerShape(4.dp),
                            border = BorderStroke(1.dp, if (isSelected) DedsecPink else TerminalBorder),
                            modifier = Modifier.clickable { deviceFilter = chip }
                        ) {
                            Text(
                                text = chip,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) DedsecPink else TextMuted,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Filtered Nearby Devices List
                val filteredDevices = nearbyDevices.filter { dev ->
                    when (deviceFilter) {
                        "TVS" -> dev.category == DeviceCategory.SMART_TV
                        "PHONES" -> dev.category == DeviceCategory.SMARTPHONE
                        "SPEAKERS" -> dev.category == DeviceCategory.SMART_SPEAKER
                        "LAPTOPS" -> dev.category == DeviceCategory.LAPTOP
                        else -> true
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredDevices, key = { it.id }) { dev ->
                        NearbyDeviceCard(
                            device = dev,
                            onTapCall = { onTapDeviceCall(dev) },
                            onHack = { onHackDevice(dev) }
                        )
                    }
                }
            } else {
                // ctOS Grid Nodes List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(nodes, key = { it.id }) { node ->
                        NetworkNodeCard(
                            node = node,
                            onExploit = { exp -> onExploitNode(node, exp) }
                        )
                    }
                }
            }
        }

        CrtScanlineOverlay(scanlineAlpha = 0.05f)
    }
}

@Composable
private fun NearbyDeviceCard(
    device: NearbyElectronicDevice,
    onTapCall: () -> Unit,
    onHack: () -> Unit
) {
    val icon: ImageVector = when (device.category) {
        DeviceCategory.SMART_TV -> Icons.Default.Tv
        DeviceCategory.SMARTPHONE -> Icons.Default.PhoneAndroid
        DeviceCategory.SMART_SPEAKER -> Icons.Default.Speaker
        DeviceCategory.LAPTOP -> Icons.Default.Laptop
        DeviceCategory.WEARABLE -> Icons.Default.Headphones
        DeviceCategory.SURVEILLANCE -> Icons.Default.CameraAlt
    }

    Card(
        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
        colors = CardDefaults.cardColors(containerColor = TerminalSurface),
        border = BorderStroke(
            1.dp,
            if (device.isTapped) DedsecPink else if (device.isHacked) NeonGreen else TerminalBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("nearby_device_${device.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: Device icon + Name + Category + Distance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(TerminalSurfaceVariant, CutCornerShape(4.dp))
                            .border(
                                1.dp,
                                if (device.isTapped) DedsecPink else CyberCyan,
                                CutCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = device.category.name,
                            tint = if (device.isTapped) DedsecPink else if (device.isHacked) NeonGreenBright else CyberCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (device.isHacked) NeonGreenBright else TextWhite
                            )
                        )
                        Text(
                            text = "${device.vendor} // ${device.model}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                }

                // Distance Badge
                Surface(
                    color = CyberBlack,
                    shape = CutCornerShape(4.dp),
                    border = BorderStroke(1.dp, TerminalBorder)
                ) {
                    Text(
                        text = "%.1fm // %d dBm".format(device.distanceMeters, device.signalDbm),
                        style = MaterialTheme.typography.labelSmall.copy(color = WarningAmber, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Protocol & IP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PROTO: ${device.protocol}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                )
                Text(
                    text = "IP: ${device.ip}",
                    style = MaterialTheme.typography.bodySmall.copy(color = CyberCyan, fontSize = 11.sp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Intercepted call / voice preview banner
            Surface(
                color = CyberBlack,
                shape = CutCornerShape(4.dp),
                border = BorderStroke(1.dp, if (device.isTapped) DedsecPink.copy(alpha = 0.6f) else TerminalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneInTalk,
                        contentDescription = "Call stream",
                        tint = if (device.isTapped) DedsecPink else WarningAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VOICE FEED: ${device.callSubject}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (device.isTapped) DedsecPink else TextWhite,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Listen to Call (Wiretap) & Hack Device
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Listen to calls / tap audio button
                Button(
                    onClick = onTapCall,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DedsecPink,
                        contentColor = TextWhite
                    ),
                    shape = CutCornerShape(4.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("tap_call_${device.id}")
                ) {
                    Icon(imageVector = Icons.Default.PhoneInTalk, contentDescription = "Listen Call", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (device.isTapped) "ACTIVE WIRETAP" else "LISTEN TO CALL",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Hack device button
                Button(
                    onClick = onHack,
                    enabled = !device.isHacked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (device.isHacked) TerminalSurfaceVariant else NeonGreen,
                        contentColor = CyberBlack
                    ),
                    shape = CutCornerShape(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("hack_device_${device.id}")
                ) {
                    Icon(imageVector = Icons.Default.LockOpen, contentDescription = "Hack", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (device.isHacked) "BREACHED" else "HACK DEVICE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun NetworkNodeCard(
    node: NetworkNode,
    onExploit: (NodeExploit?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val icon: ImageVector = when (node.type) {
        NodeType.CTOS_TRAFFIC_LIGHT -> Icons.Default.Traffic
        NodeType.CTOS_SURVEILLANCE_CAM -> Icons.Default.CameraAlt
        NodeType.CTOS_POWER_GRID -> Icons.Default.ElectricBolt
        NodeType.CTOS_CELL_TOWER -> Icons.Default.WifiTethering
        NodeType.CTOS_DRONE_PATROL -> Icons.Default.FlightTakeoff
        NodeType.CTOS_SMART_METER -> Icons.Default.Home
        NodeType.WIFI_HOST -> Icons.Default.Router
        NodeType.BLUME_DATA_RELAY -> Icons.Default.Wifi
    }

    val (statusColor, statusText) = when (node.status) {
        ExploitStatus.VULNERABLE -> Pair(WarningAmber, "VULNERABLE")
        ExploitStatus.EXPLOITED -> Pair(NeonGreenBright, "COMPROMISED")
        ExploitStatus.BREACHING -> Pair(CyberCyan, "BREACHING...")
        ExploitStatus.BLACKOUT -> Pair(AlertRed, "BLACKOUT")
        ExploitStatus.SECURE -> Pair(TextMuted, "SECURE")
    }

    Card(
        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
        colors = CardDefaults.cardColors(containerColor = TerminalSurface),
        border = BorderStroke(
            1.dp,
            if (node.status == ExploitStatus.EXPLOITED) NeonGreen else TerminalBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("node_card_${node.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(TerminalSurfaceVariant, CutCornerShape(4.dp))
                            .border(1.dp, TerminalBorder, CutCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = node.type.name,
                            tint = if (node.status == ExploitStatus.EXPLOITED) NeonGreenBright else CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = node.name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (node.status == ExploitStatus.EXPLOITED) NeonGreenBright else TextWhite
                            )
                        )
                        Text(
                            text = "${node.id} // ${node.ip} // ${node.signalDbm} dBm",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = CutCornerShape(4.dp),
                    border = BorderStroke(1.dp, statusColor)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(color = statusColor, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "OS: ${node.osFingerprint}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                )
                Text(
                    text = "PORTS: ${node.openPorts.joinToString(",")}",
                    style = MaterialTheme.typography.bodySmall.copy(color = CyberCyan, fontSize = 11.sp)
                )
            }

            AnimatedVisibility(visible = expanded || node.exploits.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "AVAILABLE ZERO-DAY EXPLOITS:",
                        style = MaterialTheme.typography.labelSmall.copy(color = DedsecPink, letterSpacing = 0.5.sp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    if (node.exploits.isEmpty()) {
                        Text(
                            text = "No direct remote exploit vectors. Node is monitored.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    } else {
                        node.exploits.forEach { exploit ->
                            Surface(
                                color = TerminalSurfaceVariant,
                                shape = CutCornerShape(4.dp),
                                border = BorderStroke(1.dp, if (node.status == ExploitStatus.EXPLOITED) NeonGreen.copy(alpha = 0.4f) else DedsecPink.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { onExploit(exploit) }
                                    .testTag("exploit_${exploit.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "[HACK] ${exploit.name}",
                                            style = MaterialTheme.typography.labelMedium.copy(color = NeonGreenBright)
                                        )
                                        Text(
                                            text = exploit.description,
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Surface(
                                        color = DedsecPink.copy(alpha = 0.2f),
                                        shape = CutCornerShape(4.dp),
                                        border = BorderStroke(1.dp, DedsecPink)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LockOpen,
                                                contentDescription = "Inject",
                                                tint = DedsecPink,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "+%,d".format(exploit.followerReward),
                                                style = MaterialTheme.typography.labelSmall.copy(color = DedsecPink)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
