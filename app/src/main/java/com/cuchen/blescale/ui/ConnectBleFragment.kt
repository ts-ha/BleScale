package com.cuchen.blescale.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cuchen.blescale.ble.BleManager
import com.cuchen.blescale.ui.screen.ScanScreen
import com.cuchen.blescale.ui.theme.BleScaleTheme
import com.cuchen.blescale.ui.viewmodel.ScanScreenViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConnectBleFragment : Fragment() {
//    private val viewModel: ScanScreenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                BleScaleTheme {
//                    ScanScreen( input = viewModel.inputs)
                }
            }
        }
    }
}