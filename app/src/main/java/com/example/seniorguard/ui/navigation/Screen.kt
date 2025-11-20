package com.example.seniorguard.ui.navigation

sealed class Screen(val route: String) {
    object ModeSelect : Screen("mode_select")
    object Guardian : Screen("guardian")
    //object NotificationDetail : Screen("notification/{eventId}")
    object Monitoring : Screen("monitoring")
    //object CameraSetup : Screen("camera_setup")
}