package com.orinugoori.tfthelper.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.orinugoori.tfthelper.core.constants.AppConstants
import com.orinugoori.tfthelper.data.remote.api.RetrofitInstance
import com.orinugoori.tfthelper.data.repository.AugmentRepositoryImpl
import com.orinugoori.tfthelper.domain.model.Augment
import com.orinugoori.tfthelper.domain.model.CacheInfo
import com.orinugoori.tfthelper.domain.repository.AugmentRepository
import com.orinugoori.tfthelper.domain.usecase.AugmentSearchUseCase
import com.orinugoori.tfthelper.presentation.utils.SearchHistoryManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * 증강체 뷰모델
 */
class AugmentViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: AugmentRepository = AugmentRepositoryImpl(
        context = application.applicationContext,
        api = RetrofitInstance.api
    )
    
    private val searchUseCase = AugmentSearchUseCase()
    private val searchHistoryManager = SearchHistoryManager(application.applicationContext)
    
    // UI 상태 관리
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    // 증강 데이터
    private val _augments = MutableStateFlow<List<Augment>>(emptyList())
    val augments: StateFlow<List<Augment>> = _augments.asStateFlow()
    
    // 필터링된 증강 데이터
    private val _filteredAugments = MutableStateFlow<List<Augment>>(emptyList())
    val filteredAugments: StateFlow<List<Augment>> = _filteredAugments.asStateFlow()
    
    // 필터 상태
    private val _selectedTier = MutableStateFlow(AppConstants.AugmentTiers.ALL)
    val selectedTier: StateFlow<String> = _selectedTier.asStateFlow()
    
    // 검색 쿼리
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // 검색 관련 상태
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()
    
    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions: StateFlow<List<String>> = _searchSuggestions.asStateFlow()
    
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    
    /**
     * UI 상태 정의
     */
    sealed class UiState {
        object Loading : UiState()
        object LoadingFromCache : UiState()
        data class Success(val message: String = "") : UiState()
        data class Error(val message: String) : UiState()
        object NetworkError : UiState()
        object CacheExpired : UiState()
    }
    
    init {
        _uiState.value = UiState.Loading
        
        viewModelScope.launch {
            delay(100)
            loadAugments()
        }
        
        loadSearchHistory()
    }
    
    /**
     * 증강 데이터 로드 (캐시 우선, 그 다음 서버)
     */
    private fun loadAugments() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                
                // 1. 캐시에서 먼저 시도
                val cachedAugments = repository.getCachedAugments()
                if (cachedAugments != null && cachedAugments.isNotEmpty()) {
                    Log.d("AugmentViewModel", "캐시에서 데이터 로드 성공")
                    updateAugmentData(cachedAugments)
                    _uiState.value = UiState.Success("캐시에서 로드됨")
                    return@launch
                }
                
                // 2. 캐시가 없거나 만료된 경우 서버에서 가져오기
                Log.d("AugmentViewModel", "서버에서 데이터 가져오는 중...")
                val serverAugments = repository.fetchAugmentsFromServer()
                updateAugmentData(serverAugments)
                _uiState.value = UiState.Success("최신 데이터 로드 완료")
                
            } catch (e: IOException) {
                Log.e("AugmentViewModel", "네트워크 오류", e)
                _uiState.value = UiState.NetworkError
                
            } catch (e: Exception) {
                Log.e("AugmentViewModel", "데이터 로드 실패", e)
                _uiState.value = UiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다")
            }
        }
    }
    
    /**
     * 강제 새로고침 (캐시 무시하고 서버에서 가져오기)
     */
    fun refreshAugments() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                repository.clearCache()
                
                val serverAugments = repository.fetchAugmentsFromServer()
                updateAugmentData(serverAugments)
                _uiState.value = UiState.Success("데이터 새로고침 완료")
                
            } catch (e: IOException) {
                Log.e("AugmentViewModel", "새로고침 중 네트워크 오류", e)
                _uiState.value = UiState.NetworkError
                
            } catch (e: Exception) {
                Log.e("AugmentViewModel", "새로고침 실패", e)
                _uiState.value = UiState.Error(e.message ?: "새로고침 중 오류가 발생했습니다")
            }
        }
    }
    
    /**
     * 오류 상태에서 재시도
     */
    fun retryLoading() {
        loadAugments()
    }
    
    /**
     * 증강 데이터 업데이트
     */
    private fun updateAugmentData(augments: List<Augment>) {
        _augments.value = augments
        applyFilters()
        Log.d("AugmentViewModel", "증강 데이터 업데이트: ${augments.size}개")
    }
    
    /**
     * 티어 필터 변경
     */
    fun updateTierFilter(tier: String) {
        _selectedTier.value = tier
        applyFilters()
    }
    
    /**
     * 검색 쿼리 변경
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }
    
    /**
     * 모든 필터 초기화
     */
    fun clearAllFilters() {
        _selectedTier.value = AppConstants.AugmentTiers.ALL
        _searchQuery.value = ""
        applyFilters()
    }
    
    /**
     * 필터 적용
     */
    private fun applyFilters() {
        val filtered = searchUseCase.searchAugments(
            augments = _augments.value,
            query = _searchQuery.value,
            selectedTier = _selectedTier.value
        )
        
        _filteredAugments.value = filtered
        Log.d("AugmentViewModel", "필터 적용 결과: ${filtered.size}개 (전체: ${_augments.value.size}개)")
    }
    
    /**
     * 캐시 정보 가져오기
     */
    fun getCacheInfo(): CacheInfo {
        return repository.getCacheInfo()
    }
    
    /**
     * 현재 사용 중인 데이터 버전 가져오기
     */
    fun getCurrentVersion(): String {
        return repository.getCurrentVersion()
    }
    
    /**
     * 티어별 증강 개수 가져오기
     */
    fun getAugmentCountByTier(): Map<String, Int> {
        val counts = mutableMapOf<String, Int>()
        _augments.value.forEach { augment ->
            val tier = augment.tier.ifEmpty { "기타" }
            counts[tier] = (counts[tier] ?: 0) + 1
        }
        return counts
    }
    
    // 검색 기능
    fun searchAugmentsAdvanced(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            _searchQuery.value = query
            
            if (query.isBlank()) {
                _filteredAugments.value = _augments.value
                _searchSuggestions.value = emptyList()
                _isSearching.value = false
                return@launch
            }
            
            val searchResults = searchUseCase.searchAugments(
                augments = _augments.value,
                query = query,
                selectedTier = _selectedTier.value
            )
            
            _filteredAugments.value = searchResults
            
            val suggestions = searchUseCase.generateSearchSuggestions(_augments.value, query)
            _searchSuggestions.value = suggestions
            
            _isSearching.value = false
            
            if (query.length >= AppConstants.Search.MIN_SEARCH_LENGTH) {
                searchHistoryManager.addToHistory(query)
                loadSearchHistory()
            }
        }
    }
    
    fun clearSearch() {
        _filteredAugments.value = _augments.value
        _searchQuery.value = ""
        _searchSuggestions.value = emptyList()
    }
    
    fun searchWithSuggestion(suggestion: String) {
        searchAugmentsAdvanced(suggestion)
    }
    
    fun clearSearchHistory() {
        searchHistoryManager.clearHistory()
        _searchHistory.value = emptyList()
    }
    
    private fun loadSearchHistory() {
        _searchHistory.value = searchHistoryManager.getHistory()
    }
    
    fun advancedSearch(query: String, selectedTier: String) {
        viewModelScope.launch {
            _isSearching.value = true
            
            val searchResults = searchUseCase.searchAugments(
                augments = _augments.value,
                query = query,
                selectedTier = selectedTier
            )
            
            _filteredAugments.value = searchResults
            _isSearching.value = false
        }
    }
}