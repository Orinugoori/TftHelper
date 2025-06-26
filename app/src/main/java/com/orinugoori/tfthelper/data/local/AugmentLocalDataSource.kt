package com.orinugoori.tfthelper.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orinugoori.tfthelper.domain.model.Augment
import com.orinugoori.tfthelper.core.constants.AppConstants

/**
 * 로컬 캐시 데이터 관리를 담당하는 클래스
 * SharedPreferences를 사용하여 증강체 데이터를 캐싱
 */
class AugmentLocalDataSource(context: Context) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        AppConstants.Cache.PREFERENCES_NAME, 
        Context.MODE_PRIVATE
    )
    
    private val gson = Gson()
    
    /**
     * 증강체 목록을 로컬에 저장하는 함수
     * @param augments 저장할 증강체 목록
     */
    fun saveAugments(augments: List<Augment>) {
        val json = gson.toJson(augments)
        sharedPreferences.edit()
            .putString(CACHE_KEY_AUGMENTS, json)
            .putLong(CACHE_KEY_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }
    
    /**
     * 로컬에서 증강체 목록을 가져오는 함수
     * @return 캐시된 증강체 목록 또는 null
     */
    fun getAugments(): List<Augment>? {
        val json = sharedPreferences.getString(CACHE_KEY_AUGMENTS, null) ?: return null
        return try {
            val type = object : TypeToken<List<Augment>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 캐시가 유효한지 확인하는 함수
     * @return 캐시 유효성 여부
     */
    fun isCacheValid(): Boolean {
        val timestamp = sharedPreferences.getLong(CACHE_KEY_TIMESTAMP, 0)
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - timestamp
        
        return elapsedTime < AppConstants.Cache.CACHE_EXPIRY_TIME
    }
    
    /**
     * 검색 기록을 저장하는 함수
     * @param query 검색어
     */
    fun saveSearchHistory(query: String) {
        val currentHistory = getSearchHistory().toMutableList()
        
        // 중복 제거
        currentHistory.remove(query)
        // 맨 앞에 추가
        currentHistory.add(0, query)
        // 최대 개수 제한
        if (currentHistory.size > MAX_SEARCH_HISTORY) {
            currentHistory.removeAt(currentHistory.size - 1)
        }
        
        val json = gson.toJson(currentHistory)
        sharedPreferences.edit()
            .putString(CACHE_KEY_SEARCH_HISTORY, json)
            .apply()
    }
    
    /**
     * 검색 기록을 가져오는 함수
     * @return 검색 기록 목록
     */
    fun getSearchHistory(): List<String> {
        val json = sharedPreferences.getString(CACHE_KEY_SEARCH_HISTORY, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * 검색 기록을 삭제하는 함수
     */
    fun clearSearchHistory() {
        sharedPreferences.edit()
            .remove(CACHE_KEY_SEARCH_HISTORY)
            .apply()
    }
    
    /**
     * API 버전 정보를 저장하는 함수
     * @param version 현재 버전
     */
    fun saveCurrentVersion(version: String) {
        sharedPreferences.edit()
            .putString(CACHE_KEY_VERSION, version)
            .apply()
    }
    
    /**
     * 현재 저장된 API 버전을 가져오는 함수
     * @return 저장된 버전 또는 기본값
     */
    fun getCurrentVersion(): String {
        return sharedPreferences.getString(CACHE_KEY_VERSION, AppConstants.API.DEFAULT_VERSION) 
            ?: AppConstants.API.DEFAULT_VERSION
    }
    
    /**
     * 모든 캐시 데이터를 삭제하는 함수
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
    
    companion object {
        private const val CACHE_KEY_AUGMENTS = "cached_augments"
        private const val CACHE_KEY_TIMESTAMP = "cache_timestamp"
        private const val CACHE_KEY_SEARCH_HISTORY = "search_history"
        private const val CACHE_KEY_VERSION = "api_version"
        private const val MAX_SEARCH_HISTORY = 10
    }
}