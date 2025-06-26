package com.orinugoori.tfthelper.domain.repository

import com.orinugoori.tfthelper.domain.model.Augment
import kotlinx.coroutines.flow.Flow

/**
 * 증강체 데이터 접근을 위한 Repository 인터페이스
 * Clean Architecture의 Domain Layer에서 정의하는 계약
 */
interface AugmentRepository {
    
    /**
     * 모든 증강체 목록을 가져오는 함수
     * @return 증강체 목록의 Flow
     */
    fun getAllAugments(): Flow<List<Augment>>
    
    /**
     * 특정 티어의 증강체 목록을 가져오는 함수
     * @param tier 증강체 티어 (실버, 골드, 프리즘)
     * @return 해당 티어의 증강체 목록의 Flow
     */
    fun getAugmentsByTier(tier: String): Flow<List<Augment>>
    
    /**
     * 증강체를 검색하는 함수
     * @param query 검색 쿼리
     * @return 검색 결과 증강체 목록의 Flow
     */
    fun searchAugments(query: String): Flow<List<Augment>>
    
    /**
     * 특정 증강체를 ID로 가져오는 함수
     * @param id 증강체 ID
     * @return 증강체 정보 또는 null
     */
    suspend fun getAugmentById(id: String): Augment?
    
    /**
     * 증강체 데이터를 새로고침하는 함수
     * @return 성공 여부
     */
    suspend fun refreshAugments(): Result<Unit>
    
    /**
     * 캐시된 데이터가 유효한지 확인하는 함수
     * @return 캐시 유효성 여부
     */
    suspend fun isCacheValid(): Boolean
}