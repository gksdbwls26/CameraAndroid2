package com.example.seniorguard.camera


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import com.example.seniorguard.mediapipe.PoseLandmarkerHelper
import com.example.seniorguard.camera.CameraManager

/**
 * CameraX의 프레임을 분석하는 클래스
 * - ImageAnalysis.Analyzer를 구현
 * - MediaPipe에 프레임 전달 → 골격 추출 → ViewModel에 전달
 */

class FrameAnalyzer(
    private val cameraManager: CameraManager,
    private val isFrontCamera: Boolean
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        cameraManager.detectLiveStreamSafely(image, isFrontCamera)
    }
}