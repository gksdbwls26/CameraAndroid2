package com.example.seniorguard.mediapipe

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

/**
 * MediaPipe 결과를 앱에서 다루기 쉽게 래핑한 클래스
 */
data class ResultBundle(
    val results: List<PoseLandmarkerResult>,
    val inferenceTime: Long,
    val inputImageHeight: Int,
    val inputImageWidth: Int
)
