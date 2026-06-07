package com.bluetoothserialmonitor.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.bluetoothserialmonitor.domain.model.LogFormat
import com.bluetoothserialmonitor.domain.model.RecordingState
import com.bluetoothserialmonitor.presentation.common.RecordingStatusIndicator
import com.bluetoothserialmonitor.presentation.common.RecordingToggleButton
import com.bluetoothserialmonitor.presentation.theme.NeonBlue
import com.bluetoothserialmonitor.presentation.theme.NeonGreen
import com.bluetoothserialmonitor.presentation.theme.NeonRed
import com.bluetoothserialmonitor.presentation.theme.SurfaceElevated
import com.bluetoothserialmonitor.presentation.theme.SurfaceVariant

/**
 * Control and Logging Panel - Bottom section of the main screen
 * Contains TX input field, send button, recording controls, and format selector
 */
@Composable
fun ControlLoggingPanel(
    txInput: String,
    recordingState: RecordingState,
    logFormat: LogFormat,
    onTxInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onToggleRecording: () -> Unit,
    onFormatChange: (LogFormat) -> Unit,
    isConnected: Boolean = true,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SurfaceElevated,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: TX Command Input
            TxCommandInputSection(
                value = txInput,
                onValueChange = onTxInputChange,
                onSend = onSendClick,
                enabled = isConnected
            )
            
            HorizontalDivider(color = SurfaceVariant)
            
            // Section 2: Recording Controls
            RecordingControlSection(
                isRecording = recordingState.isRecording,
                elapsedSeconds = recordingState.elapsedSeconds,
                format = logFormat,
                filePath = recordingState.filePath,
                onToggleRecording = onToggleRecording,
                onFormatChange = onFormatChange
            )
        }
    }
}

/**
 * TX Command Input Field with Send Button
 */
@Composable
private fun TxCommandInputSection(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Send Command (TX)",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Quick command suggestions chip row
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChip(
                    onClick = { onValueChange("AT\n") },
                    label = { Text("AT", style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonBlue.copy(alpha = 0.2f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = NeonBlue,
                        selectedBorderColor = NeonBlue
                    ),
                    selected = value == "AT\n"
                )
                
                FilterChip(
                    onClick = { onValueChange("HELP\n") },
                    label = { Text("HELP", style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonBlue.copy(alpha = 0.2f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = NeonBlue,
                        selectedBorderColor = NeonBlue
                    ),
                    selected = value == "HELP\n"
                )
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Input field
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Enter command...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                enabled = enabled,
                maxLines = 3,
                minLines = 1,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (enabled) NeonBlue else SurfaceVariant,
                    unfocusedBorderColor = SurfaceVariant,
                    disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            )
            
            // Send button
            Button(
                onClick = onSend,
                modifier = Modifier
                    .height(56.dp)
                    .aspectRatio(1f),
                enabled = enabled && value.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (enabled && value.isNotBlank()) {
                        NeonGreen
                    } else {
                        SurfaceVariant
                    }
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Send",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Recording Control Section with toggle, timer, and format selector
 */
@Composable
private fun RecordingControlSection(
    isRecording: Boolean,
    elapsedSeconds: Long,
    format: LogFormat,
    filePath: String?,
    onToggleRecording: () -> Unit,
    onFormatChange: (LogFormat) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Data Logging",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Recording status indicator
            RecordingStatusIndicator(
                isRecording = isRecording,
                elapsedSeconds = elapsedSeconds
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Record toggle button
            RecordingToggleButton(
                isRecording = isRecording,
                onClick = onToggleRecording,
                modifier = Modifier.size(56.dp)
            )
            
            // Format selector and info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Format dropdown
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = "${format.extension.uppercase()} Format",
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            IconButton(onClick = { /* Show format selector */ }) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = "Format",
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
                    
                    // Format selection menu would go here
                    DropdownMenu(
                        expanded = false,
                        onDismissRequest = { }
                    ) {
                        LogFormat.values().forEach { fmt ->
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = format == fmt,
                                            onClick = { onFormatChange(fmt) }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(fmt.extension.uppercase())
                                    }
                                },
                                onClick = { onFormatChange(fmt) }
                            )
                        }
                    }
                }
                
                // File path display (when recording or after)
                filePath?.let { path ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "📁 $path",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp),
                            maxLines = 1
                        )
                    }
                }
            }
            
            // Format quick select chips
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LogFormat.values().forEach { fmt ->
                    AssistChip(
                        onClick = { onFormatChange(fmt) },
                        label = { 
                            Text(
                                text = fmt.extension.uppercase(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.height(32.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (format == fmt) {
                                NeonBlue.copy(alpha = 0.2f)
                            } else {
                                SurfaceVariant
                            }
                        ),
                        border = AssistChipDefaults.assistChipBorder(
                            borderColor = if (format == fmt) NeonBlue else SurfaceVariant
                        )
                    )
                }
            }
        }
        
        // Info text about storage
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceVariant
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Recorded data is saved to local storage separately from the visual buffer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Simple format selector component
 */
@Composable
fun LogFormatSelector(
    selectedFormat: LogFormat,
    onFormatSelected: (LogFormat) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LogFormat.values().forEach { format ->
            FilterChip(
                onClick = { onFormatSelected(format) },
                label = { 
                    Text(
                        text = format.extension.uppercase(),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = selectedFormat == format,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonBlue.copy(alpha = 0.2f),
                    selectedLabelColor = NeonBlue
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = SurfaceVariant,
                    selectedBorderColor = NeonBlue
                )
            )
        }
    }
}
