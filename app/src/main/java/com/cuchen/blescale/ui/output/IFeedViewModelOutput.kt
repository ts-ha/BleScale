package com.cuchen.blescale.ui.output

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface IFeedViewModelOutput {

    val feedUiEffect: SharedFlow<FeedUiEffect>
}

sealed class FeedUiEffect {
    data class OpenMovieDetail(val movieName: String) : FeedUiEffect()
    data class OpenMovieRice(val scale: Int, val riceType: String) : FeedUiEffect()
    object OpenInfoDialog : FeedUiEffect()
}
