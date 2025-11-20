package com.example.seniorguard.ui.guardian

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seniorguard.ui.guardian.GuardianBottomNav
import com.example.seniorguard.data.model.SeniorData
import com.example.seniorguard.data.model.FallEvent

@Composable
fun GuardianScreen(
    navController: NavHostController,
    viewModel: GuardianViewModel = hiltViewModel()
) {
    val senior by viewModel.selectedSenior.collectAsState()
    val history by viewModel.fallHistory.collectAsState()
    val fallNum = history.size

    Column(modifier = Modifier.fillMaxSize()) {
        // 헤더
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF047857)) // green-700
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("SeniorGuard", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("보호자 모드", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                }
                Icon(Icons.Default.Notifications, contentDescription = "알림", tint = Color.White)
            }
        }

        // 메인 컨텐츠
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF9FAFB)) // gray-50
                .padding(16.dp)
        ) {
            // 피보호자 상태 카드
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFFD1FAE5), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = "User", tint = Color(0xFF047857))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("${senior.name}님", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("${senior.age}세", fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                        Row(
                            modifier = Modifier
                                .background(Color(0xFFD1FAE5), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "정상", tint = Color(0xFF047857), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("정상", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF047857))
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("모니터링 기기", fontSize = 14.sp, color = Color.Gray)
                        Text("✓ 연결됨", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF047857))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("마지막 확인", fontSize = 14.sp, color = Color.Gray)
                        Text(senior.lastCheck, fontSize = 14.sp, color = Color.DarkGray)
                    }
                }
            }

            // 오늘의 상태
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, contentDescription = "Activity", tint = Color(0xFF2563EB))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("오늘의 상태", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFDBEAFE), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text("양호", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                            Text("활동 수준", fontSize = 14.sp, color = Color.Gray)
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFD1FAE5), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text("$fallNum 건", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                            Text("낙상 감지", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // 최근 알림
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = "알림", tint = Color(0xFFF97316))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("최근 알림", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    history.take(2).forEach { event ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("notification") }
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        when (event.severity) {
                                            "critical" -> Text("긴급", fontSize = 12.sp, color = Color.Red,
                                                modifier = Modifier.background(Color(0xFFFECACA), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp))
                                            "warning" -> Text("경고", fontSize = 12.sp, color = Color(0xFFF97316),
                                                modifier = Modifier.background(Color(0xFFFFEDD5), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("${event.date} ${event.time}", fontSize = 12.sp, color = Color.Gray)
                                    }
                                    Text(event.type, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Text("›", fontSize = 16.sp, color = Color.LightGray)
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        // 하단 네비게이션
        GuardianBottomNav(current = "home", onChange = { route -> navController.navigate(route) })
    }
}

@Preview(showBackground = true)
@Composable
fun GuardianScreenPreview() {
    val sampleSenior = SeniorData("김철수", 78, "2025-11-16 19:45")
    val sampleEvents = listOf(
        FallEvent(1, "critical", "2025-11-16", "19:40", "낙상 감지"),
        FallEvent(2, "warning", "2025-11-15", "21:10","낙상 감지" )
    )
}