package com.orinugoori.tfthelper

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class AugmentRepository(private val context: Context) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("tft_cache", Context.MODE_PRIVATE)

    private val gson = Gson()

    companion object {
        private const val KEY_AUGMENTS_DATA = "augments_data"
        private const val KEY_CACHE_TIME = "cache_time"
        private const val KEY_CACHED_VERSION = "cached_version"
        private const val CACHE_DURATION = 24 * 60 * 60 * 1000L // 24시간
    }

    /**
     * 캐시된 증강 데이터 가져오기
     * @return 캐시된 데이터 또는 null (만료되었거나 없는 경우)
     */
    suspend fun getCachedAugments(): List<Augment>? = withContext(Dispatchers.IO) {
        try {
            val cachedData = sharedPrefs.getString(KEY_AUGMENTS_DATA, null)
            val cacheTime = sharedPrefs.getLong(KEY_CACHE_TIME, 0)
            val cachedVersion = sharedPrefs.getString(KEY_CACHED_VERSION, null)

            if (cachedData.isNullOrEmpty() || cachedVersion.isNullOrEmpty()) {
                Log.d("AugmentRepository", "캐시 데이터가 없습니다.")
                return@withContext null
            }

            // 캐시 만료 확인
            val isExpired = System.currentTimeMillis() - cacheTime > CACHE_DURATION
            if (isExpired) {
                Log.d("AugmentRepository", "캐시가 만료되었습니다.")
                return@withContext null
            }

            // 버전 확인 (최신 버전과 다르면 캐시 무효화)
            val latestVersion = getLatestVersion()
            if (cachedVersion != latestVersion) {
                Log.d("AugmentRepository", "버전이 달라 캐시를 무효화합니다. 캐시: $cachedVersion, 최신: $latestVersion")
                clearCache()
                return@withContext null
            }

            val type = object : TypeToken<List<Augment>>() {}.type
            val augments: List<Augment> = gson.fromJson(cachedData, type)

            Log.d("AugmentRepository", "캐시에서 ${augments.size}개 증강 로드됨")
            return@withContext augments

        } catch (e: Exception) {
            Log.e("AugmentRepository", "캐시 로드 실패", e)
            return@withContext null
        }
    }

    /**
     * 증강 데이터 캐시에 저장
     */
    suspend fun cacheAugments(augments: List<Augment>, version: String) = withContext(Dispatchers.IO) {
        try {
            val augmentsJson = gson.toJson(augments)

            sharedPrefs.edit()
                .putString(KEY_AUGMENTS_DATA, augmentsJson)
                .putLong(KEY_CACHE_TIME, System.currentTimeMillis())
                .putString(KEY_CACHED_VERSION, version)
                .apply()

            Log.d("AugmentRepository", "${augments.size}개 증강이 캐시에 저장됨 (버전: $version)")

        } catch (e: Exception) {
            Log.e("AugmentRepository", "캐시 저장 실패", e)
        }
    }

    /**
     * 최신 버전 정보 가져오기
     */
    suspend fun getLatestVersion(): String = withContext(Dispatchers.IO) {
        try {
            val versions = RetrofitInstance.api.getVersions()
            val latestVersion = versions.firstOrNull() ?: "14.24.1"
            Log.d("AugmentRepository", "최신 버전: $latestVersion")
            return@withContext latestVersion
        } catch (e: Exception) {
            Log.e("AugmentRepository", "버전 정보 가져오기 실패", e)
            // 기본값 반환
            return@withContext "14.24.1"
        }
    }

    /**
     * 서버에서 최신 증강 데이터 가져오기
     */
    suspend fun fetchAugmentsFromServer(): List<Augment> = withContext(Dispatchers.IO) {
        val version = getLatestVersion()
        val response = RetrofitInstance.api.getAugments(version)
        val rawAugments = response.data.values.toList()
        val processedAugments = processAugments(rawAugments)

        // 새로 받은 데이터를 캐시에 저장
        cacheAugments(processedAugments, version)

        Log.d("AugmentRepository", "서버에서 ${processedAugments.size}개 증강 로드됨")
        return@withContext processedAugments
    }

    /**
     * 캐시 삭제
     */
    fun clearCache() {
        sharedPrefs.edit()
            .remove(KEY_AUGMENTS_DATA)
            .remove(KEY_CACHE_TIME)
            .remove(KEY_CACHED_VERSION)
            .apply()
        Log.d("AugmentRepository", "캐시가 삭제되었습니다.")
    }

    /**
     * 캐시 상태 정보 가져오기
     */
    fun getCacheInfo(): CacheInfo {
        val cacheTime = sharedPrefs.getLong(KEY_CACHE_TIME, 0)
        val cachedVersion = sharedPrefs.getString(KEY_CACHED_VERSION, null)
        val hasData = sharedPrefs.contains(KEY_AUGMENTS_DATA)

        return CacheInfo(
            hasCache = hasData,
            cacheTime = cacheTime,
            cachedVersion = cachedVersion,
            isExpired = if (cacheTime > 0) {
                System.currentTimeMillis() - cacheTime > CACHE_DURATION
            } else true
        )
    }
}

/**
 * 캐시 정보를 담는 데이터 클래스
 */
data class CacheInfo(
    val hasCache: Boolean,
    val cacheTime: Long,
    val cachedVersion: String?,
    val isExpired: Boolean
)