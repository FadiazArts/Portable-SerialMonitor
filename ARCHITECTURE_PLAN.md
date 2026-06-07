# Bluetooth Serial Monitor & Logger - UI Architecture Plan

## 1. Design System & Theme

### Color Palette (Dark Mode Default)
```kotlin
// Core Background Colors
val DeepNavy = Color(0xFF0A0E17)        // Main background
val Onyx = Color(0xFF151A25)            // Card/Panel background
val SurfaceElevated = Color(0xFF1E2532) // Elevated surfaces

// Accent Colors (Functional Neon)
val NeonGreen = Color(0xFF00FF88)       // Connected/Recording success
val NeonRed = Color(0xFFFF3B6A)         // Disconnected/Error
val NeonBlue = Color(0xFF00D4FF)        // Chart line primary
val NeonPurple = Color(0xFFB967FF)      // Chart line secondary
val NeonOrange = Color(0xFFFF9F43)      // Warnings

// Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA0A8B8)
val TextMonospace = Color(0xFF00FF88)   // Terminal text

// Status Colors
val StatusConnected = NeonGreen
val StatusDisconnected = NeonRed
val StatusRecording = NeonRed
```

### Typography
```kotlin
// Main UI: Modern Sans-Serif (e.g., Inter, Roboto)
val typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp
    ),
    // Terminal/Monospace data
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)
```

---

## 2. State Management Architecture

### State Models (Sealed Classes & Data Classes)

```kotlin
// Connection State
sealed class BluetoothConnectionState {
    object Disconnected : BluetoothConnectionState()
    object Scanning : BluetoothConnectionState()
    data class Connecting(val deviceName: String) : BluetoothConnectionState()
    data class Connected(val deviceName: String, val macAddress: String) : BluetoothConnectionState()
    data class Error(val message: String) : BluetoothConnectionState()
}

// Recording State
data class RecordingState(
    val isRecording: Boolean = false,
    val startTime: Long? = null,
    val elapsedSeconds: Long = 0,
    val format: LogFormat = LogFormat.CSV,
    val filePath: String? = null
)

enum class LogFormat {
    CSV, TXT, JSON
}

// Terminal Buffer State
data class TerminalBufferState(
    val lines: ImmutableList<String> = persistentListOf(),
    val maxLines: Int = 1000,
    val autoScrollEnabled: Boolean = true,
    val totalLinesReceived: Long = 0
)

// Chart Data State
data class ChartDataState(
    val dataPoints: ImmutableList<Float> = persistentListOf(),
    val maxPoints: Int = 500,
    val zoomLevel: Float = 1.0f,
    val scrollOffset: Int = 0,
    val autoScrollEnabled: Boolean = true,
    val minY: Float = 0f,
    val maxY: Float = 100f
)

// Main UI State (Hoisted)
data class SerialMonitorUiState(
    val connectionState: BluetoothConnectionState = BluetoothConnectionState.Disconnected,
    val recordingState: RecordingState = RecordingState(),
    val terminalBuffer: TerminalBufferState = TerminalBufferState(),
    val chartData: ChartDataState = ChartDataState(),
    val txInput: String = "",
    val selectedTab: SerialMonitorTab = SerialMonitorTab.CHART,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class SerialMonitorTab {
    CHART, TERMINAL
}
```

### ViewModel (State Hoisting)

```kotlin
@HiltViewModel
class SerialMonitorViewModel @Inject constructor(
    private val bluetoothService: BluetoothService,
    private val loggingRepository: LoggingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SerialMonitorUiState())
    val uiState: StateFlow<SerialMonitorUiState> = _uiState.asStateFlow()

    // Actions
    fun scanForDevices() { /* ... */ }
    fun connectToDevice(device: BluetoothDevice) { /* ... */ }
    fun disconnect() { /* ... */ }
    fun sendCommand(command: String) { /* ... */ }
    fun toggleRecording() { /* ... */ }
    fun updateTxInput(input: String) { /* ... */ }
    fun switchTab(tab: SerialMonitorTab) { /* ... */ }
    fun toggleAutoScroll() { /* ... */ }
    fun setLogFormat(format: LogFormat) { /* ... */ }

    // Background data processing (coroutines)
    private fun processIncomingData(data: String) {
        viewModelScope.launch {
            // Update terminal buffer with FIFO logic
            // Update chart data points
            // Write to file if recording
        }
    }
}
```

---

## 3. Modular Composable Component Structure

### Main Screen Layout

```kotlin
@Composable
fun SerialMonitorApp(
    viewModel: SerialMonitorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { ConnectionHeader(...) },
        bottomBar = { ControlLoggingPanel(...) }
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
```

### Component Breakdown

#### 1. Header & Connection Bar

```kotlin
@Composable
fun ConnectionHeader(
    connectionState: BluetoothConnectionState,
    onScanClick: () -> Unit,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // LED Status Indicator
    // Device Selector Dropdown/BottomSheet
    // Connect/Disconnect Button
}

@Composable
fun StatusLedIndicator(
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    // Pulsing animation for connected state
}

@Composable
fun DeviceSelectorDropdown(
    devices: List<BluetoothDevice>,
    selectedDevice: BluetoothDevice?,
    onDeviceSelected: (BluetoothDevice) -> Unit,
    onScanRequested: () -> Unit
) {
    // Exposed dropdown menu or bottom sheet
}
```

#### 2. Real-Time Plot Chart (Tab 1)

```kotlin
@Composable
fun RealTimeChart(
    chartData: ChartDataState,
    onZoomChange: (Float) -> Unit,
    onScrollChange: (Int) -> Unit,
    onToggleAutoScroll: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Custom Canvas-based chart or library integration
    // Zoom/Pan gestures
    // Auto-scroll toggle button overlay
}

@Composable
fun ChartControlsOverlay(
    zoomLevel: Float,
    autoScrollEnabled: Boolean,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onToggleAutoScroll: () -> Unit
) {
    // Floating controls for chart interaction
}
```

#### 3. Serial Terminal & Log Monitor (Tab 2)

```kotlin
@Composable
fun SerialTerminal(
    terminalBuffer: TerminalBufferState,
    onToggleAutoScroll: () -> Unit,
    modifier: Modifier = Modifier
) {
    // LazyColumn for efficient rendering
    // Monospace font for all text
    // Visual buffer limit indicator
}

@Composable
fun TerminalLineItem(
    line: String,
    lineNumber: Long,
    modifier: Modifier = Modifier
) {
    Text(
        text = line,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
fun BufferLimitIndicator(
    currentLines: Int,
    maxLines: Int,
    modifier: Modifier = Modifier
) {
    // Progress bar or text indicator: "750/1000 lines"
}
```

#### 4. Control & Logging Panel (Bottom Section)

```kotlin
@Composable
fun ControlLoggingPanel(
    txInput: String,
    recordingState: RecordingState,
    logFormat: LogFormat,
    onTxInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onToggleRecording: () -> Unit,
    onFormatChange: (LogFormat) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // TX Input Field + Send Button
        TxCommandInput(
            value = txInput,
            onValueChange = onTxInputChange,
            onSend = onSendClick
        )

        // Recording Controls
        RecordingControls(
            isRecording = recordingState.isRecording,
            elapsedSeconds = recordingState.elapsedSeconds,
            format = logFormat,
            onToggleRecording = onToggleRecording,
            onFormatChange = onFormatChange
        )
    }
}

@Composable
fun TxCommandInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    // OutlinedTextField + Send IconButton
}

@Composable
fun RecordingControls(
    isRecording: Boolean,
    elapsedSeconds: Long,
    format: LogFormat,
    onToggleRecording: () -> Unit,
    onFormatChange: (LogFormat) -> Unit,
    modifier: Modifier = Modifier
) {
    // Start/Stop toggle with pulsing animation when active
    // Timer display
    // Format dropdown (.csv, .txt)
}

@Composable
fun RecordingTimer(
    elapsedSeconds: Long,
    modifier: Modifier = Modifier
) {
    // MM:SS format timer
}
```

#### 5. Tab Navigation (Middle Section)

```kotlin
@Composable
fun SerialMonitorContent(
    uiState: SerialMonitorUiState,
    onTabChange: (SerialMonitorTab) -> Unit,
    onSendCommand: (String) -> Unit,
    onToggleAutoScroll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Tab Row
        SerialMonitorTabRow(
            selectedTab = uiState.selectedTab,
            onTabSelected = onTabChange
        )

        // Tab Content (40-50% screen height each)
        Box(modifier = Modifier.weight(1f)) {
            when (uiState.selectedTab) {
                SerialMonitorTab.CHART -> RealTimeChart(
                    chartData = uiState.chartData,
                    onToggleAutoScroll = onToggleAutoScroll
                )
                SerialMonitorTab.TERMINAL -> SerialTerminal(
                    terminalBuffer = uiState.terminalBuffer,
                    onToggleAutoScroll = onToggleAutoScroll
                )
            }
        }
    }
}

@Composable
fun SerialMonitorTabRow(
    selectedTab: SerialMonitorTab,
    onTabSelected: (SerialMonitorTab) -> Unit,
    modifier: Modifier = Modifier
) {
    // PrimaryScrollableTabRow or custom tabs
}
```

---

## 4. Layout Strategy

### Screen Division

```
┌─────────────────────────────────────┐
│  Header: Connection Bar (8%)        │
│  [LED] [Device Dropdown] [Connect]  │
├─────────────────────────────────────┤
│                                     │
│  Tab Row: Chart | Terminal (8%)     │
│                                     │
├─────────────────────────────────────┤
│                                     │
│  Active Tab Content (42%)           │
│  - Chart OR Terminal                │
│  - Interactive controls overlay     │
│                                     │
├─────────────────────────────────────┤
│                                     │
│  Control & Logging Panel (42%)      │
│  - TX Input + Send                  │
│  - Recording Toggle + Timer         │
│  - Format Selector                  │
│                                     │
└─────────────────────────────────────┘
```

### Alternative Layout (Split View)

```kotlin
@Composable
fun SplitViewLayout(
    chartData: ChartDataState,
    terminalBuffer: TerminalBufferState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Chart (50%)
        RealTimeChart(
            chartData = chartData,
            modifier = Modifier.weight(1f)
        )

        // Divider (optional, draggable)
        HorizontalDivider()

        // Terminal (50%)
        SerialTerminal(
            terminalBuffer = terminalBuffer,
            modifier = Modifier.weight(1f)
        )
    }
}
```

---

## 5. Performance & Memory Efficiency Considerations

### Rolling Buffer Implementation (FIFO)

```kotlin
class RollingBuffer<T>(private val maxSize: Int) {
    private val buffer = ArrayDeque<T>(maxSize)

    fun add(item: T) {
        if (buffer.size >= maxSize) {
            buffer.removeFirst() // Remove oldest
        }
        buffer.addLast(item)
    }

    fun getAll(): List<T> = buffer.toList()

    fun clear() {
        buffer.clear()
    }
}

// Usage in ViewModel
private val terminalBuffer = RollingBuffer<String>(maxSize = 1000)
private val chartBuffer = RollingBuffer<Float>(maxSize = 500)
```

### Memory Optimization Strategies

1. **Immutable Collections**: Use `persistentListOf()` from androidx.collection to minimize copying overhead.

2. **Lazy Loading for Terminal**:
   ```kotlin
   LazyColumn(
       reverseLayout = true, // Auto-scroll to bottom
       modifier = modifier
   ) {
       itemsIndexed(buffer.lines.reversed()) { index, line ->
           TerminalLineItem(line = line, lineNumber = totalLines - index)
       }
   }
   ```

3. **Chart Rendering Optimization**:
   - Use `Canvas` with `drawPath` for smooth lines
   - Downsample data points when zoomed out
   - Limit redraw frequency with `derivedStateOf`

4. **Coroutine-Based Data Processing**:
   ```kotlin
   viewModelScope.launch(Dispatchers.IO) {
       // Process incoming Bluetooth data
       // Write to file system (recording)
       // Update state on Main thread only when necessary
   }
   ```

5. **StateFlow Throttling**:
   ```kotlin
   val throttledUiState = uiState
       .debounce(50) // Reduce update frequency
       .distinctUntilChanged()
   ```

6. **File I/O Separation**:
   - Recording writes directly to disk via `OutputStream`
   - UI buffer is independent (visual only)
   - Use `BufferedWriter` for efficient file writing

7. **Bluetooth Service Isolation**:
   - Run Bluetooth operations in a foreground service
   - Use `CoroutineChannel` or `Flow` for communication with UI
   - Avoid blocking the main thread

8. **Garbage Collection Awareness**:
   - Avoid creating unnecessary objects in hot paths
   - Reuse `StringBuilder` for string concatenation in terminal
   - Pre-allocate arrays where possible

---

## 6. Recommended Library Dependencies

```gradle
dependencies {
    // Jetpack Compose
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.ui:ui-tooling-preview")
    
    // Lifecycle & ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    
    // Coroutines & Flow
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    
    // Dependency Injection (Hilt)
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-compiler:2.48.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Charts (Optional: MPAndroidChart or custom Canvas)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    
    // Bluetooth
    implementation("androidx.core:core-ktx:1.12.0")
    
    // Persistent Collections
    implementation("androidx.collection:collection-ktx:1.4.0-beta01")
    
    // Animation
    implementation("androidx.compose.animation:animation")
}
```

---

## 7. File Structure Recommendation

```
app/
├── data/
│   ├── repository/
│   │   └── LoggingRepository.kt
│   └── service/
│       └── BluetoothService.kt
├── domain/
│   ├── model/
│   │   ├── BluetoothConnectionState.kt
│   │   ├── RecordingState.kt
│   │   └── SerialMonitorUiState.kt
│   └── usecase/
├── presentation/
│   ├── components/
│   │   ├── ConnectionHeader.kt
│   │   ├── RealTimeChart.kt
│   │   ├── SerialTerminal.kt
│   │   ├── ControlLoggingPanel.kt
│   │   └── common/
│   │       ├── StatusLedIndicator.kt
│   │       └── RecordingTimer.kt
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── viewmodel/
│       └── SerialMonitorViewModel.kt
└── MainActivity.kt
```

---

## 8. Key UX Interactions

1. **Connection Feedback**: 
   - LED pulses green when connected
   - Red flash on disconnection/error
   - Snackbar on connection errors

2. **Recording Feedback**:
   - Pulsing red border around recording button
   - Live timer display (MM:SS)
   - Toast notification when recording starts/stops
   - File path shown after recording stops

3. **Auto-Scroll Behavior**:
   - Toggle button with icon change
   - Smooth scroll animation when enabled
   - Pause auto-scroll when user manually scrolls

4. **Chart Interactions**:
   - Pinch-to-zoom gesture
   - Drag-to-pan when zoomed in
   - Double-tap to reset zoom
   - Long-press to show data point value

5. **Terminal UX**:
   - Syntax highlighting for commands vs responses
   - Copy-to-clipboard on long-press
   - Clear buffer button
   - Search/filter functionality (future enhancement)

---

This architecture provides a clean separation of concerns, excellent performance characteristics, and a modern, professional UI that meets all specified requirements. The modular component structure allows for easy testing, maintenance, and future enhancements.
