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

    private val EXCLUDED_AUGMENT_NAMES = setOf(
        "넋을 빼놓는 공연",
        "매각",
        "사이버네틱 이식술 Ⅰ",
        "사이버네틱 집합체",
        "감정의 유대",
        "거물 중의 거물",
        "대마불사",
        "더블 펑크",
        "동전 투입",
        "두근두근",
        "메탈 마니아",
        "번쩍번쩍",
        "빨라지는 리듬",
        "영롱하고 황홀한",
        "영웅적인 존재",
        "우리는 하나",
        "이게 재즈다",
        "죽음의 고통",
        "칼끝에 올라선 삶",
        "템포를 높여라",
        "팬을 위하여",
        "해방",
        "현상금 사냥꾼",
        "인재 물색"
    )

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
                val cachedAugments = gson.fromJson<List<Augment>>(cachedJson, type)

                // 🔥 캐시된 데이터도 필터링 적용
                filterValidAugments(cachedAugments)

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
            analyzeApiDataForDebugging(response)
            verifyBlacklistedAugments(response)

            // 3. 데이터 처리 (티어 추출 및 설명 정리)
            val processedAugments = processAugmentData(response)
            val filteredAugments = filterValidAugments(processedAugments)

            // 4. 캐시에 저장
            cacheAugments(filteredAugments, latestVersion)

            Log.d("AugmentRepository", "Data Dragon에서 ${processedAugments.size}개 증강 데이터 로드 완료")

            filteredAugments

        } catch (e: IOException) {
            Log.e("AugmentRepository", "네트워크 오류", e)
            throw e
        } catch (e: Exception) {
            Log.e("AugmentRepository", "서버 데이터 가져오기 실패", e)
            throw e
        }
    }

    /**
     * 🔥 증강체 필터링 - 시즌 10 및 하드코딩된 증강체 제거
     */
    private fun filterValidAugments(augments: List<Augment>): List<Augment> {
        val originalCount = augments.size

        val filteredAugments = augments.filter { augment ->
            val imageName = augment.image.full.lowercase()
            val augmentName = augment.name

            // 🚫 제외 조건들
            val isExcluded = when {
                // 1. 이미지명에 tutorial 포함된 경우
                imageName.contains("tutorial") -> {
                    Log.d("AugmentRepository", "🚫 Tutorial 제외: ${augment.name} (${augment.image.full})")
                    true
                }
                // 2. 이미지명에 revival 포함된 경우
                imageName.contains("revival") -> {
                    Log.d("AugmentRepository", "🚫 Revival 제외: ${augment.name} (${augment.image.full})")
                    true
                }
                // 3. 하드코딩된 증강체 이름 리스트에 포함된 경우
                EXCLUDED_AUGMENT_NAMES.contains(augmentName) -> {
                    Log.d("AugmentRepository", "🚫 하드코딩 제외: ${augment.name}")
                    true
                }
                // 4. 시즌 10 관련 패턴들 추가 체크
                imageName.contains("set10") -> {
                    Log.d("AugmentRepository", "🚫 Set10 제외: ${augment.name} (${augment.image.full})")
                    true
                }
                else -> false
            }

            !isExcluded
        }

        Log.d("AugmentRepository", "📊 필터링 결과: ${originalCount}개 → ${filteredAugments.size}개 (${originalCount - filteredAugments.size}개 제외)")

        return filteredAugments
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


// AugmentRepository.kt에 추가할 디버깅용 함수들

/**
 * API 응답 데이터를 상세히 분석하고 로그로 출력하는 함수
 * 개발 중에만 사용하고, 릴리즈 전에 제거할 것
 */
fun analyzeApiDataForDebugging(response: AugmentResponse) {
    Log.d("TFT_API_DEBUG", "=== TFT 증강체 API 데이터 분석 시작 ===")
    Log.d("TFT_API_DEBUG", "총 증강체 개수: ${response.data.size}")

    // 1. ID 패턴 분석
    analyzeIdPatterns(response)

    // 2. 이미지 파일명 패턴 분석
    analyzeImagePatterns(response)

    // 3. 설명 내용 분석
    analyzeDescriptions(response)

    // 4. 이름 패턴 분석
    analyzeNamePatterns(response)

    Log.d("TFT_API_DEBUG", "=== TFT 증강체 API 데이터 분석 완료 ===")
}

/**
 * 증강체 ID 패턴 분석
 */
private fun analyzeIdPatterns(response: AugmentResponse) {
    Log.d("TFT_API_DEBUG", "\n=== ID 패턴 분석 ===")

    val searchPatterns = listOf("revival", "tutorial", "tft_set10", "tft10", "test", "debug", "temp", "placeholder")

    searchPatterns.forEach { pattern ->
        val matchingAugments = response.data.values.filter {
            it.id.lowercase().contains(pattern)
        }

        if (matchingAugments.isNotEmpty()) {
            Log.d("TFT_API_DEBUG", "🔍 '$pattern' 패턴 발견 (${matchingAugments.size}개):")
            matchingAugments.forEach { augment ->
                Log.d("TFT_API_DEBUG", "  - ID: ${augment.id}")
                Log.d("TFT_API_DEBUG", "    이름: ${augment.name}")
                Log.d("TFT_API_DEBUG", "    이미지: ${augment.image.full}")
            }
        } else {
            Log.d("TFT_API_DEBUG", "❌ '$pattern' 패턴: 발견되지 않음")
        }
    }
}

/**
 * 이미지 파일명 패턴 분석
 */
private fun analyzeImagePatterns(response: AugmentResponse) {
    Log.d("TFT_API_DEBUG", "\n=== 이미지 파일명 패턴 분석 ===")

    val imagePatterns = listOf("revival", "tutorial", "tft_set10", "set10", "test", "debug")

    imagePatterns.forEach { pattern ->
        val matchingAugments = response.data.values.filter {
            it.image.full.lowercase().contains(pattern)
        }

        if (matchingAugments.isNotEmpty()) {
            Log.d("TFT_API_DEBUG", "🖼️ 이미지에서 '$pattern' 패턴 발견 (${matchingAugments.size}개):")
            matchingAugments.forEach { augment ->
                Log.d("TFT_API_DEBUG", "  - 이름: ${augment.name}")
                Log.d("TFT_API_DEBUG", "    이미지: ${augment.image.full}")
            }
        } else {
            Log.d("TFT_API_DEBUG", "❌ 이미지에서 '$pattern' 패턴: 발견되지 않음")
        }
    }
}

/**
 * 증강체 설명 내용 분석
 */
private fun analyzeDescriptions(response: AugmentResponse) {
    Log.d("TFT_API_DEBUG", "\n=== 설명 내용 분석 ===")

    val descriptionKeywords = listOf(
        "튜토리얼", "tutorial",
        "테스트", "test",
        "임시", "temp", "temporary",
        "디버그", "debug",
        "개발자", "developer",
        "내부 테스트", "internal test",
        "플레이스홀더", "placeholder"
    )

    descriptionKeywords.forEach { keyword ->
        val matchingAugments = response.data.values.filter {
            it.description.lowercase().contains(keyword.lowercase())
        }

        if (matchingAugments.isNotEmpty()) {
            Log.d("TFT_API_DEBUG", "📝 설명에서 '$keyword' 키워드 발견 (${matchingAugments.size}개):")
            matchingAugments.forEach { augment ->
                Log.d("TFT_API_DEBUG", "  - 이름: ${augment.name}")
                Log.d("TFT_API_DEBUG", "    설명: ${augment.description.take(100)}...")
            }
        } else {
            Log.d("TFT_API_DEBUG", "❌ 설명에서 '$keyword' 키워드: 발견되지 않음")
        }
    }
}

/**
 * 증강체 이름 패턴 분석
 */
private fun analyzeNamePatterns(response: AugmentResponse) {
    Log.d("TFT_API_DEBUG", "\n=== 이름 패턴 분석 ===")

    // 특수 문자나 패턴이 있는 이름들 찾기
    val specialPatterns = listOf("TFT_", "DEBUG_", "TEST_", "[", "]", "(개발)", "(테스트)", "(임시)")

    specialPatterns.forEach { pattern ->
        val matchingAugments = response.data.values.filter {
            it.name.contains(pattern)
        }

        if (matchingAugments.isNotEmpty()) {
            Log.d("TFT_API_DEBUG", "🏷️ 이름에서 '$pattern' 패턴 발견 (${matchingAugments.size}개):")
            matchingAugments.forEach { augment ->
                Log.d("TFT_API_DEBUG", "  - 이름: ${augment.name}")
            }
        } else {
            Log.d("TFT_API_DEBUG", "❌ 이름에서 '$pattern' 패턴: 발견되지 않음")
        }
    }
}

/**
 * 현재 블랙리스트에 있는 증강체들이 실제로 API에 존재하는지 확인
 */
private fun verifyBlacklistedAugments(response: AugmentResponse) {
    Log.d("TFT_API_DEBUG", "\n=== 블랙리스트 검증 ===")

    val blacklistedNames = setOf(
        // Revival 관련
        "광란의 축제", "넋을 빼놓는 공연", "밴드 전원 집합",
        "보호막 강타", "레트로 게이머", "랩의 여왕", "파티 개시자",

        // Tutorial 관련
        "방벽 - 이상현상", "확산지대 - 이상현상", "정예 선봉대", "재빠른 공격",

        // 알 수 없는 것들
        "매각", "사이버네틱 이식술 1", "사이버네틱 집합체",

        // 10시즌 관련 (일부만 샘플로)
        "감정의 유대", "거물중의 거물", "대마 불사", "더블 펑크", "동전 투입"
    )

    blacklistedNames.forEach { blacklistedName ->
        val found = response.data.values.find { it.name == blacklistedName }
        if (found != null) {
            Log.d("TFT_API_DEBUG", "✅ 블랙리스트 검증: '$blacklistedName' 발견")
            Log.d("TFT_API_DEBUG", "    ID: ${found.id}")
            Log.d("TFT_API_DEBUG", "    이미지: ${found.image.full}")
        } else {
            Log.d("TFT_API_DEBUG", "❌ 블랙리스트 검증: '$blacklistedName' 미발견 (이미 제거되었거나 이름이 변경됨)")
        }
    }
}
