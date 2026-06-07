package com.bluetoothserialmonitor.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bluetoothserialmonitor.domain.model.SerialMonitorTab
import com.bluetoothserialmonitor.domain.model.SerialMonitorUiState

/**
 * Main content area with tab navigation between Chart and Terminal views
 */
@Composable
fun SerialMonitorContent(
    uiState: SerialMonitorUiState,
    onTabChange: (SerialMonitorTab) -> Unit,
    onSendCommand: (String) -> Unit,
    onToggleAutoScroll: () -> Unit,
    onZoomChange: (Float) -> Unit = {},
    onScrollChange: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Tab Row
        SerialMonitorTabRow(
            selectedTab = uiState.selectedTab,
            onTabSelected = onTabChange
        )
        
        // Tab Content (takes remaining space)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (uiState.selectedTab) {
                SerialMonitorTab.CHART -> RealTimeChart(
                    chartData = uiState.chartData,
                    onZoomChange = onZoomChange,
                    onScrollChange = onScrollChange,
                    onToggleAutoScroll = onToggleAutoScroll,
                    modifier = Modifier.fillMaxSize()
                )
                SerialMonitorTab.TERMINAL -> SerialTerminal(
                    terminalBuffer = uiState.terminalBuffer,
                    onToggleAutoScroll = onToggleAutoScroll,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Custom tab row for Chart and Terminal navigation
 */
@Composable
private fun SerialMonitorTabRow(
    selectedTab: SerialMonitorTab,
    onTabSelected: (SerialMonitorTab) -> Unit,
    modifier: Modifier = Modifier
) {
    PrimaryTabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Tab(
            selected = selectedTab == SerialMonitorTab.CHART,
            onClick = { onTabSelected(SerialMonitorTab.CHART) },
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.BarChart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Chart",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        )
        
        Tab(
            selected = selectedTab == SerialMonitorTab.TERMINAL,
            onClick = { onTabSelected(SerialMonitorTab.TERMINAL) },
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Terminal,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Terminal",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        )
    }
}
