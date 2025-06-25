package com.orinugoori.tfthelper

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.io.IOException

class AugmentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AugmentRepository(application.applicationContext)

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
    private val _selectedTier = MutableStateFlow("전체")
    val selectedTier: StateFlow<String> = _selectedTier.asStateFlow()

    // 검색 쿼리
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 🔍 검색 관련 새로운 상태들
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions: StateFlow<List<String>> = _searchSuggestions.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // 검색 히스토리 저장소 (SharedPreferences)
    private val searchHistoryPrefs = application.getSharedPreferences("search_history", Context.MODE_PRIVATE)


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
        // 안전한 초기화
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            delay(100) // Compose 초기화 대기
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

                // 2. 캐시가 없거나 만료된 경우 Community Dragon에서 가져오기
                Log.d("AugmentViewModel", "Community Dragon에서 데이터 가져오는 중...")
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
                repository.clearCache() // 캐시 삭제

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

        // 필터 적용
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
        _selectedTier.value = "전체"
        _searchQuery.value = ""
        applyFilters()
    }

    /**
     * 필터 적용
     */
    private fun applyFilters() {
        val currentAugments = _augments.value
        val currentTier = _selectedTier.value
        val currentQuery = _searchQuery.value

        val filtered = currentAugments.filter { augment ->
            // 티어 필터
            val tierMatches = currentTier == "전체" || augment.tier == currentTier

            // 검색 쿼리 필터 (이름과 설명에서 검색)
            val queryMatches = currentQuery.isEmpty() ||
                    augment.name.contains(currentQuery, ignoreCase = true) ||
                    augment.description.contains(currentQuery, ignoreCase = true)

            tierMatches && queryMatches
        }

        _filteredAugments.value = filtered
        Log.d("AugmentViewModel", "필터 적용 결과: ${filtered.size}개 (전체: ${currentAugments.size}개)")
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
     * 최신 버전 확인 및 업데이트 필요 여부 반환
     * Community Dragon은 항상 최신이므로 항상 false 반환
     */
    suspend fun checkForUpdates(): Boolean {
        return try {
            // Community Dragon은 항상 최신 데이터를 제공하므로
            // 정기적인 새로고침만 필요
            false
        } catch (e: Exception) {
            Log.e("AugmentViewModel", "업데이트 확인 실패", e)
            false
        }
    }

    /**
     * 버전 정보를 UI에 표시하기 위한 데이터 가져오기
     */
    fun getVersionInfo(): String {
        val cacheInfo = getCacheInfo()
        return "현재 버전: ${cacheInfo.version} (Community Dragon)"
    }

    /**
     * 특정 증강 찾기
     */
    fun findAugmentById(id: String): Augment? {
        return _augments.value.find { it.id == id }
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
                // 빈 검색어일 때는 전체 목록 표시
                _filteredAugments.value = _augments.value
                _searchSuggestions.value = emptyList()
                _isSearching.value = false
                return@launch
            }

            // 실시간 검색 수행
            val searchResults = _augments.value.filter { augment ->
                val queryLower = query.lowercase()

                // 다양한 검색 조건
                augment.name.lowercase().contains(queryLower) ||
                        augment.description.lowercase().contains(queryLower) ||
                        augment.tier.lowercase().contains(queryLower) ||
                        // 초성 검색 지원 (한글)
                        isInitialConsonantMatch(augment.name, query) ||
                        // 영어 이름이 있다면 영어도 검색
                        augment.name.lowercase().replace(" ", "").contains(queryLower.replace(" ", ""))
            }

            _filteredAugments.value = searchResults

            // 자동완성 제안 생성
            generateSearchSuggestions(query)

            _isSearching.value = false

            // 검색어가 3글자 이상이면 히스토리에 추가
            if (query.length >= 3) {
                addToSearchHistory(query)
            }
        }
    }

    fun clearSearch(){
        _filteredAugments.value = _augments.value
    }

    /**
     * 🔍 한글 초성 검색 지원
     */
    private fun isInitialConsonantMatch(text: String, query: String): Boolean {
        if (query.length > text.length) return false

        val consonants = mapOf(
            'ㄱ' to "가-깋", 'ㄴ' to "나-닣", 'ㄷ' to "다-딯", 'ㄹ' to "라-맇",
            'ㅁ' to "마-밓", 'ㅂ' to "바-빟", 'ㅅ' to "사-싷", 'ㅇ' to "아-잏",
            'ㅈ' to "자-짛", 'ㅊ' to "차-칟", 'ㅋ' to "카-킿", 'ㅌ' to "타-팋",
            'ㅍ' to "파-핗", 'ㅎ' to "하-힣"
        )

        try {
            for (i in query.indices) {
                val queryChar = query[i]
                val textChar = text.getOrNull(i) ?: return false

                if (consonants.containsKey(queryChar)) {
                    val range = consonants[queryChar]!!.split("-")
                    val start = range[0][0]
                    val end = range[1][0]

                    if (textChar !in start..end) {
                        return false
                    }
                } else if (queryChar.lowercaseChar() != textChar.lowercaseChar()) {
                    return false
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * 🔍 자동완성 제안 생성
     */
    private fun generateSearchSuggestions(query: String) {
        val suggestions = _augments.value
            .map { it.name }
            .filter { it.lowercase().contains(query.lowercase()) }
            .distinct()
            .take(5)
            .sorted()

        _searchSuggestions.value = suggestions
    }

    /**
     * 🔍 검색 히스토리 추가
     */
    private fun addToSearchHistory(query: String) {
        val currentHistory = _searchHistory.value.toMutableList()

        // 중복 제거
        currentHistory.remove(query)
        // 맨 앞에 추가
        currentHistory.add(0, query)
        // 최대 10개까지만 저장
        if (currentHistory.size > 10) {
            currentHistory.removeAt(currentHistory.size - 1)
        }

        _searchHistory.value = currentHistory
        saveSearchHistory(currentHistory)
    }

    /**
     * 🔍 검색 히스토리 저장
     */
    private fun saveSearchHistory(history: List<String>) {
        searchHistoryPrefs.edit()
            .putStringSet("history", history.toSet())
            .apply()
    }

    /**
     * 🔍 검색 히스토리 로드
     */
    private fun loadSearchHistory() {
        val historySet = searchHistoryPrefs.getStringSet("history", emptySet()) ?: emptySet()
        _searchHistory.value = historySet.toList().take(10)
    }

    /**
     * 🔍 검색 히스토리 클리어
     */
    fun clearSearchHistory() {
        _searchHistory.value = emptyList()
        searchHistoryPrefs.edit().clear().apply()
    }

    /**
     * 🔍 특정 검색어로 바로 검색
     */
    fun searchWithSuggestion(suggestion: String) {
        searchAugmentsAdvanced(suggestion)
    }

    /**
     * 🔍 고급 필터 검색 (티어 + 텍스트 검색 조합)
     */
    fun advancedSearch(query: String, selectedTier: String) {
        viewModelScope.launch {
            _isSearching.value = true

            val filteredByTier = if (selectedTier == "전체") {
                _augments.value
            } else {
                _augments.value.filter { it.tier == selectedTier }
            }

            val searchResults = if (query.isBlank()) {
                filteredByTier
            } else {
                filteredByTier.filter { augment ->
                    val queryLower = query.lowercase()
                    augment.name.lowercase().contains(queryLower) ||
                            augment.description.lowercase().contains(queryLower) ||
                            isInitialConsonantMatch(augment.name, query)
                }
            }

            _filteredAugments.value = searchResults
            _isSearching.value = false
        }
    }
}
