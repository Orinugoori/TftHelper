package com.orinugoori.tfthelper.data.constants

import com.orinugoori.tfthelper.data.model.AugmentData
import com.orinugoori.tfthelper.data.model.augmentData
import com.orinugoori.tfthelper.data.constants.AugmentKeywords.*

/**
 * 실버 등급 증강 데이터
 * API에서 제공하지 않는 키워드와 설명을 수동으로 관리
 */
object SilverAugmentData {
    val data: Map<String, AugmentData> = mapOf(
        "가지 뻗기" to augmentData(
            "무작위 상징과 재조합기를 획득합니다.",
            SYMBOL, RECOMBINATOR
        ),
        "거대한 거인" to augmentData(
            "현재 및 최대 플레이어 체력이 20 증가합니다. 공동 선택 라운드 시 일찍 움직일 수 있지만 이동 속도가 큰 폭으로 감소합니다.",
            HEALTH
        ),
        "검무" to augmentData(
            "이렐리아를 획득합니다. 가장 강한 아군 이렐리아가 공격 속도를 40% 얻고 두 대상 사이로 돌진해 두 대상 모두에게 물리 피해를 입히는 새로운 스킬을 얻습니다.",
            HERO
        ),
        "결과 예측" to augmentData(
            "연승을 +4연승으로 설정합니다. 4골드를 획득합니다.",
            MONEY, ETC
        ),
        "고독한 영웅" to augmentData(
            "마지막으로 생존한 아군 유닛이 공격 속도를 140%, 내구력을 35% 얻습니다.",
            BATTLE
        ),
        "과적" to augmentData(
            "다음 스테이지는 대기석이 1개가 됩니다. 그 다음, 조합 아이템 3개를 획득합니다.",
            ITEM
        ),
        "국왕시해자" to augmentData(
            "플레이어 대상 전투 승리 후 1골드를 획득합니다. 상대 플레이어의 체력이 더 높았다면, 대신 4골드를 획득합니다. 즉시 1골드를 획득합니다.",
            MONEY
        ),
        "급매" to augmentData(
            "라운드마다 상점에서 무작위 챔피언을 1명 훔칩니다. 1골드를 획득합니다.",
            MONEY
        ),
        "끈끈한 우정" to augmentData(
            "하급 챔피언 복제기를 획득합니다. 플레이어 대상 전투를 7회 치른 후 다시 획득합니다.",
            ETC
        ),
        "눈에는 눈" to augmentData(
            "아군 챔피언이 15명 사망할 때마다 무작위 조합 아이템 1개를 획득합니다. (최대 4개)",
            ITEM
        ),
        "눈에는 눈+" to augmentData(
            "무작위 조합 아이템 1개를 획득합니다. 아군 챔피언이 13명 사망할 때마다 조합 아이템을 추가로 1개 획득합니다. (최대 3개)",
            ITEM
        ),
        "다 쓸 데가 있다니까 I" to augmentData(
            "아이템을 보유하지 않은 챔피언이 죽을 때 50%의 확률로 1골드를 떨어뜨립니다.",
            MONEY
        ),
        "대격변 생성기" to augmentData(
            "전장에 있는 챔피언들이 단계가 1 높은 무작위 챔피언으로 영구히 바뀝니다. 자석제거기 2개를 획득합니다.",
            ETC, MAGNET_REMOVER
        ),
        "덩치 큰 친구들 I" to augmentData(
            "정확히 한 명의 다른 아군 옆에서 전투를 시작한 아군이 체력을 100 얻습니다. 한 챔피언이 사망하면 다른 한 명이 10초 동안 최대 체력의 10%에 해당하는 보호막을 얻습니다.",
            BATTLE
        ),
        "도둑 무리 I" to augmentData(
            "도적의 장갑 1개를 획득합니다.",
            ITEM
        ),
        "마나순환 I" to augmentData(
            "후방 가로 1열에서 전투를 시작하는 아군 유닛이 기본 공격마다 추가 마나를 2 얻습니다.",
            BATTLE
        ),
        "맹렬한 공세" to augmentData(
            "아군의 기본 공격이 대상을 태워 5초 동안 대상 최대 체력의 5%에 해당하는 피해를 입힙니다. 또한 기본 공격 시 받는 치유 효과를 33% 감소시킵니다.",
            BATTLE
        ),
        "모두를 위한 하나 I" to augmentData(
            "아군이 전장에 있는 고유 1단계 챔피언 한 명당 최대 체력을 2%, 피해 증폭을 150% 얻습니다. 1단계 챔피언 2명을 획득합니다.",
            BATTLE
        ),
        "미친 화학자" to augmentData(
            "신지드를 획득합니다. 가장 강한 아군 신지드가 기본 공격을 할 수 없게 되지만 지속적으로 뛰어다니며 맹독의 자취를 남겨 지속 마법 피해를 입힙니다. 신지드의 스킬은 항상 자신을 대상으로 하며 대신 모든 피해 흡혈 및 이동 속도를 20% 부여합니다.\n마법 피해: 140% / 210% / 315% / 420%",
            HERO
        ),
        "부스러기" to augmentData(
            "각 공동 선택 이후 선택되지 않은 유닛 1명과 해당 유닛이 보유한 아이템을 획득합니다. 1골드를 획득합니다.",
            ITEM, ETC
        ),
        "부식" to augmentData(
            "전방 가로 2열에 있는 적 챔피언들의 방어력 및 마법 저항력이 2초마다 3 감소합니다.",
            BATTLE
        ),
        "새로고침 이월" to augmentData(
            "사용하지 않은 증강 새로고침 1회마다 무료 상점 새로고침을 3회 얻습니다. 3골드를 획득합니다.",
            MONEY
        ),
        "새로고침의 날 I" to augmentData(
            "무료 상점 새로고침을 11회 얻습니다.",
            MONEY
        ),
        "생존자" to augmentData(
            "플레이어 3명이 탈락하면 60골드를 획득합니다.",
            MONEY
        ),
        "서열 상승 I" to augmentData(
            "아군이 사망할 때마다 특성을 한 개 이상 공유하는 아군이 주문력을 3, 공격력을 3%, 방어력을 3, 마법 저항력을 3 얻습니다.",
            BATTLE
        ),
        "수호자의 친구" to augmentData(
            "즉시 무작위 2단계 챔피언을 획득합니다. 레벨을 올릴 때마다 동일한 챔피언을 획득합니다.",
            CHAMPION, BATTLE
        ),
        "슈퍼스타 I" to augmentData(
            "아군의 피해량이 5% 증가합니다. 이 효과는 아군 3성 유닛 하나당 2% 증가합니다. 새로고침을 2회 얻습니다.",
            BATTLE
        ),
        "스승 I" to augmentData(
            "더 높은 단계의 아군 옆에서 전투를 시작한 아군이 공격 속도를 12%, 체력을 150 얻습니다.",
            BATTLE
        ),
        "신중한 제작" to augmentData(
            "플레이어 대상 전투를 8번 치른 후 유물 모루를 획득합니다.\n모루가 4가지 선택지를 제시합니다.",
            ITEM
        ),
        "아이템 꾸러미 I" to augmentData(
            "무작위 완성 아이템 1개를 획득합니다.",
            ITEM
        ),
        "아이템 수집가 I" to augmentData(
            "아군이 체력을 10 얻습니다. 장착한 아이템 한 종류당 아군이 체력을 2, 공격력을 1, 주문력을 1 추가로 얻습니다.",
            BATTLE
        ),
        "아이템 숙성" to augmentData(
            "완성 아이템을 4라운드 동안 대기석에 두면 지원 아이템 모루로 변합니다.",
            ITEM
        ),
        "연결 불가" to augmentData(
            "1단계 챔피언의 복사본을 1명씩 획득합니다.",
            CHAMPION
        ),
        "위력 강화" to augmentData(
            "다음 증강이 한 단계 높아집니다.",
            ETC
        ),
        "위약 효과" to augmentData(
            "8골드를 획득합니다. 아군의 공격 속도가 1% 증가합니다.",
            MONEY, BATTLE
        ),
        "위약 효과+" to augmentData(
            "15골드를 획득합니다. 아군의 공격 속도가 1% 증가합니다.",
            MONEY, BATTLE
        ),
        "위험한 행보" to augmentData(
            "아군 전략가가 체력을 20 잃지만, 플레이어 대상 전투를 7번 치른 후 30골드를 획득합니다.",
            MONEY
        ),
        "유리 대포 I" to augmentData(
            "후방 가로 1열에서 전투를 시작한 유닛이 전투 시작 시 체력이 80%로 조정되지만 피해 증폭을 12% 얻습니다.",
            BATTLE
        ),
        "유용한 금속" to augmentData(
            "조합 아이템 모루와 4골드를 획득합니다.\n모루가 4가지 선택지를 제시합니다.",
            ITEM, MONEY
        ),
        "은수저" to augmentData(
            "10의 경험치를 획득합니다.",
            MONEY
        ),
        "의무병" to augmentData(
            "스텝을 획득합니다. 가장 강한 아군 스텝의 스킬 마나 소모량이 10 감소하지만, 더 이상 회복할 수 없습니다. 스텝의 스킬이 모든 피해 흡혈을 30 부여하고 3회 타격해 각각 65%의 피해를 입힙니다.",
            HERO
        ),
        "임무 재시작" to augmentData(
            "전장과 대기석의 모든 아군 챔피언을 제거합니다. 무작위 3단계 2성 챔피언 2명, 2단계 2성 챔피언 3명, 1단계 2성 챔피언 1명을 획득합니다.",
            CHAMPION
        ),
        "입맛대로 고르는 부품" to augmentData(
            "조합 아이템을 얻을 때마다 조합 아이템 모루를 대신 획득합니다. 무작위 조합 아이템을 획득합니다.",
            ITEM
        ),
        "전리품 I" to augmentData(
            "적 유닛을 처치하면 25% 확률로 전리품을 획득합니다.",
            WINNING_STREAK, RANDOM_REWARD
        ),
        "젊음, 광란, 자유" to augmentData(
            "공동 선택 라운드에서 언제나 자유롭게 움직일 수 있습니다. 2골드를 획득합니다.",
            ETC, WINNING_STREAK
        ),
        "점심값" to augmentData(
            "적 전략가에게 8의 피해를 입힐 때마다 2골드를 획득합니다.",
            MONEY, WINNING_STREAK
        ),
        "정교한 공예" to augmentData(
            "완성 아이템을 만들 때마다 새로고침을 2회 얻습니다.",
            MONEY
        ),
        "정렬" to augmentData(
            "아군이 전방 2열에서 전투를 시작하는 유닛 하나당 방어력 및 마법 저항력을 2.5 얻습니다.",
            BATTLE
        ),
        "조작된 상점" to augmentData(
            "다음 상점 및 각 4번째 상점이 모두 3단계 챔피언으로 채워집니다.",
            ETC
        ),
        "조작된 상점+" to augmentData(
            "다음 상점 및 각 4번째 상점이 모두 3단계 챔피언으로 채워집니다. 새로고침을 5회 얻습니다.",
            MONEY, ETC
        ),
        "종목 분산 투자" to augmentData(
            "라운드마다 활성화된 고유 특성이 아닌 특성 3개당 1골드를 획득합니다. 1골드를 획득합니다.",
            MONEY
        ),
        "종목 분산 투자+" to augmentData(
            "라운드마다 활성화된 고유 특성이 아닌 특성 3개당 1골드를 획득합니다. 4골드를 획득합니다.",
            MONEY
        ),
        "준비 운동 I" to augmentData(
            "아군의 공격 속도가 즉시 6% 증가합니다. 매 라운드 종료 후에 효과가 0.5% 추가로 증가합니다.",
            BATTLE
        ),
        "중심 잡기" to augmentData(
            "전장 중앙에서 전투를 시작하는 아군 챔피언의 피해량 증폭이 15%, 최대 체력이 15% 증가합니다.",
            BATTLE
        ),
        "지원 채굴" to augmentData(
            "훈련 봇 1개를 획득합니다. 훈련 봇이 7회 사망하면 무작위 지원 아이템을 획득하고 훈련 봇이 제거됩니다.",
            ITEM
        ),
        "지원 채굴+" to augmentData(
            "훈련 봇 1개를 획득합니다. 훈련 봇이 4회 사망하면 무작위 지원 아이템을 획득하고 훈련 봇이 제거됩니다.",
            ITEM
        ),
        "참는 자에게 복을" to augmentData(
            "지난 라운드에서 챔피언을 구매하지 않았다면 무료 새로고침을 2회 얻습니다.",
            MONEY
        ),
        "체력이 곧 재산 I" to augmentData(
            "아군이 모든 피해 흡혈을 10% 얻습니다. 아군의 회복량이 처음으로 총 10000만큼 누적되면 추가로 8골드를 획득합니다.",
            BATTLE, MONEY
        ),
        "트롤 나가신다" to augmentData(
            "트런들을 획득합니다. 가장 강한 아군 트런들의 스킬이 더 이상 체력을 회복시키지 않는 대신 5초 동안 공격 속도를 140%, 영구적으로 공격력을 1.5% 부여합니다. 트런들의 최대 마나가 50 감소합니다.",
            HERO
        ),
        "판도라의 대기석" to augmentData(
            "2골드를 획득합니다. 라운드가 시작될 때마다 대기석 가장 오른쪽 3명의 챔피언이 동일한 단계의 무작위 챔피언으로 변신합니다.",
            ETC
        ),
        "판도라의 아이템" to augmentData(
            "라운드 시작: 대기석의 아이템이 무작위로 변합니다. 무작위 조합 아이템 1개를 획득합니다.",
            ETC, ITEM
        ),
        "편식" to augmentData(
            "다른 모든 증강 선택에서 증강 새로고침 횟수를 +3 얻습니다. 7골드를 획득합니다.",
            MONEY, ETC
        ),
        "하나, 둘, 다섯!" to augmentData(
            "무작위 조합 아이템 1개, 2골드, 무작위 5단계 챔피언 1명을 획득합니다.",
            ITEM, CHAMPION, MONEY
        ),
        "하나, 둘, 셋" to augmentData(
            "1단계 챔피언 2명, 2단계 챔피언 2명, 3단계 챔피언 1명을 획득합니다.",
            CHAMPION
        ),
        "협력 I" to augmentData(
            "무작위 조합 아이템 1개, 무작위 3단계 챔피언 2명을 획득합니다.",
            CHAMPION, ITEM
        ),
        "후반 전문가" to augmentData(
            "9레벨에 도달하면 33골드를 획득합니다.",
            MONEY
        ),
        "후발 주자" to augmentData(
            "전장 및 대기석에 있는 챔피언을 판매합니다. 무작위 1단계 2성 챔피언 4명을 획득합니다. 다음 3라운드 동안 상점이 비활성화됩니다.",
            CHAMPION
        ),
        "후방 지원" to augmentData(
            "아군 4명 이상이 후방 가로 2열에서 전투를 시작했다면 아군이 공격 속도를 10% 얻습니다.",
            BATTLE
        ),
        "훈련 봇 변환" to augmentData(
            "전장 및 대기석에 있는 모든 챔피언을 잃습니다. 잃은 챔피언 체력 총합의 100%를 지닌 훈련 봇을 획득합니다.",
            BATTLE
        )
    )
}
