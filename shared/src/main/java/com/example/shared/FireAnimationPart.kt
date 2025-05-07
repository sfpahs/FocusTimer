package com.example.shared

import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieConstants

sealed class AnimationPart(val clipSpec: LottieClipSpec, val iterations: Int) {
    data object Start : AnimationPart(
        clipSpec = LottieClipSpec.Frame(0, 40),
        iterations = 1
    )

    data object loding : AnimationPart(
        clipSpec = LottieClipSpec.Frame(40, 80),
        iterations = LottieConstants.IterateForever
    )

    data object End : AnimationPart(
        clipSpec = LottieClipSpec.Frame(230, 250),
        iterations = 1
    )
}

