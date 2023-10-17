package com.cuchen.blescale.usecases

import com.cuchen.blescale.DeviceData
import com.cuchen.blescale.data.DeviceConnectionState
import com.cuchen.blescale.domin.ScaleData
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface TemperatureAndHumidityReceiveManager {
    //    val data: MutableSharedFlow<Resource<ScanResult>>
    val isConnected: StateFlow<Boolean>
    val isDisconnected: StateFlow<Boolean>
    val scannedDevices: StateFlow<List<DeviceData>>
    val pairedDevices: StateFlow<List<DeviceData>>
    val errors: SharedFlow<String>
    val scaleData: SharedFlow<ScaleData>
    val deviceConnectionState: StateFlow<DeviceConnectionState>


    fun reconnect()

    fun disconnect()

    fun startReceiving()
    fun startBleConnectGatt(deviceAddress: String)

    fun closeConnection()
    fun startBleScan()
    fun stopBleScan()

    fun onConnectedStateObserve(isConnected: Boolean, data: String)
}