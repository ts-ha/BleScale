package com.cuchen.blescale.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.cuchen.blescale.R
import com.cuchen.blescale.ui.output.FeedUiEffect
import com.cuchen.blescale.ui.screen.ScanScreen
import com.cuchen.blescale.ui.theme.BleScaleTheme
import com.cuchen.blescale.ui.viewmodel.ScanScreenViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScanScreenFragment : BaseFragment() {

    private val viewModel: ScanScreenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        observeUiEffects()
        return ComposeView(requireContext()).apply {
            setContent {
                BleScaleTheme {
                    val state by viewModel.state.collectAsState()
//                    state.scannedDevices.forEach {
//                        println("scannedDevice  : ${it.address}")
//                    }
                    ScanScreen(
                        input = viewModel.inputs,

                        state = viewModel.state.collectAsState().value

                    )
                }
            }
        }
    }

    private fun observeUiEffects() {

//        val navController = findNavController(this)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val navController = findNavController(this@ScanScreenFragment)
                viewModel.output.feedUiEffect.collectLatest {
                    when (it) {
                        is FeedUiEffect.OpenMovieRice -> {

                            val bundleOf = bundleOf("scale" to it.scale, "riceType" to it.riceType)
                            Log.d("TAG", "observeUiEffects scale: ${it.scale}")

                            navController.navigate(
                                R.id.action_connectBleFragment_to_waterLevelFragment,
                                bundleOf
                            )
                        }


                        else -> {

                        }
                    }
                }
            }
        }
    }
}