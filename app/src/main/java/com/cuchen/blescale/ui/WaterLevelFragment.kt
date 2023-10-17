package com.cuchen.blescale.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cuchen.blescale.ui.theme.BleScaleTheme
import com.cuchen.blescale.ui.viewmodel.ScanScreenViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WaterLevelFragment : Fragment() {
    private val viewModel: ScanScreenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        val intent = activity?.intent

//        Log.d("TAG", "onCreateView scale: ${arguments?.getInt("scale")}")
//        Log.d("TAG", "onCreateView riceType: ${arguments?.getString("riceType")}")
        val scale = arguments?.getInt("scale") ?: 0
        return ComposeView(requireContext()).apply {
            setContent {
                BleScaleTheme {
                    Column(modifier = Modifier.background(Color.White)) {

                        Text(
                            text = String.format("메뉴 %s", arguments?.getString("riceType") ?: ""),
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .wrapContentSize(),
                            fontSize = 30.sp
                        )
                        Text(
                            text = String.format(
                                "목표 총 중량 %.1f g",
                                (scale.times(0.1)).times(1.5) + scale.times(0.1)
                            ),
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .wrapContentSize(),
                            fontSize = 30.sp
                        )

                        Text(
                            text = String.format(
                                "측정 중량 : %.1f g",
                                viewModel.state.collectAsState().value.scaleData.scale * 0.1
                            ),
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f)
                                .wrapContentSize(),
                            fontSize = 30.sp
                        )
                    }
                }
            }
        }
    }


}