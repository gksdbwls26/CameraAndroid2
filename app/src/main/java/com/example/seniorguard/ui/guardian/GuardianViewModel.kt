package com.example.seniorguard.ui.guardian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seniorguard.data.model.FallEvent
import com.example.seniorguard.data.model.SeniorData
import com.example.seniorguard.data.repository.GuardianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class GuardianViewModel @Inject constructor(
    private val repository: GuardianRepository
) : ViewModel() {

    val selectedSenior = repository.getSelectedSenior()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), SeniorData("", 0, ""))

    val fallHistory = repository.getFallHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /* DB연결시 이런식으로 구성
    val selectedSenior: StateFlow<SeniorData?> =
        repository.getSelectedSenior()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val fallHistory: StateFlow<List<FallEvent>> =
        repository.getFallHistory()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

     */
}
