package com.orinugoori.tfthelper.domain.usecase

import com.orinugoori.tfthelper.domain.model.Augment
import com.orinugoori.tfthelper.domain.repository.AugmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 증강체 검색 기능을 담당하는 UseCase
 * 비즈니스 로직을 캡슐화하여 재사용성과 테스트 용이성을 높임
 */
class AugmentSearchUseCase(
    private val repository: AugmentRepository
) {
    
    /**
     * 증강체를 검색하는 함수
     * @param query 검색 쿼리 (증강체 이름, 설명, 키워드 기반)
     * @param tierFilter 티어 필터 (null이면 전체 검색)
     * @return 검색 결과 Flow
     */
    operator fun invoke(
        query: String,
        tierFilter: String? = null
    ): Flow<List<Augment>> {
        return if (query.isBlank()) {
            // 검색어가 없으면 티어 필터에 따라 전체 또는 특정 티어 반환
            tierFilter?.let { 
                repository.getAugmentsByTier(it) 
            } ?: repository.getAllAugments()
        } else {
            // 검색어가 있으면 검색 수행 후 티어 필터 적용
            repository.searchAugments(query).map { augments ->
                tierFilter?.let { tier ->
                    augments.filter { it.tier == tier }
                } ?: augments
            }
        }
    }
    
    /**
     * 초성 검색을 수행하는 함수
     * @param chosung 초성 검색어
     * @param tierFilter 티어 필터
     * @return 초성 검색 결과 Flow
     */
    fun searchByChosung(
        chosung: String,
        tierFilter: String? = null
    ): Flow<List<Augment>> {
        return repository.getAllAugments().map { augments ->
            val filtered = augments.filter { augment ->
                augment.name.getChosung().contains(chosung, ignoreCase = true)
            }
            
            tierFilter?.let { tier ->
                filtered.filter { it.tier == tier }
            } ?: filtered
        }
    }
}

/**
 * 한글 초성을 추출하는 확장 함수 (임시)
 * 실제로는 StringExtensions에서 가져와야 함
 */
private fun String.getChosung(): String {
    val chosungList = charArrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
        'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )
    
    val result = StringBuilder()
    
    for (char in this) {
        if (char in '가'..'힣') {
            val index = (char.code - '가'.code) / 588
            result.append(chosungList[index])
        } else {
            result.append(char)
        }
    }
    
    return result.toString()
}