package com.orinugoori.tfthelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orinugoori.tfthelper.domain.model.Augment
import com.orinugoori.tfthelper.domain.usecase.AugmentSearchUseCase
import com.orinugoori.tfthelper.domain.repository.AugmentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 증강체 관련 UI 상태를 관리하는 ViewModel
 * Clean Architecture의 Presentation Layer
 */
class AugmentViewModel(
    private val augmentSearchUseCase: AugmentSearchUseCase,
    private val repository: AugmentRepository
) : ViewModel() {
    
    // UI 상태 정의
    private val _uiState = MutableStateFlow(AugmentUiState())
    val uiState: StateFlow<AugmentUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedTier = MutableStateFlow<String?>(null)
    val selectedTier: StateFlow<String?> = _selectedTier.asStateFlow()
    
    // 검색 결과
    val augments: StateFlow<List<Augment>> = combine(
        _searchQuery,
        _selectedTier
    ) { query, tier ->
        augmentSearchUseCase(query, tier)
    }.flatMapLatest { it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    init {
        loadAugments()
    }
    
    /**
     * 증강체 데이터를 로드하는 함수
     */
    fun loadAugments() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                repository.getAllAugments()
                    .catch { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message
                        )
                    }
                    .collect { augmentList ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }
    
    /**
     * 검색어 변경 함수
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * 티어 필터 변경 함수
     */
    fun updateTierFilter(tier: String?) {
        _selectedTier.value = tier
    }
    
    /**
     * 증강체 데이터 새로고침 함수
     */
    fun refreshAugments() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            
            val result = repository.refreshAugments()
            
            _uiState.value = _uiState.value.copy(
                isRefreshing = false,
                errorMessage = if (result.isFailure) result.exceptionOrNull()?.message else null
            )
        }
    }
    
    /**
     * 에러 메시지 클리어 함수
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/**
 * 증강체 화면의 UI 상태 데이터 클래스
 */
data class AugmentUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)