package com.orinugoori.tfthelper.domain.model

/**
 * 도메인 레이어의 증강체 모델
 * UI와 데이터 레이어 사이의 비즈니스 객체
 */
data class Augment(
    val id: String,
    val name: String,
    val tier: String,
    val description: String,
    val imageUrl: String,
    val keywords: List<String> = emptyList()
) {
    /**
     * 증강체가 검색 쿼리와 매치되는지 확인
     */
    fun matchesSearch(query: String): Boolean {
        val lowerQuery = query.lowercase()
        return name.lowercase().contains(lowerQuery) ||
               description.lowercase().contains(lowerQuery) ||
               keywords.any { it.lowercase().contains(lowerQuery) }
    }
    
    /**
     * 증강체의 티어를 기반으로 한 우선순위 반환
     */
    val tierPriority: Int
        get() = when (tier) {
            "실버" -> 1
            "골드" -> 2
            "프리즘" -> 3
            else -> 0
        }
}

/**
 * 증강체 확률 정보
 */
data class AugmentProbability(
    val augmentName: String,
    val probability: Int,
    val tier: String
)

/**
 * 증강체 티어 정보
 */
enum class AugmentTier(val displayName: String, val priority: Int) {
    SILVER("실버", 1),
    GOLD("골드", 2),
    PRISM("프리즘", 3);
    
    companion object {
        fun fromString(tier: String): AugmentTier? {
            return values().find { it.displayName == tier }
        }
    }
}