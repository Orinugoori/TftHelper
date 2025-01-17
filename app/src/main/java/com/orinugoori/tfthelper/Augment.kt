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

fun processAugments(
    rawAugments: List<Augment>
): List<Augment> {

    val unMatchedAugments = mutableListOf <String>()
    val processedNames = mutableSetOf<String>()

    val filteredAugments = rawAugments.filter {augment ->
        val safeName = augment.name

        val isNotDuplicate = !processedNames.contains(safeName)
        val isTierMatched = silverAugments.containsKey(safeName) || goldAugments.containsKey(safeName) || prismAugments.containsKey(safeName)

        if(isNotDuplicate && isTierMatched){
            processedNames.add(safeName)
            true
        }else{
            false
        }
    }

    val processedAugments = filteredAugments.map { augment ->

        val safeName = augment.name

        // tier 및 설명 설정
        val tier  = when {
            silverAugments.containsKey(safeName) -> "실버"
            goldAugments.containsKey(safeName) -> "골드"
            prismAugments.containsKey(safeName) -> "프리즘"
            else -> augment.tier
        }

        val (description, keyword) = when {
            silverAugments.containsKey(safeName) -> silverAugments[safeName] ?: Pair("설명이 없습니다.", emptyList())
            goldAugments.containsKey(safeName) -> goldAugments[safeName] ?: Pair("설명이 없습니다.", emptyList())
            prismAugments.containsKey(safeName) -> prismAugments[safeName] ?: Pair("설명이 없습니다.", emptyList())
            else -> {
                unMatchedAugments.add(safeName)
                Pair("설명이 없습니다.", emptyList())
            }
        }


        // 기존 정보에 tier와 description 추가
        augment.copy(
            tier = tier,
            description = description,
            keyword = keyword
        )
    }.sortedBy { it.name }

    Log.d("증강 디버깅","$unMatchedAugments")

    return processedAugments
}



val silverAugments = mapOf(
    "가지 뻗기" to Pair ("무작위 상징과 재조합기를 획득합니다.",listOf("상징", "재조합기")),
    "거대한 거인" to Pair ("현재 및 최대 플레이어 체력이 20 증가합니다. 공동 선택 라운드 시 일찍 움직일 수 있지만 이동 속도가 큰 폭으로 감소합니다.",listOf("체력")),
    "검무" to Pair ("이렐리아를 획득합니다. 가장 강한 아군 이렐리아가 공격 속도를 40% 얻고 두 대상 사이로 돌진해 두 대상 모두에게 물리 피해를 입히는 새로운 스킬을 얻습니다.", listOf("영웅")),
    "결과 예측" to Pair ("연승을 +4연승으로 설정합니다. 4골드를 획득합니다.",listOf("돈","기타")),
    "고독한 영웅" to Pair ("마지막으로 생존한 아군 유닛이 공격 속도를 140%, 내구력을 35% 얻습니다.",listOf("전투")),
    "과적" to Pair ("다음 스테이지는 대기석이 1개가 됩니다. 그 다음, 조합 아이템 3개를 획득합니다.",listOf("아이템")),
    "국왕시해자" to Pair ("플레이어 대상 전투 승리 후 1골드를 획득합니다. 상대 플레이어의 체력이 더 높았다면, 대신 4골드를 획득합니다. 즉시 1골드를 획득합니다.",listOf("돈")),
    "급매" to Pair ("라운드마다 상점에서 무작위 챔피언을 1명 훔칩니다. 1골드를 획득합니다.",listOf("돈")),
    "끈끈한 우정" to Pair ("하급 챔피언 복제기를 획득합니다. 플레이어 대상 전투를 7회 치른 후 다시 획득합니다.",listOf("기타")),
    "눈에는 눈" to Pair ("아군 챔피언이 15명 사망할 때마다 무작위 조합 아이템 1개를 획득합니다. (최대 4개)", listOf("아이템")),
    "눈에는 눈+" to Pair ("무작위 조합 아이템 1개를 획득합니다. 아군 챔피언이 13명 사망할 때마다 조합 아이템을 추가로 1개 획득합니다. (최대 3개)",listOf("아이템")),
    "다 쓸 데가 있다니까 I" to Pair ("아이템을 보유하지 않은 챔피언이 죽을 때 50%의 확률로 1골드를 떨어뜨립니다.",listOf("돈")),
    "대격변 생성기" to Pair ("전장에 있는 챔피언들이 단계가 1 높은 무작위 챔피언으로 영구히 바뀝니다. 자석제거기 2개를 획득합니다.",listOf("기타","자석제거기")),
    "덩치 큰 친구들 I" to Pair ("정확히 한 명의 다른 아군 옆에서 전투를 시작한 아군이 체력을 100 얻습니다. 한 챔피언이 사망하면 다른 한 명이 10초 동안 최대 체력의 10%에 해당하는 보호막을 얻습니다.",listOf("전투")),
    "도둑 무리 I" to Pair ("도적의 장갑 1개를 획득합니다.", listOf("아이템")),
    "마나순환 I" to Pair ("후방 가로 1열에서 전투를 시작하는 아군 유닛이 기본 공격마다 추가 마나를 2 얻습니다.",listOf("전투")),
    "맹렬한 공세" to Pair ("아군의 기본 공격이 대상을 태워 5초 동안 대상 최대 체력의 5%에 해당하는 피해를 입힙니다. 또한 기본 공격 시 받는 치유 효과를 33% 감소시킵니다.",listOf("전투")),
    "모두를 위한 하나 I" to Pair ("아군이 전장에 있는 고유 1단계 챔피언 한 명당 최대 체력을 2%, 피해 증폭을 150% 얻습니다. 1단계 챔피언 2명을 획득합니다.",listOf("전투")),
    "미친 화학자" to Pair ("신지드를 획득합니다. 가장 강한 아군 신지드가 기본 공격을 할 수 없게 되지만 지속적으로 뛰어다니며 맹독의 자취를 남겨 지속 마법 피해를 입힙니다. 신지드의 스킬은 항상 자신을 대상으로 하며 대신 모든 피해 흡혈 및 이동 속도를 20% 부여합니다.\n" + "마법 피해: 140% / 210% / 315% / 420%",listOf("영웅")),
    "부스러기" to Pair ("각 공동 선택 이후 선택되지 않은 유닛 1명과 해당 유닛이 보유한 아이템을 획득합니다. 1골드를 획득합니다.",listOf("아이템","기타")),
    "부식" to Pair ("전방 가로 2열에 있는 적 챔피언들의 방어력 및 마법 저항력이 2초마다 3 감소합니다.",listOf("전투")),
    "새로고침 이월" to Pair ("사용하지 않은 증강 새로고침 1회마다 무료 상점 새로고침을 3회 얻습니다. 3골드를 획득합니다.",listOf("돈")),
    "새로고침의 날 I" to Pair ("무료 상점 새로고침을 11회 얻습니다.",listOf("돈")),
    "생존자" to Pair ("플레이어 3명이 탈락하면 60골드를 획득합니다.",listOf("돈")),
    "서열 상승 I" to Pair ("아군이 사망할 때마다 특성을 한 개 이상 공유하는 아군이 주문력을 3, 공격력을 3%, 방어력을 3, 마법 저항력을 3 얻습니다.",listOf("전투")),
    "수호자의 친구" to Pair ("즉시 무작위 2단계 챔피언을 획득합니다. 레벨을 올릴 때마다 동일한 챔피언을 획득합니다.",listOf("챔피언", "전투")),
    "슈퍼스타 I" to Pair ("아군의 피해량이 5% 증가합니다. 이 효과는 아군 3성 유닛 하나당 2% 증가합니다. 새로고침을 2회 얻습니다.",listOf("전투")),
    "스승 I" to Pair ("더 높은 단계의 아군 옆에서 전투를 시작한 아군이 공격 속도를 12%, 체력을 150 얻습니다.", listOf("전투")),
    "신중한 제작" to Pair ("플레이어 대상 전투를 8번 치른 후 유물 모루를 획득합니다.\n" + "모루가 4가지 선택지를 제시합니다.",listOf("아이템")),
    "아이템 꾸러미 I" to Pair ("무작위 완성 아이템 1개를 획득합니다.",listOf("아이템")),
    "아이템 수집가 I" to Pair ("아군이 체력을 10 얻습니다. 장착한 아이템 한 종류당 아군이 체력을 2, 공격력을 1, 주문력을 1 추가로 얻습니다.",listOf("전투")),
    "아이템 숙성" to Pair ("완성 아이템을 4라운드 동안 대기석에 두면 지원 아이템 모루로 변합니다.",listOf("아이템")),
    "연결 불가" to Pair ("1단계 챔피언의 복사본을 1명씩 획득합니다.",listOf("챔피언")),
    "위력 강화" to Pair ("다음 증강이 한 단계 높아집니다.",listOf("기타")),
    "위약 효과" to Pair ("8골드를 획득합니다. 아군의 공격 속도가 1% 증가합니다.",listOf("돈","전투")),
    "위약 효과+" to Pair ("15골드를 획득합니다. 아군의 공격 속도가 1% 증가합니다.",listOf("돈","전투")),
    "위험한 행보" to Pair ("아군 전략가가 체력을 20 잃지만, 플레이어 대상 전투를 7번 치른 후 30골드를 획득합니다.",listOf("돈")),
    "유리 대포 I" to Pair ("후방 가로 1열에서 전투를 시작한 유닛이 전투 시작 시 체력이 80%로 조정되지만 피해 증폭을 12% 얻습니다.",listOf("전투")),
    "유용한 금속" to Pair  ("조합 아이템 모루와 4골드를 획득합니다.\n" + "모루가 4가지 선택지를 제시합니다.",listOf("아이템","돈")),
    "은수저" to Pair ("10의 경험치를 획득합니다.",listOf("돈")),
    "의무병" to Pair ("스텝을 획득합니다. 가장 강한 아군 스텝의 스킬 마나 소모량이 10 감소하지만, 더 이상 회복할 수 없습니다. 스텝의 스킬이 모든 피해 흡혈을 30 부여하고 3회 타격해 각각 65%의 피해를 입힙니다.",listOf("영웅")),
    "임무 재시작" to Pair ("전장과 대기석의 모든 아군 챔피언을 제거합니다. 무작위 3단계 2성 챔피언 2명, 2단계 2성 챔피언 3명, 1단계 2성 챔피언 1명을 획득합니다.",listOf("챔피언")),
    "입맛대로 고르는 부품" to Pair ("조합 아이템을 얻을 때마다 조합 아이템 모루를 대신 획득합니다. 무작위 조합 아이템을 획득합니다.",listOf("아이템")),
    "전리품 I" to Pair ("적 유닛을 처치하면 25% 확률로 전리품을 획득합니다.",listOf("연승","랜덤 보상")),
    "젊음, 광란, 자유" to Pair ("공동 선택 라운드에서 언제나 자유롭게 움직일 수 있습니다. 2골드를 획득합니다.",listOf("기타","연승")),
    "점심값" to Pair ("적 전략가에게 8의 피해를 입힐 때마다 2골드를 획득합니다.",listOf("돈","연승")),
    "정교한 공예" to Pair ("완성 아이템을 만들 때마다 새로고침을 2회 얻습니다.",listOf("돈")),
    "정렬" to Pair ("아군이 전방 2열에서 전투를 시작하는 유닛 하나당 방어력 및 마법 저항력을 2.5 얻습니다.",listOf("전투")),
    "조작된 상점" to Pair ("다음 상점 및 각 4번째 상점이 모두 3단계 챔피언으로 채워집니다.",listOf("기타")),
    "조작된 상점+" to Pair ("다음 상점 및 각 4번째 상점이 모두 3단계 챔피언으로 채워집니다. 새로고침을 5회 얻습니다.",listOf("돈","기타")),
    "종목 분산 투자" to Pair ("라운드마다 활성화된 고유 특성이 아닌 특성 3개당 1골드를 획득합니다. 1골드를 획득합니다.",listOf("돈")),
    "종목 분산 투자+" to Pair ("라운드마다 활성화된 고유 특성이 아닌 특성 3개당 1골드를 획득합니다. 4골드를 획득합니다.",listOf("돈")),
    "준비 운동 I" to Pair ("아군의 공격 속도가 즉시 6% 증가합니다. 매 라운드 종료 후에 효과가 0.5% 추가로 증가합니다.",listOf("전투")),
    "중심 잡기" to Pair ("전장 중앙에서 전투를 시작하는 아군 챔피언의 피해량 증폭이 15%, 최대 체력이 15% 증가합니다.",listOf("전투")),
    "지원 채굴" to Pair ("훈련 봇 1개를 획득합니다. 훈련 봇이 7회 사망하면 무작위 지원 아이템을 획득하고 훈련 봇이 제거됩니다.",listOf("아이템")),
    "지원 채굴+" to Pair ("훈련 봇 1개를 획득합니다. 훈련 봇이 4회 사망하면 무작위 지원 아이템을 획득하고 훈련 봇이 제거됩니다.",listOf("아이템")),
    "참는 자에게 복을" to Pair ("지난 라운드에서 챔피언을 구매하지 않았다면 무료 새로고침을 2회 얻습니다.",listOf("돈")),
    "체력이 곧 재산 I" to Pair ("아군이 모든 피해 흡혈을 10% 얻습니다. 아군의 회복량이 처음으로 총 10000만큼 누적되면 추가로 8골드를 획득합니다.",listOf("전투","돈")),
    "트롤 나가신다" to Pair ("트런들을 획득합니다. 가장 강한 아군 트런들의 스킬이 더 이상 체력을 회복시키지 않는 대신 5초 동안 공격 속도를 140%, 영구적으로 공격력을 1.5% 부여합니다. 트런들의 최대 마나가 50 감소합니다.",listOf("영웅")),
    "판도라의 대기석" to Pair ("2골드를 획득합니다. 라운드가 시작될 때마다 대기석 가장 오른쪽 3명의 챔피언이 동일한 단계의 무작위 챔피언으로 변신합니다.",listOf("기타")),
    "판도라의 아이템" to Pair ("라운드 시작: 대기석의 아이템이 무작위로 변합니다. 무작위 조합 아이템 1개를 획득합니다.",listOf("기타","아이템")),
    "편식" to Pair ("다른 모든 증강 선택에서 증강 새로고침 횟수를 +3 얻습니다. 7골드를 획득합니다.",listOf("돈","기타")),
    "하나, 둘, 다섯!" to Pair ("무작위 조합 아이템 1개, 2골드, 무작위 5단계 챔피언 1명을 획득합니다.",listOf("아이템", "챔피언", "돈")),
    "하나, 둘, 셋" to Pair ("1단계 챔피언 2명, 2단계 챔피언 2명, 3단계 챔피언 1명을 획득합니다.",listOf("챔피언")),
    "협력 I" to Pair ("무작위 조합 아이템 1개, 무작위 3단계 챔피언 2명을 획득합니다.",listOf("챔피언","아이템")),
    "후반 전문가" to Pair ("9레벨에 도달하면 33골드를 획득합니다.",listOf("돈")),
    "후발 주자" to Pair ("전장 및 대기석에 있는 챔피언을 판매합니다. 무작위 1단계 2성 챔피언 4명을 획득합니다. 다음 3라운드 동안 상점이 비활성화됩니다.",listOf("챔피언")),
    "후방 지원" to Pair ("아군 4명 이상이 후방 가로 2열에서 전투를 시작했다면 아군이 공격 속도를 10% 얻습니다.",listOf("전투")),
    "훈련 봇 변환" to Pair ("전장 및 대기석에 있는 모든 챔피언을 잃습니다. 잃은 챔피언 체력 총합의 100%를 지닌 훈련 봇을 획득합니다.",listOf("전투")),
)

val goldAugments = mapOf(
    "2만한 가치" to Pair ("지난 전투에 배치된 고유 2단계 챔피언 2명당 새로고침을 1회 얻습니다. 2단계 유닛을 2명 획득합니다.",listOf("돈","챔피언")),
    "2인조" to Pair ("무작위 5단계 챔피언 2명과 무작위 조합 아이템 1개의 복사본 2개를 획득합니다.",listOf("아이템","챔피언")),
    "4단계 한 쌍" to Pair ("아군에 4단계 챔피언이 정확히 2명 있다면 각 4단계 챔피언이 체력을 404, 공격 속도를 24.4 얻습니다. 무작위 4단계 챔피언 1명을 획득합니다.",listOf("전투", "챔피언")),
    "4후지원" to Pair ("다음에 구매하는 4단계 챔피언이 즉시 2성으로 업그레이드됩니다. 12골드를 획득합니다.",listOf("돈")),
    "5등급" to Pair ("루난의 허리케인 1개를 획득합니다. 루난의 허리케인이 탄환을 추가로 1개 발사하고, 각 탄환은 기존 피해량의 85%만큼 피해를 입힙니다.",listOf("전투","아이템")),
    "가시 박힌 갑옷" to Pair ("덤불 조끼 1개를 획득합니다. 아군의 덤불 조끼가 피해를 5~100%(스테이지에 따라) 더 입히고, 입힌 피해의 50%만큼 착용자의 체력을 회복시킵니다.",listOf("아이템","전투")),
    "가족 문장" to Pair ("가족 상징과 바이올렛을 획득합니다.",listOf("상징","챔피언")),
    "감시자 문장" to Pair ("감시자 상징과 렐을 획득합니다.",listOf("상징","챔피언")),
    "강력한 보호막" to Pair ("보호막이 씌워진 동안 아군 유닛이 내구력을 12% 얻습니다. 아군의 체력이 처음 50% 아래로 떨어지면 3초 동안 스테이지에 따라 보호막을 125~275 얻습니다.",listOf("전투")),
    "검은 장미단 문장" to Pair ("검은 장미단 상징을 획득합니다.",listOf("상징")),
    "격전의 지하도시" to Pair ("암시장에서 시머를 사용하지 않기로 결정할 때마다 플레이어 체력을 4 회복하고 6골드를 획득합니다. 레니를 획득합니다.(게임마다 플레이어 1명에게만 제공됩니다.)",listOf("특성 전용","체력","돈","챔피언")),
    "경쟁" to Pair ("각 플레이어 대상 전투 이후, 이번 게임에서 아군 대기석 가장 왼쪽에 있는 유닛을 배치한 다른 플레이어 2명당 2골드를 획득합니다.",listOf("돈")),
    "고물 더미 꼭대기" to Pair ("고물상 챔피언이 조합 아이템을 8개 변환할 때마다 무작위 조합 아이템 1개를 획득합니다. (최대 6개) 파우더와 트런들을 획득합니다.",listOf("특성 전용","아이템")),
    "고물상 문장" to Pair ("고물상 상징과 직스를 획득합니다.",listOf("상징","챔피언")),
    "고전압" to Pair ("이온 충격기 1개를 획득합니다. 이온 충격기의 효과 반경이 +3칸 증가하고 피해를 25% 더 입힙니다.",listOf("아이템","전투")),
    "골렘화" to Pair ("전장 및 대기석에 있는 모든 챔피언을 잃습니다. 잃은 챔피언 체력 총합의 90%, 공격력 총합의 60%를 지닌 골렘을 획득합니다. 골렘이 스테이지마다 체력을 150 얻습니다.",listOf("전투")),
    "공중전" to Pair ("점화단 챔피언이 점화단 돌진을 시작할 때 공격력을 9%, 주문력을 9 얻습니다. 각 전투에서 이동한 칸 하나당 3% 더 얻습니다. 스카와 제리를 획득합니다.",listOf("특성 전용","전투","챔피언")),
    "공허소환사" to Pair ("전투 중 선도자가 마나를 275 사용할 때마다 공허충을 1마리 소환합니다. 최대 5마리를 소환합니다. 렐과 모르가나를 획득합니다. \n공허충은 스테이지에 따라 400~600의 체력을 보유합니다.",listOf("특성 전용","전투","챔피언")),
    "과다치유" to Pair ("세 번째 기본 공격마다 115%의 추가 피해를 입히고 피해량의 50%만큼 체력을 회복합니다. 초과 회복량은 최대 300의 피해를 흡수하는 보호막으로 전환됩니다.",listOf("전투")),
    "교환의 장" to Pair ("라운드마다 무료 상점 새로고침을 획득합니다. 1골드를 획득합니다.",listOf("돈")),
    "균열 수정" to Pair ("자동기계 챔피언이 에너지를 발사할 때 가장 가까운 적에게 두 번째 에너지를 발사해 기존 피해량의 50%만큼 피해를 입힙니다. 아무무와 녹턴을 획득합니다.",listOf("특성 전용","전투","챔피언")),
    "근위대" to Pair ("크라운가드를 획득합니다. 크라운가드의 전투 시작 효과가 100% 증가합니다.",listOf("아이템","전투")),
    "금단의 마법" to Pair ("검은 장미단 챔피언 또는 사이온이 처치에 3회 관여할 때마다 사이온이 영구적으로 공격력을 1.5%, 최대 체력을 10% 얻습니다. 검은 장미단 챔피언 3명을 획득합니다.",listOf("특성 전용","전투","챔피언")),
    "금방 올게" to Pair ("다음 3라운드 동안 아무 행동을 취할 수 없습니다. 이후 완성 아이템 모루 2개를 획득합니다.",listOf("아이템")),
    "기다림의 미학" to Pair ("무작위 1단계 챔피언 1명을 3개 획득합니다. 2라운드 후, 게임이 끝날 때까지 해당 유닛의 복사본을 각 라운드 시작 시 획득합니다.",listOf("기타","챔피언")),
    "기동타격대 문장" to Pair ("기동타격대 상징과 아칼리를 획득합니다.",listOf("상징","챔피언")),
    "꿰뚫는 연꽃 I" to Pair ("아군이 치명타 확률을 5 얻으며, 스킬에 치명타가 적용될 수 있습니다. 치명타 적중 시 대상에게 3초 동안 파쇄 및 파열을 20% 적용합니다.",listOf("전투")),
    "끈질긴 연구" to Pair ("플레이어 대상 전투가 끝나고 승리 시 2의 경험치, 패배 시 3의 경험치를 획득합니다.",listOf("돈")),
    "난동꾼 문장" to Pair ("난동꾼 상징과 세트를 획득합니다.",listOf("상징","챔피언")),
    "녹서스의 단두대" to Pair ("정복자가 체력이 12% 아래로 떨어진 적을 처형합니다. 처형 시 전투가 끝날 때까지 방어력 및 마법 저항력을 5 얻습니다. 다리우스와 드레이븐을 획득합니다.",listOf("특성 전용","전투","챔피언")),
    "놀이터에 온 걸 환영해" to Pair ("전투 시작 17초 후 또는 전투 종료 시 가족 구성원이 2명 이상 살아있다면 밴더 또는 파우더 또는 바이올렛의 복사본을 획득합니다. 파우더와 바이올렛을 획득합니다.",listOf("특성 전용","랜덤 보상","챔피언")),
    "다른 태생 II" to Pair ("활성화된 특성이 없는 아군 유닛이 300~600의 체력과 45~75%의 공격 속도를 얻습니다. (스테이지 별로 체력 100, 공격 속도 10% 증가)",listOf("전투")),
    "단일 조합 사수" to Pair ("더 이상 플레이어 대상 전투에 참여한 유닛을 대기석에 놓거나 판매할 수 없습니다. 각 플레이어 대상 전투 후 전투에 참여한 유닛은 체력을 20, 공격력을 1.5%, 주문력을 1.5% 얻습니다.",listOf("전투")),
    "달빛" to Pair ("전투 시작: 무작위 1단계 챔피언 1명이 해당 라운드에만 3성으로 업그레이드되며 공격력을 20%, 주문력을 35 얻습니다.",listOf("전투")),
    "덩치 큰 친구들 II" to Pair  ("정확히 한 명의 다른 아군 옆에서 전투를 시작한 아군이 체력을 175 얻습니다. 한 챔피언이 사망하면 다른 한 명이 10초 동안 최대 체력의 15%에 해당하는 보호막을 얻습니다.",listOf("전투")),
    "도굴꾼 I" to Pair ("먼저 탈락하는 플레이어 3명에게서 각각 완성 아이템 하나를 선택해 획득합니다.",listOf("아이템")),
    "돈벼락" to Pair ("바로 8골드를 획득합니다. 이후 매 라운드 1골드를 획득합니다.",listOf("돈")),
    "돈벼락+" to Pair ("바로 18골드를 획득합니다. 이후 매 라운드 1골드를 획득합니다.",listOf("돈")),
    "돌연변이 발현" to Pair ("실험체가 체력을 12% 얻고 특별 칸이 제공됩니다. 특별 칸에 있는 실험체는 전투 시작 시 처치되며 이 유닛의 실험체 추가 효과는 다른 실험실 칸에 부여됩니다. 실험체 챔피언 3명을 획득합니다.",listOf("특성 전용","전투","챔피언")),
    "또 다른 이상 현상" to Pair ("스테이지 4-6의 이상 현상 라운드 이후, 장착한 챔피언에게 선택한 이상 현상 효과를 복제하는 아이템을 획득합니다.",listOf("전투")),
    "로켓 컬렉션" to Pair ("포수의 로켓 피해량이 15% 증가합니다. 아군 포수가 로켓을 75발 발사할 때마다 황금 징수의 총 1개를 획득합니다. (최대 2개) 트리스타나와 우르곳을 획득합니다.",listOf("특성 전용","아이템","전투","챔피언")),
    "로켓 컬렉션+" to Pair ("포수의 로켓 피해량이 15% 증가합니다. 아군 포수가 로켓을 65발 발사할 때마다 황금 징수의 총 1개를 획득합니다. (최대 2개) 트리스타나와 우르곳을 획득합니다.",listOf("특성 전용","아이템","전투","챔피언")),
    "마나순환 II" to Pair ("후방 가로 1열에서 전투를 시작하는 아군 유닛이 기본 공격마다 추가 마나를 4 얻습니다.",listOf("전투")),
    "마법 주사위" to Pair ("주사위 3개를 굴립니다. 결과 총합에 따라 보상을 획득합니다.",listOf("랜덤 보상")),
    "마법사 문장" to Pair ("마법사 상징과 블라디미르를 획득합니다.",listOf("상징","챔피언")),
    "맑은 정신" to Pair ("플레이어 대상 전투 종료 시 아군 대기석에 챔피언이 없다면 3의 경험치를 획득합니다.",listOf("돈")),
    "매복자 문장" to Pair ("매복자 상징과 카밀을 획득합니다.",listOf("상징","챔피언")),
    "먼 친구" to Pair ("전투 시작: 서로 가장 멀리 떨어진 아군 유닛 2명이 유대를 형성해 방어력, 마법 저항력, 공격력, 주문력의 22%를 서로 공유합니다.",listOf("전투")),
    "모두를 위한 하나 II" to Pair ("아군이 전장에 있는 고유 1단계 챔피언 한 명당 최대 체력을 3%, 피해 증폭을 2.5% 얻습니다. 1단계 챔피언 3명을 획득합니다.",listOf("전투","챔피언")),
    "미래 지향적 사고" to Pair ("모든 골드를 잃습니다. 플레이어 대상 전투 6회 후 기존 골드를 되찾고 추가로 70골드를 획득합니다.",listOf("돈")),
    "반군 문장" to Pair ("반군 상징과 아칼리를 획득합니다.",listOf("상징","챔피언")),
    "반짝이는 것들" to Pair ("무기고를 열어 골드를 생성하는 유물 중 1개를 선택하고 자석제거기 1개를 획득합니다.",listOf("아이템","돈","자석제거기")),
    "반짝이는 것들+" to Pair ("무기고를 열어 골드를 생성하는 유물 아이템 중 1개를 선택합니다. 자석제거기 1개와 4골드를 획득합니다.",listOf("아이템","돈","자석제거기")),
    "방랑하는 조련사 I" to Pair ("1골드와 영구 장착된 상징 2개를 지닌 훈련 봇을 획득합니다.",listOf("상징")),
    "배부른 주문술사" to Pair ("스킬을 사용한 후 챔피언이 3초 동안 모든 피해 흡혈을 20% 얻습니다. 초과 회복량은 최대 300의 피해를 흡수하는 보호막으로 전환됩니다.",listOf("전투")),
    "법 집행" to Pair ("집행자 챔피언이 공격력을 10% 얻습니다. 현상수배 상태인 적이 5명 사망할 때마다 6골드를 획득합니다. 스텝과 매디를 획득합니다.",listOf("특성 전용","전투","돈","챔피언")),
    "별이 빛나는 밤" to Pair ("상점 내 1단계 및 2단계 유닛이 일정 확률로 2성이 됩니다. 6골드를 획득합니다.",listOf("돈","기타")),
    "별이 빛나는 밤+" to Pair ("상점 내 1단계 및 2단계 유닛이 일정 확률로 2성이 됩니다. 8골드를 획득합니다.",listOf("돈","기타")),
    "보호막 강타" to Pair ("감시자가 추가 방어력 및 마법 저항력을 5% 얻습니다. 4초마다 감시자의 다음 기본 공격이 방어력 및 마법 저항력 총합의 75%에 해당하는 마법 피해를 입힙니다. 로리스를 획득합니다.",listOf("특성 전용","전투","챔피언")),
    "복제" to Pair ("조합 아이템 3개 중 1개를 선택합니다. 다음 2라운드 동안, 해당 조합 아이템의 복사본을 1개씩 획득합니다.",listOf("아이템")),
    "복제 시설" to Pair ("전장 중앙에 있는 칸을 강화합니다. 해당 칸에 있는 챔피언의 분신을 소환합니다. 분신은 70%의 체력을 가지며 마나 소모량이 20% 증가합니다.",listOf("전투")),
    "불운 방지" to Pair ("아군이 더 이상 치명타를 발동할 수 없습니다. 치명타 확률 1%마다 공격력 1%로 전환됩니다. 연습용 장갑 1개를 획득합니다.",listOf("전투","아이템")),
    "불장난" to Pair ("붉은 덩굴정령 1개를 획득합니다. 불태우기 피해량이 50% 증가합니다.",listOf("아이템","전투")),
    "불타는 영혼 I" to Pair ("전투 시작: 가장 공격 속도가 높은 아군 챔피언이 주문력을 20, 공격 속도를 20% 얻습니다. 이 효과는 3초마다 다른 아군에게 반복해서 적용됩니다.",listOf("전투")),
    "삼인방 I" to Pair ("3단계 챔피언을 2명 획득합니다. 전투 시작: 3단계 무작위 챔피언 3명이 체력을 250, 공격 속도를 18% 얻습니다.",listOf("전투")),
    "상점 오류" to Pair ("플레이어 대상 전투가 아닌 라운드에서 상점이 30초 동안 3초마다 무료로 새로고침됩니다.",listOf("기타")),
    "새로운 시대" to Pair ("즉시 및 이후 매 스테이지 시작 시 6의 경험치를 획득하고 무료 새로고침 2회를 얻습니다.",listOf("돈")),
    "새로운 시대+" to Pair ("즉시 및 이후 매 스테이지 시작 시 8의 경험치를 획득하고 무료 새로고침 3회를 얻습니다.",listOf("돈")),
    "서열 상승 II" to Pair ("아군이 사망할 때마다 특성을 한 개 이상 공유하는 아군이 주문력을 5, 공격력을 5%, 방어력을 5, 마법 저항력을 5 얻습니다.",listOf("전투")),
    "선도자 문장" to Pair ("선도자 상징과 레나타 글라스크를 획득합니다.",listOf("상징","아이템")),
    "수호자의 호의" to Pair ("5, 6, 7, 8레벨에 도달하면 조합 아이템 모루를 획득합니다. 모루가 4가지 선택지를 제시합니다.",listOf("아이템")),
    "숭고한 희생" to Pair ("전투마다 처음으로 아군이 사망하면 15+해당 아군의 방어력 및 마법 저항력의 15%가 모든 아군에게 부여됩니다.",listOf("전투")),
    "슈퍼스타 II" to Pair ("아군의 피해량이 7% 증가합니다. 이 효과는 아군 3성 유닛 하나당 5% 증가합니다. 새로고침을 4회 얻습니다.",listOf("전투","돈")),
    "스승 II" to Pair ("더 높은 단계의 아군 옆에서 전투를 시작한 아군이 공격 속도를 18%, 체력을 220 얻습니다.",listOf("전투")),
    "슬쩍하기" to Pair ("라운드마다, 이전 전투에서 처치한 첫 챔피언의 1성 복사본 1명을 획득합니다.",listOf("기타")),
    "시계태엽 윤활유" to Pair ("아군이 전투에서 3초마다 공격 속도를 10% 얻습니다.",listOf("전투")),
    "실험체 문장" to Pair ("실험체 상징과 우르곳을 획득합니다.",listOf("상징","챔피언")),
    "쓰레기 청소부" to Pair ("전투마다 적 챔피언 4명이 처음 처치될때마다 아군 챔피언에게 임시 완성 아이템 1개를 제공합니다.",listOf("전투")),
    "아드레날린 폭발" to Pair ("전투 시작 시 및 6초마다 모든 기동타격대가 2.5초 동안 85% 더 빠르게 공격합니다. 녹턴과 아칼리를 획득합니다.",listOf("특성 전용","전투","챔피언")),
    "아이템 수집가 II" to Pair ("아군이 체력을 20 얻습니다. 장착한 아이템 한 종류당 아군이 체력을 5, 공격력을 1.5, 주문력을 1.5 추가로 얻습니다.",listOf("전투")),
    "아카데미 문장" to Pair ("아카데미 상징과 레오나를 획득합니다.", listOf("상징","챔피언")),
    "아케인의 응징" to Pair ("마법사가 사망하면 주문력의 300%에 해당하는 마법 피해를 인접한 모든 유닛에게 입힙니다. 럭스와 블라디미르를 획득합니다.",listOf("특성 전용","챔피언")),
    "악의적 수익 창출" to Pair ("2골드를 획득합니다. 다음 3라운드 동안 적 챔피언이 처치될 때 2골드를 떨어뜨립니다.",listOf("돈")),
    "야수를 풀어라" to Pair ("스테락의 도전 1개를 획득합니다. 스테락의 도전 효과 발동 시, 전투가 끝날 때까지 장착 유닛이 공격 속도를 35% 얻고 10초 동안 군중 제어 효과에 면역이 됩니다.", listOf("아이템","전투")),
    "어수선한 마음" to Pair ("즉시 무작위 1단계 챔피언 4명을 획득합니다. 플레이어 대상 전투 라운드 종료 시 대기석에 챔피언이 가득 찼다면 3의 경험치를 획득합니다.",listOf("챔피언","돈")),
    "영감적인 비문" to Pair ("유닛이 죽으면 가장 가까운 아군이 최대 체력의 20%에 해당하는 보호막을 얻고 중첩되는 공격 속도가 10%만큼 증가합니다.",listOf("전투")),
    "영웅 꾸러미" to Pair ("하급 챔피언 복제기 2개와 9골드를 획득합니다. 3단계 이하의 챔피언을 복제할 수 있습니다.",listOf("기타")),
    "영원한 브론즈 I" to Pair ("아군이 브론즈 등급 특성당 피해 증폭을 1.5% 얻습니다.",listOf("전투")),
    "오늘은 아니야" to Pair ("밤의 끝자락을 획득합니다. 밤의 끝자락을 장착한 챔피언의 공격 속도가 35% 증가합니다.",listOf("아이템","전투")),
    "왕관의 의지" to Pair ("쓸데없이 큰 지팡이 1개를 획득합니다. 아군 유닛이 주문력을 10, 방어력을 10 얻습니다.",listOf("아이템","전투")),
    "요리용 냄비" to Pair ("각 라운드 시작 시, 프라이팬 또는 뒤집개를 장착한 모든 유닛이 가장 가까운 챔피언에게 영구적으로 체력을 50 부여합니다. 프라이팬 1개를 획득합니다.",listOf("기타","전투")),
    "용의 정신" to Pair ("용의 발톱 1개를 획득합니다. 용의 발톱을 장착한 챔피언이 체력을 100, 내구력을 10% 얻습니다.",listOf("아이템","전투")),
    "유리 대포 II" to Pair ("후방 가로 1열에서 전투를 시작한 유닛이 전투 시작 시 체력이 80%로 조정되지만 피해 증폭을 20% 얻습니다.",listOf("전투")),
    "육중한 강타" to Pair ("4초마다 난동꾼의 다음 기본 공격이 추가 최대 체력의 7%에 해당하는 추가 물리 피해를 입힙니다. 스텝과 트런들을 획득합니다.",listOf("특성 전용","전투","챔피언")),
    "이중 기술" to Pair ("무작위 2단계 2성 챔피언 1명과 무작위 1단계 2성 챔피언 2명을 획득합니다.",listOf("챔피언")),
    "이중 형태" to Pair ("전장에 동일한 형태전환자 2명이 서로 다른 형태를 하고 있다면 둘 다 주문력, 방어력, 마법 저항력을 24 얻고 공격력을 24% 얻습니다. 형태전환자를 3성으로 업그레이드하면 동일한 2성 챔피언 하나를 획득합니다. 스웨인과 갱플랭크를 획득합니다.",listOf("특성 전용","전투","챔피언")),
    "자동기계 문장" to Pair ("자동기계 상징과 녹턴을 획득합니다.",listOf("상징","챔피언")),
    "작은 친구들" to Pair ("전장에 있는 1단계 및 2단계 챔피언 하나당 아군 4단계 및 5단계 챔피언이 65의 체력과 7%의 공격 속도를 얻습니다.",listOf("전투")),
    "잔혹한 복수" to Pair ("레니를 2명 획득합니다. 가장 강한 아군 레니의 스킬 마나 소모량이 10 감소하고 2칸 이내 가장 멀리 있는 적에게 도약해 대상에게 110%의 피해를, 경로에 있는 적에게는 감소된 피해를 입힙니다.",listOf("영웅")),
    "재빠른 무장" to Pair ("무작위 조합 아이템 1개를 획득합니다. 플레이어 대상 전투를 마친 후 대기석에 아이템(소모품 제외)이 없을 때마다 2의 경험치를 획득합니다.",listOf("아이템","돈")),
    "재빠른 무장+" to Pair ("무작위 조합 아이템 1개와 10의 경험치를 즉시 획득합니다. 플레이어 대상 전투를 마친 후 대기석에 아이템(소모품 제외)이 없을 때마다 2의 경험치를 획득합니다.",listOf("아이템","돈")),
    "재활용 쓰레기통" to Pair ("지금 무작위 완성 아이템을 획득하고 플레이어 대상 전투 7회 후에 조합 아이템을 획득합니다. 챔피언을 판매하면 완성 아이템이 조합 아이템으로 분해됩니다. (전략가의 왕관 제외)",listOf("아이템","기타")),
    "재활용 쓰레기통+" to Pair ("지금 무작위 완성 아이템을 획득하고 플레이어 대상 전투 4회 후에 조합 아이템을 획득합니다. 챔피언을 판매하면 완성 아이템이 조합 아이템으로 분해됩니다. (전략가의 왕관 제외)",listOf("아이템","기타")),
    "저격수 문장" to Pair ("저격수 상징과 제리를 획득합니다.",listOf("상징","챔피언")),
    "저격수의 은신처" to Pair ("저격수가 동일한 칸에서 전투를 시작한 라운드마다 피해 증폭을 8% 얻습니다. (최대 +32%) 제리를 획득합니다.",listOf("특성 전용","전투","챔피언")),
    "전리품 II" to Pair ("적 유닛을 처치하면 30% 확률로 전리품을 획득합니다.",listOf("랜덤 보상","연승")),
    "전리품 폭발" to Pair ("매복자가 적을 처치할 때마다 치명타 확률에 비례해 일정 확률로 전리품을 떨어뜨립니다. 전리품의 가치에도 치명타가 적용되어 더 많은 전리품을 제공할 수 있습니다. 카밀과 파우더를 획득합니다.",listOf("특성 전용","랜덤 보상","챔피언")),
    "점수판 등반가" to Pair ("매 라운드 하위 4위권일 경우, 아군의 공격력과 주문력이 영구히 1.5% 증가합니다. 상위 4위권일 경우, 아군의 체력이 10% 증가합니다.",listOf("전투")),
    "점화단 문장" to Pair ("점화단 상징과 제리를 획득합니다.",listOf("상징","챔피언")),
    "정복자 문장" to Pair ("정복자 상징과 렐을 획득합니다.",listOf("상징","챔피언")),
    "정신 결속" to Pair ("아군이 5초마다 최대 체력의 5%를 회복합니다. 잃은 플레이어 체력 10당 체력 회복량이 0.5% 증가합니다.",listOf("전투")),
    "주시자 문장" to Pair ("주시자 상징과 밴더를 획득합니다.",listOf("상징","챔피언")),
    "죽음의 고통" to Pair ("플레이어 대상 전투 패배 후 2골드를 획득합니다. 4번 패배할 때마다 무작위 조합 아이템을 획득합니다.",listOf("돈","아이템")),
    "준비 운동 II" to Pair ("아군의 공격 속도가 즉시 8% 증가합니다. 매 라운드 종료 후에 효과가 1% 추가로 증가합니다.",listOf("전투")),
    "지배" to Pair ("지배자가 보호막이 씌워진 동안 공격 속도를 10% 얻습니다. 지배자가 적을 처치하면 모든 지배자가 3초 동안 보호막을 100 얻습니다. 카시오페아를 획득합니다.",listOf("특성 전용","전투","챔피언")),
    "지배자 문장" to Pair ("지배자 상징과 블리츠크랭크를 획득합니다.",listOf("상징","챔피언")),
    "지원 상자" to Pair ("지원 아이템 4개 중 하나를 선택합니다.",listOf("아이템")),
    "진심 발휘" to Pair ("밴더를 획득합니다. 가장 강한 아군 밴더가 새로운 스킬을 얻습니다. 더 이상 방어력 및 마법 저항력을 제공하지 않지만 85% 증가한 피해를 입히고 대상을 뒤로 날려 보내 적중한 모든 적에게 기존 피해량의 25%만큼 피해를 입힙니다.",listOf("영웅")),
    "집행자 문장" to Pair ("집행자 상징을 획득합니다.",listOf("상징")),
    "창의 의지" to Pair ("아군이 공격력을 10%, 마나를 10 얻습니다. B.F. 대검 1개를 획득합니다.",listOf("아이템","전투")),
    "철퇴의 의지" to Pair ("연습용 장갑 1개를 획득합니다. 아군이 공격 속도를 8%, 치명타 확률을 20% 얻습니다.",listOf("아이템","전투")),
    "체력이 곧 재산 II" to Pair ("아군이 모든 피해 흡혈을 15% 얻습니다. 아군의 회복량이 처음으로 총 10000만큼 누적되면 추가로 15골드를 획득합니다.",listOf("전투","돈")),
    "출정" to Pair ("플레이어에게 60의 피해를 입힌 후 고단계 챔피언과 아이템이 들어 있는 상자 1개를 획득합니다.",listOf("아이템","챔피언")),
    "큰 꾸러미" to Pair ("무작위 조합 아이템 3개, 2골드, 재조합기를 획득합니다.",listOf("아이템","재조합기")),
    "투사 문장" to Pair ("투사 상징과 우르곳을 획득합니다.",listOf("상징","챔피언")),
    "투자 전략 I" to Pair ("이자로 획득한 골드당 아군 챔피언이 영구적으로 최대 체력을 7 얻습니다.",listOf("전투")),
    "특성 추적자" to Pair ("처음으로 한 전투에서 고유 특성이 아닌 특성 8개를 활성화하면 무작위 상징 5개를 획득합니다.",listOf("상징")),
    "특성: 계엄령" to Pair ("암베사가 스킬을 사용하면 케이틀린이 대상에게 강화된 공격을 발사해 225%의 피해를 입힙니다. 암베사가 케이틀린 공격력의 25%를 얻습니다. 케이틀린과 암베사를 획득합니다.",listOf("특성","전투","챔피언")),
    "특성: 뜻밖의 2인조" to Pair ("징크스와 세비카가 공격력을 10%, 체력을 100 얻습니다. 둘 중 한 명이 스킬을 사용하면 다른 한 명이 마나를 10 얻습니다. 세비카의 팔에 행운이 더 따릅니다. 징크스와 세비카를 획득합니다.",listOf("특성","전투","챔피언")),
    "특성: 모략" to Pair ("실코와 함께 배치할 경우 파우더가 지배자 특성을 얻지만, 더 이상 가족 특성의 효과를 받지 않습니다. 파우더의 원숭이가 폭발하면 실코의 괴수 3마리가 생성됩니다. 2성 파우더와 실코를 획득합니다.",listOf("특성","전투","챔피언")),
    "특성: 배신" to Pair ("매디의 스킬 및 기본 공격은 항상 암베사의 대상을 대상으로 지정합니다. 매디 또는 암베사가 2명 이상 처치한 라운드마다 매디를 1명 획득합니다. 매디와 암베사를 획득합니다.",listOf("특성","전투","챔피언")),
    "특성: 자매" to Pair ("자매 특성을 얻습니다. 바이가 처치에 관여하면 징크스가 5초 동안 추가 공격 속도를 75% 얻습니다. 징크스가 처치에 관여하면 바이가 7초 동안 추가 공격력을 40% 얻습니다. 바이와 징크스를 획득합니다.",listOf("특성","전투","챔피언")),
    "특성: 재회" to Pair ("바이가 스킬을 사용하면 에코가 바이의 대상에게 잔상 3개를 보내 50%의 피해를 입힙니다. 에코가 스킬을 사용하면 바이가 에코의 대상에게 지진 강타를 사용해 150%의 피해를 입힙니다. 바이와 에코를 획득합니다.",listOf("특성","전투","챔피언")),
    "파랗게 물들여라" to Pair ("전투마다 반군이 처음으로 5명 처치되면 별 레벨이 하나 낮고 체력이 400 낮은 해당 반군의 복사본을 소환합니다. 아칼리와 이렐리아를 획득합니다.", listOf("특성 전용","전투","챔피언")),
    "파이트 머니" to Pair ("조합 아이템 2개를 획득합니다. 5회 승리할 때마다 조합 아이템을 획득합니다.",listOf("아이템")),
    "판도라의 아이템 II" to Pair ("라운드 시작: 대기석의 아이템이 무작위로 변합니다. 무작위 조합 아이템 2개를 획득합니다.",listOf("아이템","기타")),
    "포수 문장" to Pair ("포수 상징과 트리스타나를 획득합니다.",listOf("상징","챔피언")),
    "포탑 방어" to Pair ("무작위 상징을 장착한 훈련 봇을 획득합니다. 훈련 봇은 원거리에서 적을 공격하며 게임이 진행될수록 업그레이드됩니다.",listOf("상징","전투")),
    "피나는 훈련" to Pair ("투사가 이전 전투에서 패배했다면 영구적으로 공격력을 1.5 얻습니다. 승리하면 대신 체력을 45 얻습니다. 우르곳을 획득합니다.",listOf("특성 전용", "전투","챔피언")),
    "핏빛 계약" to Pair ("블라디미르를 획득합니다. 가장 강한 아군 블라디미르의 사거리가 +3 증가하고 기본 공격할 때마다 추가 마나를 3 얻습니다. 블라디미르의 스킬이 더 이상 체력을 회복하지 않지만 피해 증폭을 10% 부여하며, 80%의 추가 피해를 입히고 가장 가까운 적 2명에게 추가로 피해를 확산합니다.",listOf("영웅")),
    "학술 연구" to Pair ("아이템을 완성할 때마다 대신 완성 아이템 모루를 획득합니다. 모루는 항상 아카데미 아이템과 완성한 아이템을 제공합니다. 무작위 조합 아이템 1개를 획득합니다. 럭스와 이즈리얼을 획득합니다.",listOf("특성 전용","아이템","챔피언")),
    "협력 II" to Pair ("무작위 지원 아이템 1개와 무작위 4단계 챔피언 2명을 획득합니다.",listOf("아이템","챔피언")),
    "화공 남작 문장" to Pair ("화공 남작 상징과 레나타 글라스크를 획득합니다.",listOf("상징","챔피언")),
    "황금 더미" to Pair ("훈련 봇 1개를 획득합니다. 10초마다 모든 훈련 봇이 1골드를 제공합니다.",listOf("돈")),
    "황금빛 발견" to Pair ("이상 현상으로 진화한 챔피언이 적을 3명 처치할 때마다 2골드를 떨어뜨립니다. 무료 새로고침을 10회 얻습니다.",listOf("돈")),
    "휴대용 대장간" to Pair ("유물 4개 중 하나를 선택합니다.\n유물은 고유한 효과를 지닌 강력한 아이템입니다.",listOf("아이템")),
    "흡혈의 활력" to Pair ("적 전략가에게 입힌 피해량의 20%만큼 플레이어 체력을 회복합니다. 아군 유닛이 모든 피해 흡혈을 12% 얻습니다.",listOf("전투","체력")),
    "희생양" to Pair ("훈련 봇과 4골드를 획득합니다. 플레이어 대상 전투에서 훈련 봇이 맨 처음으로 죽으면 1골드를 획득합니다.",listOf("돈")),
)


val prismAugments = mapOf(
    "가족 왕관" to Pair ("가족 상징, 구원, 밴더, 바이올렛을 획득합니다.",listOf("상징","아이템","챔피언")),
    "간이 대장간" to Pair ("지금 그리고 플레이어 대상 전투 9번마다 유물 모루를 획득합니다. 유물은 고유한 효과를 지닌 강력한 아이템입니다.",listOf("아이템")),
    "감시자 왕관" to Pair ("감시자 상징, 크라운가드, 로리스를 획득합니다.",listOf("상징","아이템","챔피언")),
    "검은 장미단 왕관" to Pair ("검은 장미단 상징, 모렐로노미콘, 카시오페아를 획득합니다.",listOf("상징","아이템","챔피언")),
    "계산된 강화" to Pair ("전투마다 후방 2열에 있는 무작위 챔피언 4명이 공격력을 35%, 주문력을 40 얻습니다.",listOf("전투")),
    "고귀한 모험" to Pair ("2단계 챔피언을 3명 획득합니다. 이 중에서 두 명을 3성으로 업그레이드하면 전리품 구를 획득합니다. 선택 시 및 매 스테이지 시작 시 하급 챔피언 복제기 1개를 획득합니다.",listOf("랜덤 보상")),
    "고물상 왕관" to Pair ("고물상 상징, 직스, 무작위 조합 아이템 2개를 획득합니다.",listOf("상징","아이템","챔피언")),
    "공허의 무리" to Pair ("즈롯 차원문 1개를 획득하고 플레이어 대상 전투 11회마다 추가로 1개를 획득합니다. 즈롯 차원문 공허충이 공격 속도를 40%, 모든 피해 흡혈을 40% 얻습니다.",listOf("아이템")),
    "구독 서비스" to Pair ("즉시 및 각 스테이지 시작 시 서로 다른 4단계 챔피언 4명으로 구성된 상점을 열고 6골드를 획득합니다.",listOf("돈","기타")),
    "극진한 헌신" to Pair ("무작위 상징 1개를 획득합니다. 즉시 및 각 스테이지 시작 시 무작위 상징의 특성을 보유한 1성 챔피언을 획득합니다. 챔피언의 단계는 스테이지와 동일하며 최대 5입니다.",listOf("상징","챔피언")),
    "기다림의 미학 II" to Pair ("무작위 2단계 챔피언 1명을 획득합니다. 게임이 끝날 때까지 해당 유닛의 복사본을 각 라운드 시작 시 획득합니다.",listOf("챔피언", "기타")),
    "기동타격대 왕관" to Pair ("기동타격대 상징, 구인수의 격노검, 녹턴을 획득합니다.",listOf("상징","챔피언","아이템")),
    "꼬꼬마 거인" to Pair ("플레이어 대상 전투가 끝날 때마다 2의 플레이어 체력과 1골드를 얻습니다. 또한 전략가의 이동 속도가 증가합니다.",listOf("돈","체력")),
    "꼬꼬마 거인+" to Pair ("플레이어 대상 전투가 끝날 때마다 2의 플레이어 체력과 1골드를 얻습니다. 또한 전략가의 이동 속도가 증가합니다. 즉시 15골드를 획득합니다.",listOf("돈","체력")),
    "꿰뚫는 연꽃 II" to Pair ("아군이 치명타 확률을 20 얻으며, 스킬에 치명타가 적용될 수 있습니다. 치명타 적중 시 대상에게 3초 동안 파쇄 및 파열을 20% 적용합니다.",listOf("전투")),
    "난동꾼 왕관" to Pair ("난동꾼 상징, 구원, 세트를 획득합니다.",listOf("상징","아이템","챔피언")),
    "넘치는 대검" to Pair ("B.F. 대검 5개를 획득합니다. B.F. 대검이 공격 속도를 +2.5% 제공합니다.",listOf("아이템","전투")),
    "넘치는 지팡이" to Pair ("쓸데없이 큰 지팡이 5개를 획득합니다. 쓸데없이 큰 지팡이가 공격 속도를 +2.5% 제공합니다.",listOf("아이템","전투")),
    "넘치는 허리띠" to Pair ("거인의 허리띠 4개를 획득합니다. 거인의 허리띠가 추가 체력을 +75 제공합니다.",listOf("아이템","전투")),
    "다른 태생 II" to Pair ("활성화된 특성이 없는 아군 유닛이 240~530의 체력과 45~60%의 공격 속도를 얻습니다. (현재 스테이지에 비례)",listOf("전투")),
    "대관식" to Pair ("전략가의 왕관 1개를 획득합니다. 전략가의 왕관, 방패, 망토가 장착 유닛에게 추가로 공격 속도를 30%, 공격력을 30%, 주문력을 40 부여합니다.",listOf("기타","전투")),
    "덩치 큰 친구들 III" to Pair ("정확히 한 명의 다른 아군 옆에서 전투를 시작한 아군이 체력을 330 얻습니다. 한 챔피언이 사망하면 다른 한 명이 10초 동안 최대 체력의 18%에 해당하는 보호막을 얻습니다.",listOf("전투")),
    "도굴꾼 II" to Pair ("플레이어가 탈락할 때마다 해당 플레이어의 완성 아이템 하나를 선택해 획득합니다.", listOf("아이템")),
    "두 가지 목적" to Pair ("라운드마다 처음으로 경험치를 구매하면 2골드를 획득합니다. 경험치를 구매할 때마다 상점을 새로고침합니다.",listOf("돈")),
    "뒤바뀐 운명" to Pair ("푸른 파수꾼을 획득합니다. 60골드를 보유한 상태로 라운드 시작 및 플레이어 대상 전투에서 승리한 후 멜과 마법공학 총검을 획득합니다. 조건 완료 순서는 상관이 없습니다.",listOf("아이템", "챔피언")),
    "뒷골목 거래" to Pair ("수상한 외투 1개를 획득합니다. 플레이어 대상 전투 3회 후 속임수 거울 1개를 획득합니다.",listOf("아이템")),
    "레벨 업!" to Pair ("경험치를 구매하면 2의 추가 경험치를 얻습니다. 즉시 12의 경험치를 얻습니다.",listOf("돈")),
    "마법사 왕관" to Pair ("마법사 상징, 적응형 투구, 블라디미르를 획득합니다.",listOf("상징","챔피언","아이템")),
    "마지막 손질" to Pair ("지원 모루와 완성 아이템 모루를 1개씩 획득합니다.",listOf("아이템")),
    "매복자 왕관" to Pair ("매복자 상징, 정의의 손길, 카밀을 획득합니다.",listOf("상징","아이템","챔피언")),
    "반군 왕관" to Pair ("반군 상징, 보석 건틀릿, 아칼리를 획득합니다.",listOf("상징","아이템","챔피언")),
    "방랑하는 조련사 II" to Pair ("6골드와 영구 장착된 상징 3개를 지닌 훈련 봇을 획득합니다.",listOf("상징")),
    "분노 조절 문제" to Pair ("현재 및 이후 모든 완성 아이템이 방어력과 마법 저항력을 각각 45 제공하는 구인수의 격노검으로 바뀝니다. 구인수의 격노검 2중첩마다 공격력과 주문력을 1% 얻습니다.",listOf("기타")),
    "불타는 영혼 II" to Pair ("전투 시작: 가장 공격 속도가 높은 아군 챔피언이 주문력을 35, 공격 속도를 30% 얻습니다. 이 효과는 3초마다 다른 아군에게 반복해서 적용됩니다.",listOf("전투")),
    "붉고 푸른 효과" to Pair ("붉은 덩굴정령, 푸른 파수꾼, 챔피언 복제기를 1개씩 획득합니다.",listOf("아이템")),
    "빌붙기" to Pair ("전투 시작: 1개 이하의 아이템을 장착한 챔피언 최대 5명이 아이템을 장착한 가장 가까이 있는 아군으로부터 무작위 완성 아이템 복사본 1개를 획득합니다.",listOf("전투")),
    "빛비늘 정수" to Pair ("거물의 갑옷 1개를 획득합니다. 5라운드 후, 도박꾼의 칼날 1개를 획득합니다.",listOf("아이템","돈")),
    "빛의 속도" to Pair ("붉은 덩굴정령, 구인수의 격노검, 곡궁, 자석제거기를 획득합니다.",listOf("아이템","자석제거기")),
    "삼인방 II" to Pair ("3단계 챔피언을 3명 획득합니다. 아군이 공격 속도를 9% 얻습니다. 전투 시작: 3단계 무작위 챔피언 3명이 체력을 330, 공격 속도를 24% 얻습니다.",listOf("전투","챔피언")),
    "생일 선물" to Pair ("레벨 업할 때마다 2성 챔피언을 획득합니다. 획득하는 챔피언의 단계는 상점 레벨보다 4 낮습니다. (최소 1단계)",listOf("챔피언")),
    "선도자 왕관" to Pair ("선도자 상징, 쇼진의 창, 레나타 글라스크를 획득합니다.",listOf("상징","아이템","챔피언")),
    "수호자의 선택" to Pair ("레벨 업하면 강력한 아이템을 획득합니다.\n4레벨: 조합 아이템 모루\n6레벨: 완성 아이템 모루\n7레벨: 찬란한 아이템 5가지 중 1개 선택",listOf("아이템")),
    "실험체 왕관" to Pair ("실험체 상징, 피바라기, 우르곳을 획득합니다.",listOf("상징","아이템","챔피언")),
    "아카데미 왕관" to Pair ("아카데미 상징과 무작위 후원 아이템을 획득합니다.",listOf("상징","아이템")),
    "양보다 질" to Pair ("아이템을 정확히 1개 장착한 유닛의 아이템이 찬란한 아이템으로 업그레이드됩니다. 찬란한 아이템을 장착한 유닛이 추가로 체력을 12% 얻습니다. 자석제거기 2개를 획득합니다.\n도적의 장갑은 아이템 여러 개로 간주합니다.",listOf("전투", "자석제거기")),
    "연속 타격" to Pair ("지크의 전령 1개를 획득합니다. 지크의 전령 효과를 받는 챔피언이 추가로 치명타 확률을 35% 얻습니다.",listOf("아이템","전투")),
    "영광스러운 진화" to Pair ("적응형 투구를 획득합니다. 챔피언을 9명 업그레이드하면 빅토르와 보석 건틀릿을 획득합니다.",listOf("아이템","챔피언")),
    "영원한 브론즈 II" to Pair ("아군이 브론즈 등급 특성당 피해 증폭을 3.5%, 내구력을 2% 얻습니다.", listOf("전투")),
    "예상된 예상 밖의 결과" to Pair ("즉시 및 다음 2번의 스테이지 시작 시 주사위를 3개 굴립니다. 주사위 눈의 총합에 따라 다양한 보상을 획득합니다.",listOf("랜덤 보상")),
    "요지부동" to Pair ("란두인의 예언 1개를 획득합니다. 란두인의 예언 사거리가 1칸 늘어나고 아이템의 효과가 60% 증가합니다.",listOf("아이템","전투")),
    "우정과 유령" to Pair ("아군 챔피언이 사망할 때마다 사망한 챔피언의 역할군에 따라 아군이 영구적으로 체력을 5, 또는 공격력을 1%, 또는 주문력을 1 얻습니다.",listOf("전투")),
    "유물 제작소" to Pair ("각 라운드 시작 시 대기석에 놓인 완성 아이템이 무작위 유물 아이템으로 변합니다. 유물 모루 1개 및 아이템 제거기 2개를 획득합니다.",listOf("아이템","자석제거기")),
    "유연함" to Pair ("무작위 상징 1개를 획득합니다. 매 스테이지 시작 시 무작위 상징 1개를 획득합니다. 모든 아군이 장착한 상징 1개당 체력을 40 얻습니다.",listOf("상징","전투")),
    "응당한 대가" to Pair ("즉시 6레벨이 되고 4의 경험치를 얻습니다. 추후 증강이 자동으로 선택됩니다.",listOf("돈")),
    "이상한 금요일" to Pair ("무한한 삼위일체를 획득합니다. 플레이어 대상 전투를 5번 치른 후 무한한 삼위일체를 하나 더 획득합니다.",listOf("아이템")),
    "이상한 금요일 +" to Pair ("무한한 삼위일체를 획득합니다. 플레이어 대상 전투를 3번 치른 후 무한한 삼위일체를 하나 더 획득합니다..",listOf("아이템")),
    "이제 내가 주인공이야" to Pair ("어울리는 공격 아이템을 장착한 골렘 1명을 얻습니다. 이 골렘은 각 스테이지 시작 시 더 강해집니다.",listOf("전투")),
    "자동기계 왕관" to Pair ("자동기계 상징, 방패파괴자, 녹턴을 획득합니다.",listOf("상징","아이템","챔피언")),
    "장기 투자" to Pair ("더 이상 이자를 획득하지 않으며, 지금 바로 15골드를 획득합니다. 라운드 시작 시 4의 경험치를 획득합니다.",listOf("돈","연승")),
    "저격수 왕관" to Pair ("저격수 상징, 무한의 대검, 제리를 획득합니다.",listOf("상징","챔피언","아이템")),
    "전략가의 주방" to Pair ("전략가의 망토와 무작위 상징을 1개씩 획득합니다.",listOf("상징","기타")),
    "전리품 III" to Pair ("적 유닛을 처치하면 40% 확률로 전리품을 획득합니다.",listOf("랜덤 보상","연승")),
    "점화단 왕관" to Pair ("점화단 상징, 수호자의 맹세, 스카를 획득합니다.",listOf("상징","아이템","챔피언")),
    "정복자 왕관" to Pair ("정복자 상징, 최후의 속삭임, 렐을 획득합니다.",listOf("상징","아이템","챔피언")),
    "주사위 던지기" to Pair ("장난꾸러기의 장갑을 획득합니다. 장난꾸러기의 장갑은 라운드마다 두 개의 무작위 찬란한 아이템을 장착시킵니다.",listOf("아이템")),
    "주시자 왕관" to Pair ("주시자 상징, 굳건한 심장, 스카를 획득합니다.",listOf("상징","아이템","챔피언")),
    "준비 운동 III" to Pair ("아군의 공격 속도가 즉시 12% 증가합니다. 매 라운드 종료 후에 효과가 2% 추가로 증가합니다.",listOf("전투")),
    "지배자 왕관" to Pair ("지배자 상징, 수호자의 맹세, 블리츠크랭크를 획득합니다.",listOf("상징","챔피언","아이템")),
    "진정한 모습" to Pair ("피바라기를 획득합니다. 다른 플레이어에게 피해를 35 입히면 워윅과 스테락의 도전을 획득합니다.",listOf("아이템","챔피언")),
    "집행자 왕관" to Pair ("집행자 상징, 무한의 대검, 매디를 획득합니다.",listOf("상징","아이템","챔피언")),
    "찬란한 유물" to Pair ("찬란한 아이템 5개 중 하나를 선택합니다. 자석제거기를 획득합니다.",listOf("아이템","자석제거기")),
    "찬란한 재조정기" to Pair ("걸작 업그레이드와 조합 아이템 모루 1개를 획득합니다.",listOf("아이템")),
    "최대치" to Pair ("최대 레벨이 7이 됩니다. 팀 규모를 +1 증가시키는 전략가의 방패 1개와 60골드를 획득합니다.",listOf("돈","기타")),
    "출세가도" to Pair ("경험치 구매 비용이 1 감소합니다. 레벨 업할 때마다 체력을 2, 무료 새로고침을 3회 얻습니다.",listOf("돈","체력")),
    "친구 만들기!" to Pair ("무작위 3성 1단계 챔피언과 8골드를 획득합니다.",listOf("돈","챔피언")),
    "투사 왕관" to Pair ("투사 상징, 스테락의 도전, 갱플랭크를 획득합니다.",listOf("상징","챔피언","아이템")),
    "투자 전략 II" to Pair ("5골드를 획득합니다. 이자로 획득한 골드당 아군 챔피언이 영구적으로 최대 체력을 9 얻습니다. 최대 이자가 7골드로 증가합니다.",listOf("전투","돈")),
    "투자+" to Pair ("26골드를 획득합니다. 각 라운드 시작 시, 50골드를 모은 상태에서 초과 10골드마다 상점 새로고침을 1회 획득합니다. (최대 80골드)",listOf("돈")),
    "투자++" to Pair ("45골드를 획득합니다. 각 라운드 시작 시, 50골드를 모은 상태에서 초과 10골드마다 상점 새로고침을 1회 획득합니다. (최대 80골드)",listOf("돈")),
    "특성: 또 다른 결말" to Pair ("실코가 스킬을 사용할 때마다 밴더가 영구적으로 최대 체력을 30 얻습니다. 밴더가 사망할 때마다 실코가 영구적으로 주문력을 6 얻습니다.\n2성 밴더, 실코, 쇼진의 창을 획득합니다.",listOf("특성","전투","아이템","챔피언")),
    "특성: 천재" to Pair ("하이머딩거가 스킬을 사용하면 에코가 3개의 잔상을 내보내 각각 33%의 피해를 입힙니다. 에코가 스킬을 사용하면 하이머딩거가 미사일을 발사해 각각 120%만큼 피해를 입힙니다.\n하이머딩거와 에코, 보석 건틀릿을 획득합니다.",listOf("특성","전투","챔피언","아이템")),
    "파묻힌 보물 III" to Pair ("다음 6번의 라운드 시작 시 무작위 조합 아이템을 획득합니다. (이번 라운드 포함)",listOf("아이템")),
    "판도라의 아이템 III" to Pair ("라운드 시작: 대기석의 아이템이 무작위로 변합니다. (전략가의 왕관, 뒤집개 제외)\n무작위 찬란한 아이템 1개를 획득합니다.",listOf("기타","아이템")),
    "포수 왕관" to Pair ("포수 상징, 루난의 허리케인, 트리스타나를 획득합니다.",listOf("상징","아이템","챔피언")),
    "프리즘 공급" to Pair ("다음 플레이어 대상이 아닌 전투 라운드에서 놀라운 전리품이 든 추가 프리즘 구 1개를 획득합니다. 모든 골드 및 프리즘 구에서 더 많은 전리품을 획득할 수 있습니다!",listOf("랜덤 보상")),
    "프리즘 티켓" to Pair ("상점을 새로고침할 때마다 45% 확률로 무료 새로고침을 획득합니다.",listOf("돈")),
    "행운의 장갑" to Pair ("도적의 장갑이 항상 챔피언에게 최적의 아이템을 제공합니다. 연습용 장갑을 2개 획득합니다. 5라운드 후에 하나 더 획득합니다.",listOf("아이템","전투")),
    "행운의 장갑+" to Pair ("도적의 장갑이 항상 챔피언에게 최적의 아이템을 제공합니다. 연습용 장갑을 3개 획득합니다.",listOf("아이템","전투")),
    "헤지 펀드" to Pair ("25골드를 획득합니다. 최대 이자가 10골드로 증가합니다.\n이자는 저축한 10골드당 추가로 획득하는 골드입니다.",listOf("돈")),
    "혼돈의 부름" to Pair ("강력한 무작위 보상을 획득합니다. (다음 중 하나를 무작위로 획득합니다.)\n- [58골드], [경험치 64], [사라지지 않는 새로고침 40회], [지크의 전령 2개를 장착한 훈련 봇], [강철의 솔라리 펜던트 2개를 장착한 훈련 봇]\n- [선체파괴자 3개와 자석제거기 1개], [저격수의 집중 3개와 자석제거기 1개], [무작위 3성 3단계 유닛과 20골드], [도적의 장갑 3개와 자석제거기 1개], [무작위 상징 5개(중복 없음)와 자석제거기 1개]",listOf("랜덤 보상")),
    "화공 남작 왕관" to Pair ("화공 남작 상징, 내셔의 이빨, 스미치를 획득합니다.",listOf("상징","아이템","챔피언")),
    "환한 달빛" to Pair ("전투 시작: 무작위 1단계 챔피언 1명이 해당 라운드에만 4성으로 업그레이드되며 공격력을 50%, 주문력을 5 얻습니다.",listOf("전투")),
    "황금 알" to Pair ("11턴 후 부화하는 황금 알을 획득합니다. 알이 부화하면 막대한 전리품을 얻습니다. 플레이어 대상 전투에서 승리하면 부화기 타이머를 한 턴만큼 가속합니다.",listOf("랜덤 보상")),
    "흥청망청" to Pair ("레벨 업하면 레벨과 동일한 수의 무료 상점 새로고침 기회를 얻습니다. 4골드를 획득합니다.", listOf("돈"))
)
