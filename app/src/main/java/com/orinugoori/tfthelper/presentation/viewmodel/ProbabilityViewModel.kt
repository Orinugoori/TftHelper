package com.orinugoori.tfthelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orinugoori.tfthelper.domain.model.AugmentProbability
import com.orinugoori.tfthelper.domain.usecase.ProbabilityCalculatorUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 확률 계산 관련 UI 상태를 관리하는 ViewModel
 * Clean Architecture의 Presentation Layer
 */
class ProbabilityViewModel(
    private val probabilityCalculatorUseCase: ProbabilityCalculatorUseCase
) : ViewModel() {
    
    // 선택된 증강체들
    private val _selectedAugments = MutableStateFlow<List<String>>(emptyList())
    val selectedAugments: StateFlow<List<String>> = _selectedAugments.asStateFlow()
    
    // 계산된 확률들
    private val _probabilities = MutableStateFlow<List<AugmentProbability>>(emptyList())
    val probabilities: StateFlow<List<AugmentProbability>> = _probabilities.asStateFlow()
    
    // UI 상태
    private val _uiState = MutableStateFlow(ProbabilityUiState())
    val uiState: StateFlow<ProbabilityUiState> = _uiState.asStateFlow()
    
    // 현재 단계 (1, 2, 3)
    val currentStep: StateFlow<Int> = _selectedAugments.map { it.size + 1 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 1
        )
    
    /**
     * 증강체를 선택하는 함수
     */
    fun selectAugment(augmentTier: String) {
        viewModelScope.launch {
            val currentList = _selectedAugments.value.toMutableList()
            
            if (currentList.size < 3) {
                currentList.add(augmentTier)
                _selectedAugments.value = currentList
                calculateProbabilities()
            }
        }
    }
    
    /**
     * 마지막 선택을 되돌리는 함수
     */
    fun undoLastSelection() {
        viewModelScope.launch {
            val currentList = _selectedAugments.value.toMutableList()
            
            if (currentList.isNotEmpty()) {
                currentList.removeAt(currentList.size - 1)
                _selectedAugments.value = currentList
                calculateProbabilities()
            }
        }
    }
    
    /**
     * 모든 선택을 초기화하는 함수
     */
    fun resetSelections() {
        viewModelScope.launch {
            _selectedAugments.value = emptyList()
            _probabilities.value = emptyList()
            _uiState.value = ProbabilityUiState()
        }
    }
    
    /**
     * 특정 단계의 선택을 설정하는 함수
     */
    fun setSelection(step: Int, augmentTier: String) {
        viewModelScope.launch {
            val currentList = _selectedAugments.value.toMutableList()
            
            // 리스트 크기를 step에 맞게 조정
            while (currentList.size < step) {
                currentList.add("")
            }
            while (currentList.size > step) {
                currentList.removeAt(currentList.size - 1)
            }
            
            // 해당 단계 설정
            if (step <= currentList.size) {
                currentList[step - 1] = augmentTier
            }
            
            // 빈 문자열 제거
            _selectedAugments.value = currentList.filter { it.isNotEmpty() }
            calculateProbabilities()
        }
    }
    
    /**
     * 확률을 계산하는 내부 함수
     */
    private fun calculateProbabilities() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCalculating = true)
            
            try {
                val newProbabilities = probabilityCalculatorUseCase.calculateNextProbabilities(
                    _selectedAugments.value
                )
                
                _probabilities.value = newProbabilities
                _uiState.value = _uiState.value.copy(
                    isCalculating = false,
                    errorMessage = null
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCalculating = false,
                    errorMessage = e.message
                )
            }
        }
    }
    
    /**
     * 특정 단계의 확률을 직접 계산하는 함수
     */
    fun calculateForStep(step: Int, selectedAugments: List<String>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCalculating = true)
            
            try {
                val newProbabilities = when (step) {
                    2 -> probabilityCalculatorUseCase.calculateSecondProbabilities(selectedAugments[0])
                    3 -> probabilityCalculatorUseCase.calculateThirdProbabilities(
                        selectedAugments[0], 
                        selectedAugments[1]
                    )
                    else -> emptyList()
                }
                
                _probabilities.value = newProbabilities
                _uiState.value = _uiState.value.copy(
                    isCalculating = false,
                    errorMessage = null
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCalculating = false,
                    errorMessage = e.message
                )
            }
        }
    }
    
    /**
     * 에러 메시지를 클리어하는 함수
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/**
 * 확률 계산 화면의 UI 상태 데이터 클래스
 */
data class ProbabilityUiState(
    val isCalculating: Boolean = false,
    val errorMessage: String? = null
)