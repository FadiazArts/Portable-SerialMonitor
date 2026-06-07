package com.bluetoothserialmonitor.domain.model

import androidx.compose.runtime.Immutable
import androidx.collection.ImmutableList
import androidx.collection.persistentListOf

// Connection State
@Immutable
sealed class BluetoothConnectionState {
    object Disconnected : BluetoothConnectionState()
    object Scanning : BluetoothConnectionState()
    data class Connecting(val deviceName: String, val macAddress: String? = null) : BluetoothConnectionState()
    data class Connected(val deviceName: String, val macAddress: String) : BluetoothConnectionState()
    data class Error(val message: String, val errorCode: Int? = null) : BluetoothConnectionState()
}

// Recording State
@Immutable
data class RecordingState(
    val isRecording: Boolean = false,
    val startTime: Long? = null,
    val elapsedSeconds: Long = 0,
    val format: LogFormat = LogFormat.CSV,
    val filePath: String? = null,
    val fileSize: Long = 0L
)

enum class LogFormat(val extension: String, val mimeType: String) {
    CSV("csv", "text/csv"),
    TXT("txt", "text/plain"),
    JSON("json", "application/json")
}

// Terminal Buffer State
@Immutable
data class TerminalBufferState(
    val lines: ImmutableList<String> = persistentListOf(),
    val maxLines: Int = 1000,
    val autoScrollEnabled: Boolean = true,
    val totalLinesReceived: Long = 0,
    val lastUpdateTime: Long = System.currentTimeMillis()
) {
    fun isNearLimit(threshold: Float = 0.9f): Boolean {
        return lines.size >= maxLines * threshold
    }
    
    fun utilizationPercentage(): Float {
        return (lines.size.toFloat() / maxLines.toFloat()).coerceIn(0f, 1f)
    }
}

// Chart Data State
@Immutable
data class ChartDataState(
    val dataPoints: ImmutableList<Float> = persistentListOf(),
    val timestamps: ImmutableList<Long> = persistentListOf(),
    val maxPoints: Int = 500,
    val zoomLevel: Float = 1.0f,
    val scrollOffset: Int = 0,
    val autoScrollEnabled: Boolean = true,
    val minY: Float = 0f,
    val maxY: Float = 100f,
    val visibleRange: Int = 100,
    val lineColor: Long = 0xFF00D4FF, // NeonBlue
    val showGrid: Boolean = true,
    val chartType: ChartType = ChartType.LINE
) {
    enum class ChartType {
        LINE, SCATTER, AREA
    }
    
    fun isNearLimit(threshold: Float = 0.9f): Boolean {
        return dataPoints.size >= maxPoints * threshold
    }
}

// Main UI State (Hoisted)
@Immutable
data class SerialMonitorUiState(
    val connectionState: BluetoothConnectionState = BluetoothConnectionState.Disconnected,
    val recordingState: RecordingState = RecordingState(),
    val terminalBuffer: TerminalBufferState = TerminalBufferState(),
    val chartData: ChartDataState = ChartDataState(),
    val txInput: String = "",
    val selectedTab: SerialMonitorTab = SerialMonitorTab.CHART,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val availableDevices: ImmutableList<BluetoothDeviceWrapper> = persistentListOf(),
    val isScanning: Boolean = false,
    val showDeviceSelector: Boolean = false,
    val logDirectoryPath: String? = null
) {
    val isConnected: Boolean
        get() = connectionState is BluetoothConnectionState.Connected
    
    val isDisconnected: Boolean
        get() = connectionState is BluetoothConnectionState.Disconnected || 
                connectionState is BluetoothConnectionState.Error
    
    val canSendData: Boolean
        get() = isConnected && !isLoading
    
    val hasError: Boolean
        get() = connectionState is BluetoothConnectionState.Error
}

enum class SerialMonitorTab {
    CHART, TERMINAL
}

// Wrapper for BluetoothDevice (platform-specific)
@Immutable
data class BluetoothDeviceWrapper(
    val name: String,
    val macAddress: String,
    val rssi: Int? = null,
    val isConnected: Boolean = false
) {
    val signalStrength: SignalStrength
        get() = when {
            rssi == null -> SignalStrength.UNKNOWN
            rssi >= -50 -> SignalStrength.EXCELLENT
            rssi >= -60 -> SignalStrength.GOOD
            rssi >= -70 -> SignalStrength.FAIR
            else -> SignalStrength.WEAK
        }
}

enum class SignalStrength {
    EXCELLENT, GOOD, FAIR, WEAK, UNKNOWN
}
