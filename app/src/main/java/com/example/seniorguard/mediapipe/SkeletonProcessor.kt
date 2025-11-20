package com.example.seniorguard.mediapipe

import android.util.Log
import com.example.seniorguard.data.model.SkeletonData
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class SkeletonProcessor(
    private val windowSize: Int = 90,
    private val stepSize: Int = 60,
    private val onWindowReady: (List<SkeletonData>) -> Unit
) {
    private val frameBuffer = mutableListOf<SkeletonData>()

    fun processAndAddToWindow(result: PoseLandmarkerResult) {
        // 1. companion object의 함수를 사용하여 프레임 데이터를 가져옵니다.
        //    (사람이 없으면 0으로 채워진 데이터가 반환됩니다)
        val skeletonData = processSingleFrame(result)

        // ✅ 2. 'isNotEmpty' 검사를 삭제하여, 0으로 채워진 데이터도 항상 버퍼에 추가합니다.
        frameBuffer.add(skeletonData)

        // 3. 윈도우 생성 및 전송 로직 (기존과 동일)
        if (frameBuffer.size >= windowSize) {
            if ((frameBuffer.size - windowSize) % stepSize == 0) {
                Log.d(TAG, "윈도우 생성! 버퍼 크기: ${frameBuffer.size}")
                val windowToSend = frameBuffer.takeLast(windowSize)
                onWindowReady(windowToSend)
            }
        }
    }

    fun reset() {
        frameBuffer.clear()
    }

    companion object {
        private const val TAG = "SkeletonProcessor"

        private val REQUIRED_JOINT_INDICES = listOf(
            0,  // NOSE
            11, // LEFT_SHOULDER
            12, // RIGHT_SHOULDER
            13, // LEFT_ELBOW
            14, // RIGHT_ELBOW
            23, // LEFT_HIP
            24, // RIGHT_HIP
            25, // LEFT_KNEE
            26, // RIGHT_KNEE
            27, // LEFT_ANKLE
            28, // RIGHT_ANKLE
            30, // RIGHT_HEEL
            29, // LEFT_HEEL (Python 코드 순서에 맞춤)
            31, // LEFT_FOOT_INDEX
            32  // RIGHT_FOOT_INDEX
        )

        fun processSingleFrame(result: PoseLandmarkerResult): SkeletonData {
            val allLandmarks = result.landmarks().firstOrNull()

            // ✅ 3. 랜드마크 감지에 실패했을 때의 로직을 수정합니다.
            if (allLandmarks == null || allLandmarks.isEmpty()) {
                // 빈 데이터 대신, 15개 관절의 모든 값을 0f로 채운 데이터를 생성하여 반환합니다.
                val zeroJoints = List(REQUIRED_JOINT_INDICES.size) {
                    SkeletonData.Joint(x = 0f, y = 0f, z = 0f, visibility = 0f)
                }
                return SkeletonData(joints = zeroJoints)
            }

            // 랜드마크 감지에 성공했을 경우 (기존과 동일)
            val jointList = REQUIRED_JOINT_INDICES.map { index ->
                val landmark = allLandmarks[index]
                SkeletonData.Joint(
                    x = landmark.x(),
                    y = landmark.y(),
                    z = landmark.z(),
                    // orElse(0f)를 사용하여 null일 경우 0f로 처리
                    visibility = landmark.visibility().orElse(0f)
                )
            }
            return SkeletonData(joints = jointList)
        }
    }
}
