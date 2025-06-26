package com.orinugoori.tfthelper.data.repository

import android.util.Log
import com.orinugoori.tfthelper.data.local.AugmentLocalDataSource
import com.orinugoori.tfthelper.data.remote.api.RetrofitInstance
import com.orinugoori.tfthelper.data.model.toDomain
import com.orinugoori.tfthelper.domain.model.Augment
import com.orinugoori.tfthelper.domain.repository.AugmentRepository
import com.orinugoori.tfthelper.core.utils.TierUtils
import com.orinugoori.tfthelper.core.extensions.matchesChosung
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/**
 * AugmentRepository의 구현체
 * Data Layer에서 Domain Layer의 계약을 구현
 */
class AugmentRepositoryImpl(
    private val localDataSource: AugmentLocalDataSource
) : AugmentRepository {
    
    private val dataDragonApi = RetrofitInstance.dataDragonApi
    private val communityDragonApi = RetrofitInstance.communityDragonApi
    
    override fun getAllAugments(): Flow<List<Augment>> = flow {
        try {
            // 1. 로컬 캐시 확인
            if (localDataSource.isCacheValid()) {
                val cachedAugments = localDataSource.getAugments()
                if (cachedAugments != null) {
                    Log.d(TAG, "캐시에서 증강체 데이터 로드: ${cachedAugments.size}개")
                    emit(cachedAugments)
                    return@flow
                }
            }
            
            // 2. 원격 데이터 가져오기
            val augments = fetchAugmentsFromRemote()
            
            // 3. 로컬에 캐시
            localDataSource.saveAugments(augments)
            
            emit(augments)
            
        } catch (e: Exception) {
            Log.e(TAG, "증강체 데이터 로드 실패", e)
            
            // 실패 시 캐시된 데이터라도 반환
            val cachedAugments = localDataSource.getAugments()
            if (cachedAugments != null) {
                emit(cachedAugments)
            } else {
                emit(emptyList())
            }
        }
    }
    
    override fun getAugmentsByTier(tier: String): Flow<List<Augment>> = flow {
        getAllAugments().collect { augments ->
            val filteredAugments = augments.filter { it.tier == tier }
            emit(filteredAugments)
        }
    }
    
    override fun searchAugments(query: String): Flow<List<Augment>> = flow {
        getAllAugments().collect { augments ->
            val searchResults = augments.filter { augment ->
                augment.name.contains(query, ignoreCase = true) ||
                augment.description.contains(query, ignoreCase = true) ||
                augment.name.matchesChosung(query) ||
                augment.keywords.any { it.contains(query, ignoreCase = true) }
            }
            emit(searchResults)
        }
    }
    
    override suspend fun getAugmentById(id: String): Augment? {
        return try {
            val augments = localDataSource.getAugments()
            augments?.find { it.id == id }
        } catch (e: Exception) {
            Log.e(TAG, "증강체 조회 실패: $id", e)
            null
        }
    }
    
    override suspend fun refreshAugments(): Result<Unit> {
        return try {
            val augments = fetchAugmentsFromRemote()
            localDataSource.saveAugments(augments)
            Log.d(TAG, "증강체 데이터 새로고침 완료: ${augments.size}개")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "증강체 데이터 새로고침 실패", e)
            Result.failure(e)
        }
    }
    
    override suspend fun isCacheValid(): Boolean {
        return localDataSource.isCacheValid()
    }
    
    /**
     * 원격에서 증강체 데이터를 가져오는 내부 함수
     */
    private suspend fun fetchAugmentsFromRemote(): List<Augment> {
        // 1. Data Dragon에서 기본 데이터 가져오기
        val versions = dataDragonApi.getVersions()
        val currentVersion = if (versions.isSuccessful) {
            versions.body()?.firstOrNull() ?: localDataSource.getCurrentVersion()
        } else {
            localDataSource.getCurrentVersion()
        }
        
        localDataSource.saveCurrentVersion(currentVersion)
        
        val response = dataDragonApi.getAugments(currentVersion)
        if (!response.isSuccessful) {
            throw Exception("API 호출 실패: ${response.code()}")
        }
        
        val augmentData = response.body()?.data ?: throw Exception("증강체 데이터가 비어있음")
        
        // 2. 티어 정보 추가하여 Domain 모델로 변환
        return augmentData.values.mapNotNull { dto ->
            try {
                val tier = TierUtils.extractTierFromImageName(dto.image.full)
                dto.toDomain(tier)
            } catch (e: Exception) {
                Log.w(TAG, "증강체 변환 실패: ${dto.name}", e)
                null
            }
        }.filter { it.tier.isNotEmpty() } // 티어 정보가 없는 증강체 제외
    }
    
    companion object {
        private const val TAG = "AugmentRepository"
    }
}