package com.cuchen.blescale.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cuchen.blescale.DeviceData
import com.cuchen.blescale.ui.BluetoothUiState
import com.cuchen.blescale.ui.IDetailViewModelInputs

@Composable
fun DeviceScreen(
    state: BluetoothUiState, input: IDetailViewModelInputs
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BluetoothDeviceList(
            scannedDevices = state.scannedDevices,
            onClick = input,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
        )


    }
}


@Composable
fun BluetoothDeviceList(
    scannedDevices: List<DeviceData>, onClick: IDetailViewModelInputs, modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        /*item {
            Text(
                text = "Paired Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(pairedDevices) { device ->
            Text(
                text = device.name ?: "(No name)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { *//*onClick(device)*//* }
                    .padding(16.dp)
            )
        }*/

        item {
            Text(
                text = "Scanned Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(scannedDevices) { device ->
            Text(text = device.name ?: "(No name)", modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick.openImdbClicked(device.address)
                    onClick.stopBle()
                }
                .padding(16.dp))
        }
    }


}