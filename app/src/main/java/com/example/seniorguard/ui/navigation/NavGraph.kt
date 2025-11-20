package com.example.seniorguard.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.seniorguard.ui.guardian.GuardianScreen
import com.example.seniorguard.ui.mode.ModeSelectScreen
import com.example.seniorguard.ui.monitoring.MonitoringScreen
import com.example.seniorguard.ui.monitoring.MonitoringViewModel


@Composable
fun NavGraph (
    navController: NavHostController,
    startDestination: String = Screen.ModeSelect.route
){
    NavHost(navController, startDestination) {
        composable(Screen.ModeSelect.route) {
            Log.d("NavGraph", "startDestination = $startDestination")
            ModeSelectScreen(
                onSelectGuardian = { navController.navigate(Screen.Guardian.route) },
                onSelectMonitoring = { navController.navigate(Screen.Monitoring.route) }
            )
        }

        composable(Screen.Guardian.route) {
            GuardianScreen(navController = navController)
        }
        /*
        composable(Screen.NotificationDetail.route) {
            NotificationDetailScreen(navController)
        }
         */
        composable(Screen.Monitoring.route) {
            // ✅ 1. 여기서 hiltViewModel()을 호출하여 ViewModel 인스턴스를 생성합니다.
            // NavHost의 composable 스코프 내에서 생성되므로, 이 ViewModel은 화면이 재구성되어도 유지됩니다.
            val viewModel: MonitoringViewModel = hiltViewModel()

            // ✅ 2. 생성된 viewModel을 MonitoringScreen에 파라미터로 전달합니다.
            MonitoringScreen(viewModel = viewModel)
        }
        /*
        composable(Screen.CameraSetup.route) {
            CameraSetupScreen(navController)
        }

         */
    }
}
