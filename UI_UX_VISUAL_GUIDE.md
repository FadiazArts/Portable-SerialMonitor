# 📱 Bluetooth Serial Monitor & Logger - UI/UX Visual Guide

## 🎨 Design System Overview

### Color Palette (Dark Mode)

```
┌─────────────────────────────────────────────────────────────┐
│  PRIMARY COLORS                                             │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐                    │
│  │ #0A0E17  │ │ #151A25  │ │ #1E293B  │                    │
│  │  Navy    │ │  Onyx    │ │  Slate   │                    │
│  │Background│ │ Surface  │ │  Card    │                    │
│  └──────────┘ └──────────┘ └──────────┘                    │
│                                                             │
│  ACCENT COLORS                                              │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │ #00FF88  │ │ #FF3B30  │ │ #5E5CE6  │ │ #0A84FF  │       │
│  │  Green   │ │   Red    │ │  Purple  │ │   Blue   │       │
│  │Connected │ │ Error/Rec│ │  Chart   │ │  Active  │       │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
└─────────────────────────────────────────────────────────────┘
```

### Typography Scale

```
┌─────────────────────────────────────────────────────────────┐
│  SANS-SERIF (Main UI)         │  MONOSPACE (Terminal/Data)  │
│  ───────────────────────────  │  ─────────────────────────  │
│  Headline Large: 32sp         │  Terminal Data: 13sp        │
│  Headline Medium: 28sp        │  Log Entries: 12sp          │
│  Title Large: 22sp            │  Timestamps: 11sp           │
│  Title Medium: 16sp           │  Hex Data: 12sp             │
│  Body Large: 16sp             │                             │
│  Body Medium: 14sp            │  Font: Fira Code /          │
│  Label Large: 14sp            │        Roboto Mono          │
│  Label Medium: 12sp           │                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 📐 Screen Layout Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  HEADER & CONNECTION BAR (80dp)                       │  │
│  │  ┌───┐  HC-05                    ┌───────────────┐    │  │
│  │  │ ● │  00:1A:7D:DA:71:13  -45dBm│  [CONNECT]    │    │  │
│  │  └───┘  ▼                        └───────────────┘    │  │
│  │   LED   Device Selector                                │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  TAB NAVIGATION (48dp)                                │  │
│  │  ┌──────────────┐ ┌──────────────┐                   │  │
│  │  │  📊 CHART    │ │  📝 TERMINAL │                   │  │
│  │  │   (Active)   │ │              │                   │  │
│  │  └──────────────┘ └──────────────┘                   │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  ╔═══════════════════════════════════════════════════════╗  │
│  ║  REAL-TIME CHART (45% screen height ≈ 420dp)          ║  │
│  ║                                                       ║  │
│  ║   35 ┤                                                ║  │
│  ║      │     ╱╲      ╱╲                                ║  │
│  ║   30 ┤    ╱  ╲    ╱  ╲    ╱╲                          ║  │
│  ║      │   ╱    ╲  ╱    ╲  ╱  ╲                         ║  │
│  ║   25 ┤  ╱      ╲╱      ╲╱    ╲╱╲                      ║  │
│  ║      │ ╱                ╲                            ║  │
│  ║   20 ┤╱                  ╲╱╲                          ║  │
│  ║      └─────────────────────────────────────────       ║  │
│  ║      0s   10s   20s   30s   40s   50s   60s          ║  │
│  ║                                                       ║  │
│  ║  ┌──────────────────────────────────────────────┐    ║  │
│  ║  │ 🔍 Zoom  ✋ Pan  🔄 Auto-scroll: ON          │    ║  │
│  ║  └──────────────────────────────────────────────┘    ║  │
│  ╚═══════════════════════════════════════════════════════╝  │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  CONTROL & LOGGING PANEL (180dp)                      │  │
│  │  ┌───────────────────────────────────────────────┐    │  │
│  │  │  TX: [____________________________] [SEND]    │    │  │
│  │  │      Quick: [AT] [RESET] [STATUS] [+]         │    │  │
│  │  └───────────────────────────────────────────────┘    │  │
│  │                                                       │  │
│  │  ┌─────────────────┐  ┌──────────────────────────┐   │  │
│  │  │  ⏺ RECORDING    │  │  Format: [CSV ▼]         │   │  │
│  │  │  00:02:05       │  │  [.TXT] [.JSON]          │   │  │
│  │  │  💾 saved locally│  │                          │   │  │
│  │  └─────────────────┘  └──────────────────────────┘   │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎯 State Visualizations

### 1. CONNECTED STATE (Active Recording)

```
┌─────────────────────────────────────────────────────────────┐
│  STATUS: ✅ CONNECTED                                       │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  ┌─────────────────────────────────┐                 │  │
│  │  │  ● (pulsing green LED)          │                 │  │
│  │  │                                 │                 │  │
│  │  │  HM-10                          │  [DISCONNECT]   │  │
│  │  │  A4:C1:38:8D:18:B2  -52dBm     │                 │  │
│  │  │  Signal: ████░ 85%              │                 │  │
│  │  └─────────────────────────────────┘                 │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  ╔═══════════════════════════════════════════════════════╗  │
│  ║  TEMPERATURE SENSOR (°C)                              ║  │
│  ║                                                       ║  │
│  ║   35 ┤                        ╭╮                     ║  │
│  ║      │                      ╭╯ ╰╮                    ║  │
│  ║   30 ┤            ╭╮        ╯   ╰╮                   ║  │
│  ║      │          ╭╯ ╰╮      ╭╯     ╰╮                 ║  │
│  ║   25 ┤     ╭╮   ╯   ╰╮    ╭╯       ╰╮                ║  │
│  ║      │    ╭╯ ╰╮╯     ╰╮  ╭╯         ╰╮               ║  │
│  ║   20 ┤───╮╯   ╰╯       ╰╮╯           ╰╮              ║  │
│  ║      └─────────────────────────────────────────      ║  │
│  ║      Now-60s  -45s  -30s  -15s    0s                 ║  │
│  ║                                                       ║  │
│  ║  Min: 19.2°C  Max: 34.8°C  Avg: 26.4°C               ║  │
│  ╚═══════════════════════════════════════════════════════╝  │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  TX: [AT+READ:SENSOR________________] [SEND ▶]       │  │
│  │                                                       │  │
│  │  ┌──────────────────────┐  ┌───────────────────────┐  │  │
│  │  │  ⏺ RECORDING  🔴    │  │  📄 Format: CSV ▼     │  │  │
│  │  │  00:02:05            │  │  ○ TXT  ○ JSON        │  │  │
│  │  │  💾 sensor_log.csv   │  │                       │  │  │
│  │  │  📁 /storage/emulated/0/Download/               │  │  │
│  │  └──────────────────────┘  └───────────────────────┘  │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### 2. DISCONNECTED STATE

```
┌─────────────────────────────────────────────────────────────┐
│  STATUS: ❌ DISCONNECTED                                    │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  ┌─────────────────────────────────┐                 │  │
│  │  │  ○ (gray LED)                   │                 │  │
│  │  │                                 │                 │  │
│  │  │  Select Device...              │   [SCAN DEVICES]│  │
│  │  │  No device selected            │                 │  │
│  │  │                                 │                 │  │
│  │  └─────────────────────────────────┘                 │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                                                       │  │
│  │                                                       │  │
│  │              📡                                       │  │
│  │                                                       │  │
│  │           No Connection                               │  │
│  │                                                       │  │
│  │    Tap 'Scan Devices' to find and connect to a        │  │
│  │            Bluetooth device                           │  │
│  │                                                       │  │
│  │                                                       │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  TX: [________________________________] [SEND ▶]     │  │
│  │      (Disabled - Connect first)                       │  │
│  │                                                       │  │
│  │  ┌──────────────────────┐  ┌───────────────────────┐  │  │
│  │  │  ⏹ NOT RECORDING     │  │  📄 Format: TXT ▼     │  │  │
│  │  │  Start logging data   │  │  ○ CSV  ○ JSON        │  │  │
│  │  └──────────────────────┘  └───────────────────────┘  │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### 3. ERROR STATE

```
┌─────────────────────────────────────────────────────────────┐
│  STATUS: ⚠️ ERROR                                           │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  ┌─────────────────────────────────┐                 │  │
│  │  │  ◉ (pulsing red LED)            │                 │  │
│  │  │                                 │                 │  │
│  │  │  ESP32-BT                       │   [RETRY]       │  │
│  │  │  24:6F:28:A3:B7:C1  -68dBm     │                 │  │
│  │  └─────────────────────────────────┘                 │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  ⚠️  Connection Error                                 │  │
│  │                                                       │  │
│  │  Connection timeout: Device not responding            │  │
│  │                                                       │  │
│  │  [Dismiss]                          [Retry Connect]   │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  > Connecting to device...           [SYSTEM] 10s ago │  │
│  │  < Scanning for services...          [SYSTEM] 8s ago  │  │
│  │  < Attempting GATT connection...     [SYSTEM] 5s ago  │  │
│  │  < ERROR: Connection timeout after 3 attempts         │  │
│  │                                         [SYSTEM] 2s ago│  │
│  │                                                       │  │
│  │  ┌───────────────────────────────────────────────┐   │  │
│  │  │  Buffer: 4/1000 lines (0%)                    │   │  │
│  │  │  ████████████████████████░░░░░░░░░░░░░░░░░░░░ │   │  │
│  │  └───────────────────────────────────────────────┘   │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  TX: [________________________________] [SEND ▶]     │  │
│  │                                                       │  │
│  │  ┌──────────────────────┐  ┌───────────────────────┐  │  │
│  │  │  ⏹ NOT RECORDING     │  │  📄 Format: TXT ▼     │  │  │
│  │  │  Start logging data   │  │  ○ CSV  ○ JSON        │  │  │
│  │  └──────────────────────┘  └───────────────────────┘  │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### 4. TERMINAL VIEW (Buffer Near Capacity)

```
┌─────────────────────────────────────────────────────────────┐
│  ┌───────────────────────────────────────────────────────┐  │
│  │  📊 CHART     📝 TERMINAL (Active)                    │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  1    │  > COMMAND_15: Request data packet [TX]      │  │
│  │  2    │  < RESPONSE_16: 0x1020 [RX]                 │  │
│  │  3    │  < SYSTEM: Buffer 75% utilized [SYS]         │  │
│  │  4    │  > COMMAND_17: Request data packet [TX]      │  │
│  │  5    │  < RESPONSE_18: 0x1224 [RX]                 │  │
│  │  6    │  < SYSTEM: Buffer 80% utilized [SYS]         │  │
│  │  7    │  > COMMAND_19: Request data packet [TX]      │  │
│  │  8    │  < RESPONSE_20: 0x1428 [RX]                 │  │
│  │  9    │  < SYSTEM: Buffer 85% utilized [SYS]         │  │
│  │  10   │  > COMMAND_21: Request data packet [TX]      │  │
│  │  11   │  < RESPONSE_22: 0x162C [RX]                 │  │
│  │  12   │  < SYSTEM: Buffer 90% utilized [SYS]         │  │
│  │  13   │  > COMMAND_23: Request data packet [TX]      │  │
│  │  14   │  < RESPONSE_24: 0x1830 [RX]                 │  │
│  │  15   │  < SYSTEM: Buffer 95% utilized [SYS]         │  │
│  │       │                                               │  │
│  │  ┌───────────────────────────────────────────────┐   │  │
│  │  │  ⚠️  Buffer: 985/1000 lines (98.5%)          │   │  │
│  │  │  ██████████████████████████████████████████░░ │   │  │
│  │  │  Oldest entries will be removed automatically │   │  │
│  │  └───────────────────────────────────────────────┘   │  │
│  │                                                       │  │
│  │  ┌───────────────────────────────────────────────┐   │  │
│  │  │  🔄 Auto-scroll: OFF    [⬇ Scroll to Bottom]  │   │  │
│  │  │  [🗑 Clear Terminal]                          │   │  │
│  │  └───────────────────────────────────────────────┘   │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎬 Interactive Elements & Animations

### LED Status Indicator Animation

```
CONNECTED (Green, Pulsing):
    
    Frame 1:  ●  (opacity: 1.0, scale: 1.0)
    Frame 2:  ●  (opacity: 0.8, scale: 1.1)
    Frame 3:  ●  (opacity: 0.6, scale: 1.2)
    Frame 4:  ●  (opacity: 0.8, scale: 1.1)
    Frame 5:  ●  (opacity: 1.0, scale: 1.0)
    
    Duration: 2000ms (infinite loop)
    Easing: EaseInOut


DISCONNECTED (Gray, Static):
    
    Always: ○  (opacity: 0.3, scale: 1.0)


ERROR (Red, Fast Pulse):
    
    Frame 1:  ◉  (opacity: 1.0, scale: 1.0)
    Frame 2:  ◉  (opacity: 0.5, scale: 1.15)
    Frame 3:  ◉  (opacity: 1.0, scale: 1.0)
    
    Duration: 500ms (infinite loop)
    Easing: EaseOut
```

### Recording Timer Animation

```
┌─────────────────────────────────────────┐
│  ⏺ RECORDING                            │
│                                         │
│  🔴 (pulsing)  00:02:05                 │
│                                         │
│  Animation:                             │
│  - Red dot pulses every 1000ms          │
│  - Timer updates every second           │
│  - Background subtly glows red          │
└─────────────────────────────────────────┘
```

### Chart Interactions

```
ZOOM (Pinch Gesture):
  
  Pinch Out:  Chart zooms in (X-axis expands)
  Pinch In:   Chart zooms out (X-axis compresses)
  
  Zoom Levels: 1x, 2x, 5x, 10x


PAN (Drag Gesture):
  
  Drag Left:  View moves to older data
  Drag Right: View moves to newer data
  
  Snap-back when auto-scroll enabled


AUTO-SCROLL TOGGLE:
  
  ON:  Chart automatically scrolls right as new data arrives
  OFF: User can manually pan through historical data
```

---

## 📊 Component Specifications

### Connection Header

```
Height: 80dp
Padding: 16dp horizontal

┌──────────────────────────────────────────────────────────┐
│                                                          │
│  ┌────┐                                                  │
│  │ LED│  Device Name (16sp, Bold)                       │
│  │    │  MAC Address (13sp, Medium)  RSSI (12sp)        │
│  └────┘                                                  │
│                                                          │
│                                     ┌──────────────┐    │
│                                     │   BUTTON     │    │
│                                     └──────────────┘    │
│                                                          │
└──────────────────────────────────────────────────────────┘

LED Size: 16dp diameter
Button: Filled Button, Primary color when disconnected
        Outlined Button with error color when error state
```

### Real-Time Chart

```
Height: 45% of screen (≈420dp on typical device)
Padding: 16dp horizontal

Features:
- Line width: 2dp
- Gradient fill below line (opacity: 0.3)
- Grid lines: 0.5dp, outline color with 20% opacity
- Y-axis labels: 11sp, monospace
- X-axis labels: 11sp, monospace
- Data point circle: 3dp radius on touch

Controls Bar (below chart):
Height: 40dp
Icons: 24dp
Text: 12sp
```

### Serial Terminal

```
Remaining height after chart or full screen in terminal tab

Line Height: 24dp
Font: Monospace, 12sp
Line Numbers: 32dp width, right-aligned, 50% opacity

Direction Indicators:
- TX (Transmit): Green chevron (>)
- RX (Receive): Blue chevron (<)
- SYS (System): Gray info icon

Auto-scroll Button:
- Floating Action Button (mini)
- Bottom-right corner
- Appears when auto-scroll disabled and new data available
```

### Control & Logging Panel

```
Height: 180dp
Padding: 16dp

┌──────────────────────────────────────────────────────────┐
│                                                          │
│  TX Input Row (56dp):                                    │
│  ┌──────────────────────────────┐ ┌────────┐            │
│  │ TextField (outlined)         │ │ [SEND] │            │
│  └──────────────────────────────┘ └────────┘            │
│                                                          │
│  Quick Commands Row (40dp):                              │
│  [AT] [RESET] [STATUS] [INFO] [+]                       │
│                                                          │
│  Recording & Format Row (64dp):                          │
│  ┌─────────────────────┐  ┌──────────────────────┐      │
│  │ Recording Toggle    │  │ Format Selector      │      │
│  │ + Timer Display     │  │ (Dropdown/Chips)     │      │
│  └─────────────────────┘  └──────────────────────┘      │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

---

## 🎯 User Flow Diagrams

### Connection Flow

```
┌─────────────┐
│   Launch    │
│    App      │
└──────┬──────┘
       │
       ▼
┌─────────────┐     No      ┌─────────────┐
│  Bluetooth  │────────────▶│  Request     │
│  Enabled?   │             │  Permission  │
└──────┬──────┘             └─────────────┘
       │ Yes
       ▼
┌─────────────┐
│  Scan for   │◀──────────────┐
│   Devices   │               │
└──────┬──────┘               │
       │                      │
       ▼                      │
┌─────────────┐   Cancel      │
│  Show List  │──────────────┘
│  (Bottom    │
│   Sheet)    │
└──────┬──────┘
       │ Select Device
       ▼
┌─────────────┐
│  Connect    │
│  (GATT)     │
└──────┬──────┘
       │
       ├──────────Success──────────┐
       │                           │
       ▼                           ▼
┌─────────────┐           ┌─────────────┐
│  Connected  │           │   Error     │
│   State     │           │  (Retry)    │
└─────────────┘           └─────────────┘
```

### Data Logging Flow

```
┌─────────────┐
│  Recording  │
│   Toggled   │
│     ON      │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Create File │
│  (Timestamp)│
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Write Data │◀─────────────┐
│  (Append)   │              │
└──────┬──────┘              │
       │                     │
       │ New Data            │
       └─────────────────────┘
       
       │ Recording Toggled OFF
       ▼
┌─────────────┐
│  Close File │
│  Show Save  │
│  Location   │
└─────────────┘
```

---

## 📱 Responsive Layout Considerations

### Portrait Mode (Default)

```
┌─────────────────┐
│     Header      │  80dp
├─────────────────┤
│      Tabs       │  48dp
├─────────────────┤
│                 │
│     Chart       │  45% height
│    (Tab 1)      │
│                 │
├─────────────────┤
│                 │
│   Terminal      │  Remaining
│   (Tab 2)       │
│                 │
├─────────────────┤
│    Controls     │  180dp
└─────────────────┘
```

### Landscape Mode

```
┌─────────────────────────────────────────────────────┐
│     Header (full width)                             │  64dp
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌───────────────┐  ┌───────────────────────────┐   │
│  │               │  │                           │   │
│  │    Chart      │  │      Terminal             │   │
│  │   (50% w)     │  │     (50% w)               │   │
│  │               │  │                           │   │
│  │               │  │                           │   │
│  └───────────────┘  └───────────────────────────┘   │
├─────────────────────────────────────────────────────┤
│    Controls (full width)                            │  120dp
└─────────────────────────────────────────────────────┘
```

---

## 🧩 Accessibility Features

```
┌─────────────────────────────────────────────────────────────┐
│  ACCESSIBILITY CONSIDERATIONS                               │
│                                                             │
│  ✓ Content Descriptions: All icons have contentDescription  │
│  ✓ Touch Target Size: Minimum 48dp x 48dp                   │
│  ✓ Color Contrast: WCAG AA compliant (4.5:1 minimum)        │
│  ✓ TalkBack Support: Semantic ordering and labels           │
│  ✓ Font Scaling: Supports system font size changes          │
│  ✓ Focus Navigation: Logical tab order                      │
│  ✓ Haptic Feedback: Connection state changes                │
│  ✓ High Contrast Mode: Tested with system high contrast     │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 File Structure Reference

```
app/src/main/java/com/bluetoothserialmonitor/
├── domain/model/
│   └── Models.kt                 # State data classes
├── presentation/
│   ├── theme/
│   │   ├── Color.kt              # Color definitions
│   │   ├── Type.kt               # Typography
│   │   └── Theme.kt              # Theme configuration
│   ├── components/
│   │   ├── ConnectionHeader.kt
│   │   ├── RealTimeChart.kt
│   │   ├── SerialTerminal.kt
│   │   ├── ControlLoggingPanel.kt
│   │   ├── SerialMonitorContent.kt
│   │   └── common/
│   │       ├── StatusLedIndicator.kt
│   │       ├── RecordingTimer.kt
│   │       └── BufferLimitIndicator.kt
│   ├── viewmodel/
│   │   └── SerialMonitorViewModel.kt
│   └── preview/
│       └── UiVisualizationPreviews.kt  # @Preview composables
└── MainActivity.kt
```

---

## 🎨 Design Inspiration References

```
┌─────────────────────────────────────────────────────────────┐
│  INSPIRATION SOURCES                                        │
│                                                             │
│  📊 Fintech Dashboards:                                     │
│     - Bloomberg Terminal (dark mode)                        │
│     - TradingView charts                                    │
│     - Crypto exchange interfaces (Binance, Coinbase Pro)    │
│                                                             │
│  🔧 Developer Tools:                                        │
│     - VS Code Terminal                                      │
│     - Android Studio Logcat                                 │
│     - Wireshark packet analyzer                             │
│                                                             │
│  📱 Modern Material Design 3 Apps:                          │
│     - Google Home                                           │
│     - Philips Hue                                           │
│     - SmartThings                                           │
└─────────────────────────────────────────────────────────────┘
```

---

## ✅ Implementation Checklist

```
┌─────────────────────────────────────────────────────────────┐
│  UI COMPONENTS                                              │
│  ☑ ConnectionHeader with LED indicator                      │
│  ☑ Device selector dropdown/bottom sheet                    │
│  ☑ Connect/Disconnect button with states                    │
│  ☑ Tab navigation (Chart/Terminal)                          │
│  ☑ RealTimeChart with canvas rendering                      │
│  ☑ Chart controls (zoom, pan, auto-scroll)                  │
│  ☑ SerialTerminal with LazyColumn                           │
│  ☑ FIFO buffer implementation                               │
│  ☑ Auto-scroll toggle                                       │
│  ☑ TX input field with send button                          │
│  ☑ Quick command chips                                      │
│  ☑ Recording toggle with timer                              │
│  ☑ Format selector (CSV/TXT/JSON)                           │
│  ☐ File save location display                               │
│  ☐ Settings screen                                          │
│  ☐ About/Help screen                                        │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  ANIMATIONS                                                 │
│  ☑ LED pulsing (connected/error states)                     │
│  ☑ Recording timer pulse                                    │
│  ☑ Buffer limit warning animation                           │
│  ☐ Smooth tab transitions                                   │
│  ☐ Chart data update animations                             │
│  ☐ Connection state transition animations                   │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  PERFORMANCE OPTIMIZATIONS                                  │
│  ☑ Immutable state objects                                  │
│  ☑ LazyColumn for terminal                                  │
│  ☑ StateFlow throttling                                     │
│  ☑ Canvas-based chart rendering                             │
│  ☑ Coroutine-based background processing                    │
│  ☐ Chart data downsampling                                  │
│  ☐ Memory leak prevention                                   │
└─────────────────────────────────────────────────────────────┘
```

---

*Document Version: 1.0*  
*Last Updated: 2024*  
*Design System: Material Design 3 (Dark Theme)*
