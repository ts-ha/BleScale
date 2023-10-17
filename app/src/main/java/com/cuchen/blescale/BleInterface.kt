package com.cuchen.blescale

interface BleInterface {
    fun onConnectedStateObserve(isConnected: Boolean, data: String)
}