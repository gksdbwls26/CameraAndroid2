package com.example.seniorguard.ui.monitoring

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner //compose가 최신버전이어야 사용가능
import com.example.seniorguard.ui.monitoring.components.SkeletonOverlay

// Compose 기본
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue

// Compose UI
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState


// Lifecycle

import androidx.lifecycle.LifecycleOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner



// MediaPipe
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

// 프로젝트 내부
import com.example.seniorguard.ui.monitoring.MonitoringViewModel
import com.example.seniorguard.ui.monitoring.components.SkeletonOverlay
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MonitoringScreen(
    viewModel: MonitoringViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    DisposableEffect(lifecycleOwner) {
        onDispose {
            viewModel.shutdownCamera() // ViewModel의 onCleared를 직접 호출하거나 별도의 shutdown 함수 생성
        }
    }

    // UI 구성
    Box(modifier = Modifier.fillMaxSize()) {
        // ✅✅✅ 핵심 수정 사항 ✅✅✅
        // 카메라 권한 상태에 따라 UI를 분기합니다.
        if (cameraPermissionState.status.isGranted) {
            // --- 권한이 허용되었을 때 ---
            // 카메라 프리뷰와 스켈레톤 오버레이를 보여줍니다.
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx)
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->
                    // AndroidView가 준비되면 카메라 시작
                    viewModel.startCamera(context, lifecycleOwner, previewView)
                }
            )

            val skeleton by viewModel.skeletonState.collectAsState()
            SkeletonOverlay(skeleton = skeleton)

        } else {
            // --- 권한이 거부되었거나 요청 중일 때 ---
            // 사용자에게 권한이 필요하다는 안내 메시지를 보여줍니다.
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("카메라 권한이 필요합니다.")
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("권한 재요청")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    /*
    // 권한 요청 및 카메라 시작
    LaunchedEffect(cameraPermissionState.status) {
        when {
            cameraPermissionState.status.isGranted -> {
                viewModel.startCamera(context, lifecycleOwner)
            }
            cameraPermissionState.status.shouldShowRationale -> {
                // 권한 설명이 필요한 경우 (선택사항)
                // 예: Snackbar 또는 Dialog로 안내
            }
            else -> {
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }*/



}
