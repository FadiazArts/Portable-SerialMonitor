package com.bluetoothserialmonitor.presentation.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bluetoothserialmonitor.domain.model.*
import com.bluetoothserialmonitor.presentation.components.*
import com.bluetoothserialmonitor.presentation.theme.BluetoothSerialMonitorTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sin

/**
 * UI/UX Visualization Preview - Main Screen
 * 
 * This preview demonstrates the complete main screen layout with:
 * - Connection header with LED status indicator
 * - Tab navigation between Chart and Terminal views
 * - Real-time chart with simulated sensor data
 * - Serial terminal with rolling buffer
 * - Control and logging panel
 */
@Preview(showBackground = true, showSystemUi = true, device = "spec:width=1080px,height=2340px,dpi=420")
@Composable
fun MainScreenPreview() {
    BluetoothSerialMonitorTheme {
        // Simulated ViewModel State
        val uiState = remember {
            MutableStateFlow(
                SerialMonitorUiState(
                    connectionState = ConnectionState.Connected("HC-05", "00:1A:7D:DA:71:13", -45),
                    serialData = buildList {
                        repeat(50) { i ->
                            add(SerialLogEntry(
                                timestamp = System.currentTimeMillis() - (50 - i) * 100L,
                                data = "[RX] Sensor: ${(20 + sin(i * 0.5) * 10).toInt()}°C, Humidity: ${(60 + sin(i * 0.3) * 15).toInt()}%",
                                direction = DataDirection.RECEIVE
                            ))
                        }
                    },
                    chartDataPoints = buildList {
                        repeat(100) { i ->
                            add(
                                ChartDataPoint(
                                    timestamp = System.currentTimeMillis() - (100 - i) * 100L,
                                    value = 20 + sin(i * 0.3) * 15 + (Math.random() * 5).toFloat()
                                )
                            )
                        }
                    },
                    isRecording = true,
                    recordingStartTime = System.currentTimeMillis() - 125000,
                    logFormat = LogFormat.CSV,
                    bufferLimit = 1000,
                    autoScrollEnabled = true,
                    errorMessage = null
                )
            ).asStateFlow()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Section
            ConnectionHeader(
                uiState = uiState.collectAsState().value,
                onScanDevices = { /* Scan action */ },
                onConnectDisconnect = { /* Connect/Disconnect action */ },
                onDeviceSelected = { /* Device selection */ }
            )

            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = 1.dp
            )

            // Main Content with Tabs
            SerialMonitorContent(
                uiState = uiState.collectAsState().value,
                onSendCommand = { /* Send command */ },
                onToggleRecording = { /* Toggle recording */ },
                onFormatChanged = { /* Format change */ },
                onAutoScrollToggled = { /* Auto-scroll toggle */ },
                onClearTerminal = { /* Clear terminal */ }
            )
        }
    }
}

/**
 * UI/UX Visualization - Connected State with Active Recording
 * 
 * Shows the interface when:
 * - Bluetooth is connected to a device
 * - Data logging is active
 * - Real-time data is flowing
 */
@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun ConnectedWithRecordingPreview() {
    BluetoothSerialMonitorTheme {
        val uiState = SerialMonitorUiState(
            connectionState = ConnectionState.Connected("HM-10", "A4:C1:38:8D:18:B2", -52),
            serialData = listOf(
                SerialLogEntry(System.currentTimeMillis() - 5000, "> AT+RESET", DataDirection.TRANSMIT),
                SerialLogEntry(System.currentTimeMillis() - 4800, "< OK", DataDirection.RECEIVE),
                SerialLogEntry(System.currentTimeMillis() - 4500, "> READ:SENSOR:1", DataDirection.TRANSMIT),
                SerialLogEntry(System.currentTimeMillis() - 4200, "< TEMP:23.5,HUM:65.2,PRESS:1013.25", DataDirection.RECEIVE),
                SerialLogEntry(System.currentTimeMillis() - 3900, "> READ:SENSOR:2", DataDirection.TRANSMIT),
                SerialLogEntry(System.currentTimeMillis() - 3600, "< TEMP:24.1,HUM:63.8,PRESS:1013.30", DataDirection.RECEIVE),
                SerialLogEntry(System.currentTimeMillis() - 3300, "> READ:SENSOR:3", DataDirection.TRANSMIT),
                SerialLogEntry(System.currentTimeMillis() - 3000, "< TEMP:23.8,HUM:64.5,PRESS:1013.28", DataDirection.RECEIVE),
                SerialLogEntry(System.currentTimeMillis() - 2700, "> STATUS?", DataDirection.TRANSMIT),
                SerialLogEntry(System.currentTimeMillis() - 2400, "< BAT:85%,SIGNAL:-52dBm,UPTIME:3600s", DataDirection.RECEIVE),
                SerialLogEntry(System.currentTimeMillis() - 2100, "> LOG:START", DataDirection.TRANSMIT),
                SerialLogEntry(System.currentTimeMillis() - 1800, "< LOGGING_ACTIVE", DataDirection.RECEIVE),
                SerialLogEntry(System.currentTimeMillis() - 1500, "< DATA:0x4A,0x7F,0x2C,0x91", DataDirection.RECEIVE),
                SerialLogEntry(System.currentTimeMillis() - 1200, "< DATA:0x3E,0x88,0x15,0xA7", DataDirection.RECEIVE),
                SerialLogEntry(System.currentTimeMillis() - 900, "> CONFIG:SAMPLING:100ms", DataDirection.TRANSMIT),
                SerialLogEntry(System.currentTimeMillis() - 600, "< CONFIG_OK", DataDirection.RECEIVE),
                SerialLogEntry(System.currentTimeMillis() - 300, "< SENSOR_UPDATE:23.9°C", DataDirection.RECEIVE)
            ),
            chartDataPoints = List(80) { i ->
                ChartDataPoint(
                    timestamp = System.currentTimeMillis() - (80 - i) * 100L,
                    value = 20 + sin(i * 0.4) * 12 + (Math.random() * 3).toFloat()
                )
            },
            isRecording = true,
            recordingStartTime = System.currentTimeMillis() - 87000,
            logFormat = LogFormat.CSV,
            bufferLimit = 1000,
            autoScrollEnabled = true,
            errorMessage = null
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                ConnectionHeader(
                    uiState = uiState,
                    onScanDevices = {},
                    onConnectDisconnect = {},
                    onDeviceSelected = {}
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Chart Tab Preview
                RealTimeChart(
                    dataPoints = uiState.chartDataPoints,
                    isAutoScrolling = true,
                    onZoomChanged = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ControlLoggingPanel(
                    uiState = uiState,
                    onSendCommand = {},
                    onToggleRecording = {},
                    onFormatChanged = {},
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

/**
 * UI/UX Visualization - Disconnected State
 * 
 * Shows the interface when:
 * - No Bluetooth connection
 * - User needs to scan and connect
 * - Recording is disabled
 */
@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun DisconnectedStatePreview() {
    BluetoothSerialMonitorTheme {
        val uiState = SerialMonitorUiState(
            connectionState = ConnectionState.Disconnected,
            serialData = emptyList(),
            chartDataPoints = emptyList(),
            isRecording = false,
            recordingStartTime = null,
            logFormat = LogFormat.TXT,
            bufferLimit = 1000,
            autoScrollEnabled = true,
            errorMessage = null
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                ConnectionHeader(
                    uiState = uiState,
                    onScanDevices = {},
                    onConnectDisconnect = {},
                    onDeviceSelected = {}
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "No Connection",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap 'Scan Devices' to find and connect to a Bluetooth device",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.widthIn(max = 250.dp)
                        )
                    }
                }
                
                ControlLoggingPanel(
                    uiState = uiState,
                    onSendCommand = {},
                    onToggleRecording = {},
                    onFormatChanged = {},
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

/**
 * UI/UX Visualization - Terminal View with Buffer Full
 * 
 * Shows the terminal tab when:
 * - Buffer is near capacity
 * - FIFO behavior is active
 * - Multiple data directions shown
 */
@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun TerminalBufferFullPreview() {
    BluetoothSerialMonitorTheme {
        val sampleData = buildList {
            repeat(20) { i ->
                val timestamp = System.currentTimeMillis() - (20 - i) * 200L
                when (i % 3) {
                    0 -> add(SerialLogEntry(timestamp, "> COMMAND_$i: Request data packet", DataDirection.TRANSMIT))
                    1 -> add(SerialLogEntry(timestamp, "< RESPONSE_$i: 0x${(i * 256 + i).toString(16).uppercase()}", DataDirection.RECEIVE))
                    2 -> add(SerialLogEntry(timestamp, "< SYSTEM: Buffer ${i * 5}% utilized", DataDirection.SYSTEM))
                }
            }
        }

        val uiState = SerialMonitorUiState(
            connectionState = ConnectionState.Connected("ESP32-BT", "24:6F:28:A3:B7:C1", -68),
            serialData = sampleData,
            chartDataPoints = emptyList(),
            isRecording = false,
            recordingStartTime = null,
            logFormat = LogFormat.TXT,
            bufferLimit = 1000,
            autoScrollEnabled = false,
            errorMessage = null
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                ConnectionHeader(
                    uiState = uiState,
                    onScanDevices = {},
                    onConnectDisconnect = {},
                    onDeviceSelected = {}
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                SerialTerminal(
                    serialData = uiState.serialData,
                    bufferLimit = uiState.bufferLimit,
                    autoScrollEnabled = uiState.autoScrollEnabled,
                    onAutoScrollToggled = {},
                    onClearTerminal = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ControlLoggingPanel(
                    uiState = uiState,
                    onSendCommand = {},
                    onToggleRecording = {},
                    onFormatChanged = {},
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

/**
 * UI/UX Visualization - Error State
 * 
 * Shows the interface when:
 * - Connection error occurred
 * - Error message displayed
 * - User can retry connection
 */
@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun ErrorStatePreview() {
    BluetoothSerialMonitorTheme {
        val uiState = SerialMonitorUiState(
            connectionState = ConnectionState.Error("Connection timeout: Device not responding"),
            serialData = listOf(
                SerialLogEntry(System.currentTimeMillis() - 10000, "> Connecting to device...", DataDirection.SYSTEM),
                SerialLogEntry(System.currentTimeMillis() - 8000, "< Scanning for services...", DataDirection.SYSTEM),
                SerialLogEntry(System.currentTimeMillis() - 5000, "< Attempting GATT connection...", DataDirection.SYSTEM),
                SerialLogEntry(System.currentTimeMillis() - 2000, "< ERROR: Connection timeout after 3 attempts", DataDirection.SYSTEM)
            ),
            chartDataPoints = emptyList(),
            isRecording = false,
            recordingStartTime = null,
            logFormat = LogFormat.TXT,
            bufferLimit = 1000,
            autoScrollEnabled = true,
            errorMessage = "Connection timeout: Device not responding"
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                ConnectionHeader(
                    uiState = uiState,
                    onScanDevices = {},
                    onConnectDisconnect = {},
                    onDeviceSelected = {}
                )
                
                // Error Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Connection Error",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = uiState.errorMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                
                ControlLoggingPanel(
                    uiState = uiState,
                    onSendCommand = {},
                    onToggleRecording = {},
                    onFormatChanged = {},
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

/**
 * UI/UX Visualization - Component Showcase
 * 
 * Individual component previews for design system documentation
 */
@Preview(showBackground = true, widthDp = 400)
@Composable
fun ComponentShowcasePreview() {
    BluetoothSerialMonitorTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "UI Component Showcase",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            // Status LED Indicators
            Column {
                Text(
                    text = "Status LED Indicators",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusLedIndicator(connectionState = ConnectionState.Connected("Device", "00:00:00:00:00:00", -50))
                    Text("Connected", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusLedIndicator(connectionState = ConnectionState.Disconnected)
                    Text("Disconnected", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusLedIndicator(connectionState = ConnectionState.Error("Test error"))
                    Text("Error", style = MaterialTheme.typography.bodyMedium)
                }
            }
            
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            
            // Recording Timer
            Column {
                Text(
                    text = "Recording Timer",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                RecordingTimer(
                    isRecording = true,
                    startTime = System.currentTimeMillis() - 125000
                )
            }
            
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            
            // Buffer Limit Indicator
            Column {
                Text(
                    text = "Buffer Limit Indicator",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                BufferLimitIndicator(currentCount = 847, limit = 1000)
                Spacer(modifier = Modifier.height(8.dp))
                BufferLimitIndicator(currentCount = 985, limit = 1000)
            }
            
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            
            // Log Format Selector
            Column {
                Text(
                    text = "Log Format Selector",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = true,
                        onClick = {},
                        label = { Text("CSV") }
                    )
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text("TXT") }
                    )
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text("JSON") }
                    )
                }
            }
        }
    }
}

/**
 * UI/UX Visualization - Dark Theme Color Palette
 * 
 * Display all colors used in the design system
 */
@Preview(showBackground = true, widthDp = 400)
@Composable
fun ColorPalettePreview() {
    BluetoothSerialMonitorTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Color Palette",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            ColorSwatch(color = MaterialTheme.colorScheme.background, name = "Background")
            ColorSwatch(color = MaterialTheme.colorScheme.surface, name = "Surface")
            ColorSwatch(color = MaterialTheme.colorScheme.primary, name = "Primary")
            ColorSwatch(color = MaterialTheme.colorScheme.secondary, name = "Secondary")
            ColorSwatch(color = MaterialTheme.colorScheme.tertiary, name = "Tertiary")
            ColorSwatch(color = MaterialTheme.colorScheme.error, name = "Error")
            ColorSwatch(color = MaterialTheme.colorScheme.onPrimary, name = "On Primary")
            ColorSwatch(color = MaterialTheme.colorScheme.onSurface, name = "On Surface")
            ColorSwatch(color = MaterialTheme.colorScheme.outline, name = "Outline")
        }
    }
}

@Composable
private fun ColorSwatch(color: Color, name: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color, shape = MaterialTheme.shapes.small),
            contentAlignment = Alignment.Center
        ) {
            if (color.luminance() > 0.5f) {
                Text("A", color = Color.Black, style = MaterialTheme.typography.labelSmall)
            } else {
                Text("A", color = Color.White, style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
