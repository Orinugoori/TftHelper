package com.orinugoori.tfthelper.core.constants

/**
 * 앱 전체에서 사용되는 상수값들을 관리하는 클래스
 */
object AppConstants {
    
    // 앱 정보
    const val APP_NAME = "TFT Helper"
    const val APP_TITLE = "TFT 증강체 확률 계산기"
    const val APP_DESCRIPTION = "롤토체스 증강확률 계산 | 증강 리스트 확인"
    
    // 화면 라우트
    object Routes {
        const val FIRST_PAGE = "firstPage"
        const val SECOND_PAGE = "secondPage"
        const val THIRD_PAGE = "thirdPage"
        const val CALCULATOR = "calculator"
        const val AUGMENTS = "augments"
    }
    
    // 증강체 티어
    object AugmentTiers {
        const val ALL = "전체"
        const val SILVER = "실버"
        const val GOLD = "골드"
        const val PRISM = "프리즘"
        
        val ALL_TIERS = listOf(ALL, SILVER, GOLD, PRISM)
        val SELECTION_TIERS = listOf(SILVER, GOLD, PRISM)
    }
    
    // UI 상수
    object UI {
        const val DEFAULT_PADDING = 16
        const val CARD_CORNER_RADIUS = 12
        const val BUTTON_HEIGHT = 60
        const val ICON_SIZE = 64
        const val TIER_BUTTON_HEIGHT = 56
        
        // 애니메이션
        const val ANIMATION_DURATION = 300
        const val SPLASH_DURATION = 3000L
    }
    
    // 메시지
    object Messages {
        const val SELECT_FIRST_AUGMENT = "첫번째 증강을 선택해 주세요"
        const val SELECT_SECOND_AUGMENT = "두번째 증강을 선택해 주세요"
        const val FIRST_AUGMENT_LABEL = "첫번째 증강"
        const val SECOND_AUGMENT_LABEL = "두번째 증강"
        const val THIRD_AUGMENT_LABEL = "세번째 증강"
        const val SELECT_OPTION = "선택 하기"
        const val NEXT_BUTTON = "다음"
        const val BACK_TO_FIRST = "처음으로 돌아가기"
        const val LOADING_AUGMENTS = "증강 데이터를 불러오는 중..."
        const val CACHE_EXPIRED = "캐시가 만료되었습니다\n새로운 데이터를 가져오는 중..."
        const val NETWORK_ERROR = "네트워크 연결 오류\n인터넷 연결을 확인하고 다시 시도해주세요"
    }
    
    // 광고 관련
    object Ads {
        const val TEST_INTERSTITIAL_AD_ID = "ca-app-pub-3940256099942544/1033173712"
    }
    
    // 캐시 관련
    object Cache {
        const val AUGMENT_CACHE_PREFS = "augment_cache"
        const val SEARCH_HISTORY_PREFS = "search_history"
        const val CACHED_AUGMENTS_KEY = "cached_augments"
        const val CACHED_VERSION_KEY = "cached_version"
        const val CACHE_TIMESTAMP_KEY = "cache_timestamp"
        const val CACHE_DURATION_MS = 24 * 60 * 60 * 1000L // 24시간
        const val MAX_SEARCH_HISTORY = 10
    }
    
    // API 관련
    object Api {
        const val DATA_DRAGON_BASE_URL = "https://ddragon.leagueoflegends.com/"
        const val COMMUNITY_DRAGON_BASE_URL = "https://raw.githubusercontent.com/CommunityDragon/Data/master/"
        const val DEFAULT_VERSION = "15.1.1"
        const val DEFAULT_LANGUAGE = "ko_KR"
        const val CONNECT_TIMEOUT = 30L
        const val READ_TIMEOUT = 30L
    }
    
    // 검색 관련
    object Search {
        const val MIN_SEARCH_LENGTH = 3
        const val MAX_SUGGESTIONS = 5
        const val SEARCH_PLACEHOLDER = "증강체 검색... (초성 검색 지원)"
    }
}