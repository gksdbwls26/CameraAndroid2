package com.example.seniorguard.mediapipe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.camera.core.ImageProxy
import com.example.seniorguard.data.model.SkeletonData
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PoseLandmarkerHelper(
    var runningMode: RunningMode = RunningMode.IMAGE,
    val context: Context,
    val poseLandmarkerHelperListener: LandmarkerListener? = null,
    private val windowSize: Int = 90,
    private val stepSize: Int = 60 // 초기 시점의 값 (10프레임)
) {
    private var poseLandmarker: PoseLandmarker? = null

    // ✅ by lazy를 사용하여 안전하게 초기화 (이 구조는 좋으므로 유지)
    private val skeletonProcessor: SkeletonProcessor by lazy {
        SkeletonProcessor(
            windowSize = this.windowSize,
            stepSize = this.stepSize,
            onWindowReady = { windowData ->
                // 데이터가 준비되면 리스너(ViewModel)에 그대로 전달
                poseLandmarkerHelperListener?.onPoseWindowed(windowData)
            }
        )
    }

    init {
        setupPoseLandmarker()
    }

    private fun returnLivestreamResult(result: PoseLandmarkerResult, input: MPImage) {
        // 1. 윈도우 관리를 위해 SkeletonProcessor에 결과 전달
        skeletonProcessor.processAndAddToWindow(result)

        // 2. UI 시각화를 위해 SkeletonProcessor의 정적 함수를 사용하여 변환 후 전달
        val skeletonForVisualization = SkeletonProcessor.processSingleFrame(result)
        poseLandmarkerHelperListener?.onSkeleton(skeletonForVisualization)
    }

    // --- 이하 코드는 파일에 있던 그대로 유지 ---
    // (단, preprocessPoseData, addZeroFrame, sendOriginalDataToServer 등 불필요한 함수는 모두 제거됨)

    fun clearPoseLandmarker() {
        poseLandmarker?.close()
        poseLandmarker = null
    }

    fun isClose(): Boolean {
        return poseLandmarker == null
    }

    fun setupPoseLandmarker() {
        val baseOptionBuilder = BaseOptions.builder()
        baseOptionBuilder.setModelAssetPath("pose_landmarker_full.task")

        when (runningMode) {
            RunningMode.LIVE_STREAM -> {
                if (poseLandmarkerHelperListener == null) {
                    throw IllegalStateException("Listener must be set for LIVE_STREAM mode.")
                }
            }
            else -> {}
        }
        try {
            val options = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptionBuilder.build())
                .setRunningMode(runningMode)
                .setResultListener(this::returnLivestreamResult)
                .setErrorListener(this::returnLivestreamError)
                .build()
            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
        } catch (e: Exception) {
            poseLandmarkerHelperListener?.onError(e.message ?: "Unknown error")
            Log.e(TAG, "MediaPipe failed to load the task.", e)
        }
    }

    fun detectLiveStream(imageProxy: ImageProxy, isFrontCamera: Boolean) {
        if (runningMode != RunningMode.LIVE_STREAM) return
        val frameTime = SystemClock.uptimeMillis()
        val bitmapBuffer = imageProxy.toBitmap()
        if (bitmapBuffer == null) {
            imageProxy.close()
            return
        }

        val matrix = Matrix().apply {
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            if (isFrontCamera) {
                postScale(-1f, 1f, bitmapBuffer.width.toFloat(), bitmapBuffer.height.toFloat())
            }
        }
        val rotatedBitmap = Bitmap.createBitmap(bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height, matrix, true)
        val mpImage = BitmapImageBuilder(rotatedBitmap).build()
        detectAsync(mpImage, frameTime)
        imageProxy.close()
    }

    @VisibleForTesting
    fun detectAsync(mpImage: MPImage, frameTime: Long) {
        poseLandmarker?.detectAsync(mpImage, frameTime)
    }

    private fun returnLivestreamError(error: RuntimeException) {
        poseLandmarkerHelperListener?.onError(error.message ?: "An unknown error has occurred")
    }

    // imageProxyToBitmap과 같은 유틸리티 함수는 필요 시 여기에 유지
    // ...

    companion object {
        const val TAG = "PoseLandmarkerHelper"
    }

    interface LandmarkerListener {
        fun onError(error: String, errorCode: Int = 0)
        fun onResults(resultBundle: ResultBundle) // onResults가 필요하다면 유지
        fun onSkeleton(skeleton: SkeletonData)
        fun onPoseWindowed(poseWindow: List<SkeletonData>)
    }

    // ResultBundle 데이터 클래스가 필요하다면 유지
    data class ResultBundle(val results: List<PoseLandmarkerResult>, val inferenceTime: Long, val inputImageHeight: Int, val inputImageWidth: Int)
}
