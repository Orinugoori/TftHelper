package com.orinugoori.tfthelper.data.local.cache

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orinugoori.tfthelper.core.constants.AppConstants

/**
 * SharedPreferences를 이용한 로컬 캐시 관리 클래스
 */
class LocalCacheManager(context: Context) {
    
    private val augmentCachePrefs: SharedPreferences = context.getSharedPreferences(
        AppConstants.Cache.AUGMENT_CACHE_PREFS, 
        Context.MODE_PRIVATE
    )
    
    private val searchHistoryPrefs: SharedPreferences = context.getSharedPreferences(
        AppConstants.Cache.SEARCH_HISTORY_PREFS,
        Context.MODE_PRIVATE
    )
    
    private val gson = Gson()
    
    /**
     * 증강체 데이터 캐시 저장
     */
    fun saveAugmentCache(data: String, version: String) {
        augmentCachePrefs.edit().apply {
            putString(AppConstants.Cache.CACHED_AUGMENTS_KEY, data)
            putString(AppConstants.Cache.CACHED_VERSION_KEY, version)
            putLong(AppConstants.Cache.CACHE_TIMESTAMP_KEY, System.currentTimeMillis())
            apply()
        }
    }
    
    /**
     * 캐시된 증강체 데이터 로드
     */
    fun loadAugmentCache(): String? {
        return augmentCachePrefs.getString(AppConstants.Cache.CACHED_AUGMENTS_KEY, null)
    }
    
    /**
     * 캐시된 버전 정보 로드
     */
    fun getCachedVersion(): String? {
        return augmentCachePrefs.getString(AppConstants.Cache.CACHED_VERSION_KEY, null)
    }
    
    /**
     * 캐시가 유효한지 확인
     */
    fun isCacheValid(): Boolean {
        val timestamp = augmentCachePrefs.getLong(AppConstants.Cache.CACHE_TIMESTAMP_KEY, 0)
        val currentTime = System.currentTimeMillis()
        return currentTime - timestamp < AppConstants.Cache.CACHE_DURATION_MS
    }
    
    /**
     * 캐시 클리어
     */
    fun clearAugmentCache() {
        augmentCachePrefs.edit().clear().apply()
    }
    
    /**
     * 검색 기록 저장
     */
    fun saveSearchHistory(history: List<String>) {
        val json = gson.toJson(history)
        searchHistoryPrefs.edit()
            .putString("search_history", json)
            .apply()
    }
    
    /**
     * 검색 기록 로드
     */
    fun loadSearchHistory(): List<String> {
        val json = searchHistoryPrefs.getString("search_history", null) ?: return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    /**
     * 검색 기록에 새 항목 추가
     */
    fun addToSearchHistory(query: String) {
        if (query.isBlank()) return
        
        val currentHistory = loadSearchHistory().toMutableList()
        
        // 중복 제거
        currentHistory.remove(query)
        
        // 맨 앞에 추가
        currentHistory.add(0, query)
        
        // 최대 개수 제한
        val limitedHistory = currentHistory.take(AppConstants.Cache.MAX_SEARCH_HISTORY)
        
        saveSearchHistory(limitedHistory)
    }
    
    /**
     * 검색 기록 클리어
     */
    fun clearSearchHistory() {
        searchHistoryPrefs.edit().clear().apply()
    }
}
