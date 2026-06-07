package com.bluetoothserialmonitor.presentation.components.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.bluetoothserialmonitor.presentation.theme.NeonRed
import com.bluetoothserialmonitor.presentation.theme.SurfaceElevated

/**
 * Recording Timer displaying elapsed time in MM:SS format
 */
@Composable
fun RecordingTimer(
    elapsedSeconds: Long,
    modifier: Modifier = Modifier
) {
    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    
    androidx.compose.material3.Text(
        text = String.format("%02d:%02d", minutes, seconds),
        style = MaterialTheme.typography.monospaceMedium,
        color = NeonRed,
        modifier = modifier
    )
}

/**
 * Recording Button with pulsing animation when active
 */
@Composable
fun RecordingToggleButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "recordingPulse")
    
    val borderColor by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderAlpha"
    )
    
    val backgroundColor by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bgAlpha"
    )
    
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier
            .size(56.dp)
            .then(
                if (isRecording) {
                    Modifier
                        .border(
                            width = 2.dp,
                            color = NeonRed.copy(alpha = borderColor),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    NeonRed.copy(alpha = backgroundColor),
                                    SurfaceElevated
                                )
                            )
                        )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = if (isRecording) {
                NeonRed.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.primaryContainer
            },
            contentColor = if (isRecording) {
                androidx.compose.ui.graphics.Color.White
            } else {
                MaterialTheme.colorScheme.onPrimaryContainer
            }
        )
    ) {
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isRecording) {
                // Stop icon (square)
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color.White, RoundedCornerShape(2.dp))
                )
            } else {
                // Record icon (circle)
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonRed)
                )
            }
        }
    }
}

/**
 * Recording Status Indicator showing timer and status
 */
@Composable
fun RecordingStatusIndicator(
    isRecording: Boolean,
    elapsedSeconds: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Pulsing dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = if (isRecording) NeonRed else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                )
        )
        
        // Timer or "Ready" text
        if (isRecording) {
            RecordingTimer(elapsedSeconds = elapsedSeconds)
        } else {
            androidx.compose.material3.Text(
                text = "Ready to Record",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
