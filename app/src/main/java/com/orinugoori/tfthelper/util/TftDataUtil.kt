package com.orinugoori.tfthelper.util

import com.orinugoori.tfthelper.constants.AppConstants
import com.orinugoori.tfthelper.data.model.Augment
import com.orinugoori.tfthelper.data.model.AugmentResponse

/**
 * API 응답의 증강체 데이터를 처리하여 올바른 티어 정보를 추가하고 앱 모델로 변환
 * @param augmentResponse API에서 받은 AugmentResponse 객체
 * @return 처리된 Augment 모델 리스트
 */
fun processAugmentData(augmentResponse: AugmentResponse): List<Augment> {
    return augmentResponse.data.values.map { augmentDto -> // augmentDto로 명확하게 이름을 지정
        val tier = extractTierFromImageName(augmentDto.image.full) // augmentDto 사용
        val cleanedDescription = cleanHtmlTags(augmentDto.description) // augmentDto 사용

        Augment( // 새로운 Augment 데이터 모델 생성
            id = augmentDto.id,
            name = augmentDto.name,
            tier = tier,
            imageUrl = "https://ddragon.leagueoflegends.com/cdn/${augmentResponse.version}/img/tft-augment/${augmentDto.image.full}", // 이미지 URL 구성
            description = cleanedDescription
        )
    }.sortedBy { it.name }
}

/**
 * 더 간단하고 읽기 쉬운 버전의 이미지 파일명 기반 티어 추출 함수
 * 로마숫자(I, II, III)와 아라비아숫자(1, 2, 3) 모두 지원
 * TFT_Set13 같은 세트 정보는 무시하고 티어만 추출
 */
fun extractTierFromImageName(imageName: String): String {
    val cleanName = imageName.lowercase()

    // TFT_Set 패턴을 제거하여 세트 번호 간섭 방지
    val nameWithoutSet = cleanName.replace(Regex("tft_set\\d+"), "")

    return when {
        // 프리즘: 3 또는 III (세트 번호가 아닌 순수 티어 번호만)
        nameWithoutSet.contains("iii.") ||
                nameWithoutSet.matches(Regex(".*[^\\d]3\\.(png|jpg|jpeg|webp)$")) ||
                nameWithoutSet.matches(Regex(".*_3\\.(png|jpg|jpeg|webp)$")) -> "프리즘"

        // 골드: 2 또는 II (세트 번호가 아닌 순수 티어 번호만)
        nameWithoutSet.contains("ii.") ||
                nameWithoutSet.matches(Regex(".*[^\\d]2\\.(png|jpg|jpeg|webp)$")) ||
                nameWithoutSet.matches(Regex(".*_2\\.(png|jpg|jpeg|webp)$")) -> "골드"

        // 실버: 1 또는 I (세트 번호가 아닌 순수 티어 번호만)
        nameWithoutSet.contains("i.") ||
                nameWithoutSet.matches(Regex(".*[^\\d]1\\.(png|jpg|jpeg|webp)$")) ||
                nameWithoutSet.matches(Regex(".*_1\\.(png|jpg|jpeg|webp)$")) -> "실버"

        // 기본값: 실버
        else -> "실버"
    }
}

/**
 * HTML 태그 및 특수 문자를 정리하는 함수
 */
fun cleanHtmlTags(description: String): String {
    if (description.isBlank()) return "설명이 없습니다."

    var cleaned = description

    // HTML 태그 제거 (모든 종류의 태그)
    cleaned = cleaned.replace(Regex("<[^>]*>"), "")

    // TFT에서 자주 사용되는 플레이스홀더 제거
    cleaned = cleaned.replace(Regex("@[^@]*@"), "")
    cleaned = cleaned.replace(Regex("%[^%]*%"), "")
    cleaned = cleaned.replace(Regex("\\{\\{[^}]*\\}\\}"), "")

    // HTML 엔티티 변환
    cleaned = cleaned.replace("&nbsp;", " ")
    cleaned = cleaned.replace("&lt;", "<")
    cleaned = cleaned.replace("&gt;", ">")
    cleaned = cleaned.replace("&amp;", "&")
    cleaned = cleaned.replace("&quot;", "\"")
    cleaned = cleaned.replace("&#x27;", "'")

    // 연속된 공백을 하나로 통합
    cleaned = cleaned.replace(Regex("\\s+"), " ")

    // 앞뒤 공백 제거
    cleaned = cleaned.trim()

    return if (cleaned.isBlank()) "설명이 없습니다." else cleaned
}

/**
 * 증강체 이름을 정규화하는 함수
 */
fun normalizeAugmentName(name: String): String {
    return name.trim()
        .replace(Regex("\\s+"), " ") // 여러 공백을 하나로
        .replace(Regex("[^a-zA-Z0-9가-힣\\s]"), "") // <-- 영문자, 숫자, 한글, 공백을 제외한 모든 특수 문자 제거
        .lowercase() // <-- 전체를 소문자로 변환하여 검색 일관성 유지
}


/**
 * 한글 문자열에서 초성만 추출하는 함수
 */
fun getInitialConsonants(text: String): String {
    val sb = StringBuilder()
    for (char in text) {
        val ch = char.code
        if (ch in 0xAC00..0xD7A3) { // 한글 유니코드 범위
            val uniVal = ch - 0xAC00
            val cho = uniVal / (21 * 28)
            sb.append(CHO_SUNG[cho])
        } else {
            sb.append(char) // 한글이 아니면 그대로 추가
        }
    }
    return sb.toString()
}

private val CHO_SUNG = charArrayOf(
    'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
)

/**
 * 한글 초성 검색 지원 (입력된 쿼리가 텍스트의 초성으로 일치하는지 확인)
 */
fun isInitialConsonantMatch(text: String, query: String): Boolean {

    val textInitialConsonants = getInitialConsonants(text)
    val queryInitialConsonants = getInitialConsonants(query)

    return textInitialConsonants.contains(queryInitialConsonants, ignoreCase = true)
}

/**
 * 자동완성 제안 생성
 */
fun generateSearchSuggestions(augments: List<Augment>, query: String): List<String> {
    if (query.isBlank()) return emptyList()

    val normalizedQuery = normalizeAugmentName(query) // <-- 쿼리도 정규화
    val initialConsonantsQuery = getInitialConsonants(normalizedQuery) // 정규화된 쿼리에서 초성 추출

    return augments
        .map { it.name } // Augment 모델의 원본 이름 사용
        .filter { originalName ->
            val normalizedName = normalizeAugmentName(originalName) // <-- 원본 이름도 정규화
            val initialConsonantsName = getInitialConsonants(normalizedName)

            // 정규화된 이름 또는 초성으로 검색
            normalizedName.contains(normalizedQuery) || // ignoreCase는 이미 normalizeName에서 처리
                    initialConsonantsName.contains(initialConsonantsQuery, ignoreCase = true)
        }
        .distinct() // 중복 제거
        .take(AppConstants.MAX_SEARCH_SUGGESTIONS) // 제안 개수 제한
        .sorted() // 알파벳 순 정렬
}
