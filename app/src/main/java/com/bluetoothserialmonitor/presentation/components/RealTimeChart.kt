package com.bluetoothserialmonitor.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.bluetoothserialmonitor.domain.model.ChartDataState
import com.bluetoothserialmonitor.presentation.theme.ChartGrid
import com.bluetoothserialmonitor.presentation.theme.ChartLinePrimary
import com.bluetoothserialmonitor.presentation.theme.NeonBlue
import com.bluetoothserialmonitor.presentation.theme.SurfaceElevated
import kotlinx.coroutines.launch

/**
 * Real-time chart component with zoom, pan, and auto-scroll capabilities
 */
@Composable
fun RealTimeChart(
    chartData: ChartDataState,
    onZoomChange: (Float) -> Unit,
    onScrollChange: (Int) -> Unit,
    onToggleAutoScroll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var zoomLevel by remember { mutableFloatStateOf(chartData.zoomLevel) }
    var scrollOffset by remember { mutableIntStateOf(chartData.scrollOffset) }
    val scope = rememberCoroutineScope()
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Chart Canvas
        ChartCanvas(
            dataPoints = chartData.dataPoints,
            zoomLevel = zoomLevel,
            scrollOffset = scrollOffset,
            minY = chartData.minY,
            maxY = chartData.maxY,
            showGrid = chartData.showGrid,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        // Handle pinch-to-zoom
                        val newZoom = (zoomLevel * zoom).coerceIn(0.5f, 5f)
                        zoomLevel = newZoom
                        onZoomChange(newZoom)
                        
                        // Handle pan gesture
                        if (zoomLevel > 1f) {
                            val delta = (pan.x * zoomLevel).toInt()
                            scrollOffset = (scrollOffset + delta).coerceIn(
                                0,
                                (chartData.dataPoints.size * (zoomLevel - 1)).toInt()
                            )
                            onScrollChange(scrollOffset)
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        if (zoomLevel > 1f) {
                            val delta = (dragAmount * zoomLevel).toInt()
                            scrollOffset = (scrollOffset + delta).coerceIn(
                                0,
                                (chartData.dataPoints.size * (zoomLevel - 1)).toInt()
                            )
                            onScrollChange(scrollOffset)
                        }
                    }
                }
        )
        
        // Chart Controls Overlay
        ChartControlsOverlay(
            zoomLevel = zoomLevel,
            autoScrollEnabled = chartData.autoScrollEnabled,
            onZoomIn = {
                scope.launch {
                    zoomLevel = (zoomLevel + 0.2f).coerceIn(1f, 5f)
                    onZoomChange(zoomLevel)
                }
            },
            onZoomOut = {
                scope.launch {
                    zoomLevel = (zoomLevel - 0.2f).coerceIn(1f, 5f)
                    onZoomChange(zoomLevel)
                }
            },
            onResetZoom = {
                scope.launch {
                    zoomLevel = 1f
                    scrollOffset = 0
                    onZoomChange(1f)
                    onScrollChange(0)
                }
            },
            onToggleAutoScroll = onToggleAutoScroll,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        )
        
        // Data point count indicator
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            shape = RoundedCornerShape(8.dp),
            color = SurfaceElevated.copy(alpha = 0.9f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CenterFocusStrong,
                    contentDescription = null,
                    tint = NeonBlue,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${chartData.dataPoints.size} pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
    }
}

/**
 * Custom Canvas-based chart rendering
 */
@Composable
private fun ChartCanvas(
    dataPoints: List<Float>,
    zoomLevel: Float,
    scrollOffset: Int,
    minY: Float,
    maxY: Float,
    showGrid: Boolean,
    modifier: Modifier = Modifier
) {
    androidx.compose.canvas.Canvas(
        modifier = modifier
            .background(SurfaceElevated, RoundedCornerShape(12.dp))
    ) {
        val width = size.width
        val height = size.height
        
        // Draw grid
        if (showGrid) {
            val gridColor = ChartGrid
            val horizontalLines = 5
            val verticalLines = 10
            
            // Horizontal grid lines
            for (i in 0..horizontalLines) {
                val y = (height / horizontalLines) * i
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
            }
            
            // Vertical grid lines
            for (i in 0..verticalLines) {
                val x = (width / verticalLines) * i
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
            }
        }
        
        // Draw data line
        if (dataPoints.isNotEmpty()) {
            val path = Path()
            val range = (dataPoints.size / zoomLevel).toInt().coerceAtLeast(1)
            val startIndex = scrollOffset.coerceIn(0, dataPoints.size - range)
            val endIndex = (startIndex + range).coerceAtMost(dataPoints.size)
            
            val visiblePoints = dataPoints.subList(startIndex, endIndex)
            if (visiblePoints.isNotEmpty()) {
                val stepX = width / (visiblePoints.size - 1).coerceAtLeast(1)
                
                visiblePoints.forEachIndexed { index, value ->
                    val x = index * stepX
                    val normalizedValue = (value - minY) / (maxY - minY)
                    val y = height - (normalizedValue * height).coerceIn(0f, height)
                    
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                
                // Draw the line
                drawPath(
                    path = path,
                    color = ChartLinePrimary,
                    style = Stroke(width = 2f)
                )
                
                // Draw gradient fill under the line
                // (Simplified - full implementation would use drawIntoCanvas)
            }
        }
        
        // Draw Y-axis labels
        val labelRange = maxY - minY
        for (i in 0..5) {
            val value = minY + (labelRange * i / 5)
            val y = height - (height * i / 5)
            drawContext.canvas.nativeCanvas.drawText(
                String.format("%.1f", value),
                4f,
                y - 4f,
                android.graphics.Paint().apply {
                    color = Color.White.hashCode()
                    textSize = 32f
                }
            )
        }
    }
}

/**
 * Floating controls overlay for chart interactions
 */
@Composable
private fun ChartControlsOverlay(
    zoomLevel: Float,
    autoScrollEnabled: Boolean,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onResetZoom: () -> Unit,
    onToggleAutoScroll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Zoom In
        FilledIconButton(
            onClick = onZoomIn,
            modifier = Modifier.size(40.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = SurfaceElevated,
                contentColor = NeonBlue
            )
        ) {
            Icon(
                imageVector = Icons.Default.ZoomIn,
                contentDescription = "Zoom In",
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Zoom Out
        FilledIconButton(
            onClick = onZoomOut,
            modifier = Modifier.size(40.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = SurfaceElevated,
                contentColor = NeonBlue
            )
        ) {
            Icon(
                imageVector = Icons.Default.ZoomOut,
                contentDescription = "Zoom Out",
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Reset Zoom
        FilledIconButton(
            onClick = onResetZoom,
            modifier = Modifier.size(40.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = SurfaceElevated,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(
                imageVector = Icons.Default.AutoFixHigh,
                contentDescription = "Reset",
                modifier = Modifier.size(20.dp)
            )
        }
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        
        // Auto-scroll Toggle
        FilledIconButton(
            onClick = onToggleAutoScroll,
            modifier = Modifier.size(40.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (autoScrollEnabled) NeonBlue.copy(alpha = 0.2f) else SurfaceElevated,
                contentColor = if (autoScrollEnabled) NeonBlue else MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(
                imageVector = Icons.Default.CenterFocusStrong,
                contentDescription = "Auto-scroll",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
