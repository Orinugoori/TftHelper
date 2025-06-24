package com.orinugoori.tfthelper.data.constants

/**
 * TFT 증강 키워드 상수 정의
 * 증강 데이터에서 사용되는 모든 키워드를 중앙집권적으로 관리
 */
object AugmentKeywords {
    // 기본 키워드
    const val SYMBOL = "상징"
    const val RECOMBINATOR = "재조합기" 
    const val HEALTH = "체력"
    const val HERO = "영웅"
    const val MONEY = "돈"
    const val ETC = "기타"
    const val BATTLE = "전투"
    const val ITEM = "아이템"
    const val CHAMPION = "챔피언"
    const val WINNING_STREAK = "연승"
    const val RANDOM_REWARD = "랜덤 보상"
    const val TRAIT_ONLY = "특성 전용"
    const val MAGNET_REMOVER = "자석제거기"
    const val TRAIT = "특성"
    
    // 자주 사용되는 키워드 조합
    val MONEY_RELATED = listOf(MONEY, WINNING_STREAK)
    val ITEM_RELATED = listOf(ITEM, SYMBOL, RECOMBINATOR, MAGNET_REMOVER)
    val COMBAT_RELATED = listOf(BATTLE, HERO, CHAMPION)
    val REWARD_RELATED = listOf(RANDOM_REWARD, ETC)
    
    // 모든 키워드 리스트 (검증용)
    val ALL_KEYWORDS = listOf(
        SYMBOL, RECOMBINATOR, HEALTH, HERO, MONEY, ETC, BATTLE, 
        ITEM, CHAMPION, WINNING_STREAK, RANDOM_REWARD, TRAIT_ONLY, 
        MAGNET_REMOVER, TRAIT
    )
}
