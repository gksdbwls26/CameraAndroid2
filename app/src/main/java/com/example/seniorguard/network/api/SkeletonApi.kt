package com.example.seniorguard.network.api


import com.example.seniorguard.data.model.SkeletonData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface SkeletonApi {
    /**
     * 90프레임 분량의 뼈대 데이터 리스트를 서버로 전송합니다.
     * @param poseWindow List<SkeletonData> 객체. Retrofit이 JSON 배열로 자동 변환합니다.
     * @return 서버로부터의 응답 (성공 여부 등을 담을 수 있습니다)
     */
    // @POST("pose") // 서버의 엔드포인트 경로를 입력하세요. 예: /predict
        @POST("pose")
        suspend fun sendPoseData(@Body poseWindow: List<SkeletonData>): Response<Unit>
}