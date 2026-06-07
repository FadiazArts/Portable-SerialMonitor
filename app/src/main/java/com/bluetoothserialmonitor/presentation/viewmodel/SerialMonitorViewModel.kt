package com.bluetoothserialmonitor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluetoothserialmonitor.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Main ViewModel for the Serial Monitor application
 * Implements state hoisting pattern with StateFlow
 */
class SerialMonitorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SerialMonitorUiState())
    val uiState: StateFlow<SerialMonitorUiState> = _uiState.asStateFlow()

    // Actions

    /**
     * Scan for available Bluetooth devices
     */
    fun scanForDevices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, showDeviceSelector = true) }
            
            // Simulate scanning - replace with actual Bluetooth scan
            kotlinx.coroutines.delay(2000)
            
            val mockDevices = listOf(
                BluetoothDeviceWrapper("HC-05", "00:1A:7D:DA:71:13", rssi = -45),
                BluetoothDeviceWrapper("Arduino BT", "98:D3:31:20:FB:F8", rssi = -62),
                BluetoothDeviceWrapper("ESP32 Serial", "24:6F:28:A4:B5:C6", rssi = -55)
            )
            
            _uiState.update { 
                it.copy(
                    isScanning = false,
                    availableDevices = androidx.collection.persistentListOf(*mockDevices.toTypedArray())
                )
            }
        }
    }

    /**
     * Connect to a specific Bluetooth device
     */
    fun connectToDevice(device: BluetoothDeviceWrapper) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true,
                    connectionState = BluetoothConnectionState.Connecting(device.name, device.macAddress)
                )
            }
            
            // Simulate connection - replace with actual Bluetooth connection
            kotlinx.coroutines.delay(1500)
            
            _uiState.update {
                it.copy(
                    isLoading = false,
                    connectionState = BluetoothConnectionState.Connected(device.name, device.macAddress)
                )
            }
        }
    }

    /**
     * Disconnect from current device
     */
    fun disconnect() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    connectionState = BluetoothConnectionState.Disconnected
                )
            }
        }
    }

    /**
     * Send serial command (TX)
     */
    fun sendCommand(command: String) {
        if (_uiState.value.canSendData.not()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Simulate sending command - replace with actual Bluetooth write
            kotlinx.coroutines.delay(100)
            
            // Add command to terminal buffer
            addToTerminalBuffer("> $command")
            
            // Simulate response
            kotlinx.coroutines.delay(200)
            addToTerminalBuffer("< OK")
            
            _uiState.update { it.copy(isLoading = false, txInput = "") }
        }
    }

    /**
     * Toggle recording state
     */
    fun toggleRecording() {
        val currentState = _uiState.value.recordingState
        
        if (currentState.isRecording) {
            // Stop recording
            _uiState.update {
                it.copy(
                    recordingState = currentState.copy(
                        isRecording = false,
                        filePath = "/storage/emulated/0/Download/serial_log_${System.currentTimeMillis()}.${currentState.format.extension}"
                    )
                )
            }
        } else {
            // Start recording
            _uiState.update {
                it.copy(
                    recordingState = currentState.copy(
                        isRecording = true,
                        startTime = System.currentTimeMillis(),
                        elapsedSeconds = 0
                    )
                )
            }
            
            // Start timer
            startRecordingTimer()
        }
    }

    /**
     * Update TX input field
     */
    fun updateTxInput(input: String) {
        _uiState.update { it.copy(txInput = input) }
    }

    /**
     * Switch between Chart and Terminal tabs
     */
    fun switchTab(tab: SerialMonitorTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    /**
     * Toggle auto-scroll for chart or terminal
     */
    fun toggleAutoScroll() {
        when (_uiState.value.selectedTab) {
            SerialMonitorTab.CHART -> {
                _uiState.update {
                    it.copy(
                        chartData = it.chartData.copy(
                            autoScrollEnabled = !it.chartData.autoScrollEnabled
                        )
                    )
                }
            }
            SerialMonitorTab.TERMINAL -> {
                _uiState.update {
                    it.copy(
                        terminalBuffer = it.terminalBuffer.copy(
                            autoScrollEnabled = !it.terminalBuffer.autoScrollEnabled
                        )
                    )
                }
            }
        }
    }

    /**
     * Set log format for recording
     */
    fun setLogFormat(format: LogFormat) {
        _uiState.update {
            it.copy(
                recordingState = it.recordingState.copy(format = format)
            )
        }
    }

    /**
     * Show or hide device selector dropdown
     */
    fun showDeviceSelector(show: Boolean) {
        _uiState.update { it.copy(showDeviceSelector = show) }
    }

    /**
     * Clear terminal buffer
     */
    fun clearTerminalBuffer() {
        _uiState.update {
            it.copy(
                terminalBuffer = TerminalBufferState(
                    maxLines = it.terminalBuffer.maxLines,
                    autoScrollEnabled = it.terminalBuffer.autoScrollEnabled
                )
            )
        }
    }

    // Helper methods

    private fun addToTerminalBuffer(line: String) {
        val currentBuffer = _uiState.value.terminalBuffer
        val newLines = currentBuffer.lines.add(line)
        
        // FIFO behavior: remove oldest if at limit
        val finalLines = if (newLines.size > currentBuffer.maxLines) {
            newLines.removeAt(0)
            newLines
        } else {
            newLines
        }
        
        _uiState.update {
            it.copy(
                terminalBuffer = currentBuffer.copy(
                    lines = finalLines,
                    totalLinesReceived = currentBuffer.totalLinesReceived + 1
                )
            )
        }
    }

    private fun startRecordingTimer() {
        viewModelScope.launch {
            while (_uiState.value.recordingState.isRecording) {
                kotlinx.coroutines.delay(1000)
                _uiState.update {
                    it.copy(
                        recordingState = it.recordingState.copy(
                            elapsedSeconds = it.recordingState.elapsedSeconds + 1
                        )
                    )
                }
            }
        }
    }

    /**
     * Simulate incoming data (for testing/demo purposes)
     * Replace with actual Bluetooth data stream
     */
    fun simulateIncomingData(data: String) {
        viewModelScope.launch {
            addToTerminalBuffer(data)
            
            // Parse numeric data for chart
            data.toFloatOrNull()?.let { value ->
                val currentChart = _uiState.value.chartData
                val newPoints = currentChart.dataPoints.add(value)
                
                // FIFO behavior for chart
                val finalPoints = if (newPoints.size > currentChart.maxPoints) {
                    newPoints.removeAt(0)
                    newPoints
                } else {
                    newPoints
                }
                
                _uiState.update {
                    it.copy(
                        chartData = currentChart.copy(
                            dataPoints = finalPoints,
                            timestamps = currentChart.timestamps.add(System.currentTimeMillis())
                        )
                    )
                }
            }
        }
    }
}
