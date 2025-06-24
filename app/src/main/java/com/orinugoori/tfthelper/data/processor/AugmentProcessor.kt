package com.orinugoori.tfthelper.data.processor

import android.util.Log
import com.orinugoori.tfthelper.Augment
import com.orinugoori.tfthelper.data.constants.AugmentKeywords

/**
 * 증강 데이터 처리 클래스
 * 자동 키워드 추출과 수동 매핑을 결합하여 효율적으로 데이터 처리
 */
object AugmentProcessor {
    
    // 최소한의 수동 매핑 (자동 추출이 어려운 특별한 케이스들만)
    private val manualKeywordMapping = mapOf(
        // 특성 전용 증강들 (자동 추출하기 어려운 것들)
        "격전의 지하도시" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.HEALTH, AugmentKeywords.MONEY),
        "고물 더미 꼭대기" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.ITEM),
        "공중전" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.BATTLE),
        "공허소환사" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.BATTLE),
        "균열 수정" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.BATTLE),
        "금단의 마법" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.BATTLE),
        "녹서스의 단두대" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.BATTLE),
        "놀이터에 온 걸 환영해" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.RANDOM_REWARD),
        "돌연변이 발현" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.BATTLE),
        "로켓 컬렉션" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.ITEM, AugmentKeywords.BATTLE),
        "로켓 컬렉션+" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.ITEM, AugmentKeywords.BATTLE),
        "보호막 강타" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.BATTLE),
        "아드레날린 폭발" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.BATTLE),
        "아케인의 응징" to listOf(AugmentKeywords.TRAIT_ONLY),
        "육중한 강타" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.BATTLE),
        "이중 형태" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.BATTLE),
        "전리품 폭발" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.RANDOM_REWARD),
        "저격수의 은신처" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.BATTLE),
        "지배" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.BATTLE),
        "파랗게 물들여라" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.BATTLE),
        "피나는 훈련" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.BATTLE),
        "학술 연구" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.ITEM),
        "법 집행" to listOf(AugmentKeywords.TRAIT_ONLY, AugmentKeywords.BATTLE, AugmentKeywords.MONEY),
        
        // 영웅 증강들
        "검무" to listOf(AugmentKeywords.HERO),
        "미친 화학자" to listOf(AugmentKeywords.HERO),
        "의무병" to listOf(AugmentKeywords.HERO),
        "트롤 나가신다" to listOf(AugmentKeywords.HERO),
        "잔혹한 복수" to listOf(AugmentKeywords.HERO),
        "진심 발휘" to listOf(AugmentKeywords.HERO),
        "핏빛 계약" to listOf(AugmentKeywords.HERO),
        
        // 특별한 특성 증강들
        "특성: 계엄령" to listOf(AugmentKeywords.TRAIT, AugmentKeywords.BATTLE),
        "특성: 뜻밖의 2인조" to listOf(AugmentKeywords.TRAIT, AugmentKeywords.BATTLE),
        "특성: 모략" to listOf(AugmentKeywords.TRAIT, AugmentKeywords.BATTLE),
        "특성: 배신" to listOf(AugmentKeywords.TRAIT, AugmentKeywords.BATTLE),
        "특성: 자매" to listOf(AugmentKeywords.TRAIT, AugmentKeywords.BATTLE),
        "특성: 재회" to listOf(AugmentKeywords.TRAIT, AugmentKeywords.BATTLE),
        "특성: 또 다른 결말" to listOf(AugmentKeywords.TRAIT, AugmentKeywords.BATTLE),
        "특성: 천재" to listOf(AugmentKeywords.TRAIT, AugmentKeywords.BATTLE)
    )
    
    /**
     * 티어별 데이터 매핑
     */
    private val tierMapping = mapOf(
        "실버" to "실버",
        "골드" to "골드", 
        "프리즘" to "프리즘"
    )
    
    /**
     * 원본 증강 리스트를 처리하여 키워드와 설명이 추가된 리스트 반환
     */
    fun processAugments(rawAugments: List<Augment>): List<Augment> {
        val unMatchedAugments = mutableListOf<String>()
        val processedNames = mutableSetOf<String>()
        
        // 중복 제거 및 기본 필터링
        val filteredAugments = rawAugments.filter { augment ->
            val safeName = augment.name
            val isNotDuplicate = !processedNames.contains(safeName)
            
            if (isNotDuplicate) {
                processedNames.add(safeName)
                true
            } else {
                false
            }
        }
        
        // 각 증강에 대해 키워드 및 티어 정보 추가
        val processedAugments = filteredAugments.map { augment ->
            processAugment(augment, unMatchedAugments)
        }.sortedBy { it.name }
        
        Log.d("AugmentProcessor", "처리 완료: ${processedAugments.size}개")
        Log.d("AugmentProcessor", "미매칭: $unMatchedAugments")
        
        return processedAugments
    }
    
    /**
     * 개별 증강 처리
     */
    private fun processAugment(augment: Augment, unMatchedAugments: MutableList<String>): Augment {
        val safeName = augment.name
        
        // 1. 수동 매핑된 키워드 확인
        val manualKeywords = manualKeywordMapping[safeName]
        
        // 2. 자동 키워드 추출
        val autoKeywords = AutoKeywordExtractor.extractKeywords(augment.description)
        val specialKeywords = AutoKeywordExtractor.getSpecialKeywords(safeName)
        
        // 3. 키워드 병합 (수동 > 특별 > 자동 순서로 우선순위)
        val finalKeywords = when {
            manualKeywords != null -> manualKeywords
            specialKeywords.isNotEmpty() -> specialKeywords + autoKeywords
            else -> autoKeywords
        }.distinct()
        
        // 4. 티어 결정 (원본 API에서 제공하거나 추론)
        val tier = determineTier(augment, safeName)
        
        // 5. 키워드가 비어있으면 미매칭 리스트에 추가
        if (finalKeywords.isEmpty()) {
            unMatchedAugments.add(safeName)
        }
        
        return augment.copy(
            tier = tier,
            keyword = finalKeywords
        )
    }
    
    /**
     * 증강의 티어 결정
     */
    private fun determineTier(augment: Augment, name: String): String {
        // API에서 제공하는 티어 정보가 있으면 사용
        if (augment.tier.isNotEmpty() && tierMapping.containsKey(augment.tier)) {
            return tierMapping[augment.tier] ?: "기타"
        }
        
        // 이름 패턴으로 티어 추론
        return when {
            name.contains("문장") -> "골드"
            name.contains("왕관") -> "프리즘"
            name.contains("특성:") -> "골드"
            name.contains("+") -> "실버" // 일반적으로 + 버전은 실버
            name.contains("I") || name.contains("II") || name.contains("III") -> {
                when {
                    name.contains("III") -> "프리즘"
                    name.contains("II") -> "골드"
                    else -> "실버"
                }
            }
            else -> "실버" // 기본값
        }
    }
}
