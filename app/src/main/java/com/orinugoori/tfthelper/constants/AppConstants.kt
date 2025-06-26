package com.orinugoori.tfthelper.constants

object AppConstants {


    // API 관련 상수 (ApiInterface.kt/ RetrofitClient.kt)
    const val DATA_DRAGON_BASE_URL = "https://ddragon.leagueoflegends.com/"
    const val COMMUNITY_DRAGON_BASE_URL = "https://raw.githubusercontent.com/CommunityDragon/Data/master/"
    const val DEFAULT_API_VERSION = "15.1.1" // 기본 API 버전 (AugmentRepository.kt에서 이동)

    // 캐시 관련 상수 (AugmentRepository.kt에서 이동)
    const val AUGMENT_CACHE_PREFS_NAME = "augment_cache"
    const val KEY_CACHED_AUGMENTS = "cached_augments"
    const val KEY_CACHED_VERSION = "cached_version"
    const val KEY_CACHE_TIMESTAMP = "cache_timestamp"
    const val CACHE_DURATION_MS = 24 * 60 * 60 * 1000L // 24시간

    // 증강체 필터링/제외 이름 (AugmentRepository.kt에서 이동)
    // Set<String> 타입은 const val이 될 수 없으므로, companion object나 별도의 변수로 둡니다.
    val EXCLUDED_AUGMENT_NAMES = setOf(
        "구덩이에 굴복하라",
        "넋을 빼놓는 공연",
        "매각",
        "사이버네틱 이식술 Ⅰ",
        "사이버네틱 집합체",
        "감정의 유대",
        "거물 중의 거물",
        "대마불사",
        "더블 펑크",
        "동전 투입",
        "두근두근",
        "메탈 마니아",
        "번쩍번쩍",
        "빨라지는 리듬",
        "영롱하고 황홀한",
        "영웅적인 존재",
        "우리는 하나",
        "이게 재즈다",
        "죽음의 고통",
        "칼끝에 올라선 삶",
        "템포를 높여라",
        "팬을 위하여",
        "해방",
        "현상금 사냥꾼",
        "인재 물색"
    )

    // 광고 관련 상수 (AdViewModel.kt에서 이동 - 테스트 ID)
    // 실제 운영 ID는 build.gradle에서 관리하지만, 테스트 ID는 여기에 둘 수 있습니다.
    const val ADMOB_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712" // 테스트 전면 광고 ID

    // 검색 기록 관련 상수 (AugmentViewModel.kt에서 이동)
    const val SEARCH_HISTORY_PREFS_NAME = "search_history"
    const val KEY_SEARCH_HISTORY = "history"
    const val MAX_SEARCH_HISTORY_ITEMS = 10
    const val SEARCH_DEBOUNCE_MILLIS = 300L // 검색어 입력 후 필터링 대기 시간
    const val MAX_SEARCH_SUGGESTIONS = 5 // 자동 완성 제안 최대 개수
}