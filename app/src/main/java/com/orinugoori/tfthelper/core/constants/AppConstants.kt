package com.orinugoori.tfthelper.core.constants

/**
 * 앱 전체에서 사용되는 상수들을 정의하는 객체
 * Clean Architecture의 Core Layer
 */
object AppConstants {
    
    // 앱 기본 정보
    const val APP_TITLE = "TFT 증강체 확률 계산기"
    const val APP_DESCRIPTION = "롤토체스 증강확률 계산 | 증강 리스트 확인"
    const val SPLASH_DELAY = 3000L
    
    // 라우트 상수
    object Routes {
        const val FIRST_PAGE = "firstPage"
        const val SECOND_PAGE = "secondPage"
        const val THIRD_PAGE = "thirdPage"
        const val AUGMENTS_SCREEN = "augmentsScreen"
    }
    
    // 증강체 티어 상수
    object AugmentTiers {
        const val SILVER = "실버"
        const val GOLD = "골드"
        const val PRISM = "프리즘"
        
        val ALL_TIERS = listOf(SILVER, GOLD, PRISM)
    }
    
    // API 관련 상수
    object API {
        const val DATA_DRAGON_BASE_URL = "https://ddragon.leagueoflegends.com/"
        const val COMMUNITY_DRAGON_BASE_URL = "https://raw.communitydragon.org/"
        const val DEFAULT_VERSION = "15.1.1"
        
        // API 엔드포인트
        const val AUGMENTS_ENDPOINT = "cdn/{version}/data/ko_KR/tft-augments.json"
        const val VERSIONS_ENDPOINT = "api/versions.json"
        const val COMMUNITY_DRAGON_ENDPOINT = "latest/cdragon/tft/ko_kr.json"
    }
    
    // 캐시 관련 상수
    object Cache {
        const val PREFERENCES_NAME = "tft_helper_cache"
        const val CACHE_EXPIRY_TIME = 24 * 60 * 60 * 1000L // 24시간
        
        // 캐시 키들
        const val KEY_AUGMENTS = "cached_augments"
        const val KEY_TIMESTAMP = "cache_timestamp"
        const val KEY_VERSION = "api_version"
        const val KEY_SEARCH_HISTORY = "search_history"
    }
    
    // 확률 계산 상수
    object Probabilities {
        // 첫 번째 증강체 기본 확률
        val BASE_FIRST_PROBABILITIES = mapOf(
            AugmentTiers.SILVER to 100,
            AugmentTiers.GOLD to 0,
            AugmentTiers.PRISM to 0
        )
        
        // 두 번째 증강체 기본 확률
        val BASE_SECOND_PROBABILITIES = mapOf(
            AugmentTiers.SILVER to 85,
            AugmentTiers.GOLD to 15,
            AugmentTiers.PRISM to 0
        )
        
        // 세 번째 증강체 기본 확률
        val BASE_THIRD_PROBABILITIES = mapOf(
            AugmentTiers.SILVER to 60,
            AugmentTiers.GOLD to 30,
            AugmentTiers.PRISM to 10
        )
    }
    
    // UI 관련 상수
    object UI {
        const val SEARCH_DELAY = 300L // 검색 디바운스 시간
        const val ANIMATION_DURATION = 300
        const val DEFAULT_PADDING = 16
        
        // 이미지 관련
        const val AUGMENT_IMAGE_SIZE = 48
        const val LOGO_SIZE = 200
        const val GIF_SIZE = 100
    }
    
    // 이미지 URL 패턴
    object ImageUrls {
        const val DATA_DRAGON_AUGMENT_BASE = "https://ddragon.leagueoflegends.com/cdn/{version}/img/tft-augment/"
        const val COMMUNITY_DRAGON_AUGMENT_BASE = "https://raw.communitydragon.org/latest/game/assets/ux/tft/augmenticons/"
        
        // 기본 이미지
        const val DEFAULT_AUGMENT_IMAGE = "default_augment.png"
        const val APP_LOGO = "tft_helper_text_logo"
        const val LOADING_GIF = "rotate_spatula"
    }
    
    // 검색 관련 상수
    object Search {
        const val MAX_SEARCH_HISTORY = 10
        const val MIN_SEARCH_LENGTH = 1
        const val DEBOUNCE_TIME = 300L
        
        // 초성 검색 패턴
        val CHOSUNG_PATTERN = arrayOf(
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )
    }
    
    // 네트워크 관련 상수
    object Network {
        const val CONNECT_TIMEOUT = 30L
        const val READ_TIMEOUT = 30L
        const val WRITE_TIMEOUT = 30L
        
        // 재시도 설정
        const val MAX_RETRY_COUNT = 3
        const val RETRY_DELAY = 1000L
    }
    
    // 로그 태그
    object LogTags {
        const val REPOSITORY = "AugmentRepository"
        const val API = "TFTApi"
        const val CACHE = "Cache"
        const val SEARCH = "Search"
        const val PROBABILITY = "Probability"
    }
}