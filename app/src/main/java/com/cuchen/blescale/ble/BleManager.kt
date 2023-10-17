package com.cuchen.blescale.ble


import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.cuchen.blescale.BleInterface
import com.cuchen.blescale.DeviceData
import com.cuchen.blescale.data.DeviceConnectionState
import com.cuchen.blescale.domin.ScaleData
import com.cuchen.blescale.usecases.TemperatureAndHumidityReceiveManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


class BleManager(
    private val context: Context
) : TemperatureAndHumidityReceiveManager {
    //    private val bluetoothManager =
//        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager


    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager.adapter
    }
    private val bluetoothLeScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val _scanList = MutableStateFlow<List<DeviceData>>(emptyList())
    private val _pairedDevices = MutableStateFlow<List<DeviceData>>(emptyList())
    private val _isConnected = MutableStateFlow(false)
    private val _deviceConnectionState = MutableStateFlow<DeviceConnectionState>(
        DeviceConnectionState.isScan
    )


    private val _isDisconnected = MutableStateFlow(false)
    private val _errors = MutableSharedFlow<String>()
    private val _scaleData = MutableStateFlow(ScaleData(0, 0, 0))
    private var gattClient: BluetoothGatt? = null

    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    override val isDisconnected: StateFlow<Boolean>
        get() = _isDisconnected.asStateFlow()


    override val scannedDevices: StateFlow<List<DeviceData>>
        get() = _scanList.asStateFlow()
    override val pairedDevices: StateFlow<List<DeviceData>>
        get() = _pairedDevices.asStateFlow()
    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()

    override val scaleData: SharedFlow<ScaleData>
        get() = _scaleData.asSharedFlow()

    override val deviceConnectionState: StateFlow<DeviceConnectionState>
        get() = _deviceConnectionState.asStateFlow()


    private var connectedStateObserver: BleInterface? = null
    var bleGatt: BluetoothGatt? = null

    val SERIAL_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
    val SERIAL_VALUE = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")
    val NOTIFY_VALUE = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private val scanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
//            Log.d("onScanResult", result.toString())
            if (result.device.name != null) {
                var uuid = "null"
                if (result.scanRecord?.serviceUuids != null) {
                    uuid = result.scanRecord!!.serviceUuids.toString()
                }
                val scanItem = DeviceData(
                    result.device.name ?: "null", uuid, result.device.address ?: "null"
                )
                _scanList.update { devices ->
//                    println("_scanList size ${_scanList.value.size}")
                    if (scanItem in devices) devices else devices + scanItem
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            println("onScanFailed  $errorCode")

        }
    }

    @Suppress("DEPRECATION")
    private val gattCallback = object : BluetoothGattCallback() {
        // GATT의 연결 상태 변경을 감지하는 콜백
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            // 연결이 성공적으로 이루어진 경우
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // GATT 서버에서 사용 가능한 서비스들을 비동기적으로 탐색
                Log.d("BleManager", "연결 성공")

                gatt?.discoverServices()
              //                connectedStateObserver?.onConnectedStateObserve(
//                    true, "onConnectionStateChange:  STATE_CONNECTED" + "\n" + "---"
//                )
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // 연결 끊김
                Log.d("BleManager", "연결 해제")
                _isDisconnected.update { true }
                _deviceConnectionState.value = DeviceConnectionState.isDisconnected
//                connectedStateObserver?.onConnectedStateObserve(
//                    false, "onConnectionStateChange:  STATE_DISCONNECTED" + "\n" + "---"
//                )
            }
        }

        // 장치에 대한 새로운 서비스가 발견되었을 때 호출되는 콜백

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)

            // 원격 장치가 성공적으로 탐색된 경우
            if (status == BluetoothGatt.GATT_SUCCESS) {

                MainScope().launch {
                    _deviceConnectionState.value = DeviceConnectionState.isConnected
                    bleGatt = gatt
//                    Toast.makeText(context, " ${gatt?.device?.name} 연결 성공", Toast.LENGTH_SHORT)
//                        .show()
                    //0000fff2-0000-1000-8000-00805f9b34fb

                    // 저울 정보
//                    - 0000180f-0000-1000-8000-00805f9b34fb
//                    00002a19-0000-1000-8000-00805f9b34fb
//                    - 0000fff0-0000-1000-8000-00805f9b34fb
//                    0000fff2-0000-1000-8000-00805f9b34fb
//                    0000fff1-0000-1000-8000-00805f9b34fb // 저울 정보
                    var sendText =
                        "onServicesDiscovered:  GATT_SUCCESS" + "\n" + "                         ↓" + "\n"
                    Log.d("BleManager", "onServicesDiscovered 연결 성공")
//                    val characteristic =
//                        gatt!!.getService(SERIAL_SERVICE).getCharacteristic(SERIAL_VALUE)
//                    gatt.setCharacteristicNotification(characteristic, true)
                    for (service in gatt?.services!!) {
                        sendText += "- " + service.uuid.toString() + "\n"

                        for (characteristics in service.characteristics) {
                            if ((characteristics.properties and PROPERTY_READ) !== 0) {
                                println("Can read ${characteristics.uuid} ")
//                                gatt.readCharacteristic(characteristics)
                            } else {
                                println("Can't read ${characteristics.uuid} ")
                            }
                            if (characteristics.uuid == SERIAL_VALUE) {
//                                println("characteristics.uuid :  ${characteristics.uuid} ")
//                                gatt.readCharacteristic(characteristics)
                                if (gatt.setCharacteristicNotification(characteristics, true)) {
                                    val desc: BluetoothGattDescriptor =
                                        characteristics.getDescriptor(
                                            NOTIFY_VALUE
                                        ).apply {
                                            value =
                                                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                        }
                                    characteristics.writeType =
                                        BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        val writeDescriptor = gattClient?.writeDescriptor(
                                            desc, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                        )
                                        Log.d("TAG", "writeDescriptor: $writeDescriptor")
                                    } else {
                                        val b = gattClient?.writeDescriptor(desc)
                                        Log.d("TAG", "writeDescriptor: $b")
                                    }
                                }
                            }
                            sendText += "       " + characteristics.uuid.toString() + "\n"
                        }
                    }
                    sendText += "---"
                    Log.d("BleManager", "onServicesDiscovered 연결 성공 $sendText")
//                connectedStateObserver?.onConnectedStateObserve(
//                    true, sendText
//                )
                }.cancel()
            }
        }


        fun ByteArray.toHexString(): String =
            joinToString(separator = " ", prefix = "0x") { String.format("%02X", it) }


        override fun onDescriptorWrite(
            gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            Log.e(
                "BluetoothGattCallback", "onDescriptorWrite"
            )
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
//            characteristic?.value?.toHexString()
            Log.e(
                "BluetoothGattCallback",
                "onCharacteristicChanged ${characteristic?.value?.toHexString()}"
            )

            val scale = characteristic?.value?.copyOfRange(2, 4)?.let {
                (it[0].toInt() and 0xff shl 8) or (it[1].toInt() and 0xff)
            } ?: 0
            val negativeNumber = (characteristic?.value?.get(5)?.toInt()?.and(0xff)) ?: 0
            val time = characteristic?.value?.copyOfRange(6, 9)?.let {
                if (it[0].toInt() == 0) {
                    0
                } else {
                    it[1].toInt() + (it[2].toInt() * 60)
                }
            } ?: 0
            _scaleData.update {
                ScaleData(scale = scale, negativeNumber = negativeNumber, time = time)
            }

        }

        override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
            super.onReliableWriteCompleted(gatt, status)
            Log.e(
                "BluetoothGattCallback", "onReliableWriteCompleted"
            )
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            Log.e(
                "BluetoothGattCallback", "onReadRemoteRssi"
            )
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
        }

        override fun onServiceChanged(gatt: BluetoothGatt) {
            super.onServiceChanged(gatt)
            Log.e(
                "BluetoothGattCallback", "onServiceChanged"
            )
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            Log.e(
                "BluetoothGattCallback", "onCharacteristicChanged : ${value.toUByteArray()}"
            )
        }


        override fun onCharacteristicRead(
            gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            println("onCharacteristicRead 222")
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.i(
                        "BluetoothGattCallback",
                        "Read characteristic ${characteristic?.value?.toHexString()} "
                    )
                }

                BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                    Log.e("BluetoothGattCallback", "Read not permitted for ")
                }

                else -> {
                    Log.e("BluetoothGattCallback", "Characteristic read failed for ")
                }
            }

        }

    }

    @SuppressLint("MissingPermission")
    override fun startBleScan() {
        _scanList.update {
            it - it.toSet()
        }
        _deviceConnectionState.update {
            DeviceConnectionState.isScan
        }
        val scanSettings =
            ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()

        bluetoothLeScanner.startScan(null, scanSettings, scanCallback)
    }

    @SuppressLint("MissingPermission")
    override fun stopBleScan() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        bluetoothLeScanner.stopScan(scanCallback)
    }

    override fun onConnectedStateObserve(isConnected: Boolean, data: String) {

    }

    @SuppressLint("MissingPermission")
    override fun startBleConnectGatt(deviceAddress: String) {
        gattClient?.disconnect()
        gattClient = null
        gattClient = bluetoothAdapter.getRemoteDevice(deviceAddress).connectGatt(
            context, false, gattCallback
        )
    }

//    fun setScanList(pScanList: SnapshotStateList<DeviceData>) {
//        scanList = pScanList
//    }

    fun onConnectedStateObserve(pConnectedStateObserver: BleInterface) {
        connectedStateObserver = pConnectedStateObserver
    }


    override fun reconnect() {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }


    @SuppressLint("MissingPermission")
    override fun startReceiving() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        val scanSettings =
            ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()
        bluetoothLeScanner.startScan(null, scanSettings, scanCallback)
    }


    @SuppressLint("MissingPermission")
    override fun closeConnection() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        bluetoothLeScanner.stopScan(scanCallback)
    }


    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}