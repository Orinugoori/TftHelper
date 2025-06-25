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
     * 서버에서 최신 증강 데이터 가져오기 (Data Dragon 사용)
     */

    suspend fun fetchAugmentsFromServer(): List<Augment> = withContext(Dispatchers.IO) {
        try {
            // 1. 최신 버전 정보 가져오기
            Log.d("AugmentRepository", "최신 버전 정보 가져오는 중...")
            val versions = api.getVersions()
            val latestVersion = versions.firstOrNull() ?: "15.1.1"

            Log.d("AugmentRepository", "최신 버전: $latestVersion")

            // 2. 해당 버전의 TFT 증강 데이터 가져오기
            Log.d("AugmentRepository", "TFT 증강 데이터 가져오는 중... (버전: $latestVersion)")
            val response = api.getAugments(latestVersion)

            // 🔥 API 응답 데이터 로깅 (디버깅용)
            logAugmentData(response)           // 전체 데이터 상세 로그
            logAugmentDataGrouped(response)    // 티어별 그룹 로그
            logSpecificPatterns(response)      // 패턴별 분석 로그

            // 3. 데이터 처리 (티어 추출 및 설명 정리)
            val processedAugments = processAugmentData(response)

            // 4. 캐시에 저장
            cacheAugments(processedAugments, latestVersion)

            Log.d("AugmentRepository", "Data Dragon에서 ${processedAugments.size}개 증강 데이터 로드 완료")

            processedAugments

        } catch (e: IOException) {
            Log.e("AugmentRepository", "네트워크 오류", e)
            throw e
        } catch (e: Exception) {
            Log.e("AugmentRepository", "서버 데이터 가져오기 실패", e)
            throw e
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
            
            Log.d("AugmentRepository", "캐시 저장 완료 - 버전: $version, 증강 수: ${augments.size}")
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

    /**
     * 최신 버전과 현재 캐시된 버전 비교하여 업데이트 필요 여부 확인
     */
    suspend fun checkForUpdates(): Boolean {
        return try {
            val versions = api.getVersions()
            val latestVersion = versions.firstOrNull() ?: return false
            val cachedVersion = getCurrentVersion()
            
            latestVersion != cachedVersion
        } catch (e: Exception) {
            Log.e("AugmentRepository", "업데이트 확인 실패", e)
            false
        }
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




/**
 * API 응답에서 증강체 이름과 이미지 파일명을 로그로 출력하는 함수
 */
fun logAugmentData(response: AugmentResponse) {
    Log.d("TFT_API_DEBUG", "=== 증강체 API 응답 데이터 분석 ===")
    Log.d("TFT_API_DEBUG", "총 증강체 개수: ${response.data.size}")
    Log.d("TFT_API_DEBUG", "----------------------------------------")

    response.data.values.forEachIndexed { index, augment ->
        Log.d("TFT_API_DEBUG", "[$index] 이름: ${augment.name}")
        Log.d("TFT_API_DEBUG", "[$index] 이미지: ${augment.image.full}")
        Log.d("TFT_API_DEBUG", "[$index] 이미지 기반 티어: ${extractTierFromImageName(augment.image.full)}")
        Log.d("TFT_API_DEBUG", "----------------------------------------")
    }

    Log.d("TFT_API_DEBUG", "=== 로그 출력 완료 ===")
}

/**
 * 티어별로 그룹화해서 로그 출력하는 함수
 */
fun logAugmentDataGrouped(response: AugmentResponse) {
    Log.d("TFT_API_DEBUG", "=== 티어별 증강체 분석 ===")

    val augmentsByTier = response.data.values.groupBy { augment ->
        extractTierFromImageName(augment.name)
    }

    augmentsByTier.forEach { (tier, augments) ->
        Log.d("TFT_API_DEBUG", "🎯 $tier 티어 (${augments.size}개)")
        augments.forEach { augment ->
            Log.d("TFT_API_DEBUG", "   • ${augment.name} → ${augment.image.full}")
        }
        Log.d("TFT_API_DEBUG", "")
    }

    Log.d("TFT_API_DEBUG", "=== 티어별 분석 완료 ===")
}

/**
 * 특정 패턴의 증강체들만 필터링해서 로그 출력
 */
fun logSpecificPatterns(response: AugmentResponse) {
    Log.d("TFT_API_DEBUG", "=== 특정 패턴 증강체 분석 ===")

    val augments = response.data.values

    // I, II, III 패턴 찾기
    val romanNumeralAugments = augments.filter { augment ->
        augment.name.contains(" I") || augment.name.contains(" II") || augment.name.contains(" III")
    }

    Log.d("TFT_API_DEBUG", "🔍 로마숫자 패턴 증강체 (${romanNumeralAugments.size}개)")
    romanNumeralAugments.forEach { augment ->
        Log.d("TFT_API_DEBUG", "   • ${augment.name} → ${augment.image.full}")
    }

    // 숫자 패턴 찾기 (1, 2, 3)
    val numberPatternAugments = augments.filter { augment ->
        augment.image.full.contains("1.") || augment.image.full.contains("2.") || augment.image.full.contains("3.")
    }

    Log.d("TFT_API_DEBUG", "🔢 숫자 패턴 이미지 증강체 (${numberPatternAugments.size}개)")
    numberPatternAugments.forEach { augment ->
        Log.d("TFT_API_DEBUG", "   • ${augment.name} → ${augment.image.full}")
    }

    // 패턴이 없는 증강체들
    val noPatternAugments = augments.filter { augment ->
        !augment.name.contains(" I") && !augment.name.contains(" II") && !augment.name.contains(" III") &&
                !augment.image.full.contains("1.") && !augment.image.full.contains("2.") && !augment.image.full.contains("3.")
    }

    Log.d("TFT_API_DEBUG", "❓ 패턴 없는 증강체 (${noPatternAugments.size}개)")
    noPatternAugments.forEach { augment ->
        Log.d("TFT_API_DEBUG", "   • ${augment.name} → ${augment.image.full}")
    }

    Log.d("TFT_API_DEBUG", "=== 패턴 분석 완료 ===")
}
