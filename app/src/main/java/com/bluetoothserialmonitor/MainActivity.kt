package com.bluetoothserialmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bluetoothserialmonitor.presentation.components.*
import com.bluetoothserialmonitor.presentation.theme.BluetoothSerialMonitorTheme
import com.bluetoothserialmonitor.presentation.viewmodel.SerialMonitorViewModel

/**
 * Main Activity for Bluetooth Serial Monitor & Logger
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            BluetoothSerialMonitorTheme {
                SerialMonitorApp()
            }
        }
    }
}

/**
 * Main App Composable - Entry point for the UI
 */
@Composable
fun SerialMonitorApp(
    viewModel: SerialMonitorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
        topBar = {
            ConnectionHeader(
                uiState = uiState,
                onScanClick = viewModel::scanForDevices,
                onConnectClick = viewModel::connectToDevice,
                onDisconnectClick = viewModel::disconnect,
                onShowDeviceSelector = viewModel::showDeviceSelector
            )
        },
        bottomBar = {
            ControlLoggingPanel(
                txInput = uiState.txInput,
                recordingState = uiState.recordingState,
                logFormat = uiState.recordingState.format,
                onTxInputChange = viewModel::updateTxInput,
                onSendClick = { viewModel.sendCommand(uiState.txInput) },
                onToggleRecording = viewModel::toggleRecording,
                onFormatChange = viewModel::setLogFormat,
                isConnected = uiState.isConnected
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Middle Section: Tabbed Content (Chart or Terminal)
            SerialMonitorContent(
                uiState = uiState,
                onTabChange = viewModel::switchTab,
                onSendCommand = viewModel::sendCommand,
                onToggleAutoScroll = viewModel::toggleAutoScroll
            )
        }
    }
}
