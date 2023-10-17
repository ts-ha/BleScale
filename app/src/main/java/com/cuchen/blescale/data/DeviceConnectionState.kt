package com.cuchen.blescale.data

sealed class DeviceConnectionState {
    object isConnecting : DeviceConnectionState()
    object isConnected : DeviceConnectionState()
    object isDisconnected : DeviceConnectionState()
    object isScan : DeviceConnectionState()
}
