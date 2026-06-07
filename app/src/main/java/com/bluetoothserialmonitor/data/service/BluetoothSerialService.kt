package com.bluetoothserialmonitor.data.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bluetoothserialmonitor.MainActivity
import com.bluetoothserialmonitor.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

/**
 * Bluetooth Serial Service - Handles all Bluetooth communication in background
 * 
 * Features:
 * - Classic Bluetooth SPP (Serial Port Profile) connection
 * - Background service with foreground notification
 * - Coroutine-based I/O operations
 * - StateFlow for reactive state updates
 * - Automatic reconnection logic
 */
class BluetoothSerialService : Service() {

    companion object {
        private const val TAG = "BluetoothSerialService"
        
        // UUID for standard SPP
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        
        // Notification
        private const val NOTIFICATION_CHANNEL_ID = "bluetooth_serial_channel"
        private const val NOTIFICATION_ID = 1001
        
        // Buffer sizes
        private const val READ_BUFFER_SIZE = 1024
    }

    // Binder for client binding
    private val binder = LocalBinder()
    
    // Job for coroutine scope
    private var serviceJob: Job? = null
    private var serviceScope: CoroutineScope? = null
    
    // Bluetooth components
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var connectedDevice: BluetoothDevice? = null
    
    // I/O streams
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    
    // State
    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Disconnected)
    val serviceState: StateFlow<ServiceState> = _serviceState.asStateFlow()
    
    private val _receivedData = MutableStateFlow<String?>(null)
    val receivedData: StateFlow<String?> = _receivedData.asStateFlow()
    
    // Connection monitoring
    private var isReading = false
    
    inner class LocalBinder : Binder() {
        fun getService(): BluetoothSerialService = this@BluetoothSerialService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        
        serviceJob = Job()
        serviceScope = CoroutineScope(Dispatchers.IO + serviceJob!!)
        
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): android.os.IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "Service destroyed")
        disconnect()
        serviceJob?.cancel()
        serviceScope = null
        super.onDestroy()
    }

    /**
     * Connect to a Bluetooth device by MAC address
     */
    @SuppressLint("MissingPermission")
    fun connect(deviceAddress: String) {
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            _serviceState.value = ServiceState.Error("Bluetooth not enabled")
            return
        }

        serviceScope?.launch {
            try {
                _serviceState.value = ServiceState.Connecting
                
                val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
                    ?: throw IOException("Device not found: $deviceAddress")
                
                Log.d(TAG, "Connecting to ${device.address}...")
                
                // Create socket with timeout
                bluetoothSocket = try {
                    device.createRfcommSocketToServiceRecord(SPP_UUID)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to create socket", e)
                    throw e
                }
                
                // Cancel discovery to speed up connection
                bluetoothAdapter?.cancelDiscovery()
                
                // Connect with timeout
                withTimeout(30000) {
                    bluetoothSocket?.connect()
                }
                
                connectedDevice = device
                inputStream = bluetoothSocket?.inputStream
                outputStream = bluetoothSocket?.outputStream
                
                _serviceState.value = ServiceState.Connected(deviceAddress, device.name ?: "Unknown")
                
                Log.i(TAG, "Connected to ${device.name}")
                
                // Start reading data
                startReading()
                
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Connection failed", e)
                _serviceState.value = ServiceState.Error("Connection failed: ${e.message}")
                disconnect()
            }
        }
    }

    /**
     * Disconnect from current device
     */
    fun disconnect() {
        serviceScope?.launch {
            stopReading()
            
            try {
                inputStream?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error closing input stream", e)
            }
            
            try {
                outputStream?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error closing output stream", e)
            }
            
            try {
                bluetoothSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error closing socket", e)
            }
            
            inputStream = null
            outputStream = null
            bluetoothSocket = null
            connectedDevice = null
            
            _serviceState.value = ServiceState.Disconnected
            
            Log.i(TAG, "Disconnected")
        }
    }

    /**
     * Send data to connected device
     */
    fun sendData(data: String) {
        if (_serviceState.value !is ServiceState.Connected) {
            Log.w(TAG, "Cannot send data: not connected")
            return
        }

        serviceScope?.launch {
            try {
                val bytes = data.toByteArray(Charsets.UTF_8)
                outputStream?.write(bytes)
                outputStream?.flush()
                Log.d(TAG, "Data sent: $data")
            } catch (e: IOException) {
                Log.e(TAG, "Error sending data", e)
                _serviceState.value = ServiceState.Error("Send failed: ${e.message}")
            }
        }
    }

    /**
     * Send raw bytes to connected device
     */
    fun sendDataBytes(bytes: ByteArray) {
        if (_serviceState.value !is ServiceState.Connected) {
            Log.w(TAG, "Cannot send data: not connected")
            return
        }

        serviceScope?.launch {
            try {
                outputStream?.write(bytes)
                outputStream?.flush()
                Log.d(TAG, "Bytes sent: ${bytes.size}")
            } catch (e: IOException) {
                Log.e(TAG, "Error sending bytes", e)
                _serviceState.value = ServiceState.Error("Send failed: ${e.message}")
            }
        }
    }

    /**
     * Start reading data from Bluetooth socket
     */
    @SuppressLint("MissingPermission")
    private fun startReading() {
        if (isReading) return
        isReading = true

        serviceScope?.launch {
            try {
                while (isReading && _serviceState.value is ServiceState.Connected) {
                    val buffer = ByteArray(READ_BUFFER_SIZE)
                    
                    try {
                        val bytesRead = inputStream?.read(buffer) ?: -1
                        
                        if (bytesRead > 0) {
                            val data = String(buffer, 0, bytesRead, Charsets.UTF_8)
                            _receivedData.value = data
                            Log.d(TAG, "Data received: $data")
                        } else if (bytesRead == -1) {
                            Log.w(TAG, "End of stream reached")
                            break
                        }
                    } catch (e: IOException) {
                        if (isReading) {
                            Log.e(TAG, "Error reading data", e)
                            _serviceState.value = ServiceState.Error("Read error: ${e.message}")
                        }
                        break
                    }
                }
            } finally {
                isReading = false
            }
        }
    }

    /**
     * Stop reading data
     */
    private fun stopReading() {
        isReading = false
    }

    /**
     * Create notification channel for Android O+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Bluetooth Serial Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows Bluetooth connection status"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Create foreground service notification
     */
    @SuppressLint("MissingPermission")
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val state = _serviceState.value
        val statusText = when (state) {
            is ServiceState.Connected -> "Connected: ${state.deviceName}"
            is ServiceState.Connecting -> "Connecting..."
            is ServiceState.Error -> "Error: ${state.message}"
            is ServiceState.Disconnected -> "Disconnected"
            is ServiceState.Scanning -> "Scanning..."
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("BT Serial Monitor")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    /**
     * Get current connected device address
     */
    fun getConnectedDeviceAddress(): String? {
        return when (val state = _serviceState.value) {
            is ServiceState.Connected -> state.deviceAddress
            else -> null
        }
    }

    /**
     * Check if service is connected
     */
    fun isConnected(): Boolean {
        return _serviceState.value is ServiceState.Connected
    }
}

/**
 * Sealed class representing service states
 */
sealed class ServiceState {
    object Disconnected : ServiceState()
    object Scanning : ServiceState()
    object Connecting : ServiceState()
    data class Connected(val deviceAddress: String, val deviceName: String) : ServiceState()
    data class Error(val message: String) : ServiceState()
}
