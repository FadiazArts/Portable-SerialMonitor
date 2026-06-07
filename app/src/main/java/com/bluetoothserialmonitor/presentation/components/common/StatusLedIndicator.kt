package com.bluetoothserialmonitor.presentation.components.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bluetoothserialmonitor.presentation.theme.NeonGreen
import com.bluetoothserialmonitor.presentation.theme.NeonRed

/**
 * LED Status Indicator with pulsing animation for connected state
 */
@Composable
fun StatusLedIndicator(
    isConnected: Boolean,
    size: Dp = 12.dp,
    modifier: Modifier = Modifier
) {
    val color = if (isConnected) NeonGreen else NeonRed
    
    // Infinite pulse animation for connected state
    val infiniteTransition = rememberInfiniteTransition(label = "ledPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ledAlpha"
    )
    
    Box(
        modifier = modifier
            .size(size)
            .then(
                if (isConnected) {
                    Modifier
                        .background(
                            color = color,
                            shape = CircleShape
                        )
                        .alpha(alpha)
                        .border(
                            width = 2.dp,
                            color = color.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                } else {
                    Modifier
                        .background(
                            color = color.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                }
            )
    )
}

/**
 * Enhanced LED indicator with glow effect
 */
@Composable
fun StatusLedWithGlow(
    isConnected: Boolean,
    size: Dp = 14.dp,
    modifier: Modifier = Modifier
) {
    val color = if (isConnected) NeonGreen else NeonRed
    
    val infiniteTransition = rememberInfiniteTransition(label = "ledGlow")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ledScale"
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Glow background
        Box(
            modifier = Modifier
                .size(size * scale)
                .background(
                    color = color.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        )
        
        // Main LED
        Box(
            modifier = Modifier
                .size(size)
                .background(
                    color = color,
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = Color.White.copy(alpha = 0.8f),
                    shape = CircleShape
                )
        )
    }
}

/**
 * Connection status badge combining LED and text
 */
@Composable
fun ConnectionStatusBadge(
    isConnected: Boolean,
    deviceName: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatusLedIndicator(isConnected = isConnected)
        
        deviceName?.let { name ->
            androidx.compose.material3.Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = if (isConnected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}
