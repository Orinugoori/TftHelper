package com.orinugoori.tfthelper

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class AugmentRepository(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("augment_cache", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val api = RetrofitInstance.api
    private val dataDragonApi = RetrofitInstance.dataDragonApi
    
    companion object {
        private const val KEY_CACHED_AUGMENTS = "cached_augments"
        private const val KEY_CACHED_VERSION = "cached_version"
        private const val KEY_CACHE_TIMESTAMP = "cache_timestamp"
        private const val CACHE_DURATION_MS = 24 * 60 * 60 * 1000L // 24시간
    }

    /**
     * 캐시에서 증강 데이터 가져오기
     */
    fun getCachedAugments(): List<Augment>? {
        val cacheTimestamp = prefs.getLong(KEY_CACHE_TIMESTAMP, 0)
        val currentTime = System.currentTimeMillis()
        
        // 캐시가 만료되었는지 확인
        if (currentTime - cacheTimestamp > CACHE_DURATION_MS) {
            Log.d("AugmentRepository", "캐시가 만료됨")
            return null
        }
        
        val cachedJson = prefs.getString(KEY_CACHED_AUGMENTS, null)
        return if (cachedJson != null) {
            try {
                val type = object : TypeToken<List<Augment>>() {}.type
                gson.fromJson<List<Augment>>(cachedJson, type)
            } catch (e: Exception) {
                Log.e("AugmentRepository", "캐시 파싱 실패", e)
                null
            }
        } else {
            null
        }
    }

    /**
     * 서버에서 최신 증강 데이터 가져오기 (Community Dragon 사용)
     */
    suspend fun fetchAugmentsFromServer(): List<Augment> = withContext(Dispatchers.IO) {
        try {
            // 1. Community Dragon에서 증강 데이터 가져오기 (설명 포함)
            Log.d("AugmentRepository", "Community Dragon에서 증강 데이터 가져오는 중...")
            val communityResponse = api.getAugments()
            val augmentResponse = communityResponse.toAugmentResponse()
            val augments = augmentResponse.data.values.toList()
            
            // 2. 현재 버전 정보는 Data Dragon에서 가져오기 (호환성 유지)
            val currentVersion = getCurrentDataDragonVersion()
            
            // 3. 캐시에 저장
            cacheAugments(augments, currentVersion)
            
            Log.d("AugmentRepository", "Community Dragon에서 ${augments.size}개 증강 데이터 로드 완료")
            
            augments
            
        } catch (e: IOException) {
            Log.e("AugmentRepository", "네트워크 오류", e)
            throw e
        } catch (e: Exception) {
            Log.e("AugmentRepository", "서버 데이터 가져오기 실패", e)
            throw e
        }
    }

    /**
     * Data Dragon에서 버전 정보 가져오기 (기존 호환성을 위해)
     */
    private suspend fun getCurrentDataDragonVersion(): String {
        return try {
            // Data Dragon API는 별도 인스턴스에서 버전 정보 가져오기
            // Community Dragon은 항상 최신이므로 현재 게임 버전으로 설정
            "15.1.1" // 임시로 고정, 필요시 Data Dragon API 호출 가능
        } catch (e: Exception) {
            Log.e("AugmentRepository", "버전 정보 가져오기 실패", e)
            "15.1.1" // 기본값
        }
    }

    /**
     * 증강 데이터를 캐시에 저장
     */
    private fun cacheAugments(augments: List<Augment>, version: String) {
        try {
            val json = gson.toJson(augments)
            prefs.edit()
                .putString(KEY_CACHED_AUGMENTS, json)
                .putString(KEY_CACHED_VERSION, version)
                .putLong(KEY_CACHE_TIMESTAMP, System.currentTimeMillis())
                .apply()
            
            Log.d("AugmentRepository", "캐시 저장 완료 - 버전: $version")
        } catch (e: Exception) {
            Log.e("AugmentRepository", "캐시 저장 실패", e)
        }
    }

    /**
     * 캐시 삭제
     */
    fun clearCache() {
        prefs.edit()
            .remove(KEY_CACHED_AUGMENTS)
            .remove(KEY_CACHED_VERSION)
            .remove(KEY_CACHE_TIMESTAMP)
            .apply()
        
        Log.d("AugmentRepository", "캐시 삭제 완료")
    }

    /**
     * 캐시 정보 가져오기
     */
    fun getCacheInfo(): CacheInfo {
        val version = prefs.getString(KEY_CACHED_VERSION, "없음") ?: "없음"
        val timestamp = prefs.getLong(KEY_CACHE_TIMESTAMP, 0)
        val isExpired = System.currentTimeMillis() - timestamp > CACHE_DURATION_MS
        
        return CacheInfo(
            version = version,
            timestamp = timestamp,
            isExpired = isExpired
        )
    }

    /**
     * 현재 사용 중인 버전 가져오기
     */
    fun getCurrentVersion(): String {
        return prefs.getString(KEY_CACHED_VERSION, "15.1.1") ?: "15.1.1"
    }
}

/**
 * 캐시 정보 데이터 클래스
 */
data class CacheInfo(
    val version: String,
    val timestamp: Long,
    val isExpired: Boolean
)
