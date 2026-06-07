package com.bluetoothserialmonitor.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bluetoothserialmonitor.domain.model.TerminalBufferState
import com.bluetoothserialmonitor.presentation.common.BufferLimitIndicator
import com.bluetoothserialmonitor.presentation.theme.MonospaceMedium
import com.bluetoothserialmonitor.presentation.theme.SurfaceElevated
import com.bluetoothserialmonitor.presentation.theme.TextMonospace

/**
 * Serial Terminal component with LazyColumn for efficient rendering
 * Implements FIFO buffer behavior with visual limit indicator
 */
@Composable
fun SerialTerminal(
    terminalBuffer: TerminalBufferState,
    onToggleAutoScroll: () -> Unit,
    onClearBuffer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    // Auto-scroll to bottom when new data arrives and auto-scroll is enabled
    LaunchedEffect(terminalBuffer.lines.size, terminalBuffer.autoScrollEnabled) {
        if (terminalBuffer.autoScrollEnabled && terminalBuffer.lines.isNotEmpty()) {
            listState.animateScrollToItem(terminalBuffer.lines.size - 1)
        }
    }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Terminal header with buffer indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Serial Monitor",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Buffer limit indicator
                    CompactBufferIndicator(
                        terminalBuffer = terminalBuffer,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Clear button
                    TextButton(
                        onClick = onClearBuffer,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = "Clear",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            
            // Terminal content area
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                color = SurfaceElevated,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Box {
                    if (terminalBuffer.lines.isEmpty()) {
                        // Empty state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "No data received",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Connect to a device to start monitoring",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        // Data lines
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            itemsIndexed(
                                items = terminalBuffer.lines,
                                key = { _, line -> line.hashCode() }
                            ) { index, line ->
                                TerminalLineItem(
                                    line = line,
                                    lineNumber = (terminalBuffer.totalLinesReceived - 
                                        terminalBuffer.lines.size + index + 1).coerceAtLeast(1)
                                )
                            }
                            
                            // Add spacer at bottom for scrolling room
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                    
                    // Auto-scroll toggle button (floating)
                    AnimatedVisibility(
                        visible = !terminalBuffer.autoScrollEnabled,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = onToggleAutoScroll,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = "Auto-scroll"
                                )
                            },
                            text = {
                                Text(text = "Auto-scroll")
                            },
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            // Footer with FIFO badge and stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FifoBehaviorBadge()
                
                Text(
                    text = "Total: ${terminalBuffer.totalLinesReceived} lines",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
    }
}

/**
 * Individual terminal line item with monospace font
 */
@Composable
private fun TerminalLineItem(
    line: String,
    lineNumber: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Line number
        Text(
            text = String.format("%06d", lineNumber),
            style = MaterialTheme.typography.monospaceSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.width(56.dp)
        )
        
        // Line content with syntax highlighting hints
        Text(
            text = line,
            style = MonospaceMedium,
            color = TextMonospace,
            maxLines = 10,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Compact buffer indicator for terminal header
 */
@Composable
private fun CompactBufferIndicator(
    terminalBuffer: TerminalBufferState,
    modifier: Modifier = Modifier
) {
    val utilization = terminalBuffer.utilizationPercentage()
    val isNearLimit = terminalBuffer.isNearLimit()
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Small dot indicator
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(
                    color = if (isNearLimit) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    shape = RoundedCornerShape(3.dp)
                )
        )
        
        Text(
            text = "${terminalBuffer.lines.size}/${terminalBuffer.maxLines}",
            style = MaterialTheme.typography.labelSmall,
            color = if (isNearLimit) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
    }
}
