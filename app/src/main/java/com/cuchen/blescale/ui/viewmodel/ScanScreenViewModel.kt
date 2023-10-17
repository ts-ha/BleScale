package com.cuchen.blescale.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuchen.blescale.ui.BluetoothUiState
import com.cuchen.blescale.ui.IDetailViewModelInputs
import com.cuchen.blescale.ui.output.FeedUiEffect
import com.cuchen.blescale.ui.output.IFeedViewModelOutput
import com.cuchen.blescale.usecases.TemperatureAndHumidityReceiveManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanScreenViewModel @Inject constructor(
    private val bluetoothController: TemperatureAndHumidityReceiveManager
) : ViewModel(), IDetailViewModelInputs, IFeedViewModelOutput {

    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothController.scannedDevices, bluetoothController.pairedDevices, _state, bluetoothController.deviceConnectionState, bluetoothController.scaleData
    ) { scannedDevices, pairedDevices, state , deviceConnectionState , scaleData->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,

            deviceConnectionState = deviceConnectionState,
            scaleData = scaleData,

        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    val inputs: IDetailViewModelInputs = this
    val output: IFeedViewModelOutput = this

    private val _feedUiEffect = MutableSharedFlow<FeedUiEffect>(replay = 0)

    override val feedUiEffect: SharedFlow<FeedUiEffect>
        get() = _feedUiEffect

    //    val dd = bluetoothController.scannedDevices.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000))
    init {
       /* bluetoothController.scannedDevices.onEach { it ->
            println("scannedDevices : ${
                it.forEach {
                    "device info ${it.address}"
                }
            }")
        }.launchIn(viewModelScope)*/
    }

    override fun startBle() {
//        bluetoothController.startReceiving()
        bluetoothController.startBleScan()
    }

    override fun stopBle() {
        bluetoothController.stopBleScan()
    }

    override fun openImdbClicked(deviceAddress: String) {
        bluetoothController.startBleConnectGatt(deviceAddress)
    }

    override fun rateClicked() {
    }

    override fun setScale(scale: Int, riceType: String) {
        viewModelScope.launch {
            _feedUiEffect.emit(
                FeedUiEffect.OpenMovieRice(scale, riceType)
            )
        }
    }

}