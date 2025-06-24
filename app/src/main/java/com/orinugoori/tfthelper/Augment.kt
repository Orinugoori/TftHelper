package com.orinugoori.tfthelper

import android.util.Log

data class AugmentResponse(
    val data: Map<String, Augment>
)

data class Augment(
    val id: String,
    val tier : String = "",
    val name: String,
    val image: ImageInfo,
    val description : String = "",
    val keyword : List<String> = emptyList()
)

data class ImageInfo(
    val full: String
)

/**
 * API에서 받아온 설명 텍스트를 정리하는 함수
 * HTML 태그, 플레이스홀더 등을 제거
 */
fun cleanApiDescription(description: String): String {
    if (description.isBlank()) return "설명이 없습니다."
    
    var cleaned = description
    
    // HTML 태그 제거
    cleaned = cleaned.replace(Regex("<[^>]*>"), "")
    
    // 플레이스홀더 패턴 제거 (예: <placeholder>%, @AttackSpeed@% 등)
    cleaned = cleaned.replace(Regex("<placeholder>"), "")
    cleaned = cleaned.replace(Regex("@[^@]*@"), "")
    cleaned = cleaned.replace(Regex("%[^%]*%"), "")
    
    // 연속된 공백을 하나로 통합
    cleaned = cleaned.replace(Regex("\\s+"), " ")
    
    // 앞뒤 공백 제거
    cleaned = cleaned.trim()
    
    return if (cleaned.isBlank()) "설명이 없습니다." else cleaned
}

/**
 * 증강 이름을 정규화하는 함수 (공백, 특수문자 정리)
 */
fun normalizeAugmentName(name: String): String {
    return name.trim()
        .replace(Regex("\\s+"), " ")
        .replace("'", "'")
        .replace("'", "'")
        .replace(""", "\"")
        .replace(""", "\"")
}

/**
 * 증강 ID를 기반으로 티어를 추론하는 함수
 * TFT API의 ID 패턴을 분석하여 티어를 결정
 */
fun inferTierFromId(id: String): String {
    return when {
        id.contains("silver", ignoreCase = true) || id.contains("_1_", ignoreCase = true) -> "실버"
        id.contains("gold", ignoreCase = true) || id.contains("_2_", ignoreCase = true) -> "골드"
        id.contains("prismatic", ignoreCase = true) || id.contains("_3_", ignoreCase = true) -> "프리즘"
        id.contains("_I") -> "실버"
        id.contains("_II") -> "골드"
        id.contains("_III") -> "프리즘"
        else -> "실버" // 기본값
    }
}

/**
 * 개선된 증강 처리 함수 - API 데이터와 하드코딩 데이터를 모두 활용
 */
fun processAugments(rawAugments: List<Augment>): List<Augment> {
    val processedNames = mutableSetOf<String>()
    val unMatchedAugments = mutableListOf<String>()
    
    val filteredAugments = rawAugments.filter { augment ->
        val normalizedName = normalizeAugmentName(augment.name)
        val isNotDuplicate = !processedNames.contains(normalizedName)
        
        if (isNotDuplicate) {
            processedNames.add(normalizedName)
            true
        } else {
            false
        }
    }
    
    val processedAugments = filteredAugments.map { augment ->
        val normalizedName = normalizeAugmentName(augment.name)
        
        // 먼저 하드코딩된 데이터에서 확인
        val (tier, description, keyword) = when {
            silverAugments.containsKey(normalizedName) -> {
                val (desc, kw) = silverAugments[normalizedName] ?: Pair("설명이 없습니다.", emptyList())
                Triple("실버", desc, kw)
            }
            goldAugments.containsKey(normalizedName) -> {
                val (desc, kw) = goldAugments[normalizedName] ?: Pair("설명이 없습니다.", emptyList())
                Triple("골드", desc, kw)
            }
            prismAugments.containsKey(normalizedName) -> {
                val (desc, kw) = prismAugments[normalizedName] ?: Pair("설명이 없습니다.", emptyList())
                Triple("프리즘", desc, kw)
            }
            else -> {
                // API에서 온 새로운 데이터 처리
                unMatchedAugments.add(normalizedName)
                val inferredTier = inferTierFromId(augment.id)
                val cleanedDescription = cleanApiDescription(augment.description)
                val inferredKeywords = inferKeywordsFromDescription(cleanedDescription)
                Triple(inferredTier, cleanedDescription, inferredKeywords)
            }
        }
        
        augment.copy(
            name = normalizedName,
            tier = tier,
            description = description,
            keyword = keyword
        )
    }.sortedBy { it.name }
    
    Log.d("증강 디버깅", "매칭되지 않은 증강: $unMatchedAugments")
    Log.d("증강 디버깅", "총 처리된 증강 수: ${processedAugments.size}")
    
    return processedAugments
}

/**
 * 설명에서 키워드를 추론하는 함수
 */
fun inferKeywordsFromDescription(description: String): List<String> {
    val keywords = mutableListOf<String>()
    val lowerDesc = description.lowercase()
    
    when {
        lowerDesc.contains("골드") || lowerDesc.contains("gold") -> keywords.add("돈")
        lowerDesc.contains("아이템") || lowerDesc.contains("item") -> keywords.add("아이템")
        lowerDesc.contains("챔피언") || lowerDesc.contains("champion") -> keywords.add("챔피언")
        lowerDesc.contains("공격") || lowerDesc.contains("피해") || lowerDesc.contains("damage") -> keywords.add("전투")
        lowerDesc.contains("체력") || lowerDesc.contains("health") -> keywords.add("전투")
        lowerDesc.contains("상징") || lowerDesc.contains("emblem") -> keywords.add("상징")
    }
    
    return keywords
}

val silverAugments = mapOf(
    "가지 뻗기" to Pair("무작위 상징과 재조합기를 획득합니다.", listOf("상징", "재조합기")),
    "거대한 거인" to Pair("현재 및 최대 플레이어 체력이 20 증가합니다. 공동 선택 라운드 시 일찍 움직일 수 있지만 이동 속도가 큰 폭으로 감소합니다.", listOf("체력")),
    "검무" to Pair("이렐리아를 획득합니다. 가장 강한 아군 이렐리아가 공격 속도를 40% 얻고 두 대상 사이로 돌진해 두 대상 모두에게 물리 피해를 입히는 새로운 스킬을 얻습니다.", listOf("영웅")),
    "결과 예측" to Pair("연승을 +4연승으로 설정합니다. 4골드를 획득합니다.", listOf("돈", "기타")),
    "고독한 영웅" to Pair("마지막으로 생존한 아군 유닛이 공격 속도를 140%, 내구력을 35% 얻습니다.", listOf("전투")),
    "과적" to Pair("다음 스테이지는 대기석이 1개가 됩니다. 그 다음, 조합 아이템 3개를 획득합니다.", listOf("아이템")),
    "국왕시해자" to Pair("플레이어 대상 전투 승리 후 1골드를 획득합니다. 상대 플레이어의 체력이 더 높았다면, 대신 4골드를 획득합니다. 즉시 1골드를 획득합니다.", listOf("돈")),
    "급매" to Pair("라운드마다 상점에서 무작위 챔피언을 1명 훔칩니다. 1골드를 획득합니다.", listOf("돈")),
    "끈끈한 우정" to Pair("하급 챔피언 복제기를 획득합니다. 플레이어 대상 전투를 7회 치른 후 다시 획득합니다.", listOf("기타")),
    "눈에는 눈" to Pair("아군 챔피언이 15명 사망할 때마다 무작위 조합 아이템 1개를 획득합니다. (최대 4개)", listOf("아이템")),
    "눈에는 눈+" to Pair("무작위 조합 아이템 1개를 획득합니다. 아군 챔피언이 13명 사망할 때마다 조합 아이템을 추가로 1개 획득합니다. (최대 3개)", listOf("아이템")),
    "다 쓸 데가 있다니까 I" to Pair("아이템을 보유하지 않은 챔피언이 죽을 때 50%의 확률로 1골드를 떨어뜨립니다.", listOf("돈")),
    "대격변 생성기" to Pair("전장에 있는 챔피언들이 단계가 1 높은 무작위 챔피언으로 영구히 바뀝니다. 자석제거기 2개를 획득합니다.", listOf("기타", "자석제거기")),
    "덩치 큰 친구들 I" to Pair("정확히 한 명의 다른 아군 옆에서 전투를 시작한 아군이 체력을 100 얻습니다. 한 챔피언이 사망하면 다른 한 명이 10초 동안 최대 체력의 10%에 해당하는 보호막을 얻습니다.", listOf("전투")),
    "도둑 무리 I" to Pair("도적의 장갑 1개를 획득합니다.", listOf("아이템")),
    "마나순환 I" to Pair("후방 가로 1열에서 전투를 시작하는 아군 유닛이 기본 공격마다 추가 마나를 2 얻습니다.", listOf("전투")),
    "맹렬한 공세" to Pair("아군의 기본 공격이 대상을 태워 5초 동안 대상 최대 체력의 5%에 해당하는 피해를 입힙니다. 또한 기본 공격 시 받는 치유 효과를 33% 감소시킵니다.", listOf("전투")),
    "모두를 위한 하나 I" to Pair("아군이 전장에 있는 고유 1단계 챔피언 한 명당 최대 체력을 2%, 피해 증폭을 150% 얻습니다. 1단계 챔피언 2명을 획득합니다.", listOf("전투")),
    "미친 화학자" to Pair("신지드를 획득합니다. 가장 강한 아군 신지드가 기본 공격을 할 수 없게 되지만 지속적으로 뛰어다니며 맹독의 자취를 남겨 지속 마법 피해를 입힙니다.", listOf("영웅")),
    "부스러기" to Pair("각 공동 선택 이후 선택되지 않은 유닛 1명과 해당 유닛이 보유한 아이템을 획득합니다. 1골드를 획득합니다.", listOf("아이템", "기타")),
    "부식" to Pair("전방 가로 2열에 있는 적 챔피언들의 방어력 및 마법 저항력이 2초마다 3 감소합니다.", listOf("전투")),
    "새로고침 이월" to Pair("사용하지 않은 증강 새로고침 1회마다 무료 상점 새로고침을 3회 얻습니다. 3골드를 획득합니다.", listOf("돈")),
    "새로고침의 날 I" to Pair("무료 상점 새로고침을 11회 얻습니다.", listOf("돈")),
    "생존자" to Pair("플레이어 3명이 탈락하면 60골드를 획득합니다.", listOf("돈")),
    "서열 상승 I" to Pair("아군이 사망할 때마다 특성을 한 개 이상 공유하는 아군이 주문력을 3, 공격력을 3%, 방어력을 3, 마법 저항력을 3 얻습니다.", listOf("전투")),
    "수호자의 친구" to Pair("즉시 무작위 2단계 챔피언을 획득합니다. 레벨을 올릴 때마다 동일한 챔피언을 획득합니다.", listOf("챔피언", "전투")),
    "슈퍼스타 I" to Pair("아군의 피해량이 5% 증가합니다. 이 효과는 아군 3성 유닛 하나당 2% 증가합니다. 새로고침을 2회 얻습니다.", listOf("전투")),
    "스승 I" to Pair("더 높은 단계의 아군 옆에서 전투를 시작한 아군이 공격 속도를 12%, 체력을 150 얻습니다.", listOf("전투")),
    "신중한 제작" to Pair("플레이어 대상 전투를 8번 치른 후 유물 모루를 획득합니다. 모루가 4가지 선택지를 제시합니다.", listOf("아이템")),
    "아이템 꾸러미 I" to Pair("무작위 완성 아이템 1개를 획득합니다.", listOf("아이템")),
    "아이템 수집가 I" to Pair("아군이 체력을 10 얻습니다. 장착한 아이템 한 종류당 아군이 체력을 2, 공격력을 1, 주문력을 1 추가로 얻습니다.", listOf("전투")),
    "아이템 숙성" to Pair("완성 아이템을 4라운드 동안 대기석에 두면 지원 아이템 모루로 변합니다.", listOf("아이템")),
    "연결 불가" to Pair("1단계 챔피언의 복사본을 1명씩 획득합니다.", listOf("챔피언")),
    "위력 강화" to Pair("다음 증강이 한 단계 높아집니다.", listOf("기타")),
    "위약 효과" to Pair("8골드를 획득합니다. 아군의 공격 속도가 1% 증가합니다.", listOf("돈", "전투")),
    "위약 효과+" to Pair("15골드를 획득합니다. 아군의 공격 속도가 1% 증가합니다.", listOf("돈", "전투")),
    "위험한 행보" to Pair("아군 전략가가 체력을 20 잃지만, 플레이어 대상 전투를 7번 치른 후 30골드를 획득합니다.", listOf("돈")),
    "유리 대포 I" to Pair("후방 가로 1열에서 전투를 시작한 유닛이 전투 시작 시 체력이 80%로 조정되지만 피해 증폭을 12% 얻습니다.", listOf("전투")),
    "유용한 금속" to Pair("조합 아이템 모루와 4골드를 획득합니다. 모루가 4가지 선택지를 제시합니다.", listOf("아이템", "돈")),
    "은수저" to Pair("10의 경험치를 획득합니다.", listOf("돈")),
    "의무병" to Pair("스텝을 획득합니다. 가장 강한 아군 스텝의 스킬 마나 소모량이 10 감소하지만, 더 이상 회복할 수 없습니다.", listOf("영웅"))
)

val goldAugments = mapOf(
    "2만한 가치" to Pair("지난 전투에 배치된 고유 2단계 챔피언 2명당 새로고침을 1회 얻습니다. 2단계 유닛을 2명 획득합니다.", listOf("돈", "챔피언")),
    "2인조" to Pair("무작위 5단계 챔피언 2명과 무작위 조합 아이템 1개의 복사본 2개를 획득합니다.", listOf("아이템", "챔피언")),
    "4단계 한 쌍" to Pair("아군에 4단계 챔피언이 정확히 2명 있다면 각 4단계 챔피언이 체력을 404, 공격 속도를 24.4 얻습니다.", listOf("전투", "챔피언")),
    "4후지원" to Pair("다음에 구매하는 4단계 챔피언이 즉시 2성으로 업그레이드됩니다. 12골드를 획득합니다.", listOf("돈")),
    "5등급" to Pair("루난의 허리케인 1개를 획득합니다. 루난의 허리케인이 탄환을 추가로 1개 발사합니다.", listOf("전투", "아이템")),
    "가시 박힌 갑옷" to Pair("덤불 조끼 1개를 획득합니다. 아군의 덤불 조끼가 피해를 더 입히고 체력을 회복시킵니다.", listOf("아이템", "전투")),
    "가족 문장" to Pair("가족 상징과 바이올렛을 획득합니다.", listOf("상징", "챔피언")),
    "감시자 문장" to Pair("감시자 상징과 렐을 획득합니다.", listOf("상징", "챔피언")),
    "강력한 보호막" to Pair("보호막이 씌워진 동안 아군 유닛이 내구력을 12% 얻습니다.", listOf("전투")),
    "검은 장미단 문장" to Pair("검은 장미단 상징을 획득합니다.", listOf("상징")),
    "경쟁" to Pair("각 플레이어 대상 전투 이후, 다른 플레이어 2명당 2골드를 획득합니다.", listOf("돈")),
    "고물상 문장" to Pair("고물상 상징과 직스를 획득합니다.", listOf("상징", "챔피언")),
    "고전압" to Pair("이온 충격기 1개를 획득합니다. 이온 충격기의 효과 반경이 증가하고 피해를 더 입힙니다.", listOf("아이템", "전투")),
    "골렘화" to Pair("전장 및 대기석에 있는 모든 챔피언을 잃습니다. 골렘을 획득합니다.", listOf("전투")),
    "공중전" to Pair("점화단 챔피언이 점화단 돌진을 시작할 때 공격력과 주문력을 얻습니다.", listOf("특성 전용", "전투", "챔피언")),
    "공허소환사" to Pair("전투 중 선도자가 마나를 사용할 때마다 공허충을 소환합니다.", listOf("특성 전용", "전투", "챔피언")),
    "과다치유" to Pair("세 번째 기본 공격마다 추가 피해를 입히고 체력을 회복합니다.", listOf("전투")),
    "교환의 장" to Pair("라운드마다 무료 상점 새로고침을 획득합니다. 1골드를 획득합니다.", listOf("돈")),
    "균열 수정" to Pair("자동기계 챔피언이 에너지를 발사할 때 추가 에너지를 발사합니다.", listOf("특성 전용", "전투", "챔피언")),
    "근위대" to Pair("크라운가드를 획득합니다. 크라운가드의 전투 시작 효과가 증가합니다.", listOf("아이템", "전투"))
)

val prismAugments = mapOf(
    "가족 왕관" to Pair("가족 상징, 구원, 밴더, 바이올렛을 획득합니다.", listOf("상징", "아이템", "챔피언")),
    "간이 대장간" to Pair("지금 그리고 플레이어 대상 전투 9번마다 유물 모루를 획득합니다.", listOf("아이템")),
    "감시자 왕관" to Pair("감시자 상징, 크라운가드, 로리스를 획득합니다.", listOf("상징", "아이템", "챔피언")),
    "검은 장미단 왕관" to Pair("검은 장미단 상징, 모렐로노미콘, 카시오페아를 획득합니다.", listOf("상징", "아이템", "챔피언")),
    "계산된 강화" to Pair("전투마다 후방 2열에 있는 무작위 챔피언 4명이 공격력과 주문력을 얻습니다.", listOf("전투")),
    "고귀한 모험" to Pair("2단계 챔피언을 3명 획득합니다. 두 명을 3성으로 업그레이드하면 전리품 구를 획득합니다.", listOf("랜덤 보상")),
    "고물상 왕관" to Pair("고물상 상징, 직스, 무작위 조합 아이템 2개를 획득합니다.", listOf("상징", "아이템", "챔피언")),
    "공허의 무리" to Pair("즈롯 차원문 1개를 획득하고 플레이어 대상 전투 11회마다 추가로 1개를 획득합니다.", listOf("아이템")),
    "구독 서비스" to Pair("즉시 및 각 스테이지 시작 시 서로 다른 4단계 챔피언 4명으로 구성된 상점을 열고 6골드를 획득합니다.", listOf("돈", "기타")),
    "극진한 헌신" to Pair("무작위 상징 1개를 획득합니다. 즉시 및 각 스테이지 시작 시 해당 특성 챔피언을 획득합니다.", listOf("상징", "챔피언")),
    "기다림의 미학 II" to Pair("무작위 2단계 챔피언 1명을 획득합니다. 게임이 끝날 때까지 해당 유닛의 복사본을 각 라운드 시작 시 획득합니다.", listOf("챔피언", "기타")),
    "기동타격대 왕관" to Pair("기동타격대 상징, 구인수의 격노검, 녹턴을 획득합니다.", listOf("상징", "챔피언", "아이템")),
    "꼬꼬마 거인" to Pair("플레이어 대상 전투가 끝날 때마다 2의 플레이어 체력과 1골드를 얻습니다.", listOf("돈", "체력")),
    "꼬꼬마 거인+" to Pair("플레이어 대상 전투가 끝날 때마다 2의 플레이어 체력과 1골드를 얻습니다. 즉시 15골드를 획득합니다.", listOf("돈", "체력")),
    "꿰뚫는 연꽃 II" to Pair("아군이 치명타 확률을 20 얻으며, 스킬에 치명타가 적용될 수 있습니다.", listOf("전투")),
    "난동꾼 왕관" to Pair("난동꾼 상징, 구원, 세트를 획득합니다.", listOf("상징", "아이템", "챔피언")),
    "넘치는 대검" to Pair("B.F. 대검 5개를 획득합니다. B.F. 대검이 공격 속도를 추가로 제공합니다.", listOf("아이템", "전투")),
    "넘치는 지팡이" to Pair("쓸데없이 큰 지팡이 5개를 획득합니다. 쓸데없이 큰 지팡이가 공격 속도를 추가로 제공합니다.", listOf("아이템", "전투")),
    "넘치는 허리띠" to Pair("거인의 허리띠 4개를 획득합니다. 거인의 허리띠가 추가 체력을 더 제공합니다.", listOf("아이템", "전투")),
    "다른 태생 II" to Pair("활성화된 특성이 없는 아군 유닛이 체력과 공격 속도를 얻습니다. (현재 스테이지에 비례)", listOf("전투"))
)
