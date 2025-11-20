package com.example.seniorguard.network

import com.example.seniorguard.network.api.SkeletonApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

/**
 * Retrofit 초기화 + API 인터페이스 바인딩 역할을 함.
 */
private const val BASE_URL = "http://192.168.219.101:8000/"
private val json = Json { ignoreUnknownKeys = true }
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .build()

/**
 * 실제 API 서비스를 생성하는 싱글턴 객체.
 * 다른 파일에서는 이 객체를 통해 API 함수를 호출합니다. (예: SkeletonApiService.retrofitService.sendPoseData(...))
 */
object SkeletonApiService {
    val retrofitService: SkeletonApi by lazy {
        retrofit.create(SkeletonApi::class.java)
    }
}
