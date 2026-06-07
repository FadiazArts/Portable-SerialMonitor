package com.bluetoothserialmonitor.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bluetoothserialmonitor.domain.model.*
import com.bluetoothserialmonitor.presentation.components.common.StatusLedWithGlow
import com.bluetoothserialmonitor.presentation.theme.NeonBlue
import com.bluetoothserialmonitor.presentation.theme.NeonGreen
import com.bluetoothserialmonitor.presentation.theme.NeonRed
import com.bluetoothserialmonitor.presentation.theme.SurfaceElevated
import com.bluetoothserialmonitor.presentation.theme.SurfaceVariant

/**
 * Main connection header bar with status indicator, device selector, and connect button
 */
@Composable
fun ConnectionHeader(
    uiState: SerialMonitorUiState,
    onScanClick: () -> Unit,
    onConnectClick: (BluetoothDeviceWrapper) -> Unit,
    onDisconnectClick: () -> Unit,
    onShowDeviceSelector: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        color = SurfaceElevated,
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top row: Status LED and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusLedWithGlow(
                        isConnected = uiState.isConnected,
                        size = 16.dp
                    )
                    
                    Text(
                        text = "Bluetooth Serial Monitor",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Connection status chip
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = when (uiState.connectionState) {
                                is BluetoothConnectionState.Connected -> "Connected"
                                is BluetoothConnectionState.Connecting -> "Connecting..."
                                is BluetoothConnectionState.Scanning -> "Scanning"
                                is BluetoothConnectionState.Disconnected -> "Disconnected"
                                is BluetoothConnectionState.Error -> "Error"
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (uiState.connectionState) {
                            is BluetoothConnectionState.Connected -> NeonGreen.copy(alpha = 0.2f)
                            is BluetoothConnectionState.Error -> NeonRed.copy(alpha = 0.2f)
                            is BluetoothConnectionState.Scanning -> NeonBlue.copy(alpha = 0.2f)
                            else -> SurfaceVariant
                        }
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        borderColor = when (uiState.connectionState) {
                            is BluetoothConnectionState.Connected -> NeonGreen
                            is BluetoothConnectionState.Error -> NeonRed
                            is BluetoothConnectionState.Scanning -> NeonBlue
                            else -> Color.Transparent
                        }
                    )
                )
            }
            
            // Bottom row: Device selector and connect button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Device Selector Dropdown
                ExposedDropdownMenuBox(
                    expanded = uiState.showDeviceSelector,
                    onExpandedChange = onShowDeviceSelector,
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = when {
                            uiState.connectionState is BluetoothConnectionState.Connected -> 
                                (uiState.connectionState as BluetoothConnectionState.Connected).deviceName
                            uiState.availableDevices.isNotEmpty() -> "Select Device"
                            else -> "No Devices Found"
                        },
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            Row {
                                IconButton(onClick = onScanClick) {
                                    Icon(
                                        imageVector = if (uiState.isScanning) {
                                            Icons.Default.Refresh
                                        } else {
                                            Icons.Default.BluetoothSearching
                                        },
                                        contentDescription = "Scan",
                                        tint = if (uiState.isScanning) NeonBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = SurfaceVariant
                        )
                    )
                    
                    DropdownMenu(
                        expanded = uiState.showDeviceSelector,
                        onDismissRequest = { onShowDeviceSelector(false) }
                    ) {
                        if (uiState.isScanning) {
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = NeonBlue
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Scanning...")
                                    }
                                },
                                onClick = { }
                            )
                        } else if (uiState.availableDevices.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Tap scan to find devices") },
                                onClick = { }
                            )
                        } else {
                            uiState.availableDevices.forEach { device ->
                                DropdownMenuItem(
                                    text = { 
                                        Column {
                                            Text(
                                                text = device.name.ifEmpty { "Unknown Device" },
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = device.macAddress,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        onConnectClick(device)
                                        onShowDeviceSelector(false)
                                    },
                                    leadingIcon = {
                                        // Signal strength indicator
                                        Text(
                                            text = when (device.signalStrength) {
                                                SignalStrength.EXCELLENT -> "▮▮▮"
                                                SignalStrength.GOOD -> "▮▮▯"
                                                SignalStrength.FAIR -> "▮▯▯"
                                                SignalStrength.WEAK -> "▯▯▯"
                                                SignalStrength.UNKNOWN -> ""
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = when (device.signalStrength) {
                                                SignalStrength.EXCELLENT, SignalStrength.GOOD -> NeonGreen
                                                SignalStrength.FAIR -> NeonOrange
                                                else -> NeonRed
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Connect/Disconnect Button
                Button(
                    onClick = {
                        if (uiState.isConnected) {
                            onDisconnectClick()
                        } else if (uiState.availableDevices.isNotEmpty()) {
                            // Show dropdown if devices available
                            onShowDeviceSelector(true)
                        } else {
                            onScanClick()
                        }
                    },
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !uiState.isLoading && 
                            uiState.connectionState !is BluetoothConnectionState.Connecting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when {
                            uiState.isConnected -> NeonRed
                            uiState.connectionState is BluetoothConnectionState.Connecting -> 
                                SurfaceVariant
                            else -> NeonBlue
                        }
                    )
                ) {
                    Text(
                        text = when {
                            uiState.isConnected -> "Disconnect"
                            uiState.connectionState is BluetoothConnectionState.Connecting -> "..."
                            else -> "Connect"
                        },
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            
            // Error message display
            if (uiState.connectionState is BluetoothConnectionState.Error) {
                val error = uiState.connectionState as BluetoothConnectionState.Error
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = NeonRed.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "⚠️ ${error.message}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonRed,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
