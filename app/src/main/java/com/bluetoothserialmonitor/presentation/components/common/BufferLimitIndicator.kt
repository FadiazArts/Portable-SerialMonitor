package com.bluetoothserialmonitor.presentation.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.bluetoothserialmonitor.domain.model.TerminalBufferState
import com.bluetoothserialmonitor.presentation.theme.NeonOrange
import com.bluetoothserialmonitor.presentation.theme.SurfaceElevated

/**
 * Buffer Limit Indicator showing current utilization
 */
@Composable
fun BufferLimitIndicator(
    currentLines: Int,
    maxLines: Int,
    modifier: Modifier = Modifier
) {
    val utilization = (currentLines.toFloat() / maxLines.toFloat()).coerceIn(0f, 1f)
    val isNearLimit = utilization >= 0.9f
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Progress bar
        LinearProgressIndicator(
            progress = { utilization },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            trackColor = SurfaceElevated,
            color = if (isNearLimit) NeonOrange else MaterialTheme.colorScheme.primary
        )
        
        // Text indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Text(
                text = "Buffer",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            androidx.compose.material3.Text(
                text = "$currentLines / $maxLines lines",
                style = MaterialTheme.typography.labelSmall,
                color = if (isNearLimit) NeonOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}

/**
 * Compact buffer indicator for tight spaces
 */
@Composable
fun CompactBufferIndicator(
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
                    color = if (isNearLimit) NeonOrange else MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(3.dp)
                )
        )
        
        androidx.compose.material3.Text(
            text = "${terminalBuffer.lines.size}/${terminalBuffer.maxLines}",
            style = MaterialTheme.typography.labelSmall,
            color = if (isNearLimit) NeonOrange else MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
    }
}

/**
 * FIFO behavior notification badge
 */
@Composable
fun FifoBehaviorBadge(
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = SurfaceElevated
    ) {
        androidx.compose.material3.Text(
            text = "FIFO",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
