package com.orinugoori.tfthelper

import android.app.Application
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

    // 키워드 리스트
    private val _keywordList = MutableStateFlow<Set<String>>(emptySet())
    val keywordList: StateFlow<Set<String>> = _keywordList.asStateFlow()

    // 필터 상태
    private val _selectedTier = MutableStateFlow("전체")
    val selectedTier: StateFlow<String> = _selectedTier.asStateFlow()

    private val _selectedKeyword = MutableStateFlow("전체")
    val selectedKeyword: StateFlow<String> = _selectedKeyword.asStateFlow()

    // 검색 쿼리
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

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
     * 증강 데이터 업데이트 및 키워드 추출
     */
    private fun updateAugmentData(augments: List<Augment>) {
        _augments.value = augments

        // 키워드 추출
        val allKeywords = augments.flatMap { it.keyword }.toSet()
        _keywordList.value = allKeywords

        // 필터 적용
        applyFilters()

        Log.d("AugmentViewModel", "증강 데이터 업데이트: ${augments.size}개, 키워드: ${allKeywords.size}개")
    }

    /**
     * 티어 필터 변경
     */
    fun updateTierFilter(tier: String) {
        _selectedTier.value = tier
        applyFilters()
    }

    /**
     * 키워드 필터 변경
     */
    fun updateKeywordFilter(keyword: String) {
        _selectedKeyword.value = keyword
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
        _selectedKeyword.value = "전체"
        _searchQuery.value = ""
        applyFilters()
    }

    /**
     * 필터 적용
     */
    private fun applyFilters() {
        val currentAugments = _augments.value
        val currentTier = _selectedTier.value
        val currentKeyword = _selectedKeyword.value
        val currentQuery = _searchQuery.value

        val filtered = currentAugments.filter { augment ->
            // 티어 필터
            val tierMatches = currentTier == "전체" || augment.tier == currentTier

            // 키워드 필터
            val keywordMatches = currentKeyword == "전체" ||
                    augment.keyword.contains(currentKeyword)

            // 검색 쿼리 필터 (이름과 설명에서 검색)
            val queryMatches = currentQuery.isEmpty() ||
                    augment.name.contains(currentQuery, ignoreCase = true) ||
                    augment.description.contains(currentQuery, ignoreCase = true)

            tierMatches && keywordMatches && queryMatches
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
     */
    suspend fun checkForUpdates(): Boolean {
        return try {
            val versions = RetrofitInstance.api.getVersions()
            val latestVersion = versions.firstOrNull() ?: return false
            val currentVersion = getCurrentVersion()
            
            latestVersion != currentVersion
        } catch (e: Exception) {
            Log.e("AugmentViewModel", "버전 확인 실패", e)
            false
        }
    }

    /**
     * 버전 정보를 UI에 표시하기 위한 데이터 가져오기
     */
    fun getVersionInfo(): String {
        val cacheInfo = getCacheInfo()
        return "현재 버전: ${cacheInfo.version}"
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
    fun searchAugments(query: String) {
        // 증강 이름이나 설명에서 검색어 포함하는 항목 필터링
        viewModelScope.launch {
            val searchResults = _augments.value.filter { augment ->
                augment.name.contains(query, ignoreCase = true) ||
                        augment.description.contains(query, ignoreCase = true) ||
                        augment.keyword.any { keyword -> keyword.contains(query, ignoreCase = true) }
            }
            _filteredAugments.value = searchResults
        }
    }

    // 검색 초기화 (선택사항)
    fun clearSearch() {
        _filteredAugments.value = _augments.value
    }

    // ===== 기존 코드와의 호환성 유지 =====

    /**
     * 기존 filterAugmentsByTier 함수 (호환성 유지)
     */
    fun filterAugmentsByTier(tier: String) {
        updateTierFilter(tier)
    }

    /**
     * 기존 filterAugmentsByKeyword 함수 (호환성 유지)
     */
    fun filterAugmentsByKeyword(keyword: String) {
        updateKeywordFilter(keyword)
    }
}