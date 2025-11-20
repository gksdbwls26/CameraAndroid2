package com.example.seniorguard.data.repository

import com.example.seniorguard.data.model.FallEvent
import com.example.seniorguard.data.model.SeniorData
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// 실제 GuardianRepository 인터페이스를 구현한다고 가정
interface GuardianRepository {
    fun getSelectedSenior(): Flow<SeniorData>
    fun getFallHistory(): Flow<List<FallEvent>>
}

// Fake 구현체
class FakeGuardianRepository @Inject constructor() : GuardianRepository {
    override fun getSelectedSenior(): Flow<SeniorData> =
        flowOf(
            SeniorData(
                name = "김철수",
                age = 78,
                lastCheck = "2025-11-16 19:45"
            )
        )

    override fun getFallHistory(): Flow<List<FallEvent>> =
        flowOf(
            listOf(
                FallEvent(
                    id = 1,
                    severity = "critical",
                    date = "2025-11-16",
                    time = "19:40",
                    type = "낙상 감지"
                ),
                FallEvent(
                    id = 2,
                    severity = "warning",
                    date = "2025-11-15",
                    time = "21:10",
                    type = "낙상 위험 자세"
                )
            )
        )
}

/* 차후 DB확장시
class GuardianRepository @Inject constructor(
    private val api: GuardianApi, // Retrofit 등
    private val dao: GuardianDao  // Room DB 등
) {

    fun getSelectedSenior(): Flow<SeniorData> =
        dao.getSenior().map { entity ->
            SeniorData(entity.name, entity.age, entity.lastCheck)
        }

    fun getFallHistory(): Flow<List<FallEvent>> =
        dao.getFallEvents().map { list ->
            list.map { entity ->
                FallEvent(entity.id, entity.severity, entity.date, entity.time, entity.type)
            }
        }
        }
 */

