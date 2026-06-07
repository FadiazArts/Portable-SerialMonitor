package com.bluetoothserialmonitor.presentation.theme

import androidx.compose.ui.graphics.Color

// Core Background Colors
val DeepNavy = Color(0xFF0A0E17)        // Main background
val Onyx = Color(0xFF151A25)            // Card/Panel background
val SurfaceElevated = Color(0xFF1E2532) // Elevated surfaces
val SurfaceVariant = Color(0xFF2A3244)  // Variant surface for contrast

// Accent Colors (Functional Neon)
val NeonGreen = Color(0xFF00FF88)       // Connected/Recording success
val NeonRed = Color(0xFFFF3B6A)         // Disconnected/Error
val NeonBlue = Color(0xFF00D4FF)        // Chart line primary
val NeonPurple = Color(0xFFB967FF)      // Chart line secondary
val NeonOrange = Color(0xFFFF9F43)      // Warnings
val NeonYellow = Color(0xFFFFE66D)      // Highlights

// Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA0A8B8)
val TextTertiary = Color(0xFF6B7280)
val TextMonospace = Color(0xFF00FF88)   // Terminal text

// Status Colors
val StatusConnected = NeonGreen
val StatusDisconnected = NeonRed
val StatusRecording = NeonRed
val StatusScanning = NeonBlue

// Chart Colors
val ChartLinePrimary = NeonBlue
val ChartLineSecondary = NeonPurple
val ChartGrid = Color(0xFF2D3748)
val ChartBackground = Color(0x0A00D4FF) // Very transparent blue

// Utility Colors
val DividerColor = Color(0xFF2D3748)
val ScrollbarColor = Color(0xFF4A5568)
val CardBorder = Color(0xFF2D3748)
