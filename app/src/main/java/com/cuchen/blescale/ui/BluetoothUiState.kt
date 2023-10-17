package com.cuchen.blescale.ui

import com.cuchen.blescale.DeviceData
import com.cuchen.blescale.data.DeviceConnectionState
import com.cuchen.blescale.domin.ScaleData

data class BluetoothUiState(
    val scannedDevices: List<DeviceData> = emptyList(),
    val pairedDevices: List<DeviceData> = emptyList(),
    val deviceConnectionState: DeviceConnectionState = DeviceConnectionState.isScan,
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val isDisconnected: Boolean = false,
    val errorMessage: String? = null,
    val scaleData: ScaleData = ScaleData(0, 0, 0),
//    val messages: List<BluetoothMessage> = emptyList()
)
