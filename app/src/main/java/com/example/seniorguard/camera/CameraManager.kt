package com.example.seniorguard.camera


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.example.seniorguard.mediapipe.PoseLandmarkerHelper

/**
 * CameraX를 초기화하고 PreviewView에 연결하는 클래스
 * - 프레임 분석기(FrameAnalyzer)를 설정하여 MediaPipe로 전달 가능
 */
class CameraManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView,
    private val poseLandmarkerHelper: PoseLandmarkerHelper,
    private val frameAnalyzer: ImageAnalysis.Analyzer,
    // 👇 생성자에서 private var로 선언된 것을 private으로 변경합니다.
    // 이 객체의 상태는 startCamera/shutdown 함수를 통해서만 관리되어야 합니다.
    private var cameraProvider: ProcessCameraProvider? = null
) {

    private lateinit var cameraExecutor: ExecutorService

    /**
     * 카메라를 시작하는 함수
     * - PreviewView에 영상 출력
     * - 프레임 분석기 연결
     */
    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            // ✅ 1. cameraProvider를 클래스 멤버 변수에 할당
            cameraProvider = cameraProviderFuture.get()

            // 후면 카메라 선택
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val preview = Preview.Builder().build()

            previewView.post {
                preview.setSurfaceProvider(previewView.surfaceProvider)
            }

            // 프레임 분석기 설정
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(getCameraExecutor(), frameAnalyzer)
                }

            try {
                // 기존 카메라 세션 제거 후 새로 바인딩
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("CameraManager", "카메라 바인딩 실패: ${e.message}")
            }

        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * 프레임 분석을 위한 Executor 반환
     */
    private fun getCameraExecutor(): ExecutorService {
        if (!::cameraExecutor.isInitialized || cameraExecutor.isShutdown) { // ✅ 안전장치 추가
            cameraExecutor = Executors.newSingleThreadExecutor()
        }
        return cameraExecutor
    }

    /**
     * ✅ 2. 카메라가 시작되었는지 확인하는 함수 추가
     * cameraProvider 객체가 할당되었는지 여부로 판단합니다.
     */
    fun isCameraStarted(): Boolean {
        return cameraProvider != null
    }

    /**
     * ✅ 3. 기존 shutdown 함수 확장
     * 카메라 세션을 안전하게 종료하고, Executor를 종료하며, cameraProvider를 null로 초기화합니다.
     */
    fun shutdown() {
        Log.d("CameraManager", "카메라 세션을 종료합니다.")
        // 카메라 세션 바인딩 해제
        cameraProvider?.unbindAll()
        cameraProvider = null // 상태 초기화

        // Executor 종료
        if (::cameraExecutor.isInitialized && !cameraExecutor.isShutdown) {
            cameraExecutor.shutdown()
        }
    }

    // ... (detectLiveStreamSafely, hasCameraPermission 함수는 그대로 유지)
    fun detectLiveStreamSafely(imageProxy: ImageProxy, isFrontCamera: Boolean) {
        if (!hasCameraPermission()) {
            Log.w("CameraManager", "카메라 권한 없음. 프레임 분석 중단")
            imageProxy.close()
            return
        }
        poseLandmarkerHelper.detectLiveStream(imageProxy, isFrontCamera)
    }

    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
}
