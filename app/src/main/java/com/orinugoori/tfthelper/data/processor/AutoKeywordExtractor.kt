package com.orinugoori.tfthelper.data.processor

import com.orinugoori.tfthelper.data.constants.AugmentKeywords

/**
 * 증강 설명에서 자동으로 키워드를 추출하는 클래스
 * 하드코딩된 키워드 매핑을 줄이고 자동화를 통해 유지보수성 향상
 */
object AutoKeywordExtractor {
    
    private val keywordPatterns = mapOf(
        // 돈 관련
        "골드" to AugmentKeywords.MONEY,
        "경험치" to AugmentKeywords.MONEY,
        "새로고침" to AugmentKeywords.MONEY,
        "이자" to AugmentKeywords.MONEY,
        
        // 아이템 관련
        "아이템" to AugmentKeywords.ITEM,
        "조합" to AugmentKeywords.ITEM,
        "모루" to AugmentKeywords.ITEM,
        "상징" to AugmentKeywords.SYMBOL,
        "재조합기" to AugmentKeywords.RECOMBINATOR,
        "자석제거기" to AugmentKeywords.MAGNET_REMOVER,
        
        // 전투 관련
        "공격" to AugmentKeywords.BATTLE,
        "피해" to AugmentKeywords.BATTLE,
        "체력" to AugmentKeywords.BATTLE,
        "방어력" to AugmentKeywords.BATTLE,
        "마법 저항력" to AugmentKeywords.BATTLE,
        "보호막" to AugmentKeywords.BATTLE,
        
        // 챔피언 관련
        "챔피언" to AugmentKeywords.CHAMPION,
        "유닛" to AugmentKeywords.CHAMPION,
        "단계" to AugmentKeywords.CHAMPION,
        
        // 체력 관련
        "플레이어 체력" to AugmentKeywords.HEALTH,
        "최대 플레이어 체력" to AugmentKeywords.HEALTH,
        
        // 특성 관련
        "특성" to AugmentKeywords.TRAIT,
        
        // 영웅 특별 케이스
        "이렐리아" to AugmentKeywords.HERO,
        "신지드" to AugmentKeywords.HERO,
        "스텝" to AugmentKeywords.HERO,
        "트런들" to AugmentKeywords.HERO,
        "밴더" to AugmentKeywords.HERO,
        "블라디미르" to AugmentKeywords.HERO,
        
        // 전리품 관련
        "전리품" to AugmentKeywords.RANDOM_REWARD,
        "확률" to AugmentKeywords.RANDOM_REWARD,
        "주사위" to AugmentKeywords.RANDOM_REWARD,
        
        // 연승 관련
        "처치" to AugmentKeywords.WINNING_STREAK,
        "승리" to AugmentKeywords.WINNING_STREAK,
        "적 전략가" to AugmentKeywords.WINNING_STREAK
    )
    
    /**
     * 설명에서 키워드를 자동 추출
     */
    fun extractKeywords(description: String): List<String> {
        val extractedKeywords = mutableSetOf<String>()
        
        keywordPatterns.forEach { (pattern, keyword) ->
            if (description.contains(pattern, ignoreCase = true)) {
                extractedKeywords.add(keyword)
            }
        }
        
        // 특별 규칙들
        when {
            description.contains("승리") && description.contains("골드") -> {
                extractedKeywords.add(AugmentKeywords.WINNING_STREAK)
            }
            description.contains("탈락") -> {
                extractedKeywords.add(AugmentKeywords.MONEY)
            }
            description.contains("훈련 봇") -> {
                extractedKeywords.add(AugmentKeywords.ETC)
            }
        }
        
        // 기본값: 키워드가 없으면 기타로 분류
        if (extractedKeywords.isEmpty()) {
            extractedKeywords.add(AugmentKeywords.ETC)
        }
        
        return extractedKeywords.toList()
    }
    
    /**
     * 증강 이름에 따른 특별 키워드 매핑
     */
    fun getSpecialKeywords(augmentName: String): List<String> {
        return when {
            augmentName.contains("문장") -> listOf(AugmentKeywords.SYMBOL)
            augmentName.contains("왕관") -> listOf(AugmentKeywords.SYMBOL, AugmentKeywords.ITEM)
            augmentName.contains("특성:") -> listOf(AugmentKeywords.TRAIT)
            augmentName.contains("전리품") -> listOf(AugmentKeywords.RANDOM_REWARD)
            augmentName.contains("복제") -> listOf(AugmentKeywords.ITEM)
            augmentName.contains("주사위") -> listOf(AugmentKeywords.RANDOM_REWARD)
            else -> emptyList()
        }
    }
}
