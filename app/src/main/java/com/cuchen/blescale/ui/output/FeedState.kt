package com.cuchen.blescale.ui.output


sealed class FeedState {
    object Loading : FeedState()
//    class Main(
//        val categories: List<CategoryEntity>
//    ) : FeedState()

    class Failed(
        val reason: String
    ) : FeedState()
}
