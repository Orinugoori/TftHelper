package com.orinugoori.tfthelper.presentation.viewmodel


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.orinugoori.tfthelper.constants.AppConstants // AppConstants 임포트
import com.orinugoori.tfthelper.data.local.SearchHistoryManager // SearchHistoryManager 임포트
import com.orinugoori.tfthelper.data.model.Augment // Augment 모델 임포트 (확인)
import com.orinugoori.tfthelper.repository.AugmentRepository // AugmentRepository 임포트 (확인)
import com.orinugoori.tfthelper.repository.CacheInfo // CacheInfo 임포트 (확인)
import com.orinugoori.tfthelper.util.cleanHtmlTags
import com.orinugoori.tfthelper.util.generateSearchSuggestions
import com.orinugoori.tfthelper.util.getInitialConsonants
import com.orinugoori.tfthelper.util.isInitialConsonantMatch
import com.orinugoori.tfthelper.util.normalizeAugmentName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException


@OptIn(kotlinx.coroutines.FlowPreview::class)
class AugmentViewModel(application: Application) : AndroidViewModel(application) {

    // 레포지토리 인스턴스
    private val augmentRepository = AugmentRepository(application.applicationContext)

    // SearchHistoryManager 인스턴스 추가
    private val searchHistoryManager = SearchHistoryManager(application)

    // UI 상태 관리
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // 검색 쿼리
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 모든 증강 데이터 (원본, _augments로 통일)
    private val _augments = MutableStateFlow<List<Augment>>(emptyList())
    val augments: StateFlow<List<Augment>> = _augments.asStateFlow() // 전체 목록 UI 노출용

    // 필터링된 증강 데이터
    private val _filteredAugments = MutableStateFlow<List<Augment>>(emptyList())
    val filteredAugments: StateFlow<List<Augment>> = _filteredAugments.asStateFlow() // 필터링 결과 UI 노출용

    // 필터 상태
    private val _selectedTier = MutableStateFlow("전체")
    val selectedTier: StateFlow<String> = _selectedTier.asStateFlow()

    // 검색 히스토리
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    // 검색 제안 (자동 완성)
    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions: StateFlow<List<String>> = _searchSuggestions.asStateFlow()

    // 검색 중 여부
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()


    /**
     * UI 상태 정의
     */
    sealed class UiState {
        object Loading : UiState()
        object LoadingFromCache : UiState() // 캐시 로딩 중 상태 추가
        data class Success(val message: String = "") : UiState()
        data class Error(val message: String) : UiState()
        object NetworkError : UiState()
        object CacheExpired : UiState() // 캐시 만료 상태 추가
    }

    init {
        loadAugments() // ViewModel 초기화 시 데이터 로드
        loadSearchHistory() // 검색 기록 로드

        // 검색어 변경 감지 및 필터링 (debounce를 통해 검색 부하 줄임)
        viewModelScope.launch {
            _searchQuery
                .debounce(AppConstants.SEARCH_DEBOUNCE_MILLIS) // 상수 사용
                .map { query ->
                    // 필터링은 항상 _augments.value (원본 전체 목록)에 대해 수행
                    filterAugments(query, _selectedTier.value, _augments.value)
                }
                .collect { filteredList ->
                    _filteredAugments.value = filteredList
                    // 검색 제안 및 검색 중 상태 업데이트
                    _isSearching.value = false
                    generateSearchSuggestions(_augments.value, _searchQuery.value).also {
                        _searchSuggestions.value = it
                    }
                }
        }

        // 티어 필터 변경 감지 및 필터링
        viewModelScope.launch {
            _selectedTier
                .map { tier -> filterAugments(_searchQuery.value, tier, _augments.value) }
                .collect { filteredList ->
                    _filteredAugments.value = filteredList
                }
        }
    }


    /**
     * 증강 데이터 로드 (캐시 우선, 그 다음 서버)
     */
    private fun loadAugments() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading // 로딩 상태 시작
            try {
                // 1. 캐시에서 먼저 시도
                val cachedAugments = augmentRepository.getCachedAugments()
                if (cachedAugments != null && cachedAugments.isNotEmpty()) {
                    Log.d("AugmentViewModel", "캐시에서 데이터 로드 성공")
                    _augments.value = cachedAugments // 원본 목록 업데이트
                    _filteredAugments.value = cachedAugments // 필터링된 목록도 초기화
                    _uiState.value = UiState.Success("캐시에서 로드됨")
                    return@launch
                }

                // 2. 캐시가 없거나 만료된 경우 서버에서 가져오기
                Log.d("AugmentViewModel", "서버에서 데이터 가져오는 중...")
                val serverAugments = augmentRepository.fetchAugmentsFromServer()
                _augments.value = serverAugments // 원본 목록 업데이트
                _filteredAugments.value = serverAugments // 필터링된 목록도 초기화
                _uiState.value = UiState.Success("최신 데이터 로드 완료")

            } catch (e: IOException) {
                Log.e("AugmentViewModel", "네트워크 오류", e)
                _uiState.value = UiState.NetworkError // NetworkError 상태
            } catch (e: Exception) {
                Log.e("AugmentViewModel", "데이터 로드 실패", e)
                _uiState.value = UiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다") // 일반 Error 상태
            }
        }
    }

    /**
     * 강제 새로고침 (캐시 무시하고 서버에서 가져오기)
     */
    fun refreshAugments() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading // 로딩 상태 시작
            try {
                augmentRepository.clearCache() // 캐시 삭제
                val serverAugments = augmentRepository.fetchAugmentsFromServer()
                _augments.value = serverAugments // 원본 목록 업데이트
                _filteredAugments.value = serverAugments // 필터링된 목록도 초기화
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
     * 티어 필터 변경
     */
    fun updateTierFilter(tier: String) {
        _selectedTier.value = tier
        // applyFilters() // Flow 기반으로 자동 적용되므로 필요 없음
    }

    /**
     * 검색 쿼리 변경
     */
    fun updateSearchQuery(query: String) {
        if(_searchQuery.value == query){
            return
        }
        _searchQuery.value = query
        _isSearching.value = true // 검색어 입력 시작 시 검색 중 상태로 변경
        // applyFilters() // Flow 기반으로 자동 적용되므로 필요 없음

        if(query.isBlank()){
            _searchSuggestions.value = emptyList()
        }
    }

    /**
     * 모든 필터 초기화
     */
    fun clearAllFilters() {
        _selectedTier.value = "전체"
        _searchQuery.value = ""
        // applyFilters() // Flow 기반으로 자동 적용되므로 필요 없음
        _filteredAugments.value = _augments.value // 모든 필터 해제 시 전체 목록 표시
    }

    /**
     * 🔍 검색어 입력 취소 시 필터 초기화
     */
    fun clearSearchInput() {
        _searchQuery.value = ""
        _searchSuggestions.value = emptyList()
        _isSearching.value = false
        // applyFilters() 또는 _filteredAugments.value = _augments.value 로 전체 목록 복원
        _filteredAugments.value = _augments.value
    }

    /**
     * 통합된 필터 적용 로직 (Flow의 map에서 호출됨)
     */
    private fun filterAugments(query: String, tier: String, augments: List<Augment>): List<Augment> {
        val filteredByTier = if (tier == "전체") {
            augments
        } else {
            augments.filter { it.tier == tier }
        }

        return if (query.isBlank()) {
            filteredByTier
        } else {
            val normalizedQuery = normalizeAugmentName(query)
            val initialConsonantsQuery = getInitialConsonants(normalizedQuery)

            filteredByTier.filter { augment ->
                val normalizedAugmentName = normalizeAugmentName(augment.name)
                val cleanedDescription = cleanHtmlTags(augment.description) // 설명도 정리 후 검색

                // 1. 일반 텍스트 포함 여부 (이름 또는 설명)
                val nameContainsQuery = normalizedAugmentName.contains(normalizedQuery, ignoreCase = true)
                val descContainsQuery = cleanedDescription.contains(normalizedQuery, ignoreCase = true)

                // 2. 초성 검색 (이름 또는 설명의 초성이 쿼리의 초성을 포함하는지)
                // IMPORTANT: isInitialConsonantMatch 함수에 '쿼리의 초성 문자열'을 전달합니다.
                val nameInitialMatches = isInitialConsonantMatch(normalizedAugmentName, initialConsonantsQuery)
                val descInitialMatches = isInitialConsonantMatch(cleanedDescription, initialConsonantsQuery)

                // 이 네 가지 조건 중 하나라도 만족하면 포함됩니다.
                nameContainsQuery || descContainsQuery || nameInitialMatches || descInitialMatches
            }
        }
    }


    /**
     * 캐시 정보 가져오기
     */
    fun getCacheInfo(): CacheInfo {
        return augmentRepository.getCacheInfo()
    }

    /**
     * 현재 사용 중인 데이터 버전 가져오기
     */
    fun getCurrentVersion(): String {
        return augmentRepository.getCurrentVersion()
    }

    /**
     * 최신 버전 확인 및 업데이트 필요 여부 반환
     */
    suspend fun checkForUpdates(): Boolean {
        // Community Dragon은 항상 최신 데이터를 제공하므로 이 함수는 사용되지 않음
        // (API_MIGRATION.md에 따라 항상 false를 반환하도록 되어 있음)
        return false
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


    // 🔍 검색 관련 함수들 (SearchHistoryManager와 TftDataUtils 사용)

    /**
     * 고급 검색 실행 (실시간 검색 및 기록 관리)
     */
    fun performSearch(query: String) {
        // _searchQuery.value가 변경되면 debounce Flow에 의해 필터링이 자동으로 수행됩니다.
        // 여기서는 검색어 입력 종료 시(엔터 등) 기록을 추가하는 용도로 사용합니다.
        if (query.isNotBlank()) {
            searchHistoryManager.addSearchQuery(query)
            loadSearchHistory() // 기록 UI 갱신
        }
    }

    /**
     * 🔍 검색 히스토리 로드 (SearchHistoryManager 사용)
     */
    private fun loadSearchHistory() {
        // SearchHistoryManager에서 가져온 List를 순서를 뒤집어 최신순으로 UI에 표시합니다.
        _searchHistory.value = searchHistoryManager.getSearchHistory().toList().reversed()
    }

    /**
     * 🔍 특정 검색어로 바로 검색 (검색어 업데이트 및 기록 추가)
     */
    fun searchWithSuggestion(suggestion: String) {
        updateSearchQuery(suggestion) // 검색어 업데이트 -> debounce Flow가 검색 수행
        performSearch(suggestion) // 검색 기록 추가
    }

    /**
     * 🔍 검색 히스토리 항목 제거
     */
    fun removeSearchHistoryItem(query: String) {
        searchHistoryManager.removeSearchQuery(query)
        loadSearchHistory() // 기록 UI 갱신
    }

    /**
     * 🔍 모든 검색 히스토리 지우기
     */
    fun clearAllSearchHistory() {
        searchHistoryManager.clearSearchHistory()
        loadSearchHistory() // 기록 UI 갱신
    }

    fun clearSearchSuggestions(){
        _searchSuggestions.value = emptyList()
    }
}