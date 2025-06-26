package com.orinugoori.tfthelper.domain.repository

import com.orinugoori.tfthelper.data.model.UiAugment

/**
 * 증강체 데이터 Repository 인터페이스
 * Clean Architecture의 Domain Layer에서 정의
 */
interface AugmentRepository {
    
    /**
     * 모든 증강체 데이터 가져오기
     * @param forceRefresh 강제로 새 데이터를 가져올지 여부
     * @return 증강체 리스트
     */
    suspend fun getAugments(forceRefresh: Boolean = false): Result<List<UiAugment>>
    
    /**
     * 검색어로 증강체 필터링
     * @param query 검색어 (초성 검색 지원)
     * @return 필터링된 증강체 리스트
     */
    suspend fun searchAugments(query: String): Result<List<UiAugment>>
    
    /**
     * 티어별 증강체 필터링
     * @param tier 티어 (실버/골드/프리즘/전체)
     * @return 티어별 증강체 리스트
     */
    suspend fun getAugmentsByTier(tier: String): Result<List<UiAugment>>
    
    /**
     * 캐시 데이터 삭제
     */
    suspend fun clearCache()
}
