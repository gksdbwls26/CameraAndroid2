package com.example.seniorguard.ui.monitoring

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seniorguard.camera.CameraManager
import com.example.seniorguard.data.model.SkeletonData
import com.example.seniorguard.data.repository.FallDetectionRepository
import com.example.seniorguard.mediapipe.PoseLandmarkerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow


@HiltViewModel
class MonitoringViewModel @Inject constructor(
    private val repository: FallDetectionRepository
) : ViewModel(), PoseLandmarkerHelper.LandmarkerListener {

    private val _skeletonState = MutableStateFlow(SkeletonData.empty())
    val skeletonState: StateFlow<SkeletonData> = _skeletonState.asStateFlow()

    private lateinit var cameraManager: CameraManager
    private lateinit var poseHelper: PoseLandmarkerHelper

    fun initializeComponents(context: Context) {
        if (::poseHelper.isInitialized) {
            Log.d("MonitoringViewModel", "컴포넌트가 이미 초기화되었습니다.")
            return
        }
        Log.d("MonitoringViewModel", "새로운 Helper 인스턴스를 생성합니다.")
        poseHelper = PoseLandmarkerHelper(
            runningMode = com.google.mediapipe.tasks.vision.core.RunningMode.LIVE_STREAM,
            context = context,
            poseLandmarkerHelperListener = this
        )
    }

    // ✅ 2. startCamera 함수는 UI로부터 모든 필요한 정보를 파라미터로 받습니다.
    fun startCamera(context: Context, lifecycleOwner: LifecycleOwner, preview: PreviewView) {
        initializeComponents(context)

        if (::cameraManager.isInitialized && cameraManager.isCameraStarted()) {
            Log.d("MonitoringViewModel", "카메라가 이미 실행 중입니다. startCamera 호출을 무시합니다.")
            return
        }

        val analyzer = ImageAnalysis.Analyzer { imageProxy ->
            if (::poseHelper.isInitialized) {
                poseHelper.detectLiveStream(imageProxy, isFrontCamera = false)
            } else {
                imageProxy.close()
            }
        }

        Log.d("MonitoringViewModel", "새로운 CameraManager 인스턴스를 생성하고 카메라를 시작합니다.")
        cameraManager = CameraManager(
            context = context,
            lifecycleOwner = lifecycleOwner,
            previewView = preview, // ‼️ 파라미터로 받은 preview를 직접 사용
            poseLandmarkerHelper = poseHelper,
            frameAnalyzer = analyzer
        )
        cameraManager.startCamera()
    }

    fun shutdownCamera() {
        if (::cameraManager.isInitialized) {
            cameraManager.shutdown()
        }
    }

    override fun onCleared() {
        super.onCleared()
        shutdownCamera()
        Log.d("MonitoringViewModel", "ViewModel이 소멸되어 카메라 리소스를 해제합니다.")
    }

    // ... (onSkeleton, onPoseWindowed 등 나머지 함수는 그대로 유지)
    override fun onSkeleton(skeleton: SkeletonData) {
        _skeletonState.value = skeleton
    }

    override fun onPoseWindowed(poseWindow: List<SkeletonData>) {
        Log.d("MonitoringViewModel", "Pose window ready. Sending ${poseWindow.size} frames to server.")
        viewModelScope.launch {
            try {
                repository.sendPoseWindow(poseWindow)
            } catch (e: Exception) {
                Log.e("MonitoringViewModel", "서버 전송 중 오류 발생", e)
            }
        }
    }
    override fun onError(error: String, errorCode: Int) {
        // You can handle the error here, for example by logging it
        Log.e("MonitoringViewModel", "Pose Landmarker error: $error (code: $errorCode)")
    }

    // FIX: Add the missing 'onResults' function
    override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
    }
}
