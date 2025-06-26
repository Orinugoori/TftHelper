package com.orinugoori.tfthelper.data.repository

import android.util.Log
import com.google.gson.Gson
import com.orinugoori.tfthelper.core.constants.AppConstants
import com.orinugoori.tfthelper.data.local.cache.LocalCacheManager
import com.orinugoori.tfthelper.data.model.Augment
import com.orinugoori.tfthelper.data.model.AugmentResponse
import com.orinugoori.tfthelper.data.model.UiAugment
import com.orinugoori.tfthelper.data.remote.api.TftApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 증강체 데이터 Repository 구현체
 */
class AugmentRepositoryImpl(
    private val apiService: TftApiService,
    private val cacheManager: LocalCacheManager
) : AugmentRepository {
    
    private val gson = Gson()
    
    override suspend fun getAugments(forceRefresh: Boolean): Result<List<UiAugment>> {
        return withContext(Dispatchers.IO) {
            try {
                // 캐시 확인
                if (!forceRefresh && cacheManager.isCacheValid()) {
                    val cachedData = cacheManager.loadAugmentCache()
                    if (cachedData != null) {
                        val augmentResponse = gson.fromJson(cachedData, AugmentResponse::class.java)
                        val uiAugments = convertToUiAugments(augmentResponse.data)
                        return@withContext Result.success(uiAugments)
                    }
                }
                
                // API에서 최신 데이터 가져오기
                val versionResponse = apiService.getVersions()
                if (!versionResponse.isSuccessful) {
                    return@withContext Result.failure(
                        Exception("버전 정보를 가져오는데 실패했습니다: ${versionResponse.message()}")
                    )
                }
                
                val latestVersion = versionResponse.body()?.firstOrNull() ?: AppConstants.Api.DEFAULT_VERSION
                
                val augmentResponse = apiService.getAugments(latestVersion, AppConstants.Api.DEFAULT_LANGUAGE)
                if (!augmentResponse.isSuccessful || augmentResponse.body() == null) {
                    return@withContext Result.failure(
                        Exception("증강체 데이터를 가져오는데 실패했습니다: ${augmentResponse.message()}")
                    )
                }
                
                val augmentData = augmentResponse.body()!!
                
                // 캐시 저장
                cacheManager.saveAugmentCache(gson.toJson(augmentData), latestVersion)
                
                // UI 모델로 변환
                val uiAugments = convertToUiAugments(augmentData.data)
                
                Log.d("AugmentRepository", "Successfully loaded ${uiAugments.size} augments")
                Result.success(uiAugments)
                
            } catch (e: Exception) {
                Log.e("AugmentRepository", "Error loading augments", e)
                
                // 네트워크 오류 시 캐시된 데이터라도 반환
                val cachedData = cacheManager.loadAugmentCache()
                if (cachedData != null) {
                    try {
                        val augmentResponse = gson.fromJson(cachedData, AugmentResponse::class.java)
                        val uiAugments = convertToUiAugments(augmentResponse.data)
                        Result.success(uiAugments)
                    } catch (cacheException: Exception) {
                        Result.failure(Exception("네트워크 오류 및 캐시 데이터 손상", e))
                    }
                } else {
                    Result.failure(e)
                }
            }
        }
    }
    
    override suspend fun searchAugments(query: String): Result<List<UiAugment>> {
        return try {
            val allAugments = getAugments(forceRefresh = false).getOrThrow()
            val filteredAugments = if (query.isBlank()) {
                allAugments
            } else {
                allAugments.filter { augment ->
                    augment.searchableText.contains(query.lowercase()) ||
                    augment.name.getInitialConsonants().contains(query)
                }
            }
            Result.success(filteredAugments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAugmentsByTier(tier: String): Result<List<UiAugment>> {
        return try {
            val allAugments = getAugments(forceRefresh = false).getOrThrow()
            val filteredAugments = if (tier == AppConstants.AugmentTiers.ALL) {
                allAugments
            } else {
                allAugments.filter { it.tier.equals(tier, ignoreCase = true) }
            }
            Result.success(filteredAugments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun clearCache() {
        withContext(Dispatchers.IO) {
            cacheManager.clearAugmentCache()
        }
    }
    
    private fun convertToUiAugments(augmentMap: Map<String, Augment>): List<UiAugment> {
        return augmentMap.map { (key, augment) ->
            UiAugment(
                id = key,
                name = augment.name,
                tier = determineTier(augment.name, augment.tier),
                imageUrl = "https://ddragon.leagueoflegends.com/cdn/${AppConstants.Api.DEFAULT_VERSION}/img/tft-augment/${augment.image.full}",
                description = augment.description,
                searchableText = "${augment.name} ${augment.description}".lowercase()
            )
        }.sortedBy { it.name }
    }
    
    private fun determineTier(name: String, tier: String): String {
        return when {
            tier.isNotEmpty() -> tier
            name.contains("실버") || name.contains("Silver") -> AppConstants.AugmentTiers.SILVER
            name.contains("골드") || name.contains("Gold") -> AppConstants.AugmentTiers.GOLD
            name.contains("프리즘") || name.contains("Prism") -> AppConstants.AugmentTiers.PRISM
            else -> AppConstants.AugmentTiers.SILVER // 기본값
        }
    }
    
    /**
     * 한글 초성 검색을 위한 확장 함수
     */
    private fun String.getInitialConsonants(): String {
        val initialConsonants = arrayOf(
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )
        
        return this.map { char ->
            if (char in '가'..'힣') {
                val index = (char.code - '가'.code) / 588
                initialConsonants[index]
            } else {
                char
            }
        }.joinToString("")
    }
}
