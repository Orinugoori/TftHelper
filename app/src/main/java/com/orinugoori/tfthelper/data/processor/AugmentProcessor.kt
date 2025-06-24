package com.orinugoori.tfthelper.data.processor

import android.util.Log
import com.orinugoori.tfthelper.Augment

object AugmentProcessor {
    
    /**
     * 증강 데이터 처리 및 키워드 추출
     */
    fun processAugments(rawAugments: List<Augment>): List<Augment> {
        return rawAugments.map { augment ->
            val tier = extractTier(augment)
            val keywords = extractKeywords(augment)
            
            augment.copy(
                tier = tier,
                keyword = keywords
            )
        }.also {
            Log.d("AugmentProcessor", "증강 데이터 처리 완료: ${it.size}개")
        }
    }

    /**
     * 증강 티어 추출
     * ID나 이름 패턴을 통해 티어를 판단
     */
    private fun extractTier(augment: Augment): String {
        val id = augment.id.lowercase()
        val name = augment.name
        
        return when {
            // 프리즘 티어 패턴
            id.contains("prismatic") || 
            id.contains("_3_") ||
            name.contains("III") -> "프리즘"
            
            // 골드 티어 패턴  
            id.contains("gold") ||
            id.contains("_2_") ||
            name.contains("II") -> "골드"
            
            // 실버 티어 패턴
            id.contains("silver") ||
            id.contains("_1_") ||
            name.contains("I") -> "실버"
            
            // 기본값
            else -> "실버"
        }
    }

    /**
     * 증강 키워드 추출
     * 설명에서 주요 키워드를 추출하여 분류
     */
    private fun extractKeywords(augment: Augment): List<String> {
        val keywords = mutableSetOf<String>()
        val description = augment.description.lowercase()
        val name = augment.name.lowercase()
        
        // 챔피언 관련
        if (description.contains("챔피언") || description.contains("champion")) {
            keywords.add("챔피언")
        }
        
        // 아이템 관련
        if (description.contains("아이템") || description.contains("item") || 
            description.contains("장비") || description.contains("무기")) {
            keywords.add("아이템")
        }
        
        // 골드 관련
        if (description.contains("골드") || description.contains("gold") ||
            description.contains("돈") || description.contains("재화")) {
            keywords.add("골드")
        }
        
        // 체력 관련
        if (description.contains("체력") || description.contains("hp") ||
            description.contains("생명력") || description.contains("health")) {
            keywords.add("체력")
        }
        
        // 공격력 관련
        if (description.contains("공격력") || description.contains("데미지") ||
            description.contains("damage") || description.contains("ad")) {
            keywords.add("공격력")
        }
        
        // 방어력 관련
        if (description.contains("방어력") || description.contains("armor") ||
            description.contains("저항력") || description.contains("mr")) {
            keywords.add("방어력")
        }
        
        // 마나 관련
        if (description.contains("마나") || description.contains("mana") ||
            description.contains("스킬") || description.contains("skill")) {
            keywords.add("마나")
        }
        
        // 공격속도 관련
        if (description.contains("공격 속도") || description.contains("attack speed") ||
            description.contains("공속") || description.contains("as")) {
            keywords.add("공격속도")
        }
        
        // 치명타 관련
        if (description.contains("치명타") || description.contains("critical") ||
            description.contains("크리티컬") || description.contains("crit")) {
            keywords.add("치명타")
        }
        
        // 시너지 관련
        if (description.contains("시너지") || description.contains("synergy") ||
            description.contains("특성") || description.contains("trait")) {
            keywords.add("시너지")
        }
        
        // 이동 관련
        if (description.contains("이동") || description.contains("move") ||
            description.contains("위치") || description.contains("position")) {
            keywords.add("이동")
        }
        
        // 경험치 관련
        if (description.contains("경험치") || description.contains("exp") ||
            description.contains("레벨") || description.contains("level")) {
            keywords.add("경험치")
        }
        
        // 힐링 관련
        if (description.contains("회복") || description.contains("heal") ||
            description.contains("치유") || description.contains("재생")) {
            keywords.add("회복")
        }
        
        // 실드 관련
        if (description.contains("보호막") || description.contains("shield") ||
            description.contains("실드")) {
            keywords.add("보호막")
        }
        
        // 속도 관련
        if (description.contains("속도") || description.contains("speed") ||
            description.contains("빠른") || description.contains("fast")) {
            keywords.add("속도")
        }
        
        // 확률 관련
        if (description.contains("확률") || description.contains("chance") ||
            description.contains("랜덤") || description.contains("random")) {
            keywords.add("확률")
        }
        
        // 범위 관련
        if (description.contains("범위") || description.contains("range") ||
            description.contains("aoe") || description.contains("광역")) {
            keywords.add("범위")
        }
        
        // 디버프 관련
        if (description.contains("감소") || description.contains("debuff") ||
            description.contains("약화") || description.contains("독")) {
            keywords.add("디버프")
        }
        
        // 버프 관련
        if (description.contains("증가") || description.contains("buff") ||
            description.contains("강화") || description.contains("향상")) {
            keywords.add("버프")
        }
        
        // 기본 키워드가 없다면 "기타" 추가
        if (keywords.isEmpty()) {
            keywords.add("기타")
        }
        
        return keywords.toList()
    }
}