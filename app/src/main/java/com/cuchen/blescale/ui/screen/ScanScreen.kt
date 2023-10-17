package com.cuchen.blescale.ui.screen


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.cuchen.blescale.R
import com.cuchen.blescale.data.DeviceConnectionState
import com.cuchen.blescale.ui.BluetoothUiState
import com.cuchen.blescale.ui.IDetailViewModelInputs


@Composable
fun ScanScreen(
    input: IDetailViewModelInputs,
    state: BluetoothUiState
) {
    val checkedScan = remember { mutableStateOf(false) }

    val selectedValue = remember { mutableIntStateOf(0) }
    val selectedValueSot = remember { mutableIntStateOf(0) }
    val selectedValueInBun = remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "취사 물량 저울")
            },
//                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Yellow),
                actions = {
                    ScanBleBtn(
                        checked = checkedScan.value,
                        setScan = {
                            checkedScan.value = !checkedScan.value
//                            println("checkedScan.value : ${checkedScan.value}")
                            if (checkedScan.value) {
                                input.startBle()
                            } else {
                                input.stopBle()
                            }
                        },
                    )
                })
        },
        content = { innerPadding ->
            Column {
                println("state.isConnecting : ${state.deviceConnectionState} ")
                when (state.deviceConnectionState) {
                    DeviceConnectionState.isConnecting -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(100.dp),
                            color = Color.Green,
                            strokeWidth = 10.dp
                        )
                    }

                    DeviceConnectionState.isConnected -> {
//                        checkedScan.value = false

                        val itemsSot = listOf("IH", "FD")
                        val items = listOf("백미", "잡곡")
                        val itemsInBun = listOf("1인분", "2인분", "3인분", "4인분", "5인분")
                        Preview_MultipleRadioButtons(selectedValueSot, itemsSot)
                        Preview_MultipleRadioButtons(selectedValue, items)
                        Preview_MultipleRadioButtons(selectedValueInBun, itemsInBun)
                        Text(
                            text = String.format(
                                "목표 쌀 중량 %d g",
                                (selectedValueInBun.value + 1) * 150
                            ),
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(0.3f)
                                .wrapContentSize(),
                            fontSize = 30.sp,
                            color = if ((selectedValueInBun.value + 1) * 150 == (state.scaleData.scale / 10).toInt()) {
                                Color.Green
                            } else if ((selectedValueInBun.value + 1) * 150 > (state.scaleData.scale / 10).toInt()) {
                                Color.Red
                            } else {
                                Color.Blue
                            }
                        )
                        Row(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(0.5f)
                                .wrapContentSize()
                        ) {
                            Text(
                                text = String.format(
                                    "%.1f",
                                    (state.scaleData.scale * 0.1) / 150
                                ) + "인분",
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .weight(1f)
                                    .wrapContentSize(),
                                fontSize = 30.sp

                            )
                            Text(
                                text = String.format("측정 중량 : %.1f g", state.scaleData.scale * 0.1),
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .weight(1f)
                                    .wrapContentSize(),
                                fontSize = 30.sp

                            )
                        }

                        Button(
                            onClick = {
                                Log.d("TAG", "ScanScreen: ${selectedValue.value}")
                                input.setScale(
                                    state.scaleData.scale,
                                    items[selectedValue.value]
                                )
//                                val navController1 = rememberNavController()
//                                navController.navigate(R.id.action_connectBleFragment_to_waterLevelFragment)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(bottom = 10.dp, start = 16.dp, end = 16.dp),
                        ) {
                            Text(text = "측정완료")

                        }
                    }

                    DeviceConnectionState.isScan -> {
                        DeviceScreen(
                            state = state,
                            input = input
                        )
                    }

                    DeviceConnectionState.isDisconnected -> {
//                        input.stopBle()
//                        checkedScan.value = false
                        Text(
                            text = "연결해제",
                            modifier = Modifier
                                .padding(innerPadding)
                                .weight(1f)
                                .wrapContentSize()
                        )
                    }
                }

            }
        },
    )
}


@Composable
fun ScanBleBtn(
    setScan: () -> Unit, checked: Boolean
) {
    IconButton(
        onClick = { setScan() }, modifier = Modifier.fillMaxHeight()
    ) {
        if (checked) {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_launcher_foreground
                ), contentDescription = ""
            )
        } else {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_launcher_background
                ), contentDescription = ""
            )
        }
    }
}


@Composable
fun Preview_MultipleRadioButtons(selectedValue: MutableState<Int>, items: List<String>) {


    val isSelectedItem: (Int) -> Boolean = { selectedValue.value == it }
    val onChangeState: (Int) -> Unit = { selectedValue.value = it }

    Column(Modifier.padding(8.dp)) {
//        Text(text = "Selected value: ${selectedValue.value.ifEmpty { "NONE" }}")
        Text(text = "Selected value: ${items[selectedValue.value]}")
        Row(
            modifier = Modifier
                .padding(8.dp)
                .wrapContentSize()
        ) {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = isSelectedItem(index),
                            onClick = { onChangeState(index) },
                            role = Role.RadioButton
                        )
                        .padding(8.dp)
                        .wrapContentSize()
                ) {
                    RadioButton(
                        selected = isSelectedItem(index),
                        onClick = null
                    )
                    Text(
                        text = item,
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        }
    }
}