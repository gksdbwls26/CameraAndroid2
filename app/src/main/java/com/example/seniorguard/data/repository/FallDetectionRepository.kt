package com.example.seniorguard.data.repository

import com.example.seniorguard.data.model.SkeletonData
import com.example.seniorguard.network.api.SkeletonApi // ✅ 1. API 인터페이스 Import
import javax.inject.Inject

class FallDetectionRepository @Inject constructor(
    private val skeletonApi: SkeletonApi
) {
    /**
     * 분석 로직 (임시)
     */
    fun analyzeSkeleton(data: SkeletonData): Boolean {
        return false
    }

    /**
     * 90프레임의 뼈대 데이터 묶음을 서버로 전송합니다.
     * @param poseWindow 서버로 보낼 List<SkeletonData>
     */
    suspend fun sendPoseWindow(poseWindow: List<SkeletonData>) {
        // ✅ 3. 주입받은 skeletonApi 객체를 통해 직접 API 함수 호출
        // (네트워크 오류 처리는 ViewModel/UseCase 등 상위 레이어에서 하는 것이 일반적입니다)
        skeletonApi.sendPoseData(poseWindow)
    }
}
